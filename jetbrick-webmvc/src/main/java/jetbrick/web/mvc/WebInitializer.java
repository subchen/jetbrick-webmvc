/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.web.mvc;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.servlet.ServletContext;
import jetbrick.bean.TypeResolverUtils;
import jetbrick.config.Config;
import jetbrick.config.ConfigLoader;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.MutableIoc;
import jetbrick.ioc.annotation.IocBean;
import jetbrick.ioc.loader.IocAnnotationLoader;
import jetbrick.ioc.loader.IocPropertiesLoader;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.action.*;
import jetbrick.web.mvc.action.annotation.*;
import jetbrick.web.mvc.interceptor.Interceptor;
import jetbrick.web.mvc.multipart.FileUpload;
import jetbrick.web.mvc.multipart.FileUploadResolver;
import jetbrick.web.mvc.plugin.Plugin;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.mvc.result.view.ViewHandler;
import jetbrick.web.mvc.router.RestfulRouter;
import jetbrick.web.servlet.ServletUtils;

public final class WebInitializer {

    public static void initialize(ServletContext sc, String configLocation) {
        File webroot = ServletUtils.getWebroot(sc);

        // get config file
        if (StringUtils.isEmpty(configLocation)) {
            configLocation = WebConfig.DEFAULT_CONFIG_FILE;
        }

        // load config file
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.load("web.root", webroot.getAbsolutePath());
        configLoader.load(configLocation, sc);
        Config config = configLoader.asConfig();

        // scan components
        List<String> packageNames = config.asStringList("web.scan.packages");
        List<Class<? extends Annotation>> annotationList = new ArrayList<Class<? extends Annotation>>(3);
        annotationList.add(IocBean.class);
        annotationList.add(Controller.class);
        annotationList.add(Managed.class);
        ImplementsScanner scanner = new ImplementsScanner();
        scanner.loadFromConfig();
        scanner.autoscan(packageNames, annotationList);

        // create ioc container
        MutableIoc ioc = new MutableIoc();
        ioc.addBean(Ioc.class, ioc);
        ioc.addBean(ServletContext.class, sc);
        ioc.addBean(FileUploadResolver.class);
        ioc.addBean(ResultHandlerResolver.class);
        ioc.addBean(ViewHandlerResolver.class);
        ioc.addBean(ArgumentGetterResolver.class);
        ioc.addBean(RequestParamGetterResolver.class);
        ioc.addBean(RequestBodyGetterResolver.class);
        ioc.load(new IocPropertiesLoader(config));
        ioc.load(new IocAnnotationLoader(scanner.getList(IocBean.class)));

        // init web config
        WebConfig.servletContext = sc;
        WebConfig.webroot = ServletUtils.getWebroot(sc);
        WebConfig.ioc = ioc;
        WebConfig.config = config;
        WebConfig.development = config.asBoolean("web.development", "true");
        WebConfig.httpEncoding = config.asString("web.http.encoding", "utf-8");
        WebConfig.httpCache = config.asBoolean("web.http.cache", "false");
        WebConfig.uploaddir = config.asFile("web.upload.dir", "${java.io.tmpdir}");
        WebConfig.bypassRequestUrls = config.asObject("web.urls.bypass", BypassRequestUrls.class);
        WebConfig.router = config.asObject("web.urls.router", Router.class, RestfulRouter.class.getName());
        WebConfig.exceptionHandler = config.asObject("web.error.handler", ExceptionHandler.class);
        WebConfig.fileUploadResolver = ioc.getBean(FileUploadResolver.class);
        WebConfig.argumentGetterResolver = ioc.getBean(ArgumentGetterResolver.class);
        WebConfig.requestParamGetterResolver = ioc.getBean(RequestParamGetterResolver.class);
        WebConfig.requestBodyGetterResolver = ioc.getBean(RequestBodyGetterResolver.class);
        WebConfig.viewHandlerResolver = ioc.getBean(ViewHandlerResolver.class);
        WebConfig.resultHandlerResolver = ioc.getBean(ResultHandlerResolver.class);
        WebConfig.interceptors = config.asObjectList("web.interceptors", Interceptor.class);
        WebConfig.plugins = config.asObjectList("web.plugins", Plugin.class);

        // ioc init for config object
        if (WebConfig.bypassRequestUrls != null) {
            ioc.injectSetters(WebConfig.bypassRequestUrls);
            ioc.initialize(WebConfig.bypassRequestUrls);
        }
        if (WebConfig.router != null) {
            ioc.injectSetters(WebConfig.router);
            ioc.initialize(WebConfig.router);
        }
        if (WebConfig.exceptionHandler != null) {
            ioc.injectSetters(WebConfig.exceptionHandler);
            ioc.initialize(WebConfig.exceptionHandler);
        }
        for (Plugin plugin : WebConfig.plugins) {
            ioc.injectSetters(plugin);
            ioc.initialize(plugin);
        }
        for (Interceptor interceptor : WebConfig.interceptors) {
            ioc.injectSetters(interceptor);
            ioc.initialize(interceptor);
        }

        // register components
        registerManaged(scanner.getList(Managed.class));
        registerControllers(scanner.getList(Controller.class));
    }

    private static void registerManaged(Collection<Class<?>> classes) {
        ResultHandlerResolver resultHandlerResolver = WebConfig.getResultHandlerResolver();
        ViewHandlerResolver viewHandlerResolver = WebConfig.getViewHandlerResolver();
        ArgumentGetterResolver argumentGetterResolver = WebConfig.getArgumentGetterResolver();
        RequestParamGetterResolver requestParamGetterResolver = WebConfig.getRequestParamGetterResolver();
        RequestBodyGetterResolver requestBodyGetterResolver = WebConfig.getRequestBodyGetterResolver();
        FileUploadResolver fileUploadResolver = WebConfig.getFileUploadResolver();

        // initialize
        resultHandlerResolver.initialize();
        viewHandlerResolver.initialize();
        argumentGetterResolver.initialize();
        requestParamGetterResolver.initialize();
        requestBodyGetterResolver.initialize();
        fileUploadResolver.initialize();

        // register
        for (Class<?> cls : classes) {
            if (ResultHandler.class.isAssignableFrom(cls)) {
                Managed annotation = cls.getAnnotation(Managed.class);
                if (annotation == null || annotation.value().length == 0) {
                    Class<?> type = TypeResolverUtils.getRawType(ResultHandler.class.getTypeParameters()[0], cls);
                    resultHandlerResolver.register(type, cls);
                } else {
                    for (Class<?> type : annotation.value()) {
                        resultHandlerResolver.register(type, cls);
                    }
                }
            } else if (ViewHandler.class.isAssignableFrom(cls)) {
                viewHandlerResolver.register(cls);
            } else if (ArgumentGetter.class.isAssignableFrom(cls)) {
                Managed annotation = cls.getAnnotation(Managed.class);
                if (annotation == null || annotation.value().length == 0) {
                    Class<?> type = TypeResolverUtils.getRawType(ArgumentGetter.class.getTypeParameters()[0], cls);
                    argumentGetterResolver.register(type, cls);
                } else {
                    for (Class<?> type : annotation.value()) {
                        argumentGetterResolver.register(type, cls);
                    }
                }
            } else if (RequestParamGetter.class.isAssignableFrom(cls)) {
                Managed annotation = cls.getAnnotation(Managed.class);
                if (annotation == null || annotation.value().length == 0) {
                    Class<?> type = TypeResolverUtils.getRawType(RequestParamGetter.class.getTypeParameters()[0], cls);
                    requestParamGetterResolver.register(type, cls);
                } else {
                    for (Class<?> type : annotation.value()) {
                        requestParamGetterResolver.register(type, cls);
                    }
                }
            } else if (RequestBodyGetter.class.isAssignableFrom(cls)) {
                Managed annotation = cls.getAnnotation(Managed.class);
                if (annotation == null || annotation.value().length == 0) {
                    Class<?> type = TypeResolverUtils.getRawType(RequestBodyGetter.class.getTypeParameters()[0], cls);
                    requestBodyGetterResolver.register(type, cls);
                } else {
                    for (Class<?> type : annotation.value()) {
                        requestBodyGetterResolver.register(type, cls);
                    }
                }
            } else if (FileUpload.class.isAssignableFrom(cls)) {
                fileUploadResolver.register(cls);
            } else {
                throw new IllegalStateException("@Managed annotation is illegal in class: " + cls.getName());
            }
        }
    }

    private static void registerControllers(Collection<Class<?>> classes) {
        Router router = WebConfig.getRouter();
        for (Class<?> cls : classes) {
            Controller controller = cls.getAnnotation(Controller.class);
            if (controller != null) {
                router.registerController(cls);
            }
        }
    }
}
