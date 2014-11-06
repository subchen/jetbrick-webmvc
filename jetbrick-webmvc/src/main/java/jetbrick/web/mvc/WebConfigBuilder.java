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

import java.lang.annotation.Annotation;
import java.util.*;
import javax.servlet.FilterConfig;
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
import jetbrick.web.mvc.action.ArgumentGetterResolver;
import jetbrick.web.mvc.action.Controller;
import jetbrick.web.mvc.action.annotation.ArgumentGetter;
import jetbrick.web.mvc.multipart.DelegatedFileUpload;
import jetbrick.web.mvc.multipart.FileUpload;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.mvc.result.view.ViewHandler;
import jetbrick.web.servlet.ServletUtils;

public final class WebConfigBuilder {

    public static WebConfig build(FilterConfig fc) {
        ServletContext sc = fc.getServletContext();

        // get config file
        String configLocation = fc.getInitParameter("configLocation");
        if (StringUtils.isEmpty(configLocation)) {
            configLocation = "/WEB-INF/jetbrick-webmvc.properties";
        }

        // load config file
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.load("web.root", ServletUtils.getWebroot(sc).getAbsolutePath());
        configLoader.load("web.upload.dir", "${java.io.tmpdir}");
        configLoader.load(configLocation, sc);
        Config config = configLoader.asConfig();

        // scan components
        List<String> packageNames = config.asStringList("web.scan.packages");
        @SuppressWarnings("unchecked")
        List<Class<? extends Annotation>> annotationList = Arrays.asList(IocBean.class, Controller.class, Managed.class);

        ImplementsScanner scanner = new ImplementsScanner();
        scanner.loadFromConfig();
        scanner.autoscan(packageNames, annotationList);

        // create ioc container
        MutableIoc ioc = new MutableIoc();
        ioc.addBean(Ioc.class.getName(), ioc);
        ioc.addBean(ServletContext.class, sc);
        ioc.addBean(WebConfig.class);
        ioc.addBean(DelegatedFileUpload.class);
        ioc.addBean(ResultHandlerResolver.class);
        ioc.addBean(ViewHandlerResolver.class);
        ioc.addBean(ArgumentGetterResolver.class);
        ioc.load(new IocPropertiesLoader(config));
        ioc.load(new IocAnnotationLoader(scanner.getList(IocBean.class)));

        // put into servletContext
        sc.setAttribute(Ioc.class.getName(), ioc);

        // register others
        registerManagedComponments(ioc, scanner.getList(Managed.class));
        registerControllers(ioc, scanner.getList(Controller.class));

        return ioc.getBean(WebConfig.class);
    }

    private static void registerManagedComponments(Ioc ioc, Collection<Class<?>> classes) {
        ResultHandlerResolver resultHandlerResolver = ioc.getBean(ResultHandlerResolver.class);
        ViewHandlerResolver viewHandlerResolver = ioc.getBean(ViewHandlerResolver.class);
        ArgumentGetterResolver argumentGetterResolver = ioc.getBean(ArgumentGetterResolver.class);
        DelegatedFileUpload fileUpload = ioc.getBean(DelegatedFileUpload.class);

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
            } else if (FileUpload.class.isAssignableFrom(cls)) {
                fileUpload.register(cls);
            } else {
                throw new IllegalStateException("@Managed annotation is illegal in class: " + cls.getName());
            }
        }
    }

    private static void registerControllers(Ioc ioc, Collection<Class<?>> classes) {
        WebConfig webConfig = ioc.getBean(WebConfig.class);
        Router router = webConfig.getRouter();
        for (Class<?> cls : classes) {
            Controller controller = cls.getAnnotation(Controller.class);
            if (controller != null) {
                router.registerController(cls);
            }
        }
    }
}
