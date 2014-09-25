/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 * Email: subchen@gmail.com
 * URL: http://subchen.github.io/
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
package jetbrick.web.mvc.config;

import java.lang.annotation.Annotation;
import java.util.*;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import jetbrick.bean.TypeResolverUtils;
import jetbrick.config.Config;
import jetbrick.config.ConfigLoader;
import jetbrick.io.finder.ClassFinder;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.MutableIoc;
import jetbrick.ioc.annotation.IocBean;
import jetbrick.ioc.annotation.Managed;
import jetbrick.ioc.loader.IocAnnotationLoader;
import jetbrick.ioc.loader.IocPropertiesLoader;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.action.ArgumentGetterResolver;
import jetbrick.web.mvc.action.Controller;
import jetbrick.web.mvc.action.annotation.ArgumentGetter;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.mvc.result.view.ViewHandler;
import jetbrick.web.servlet.ServletUtils;

public final class WebConfigBuilder {

    public static WebConfig build(FilterConfig fc) {
        ServletContext sc = fc.getServletContext();

        // get config file
        String configLocation = fc.getInitParameter("configLocation");
        if (StringUtils.isEmpty(configLocation)) {
            configLocation = "webroot:/WEB-INF/jetbrick-webmvc.properties";
        }

        // load config file
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.load("web.root", ServletUtils.getWebroot(sc).getAbsolutePath());
        configLoader.load("web.upload.dir", "${java.io.tmpdir}");
        configLoader.load(configLocation, sc);
        Config config = configLoader.asConfig();

        // scan components
        List<String> packageNames = config.asStringList("web.scan.packages");
        Set<Class<?>> componentKlasses = discoveryComponents(packageNames);

        // create ioc container
        MutableIoc ioc = new MutableIoc();
        ioc.addBean(Ioc.class.getName(), ioc);
        ioc.addBean(ServletContext.class.getName(), sc);
        ioc.addBean(WebConfig.class);
        ioc.addBean(ResultHandlerResolver.class);
        ioc.addBean(ViewHandlerResolver.class);
        ioc.addBean(ArgumentGetterResolver.class);
        ioc.load(new IocPropertiesLoader(config));
        ioc.load(new IocAnnotationLoader(componentKlasses));

        // put into servletContext
        sc.setAttribute(Ioc.class.getName(), ioc);

        // register others
        registerManagedComponments(ioc, componentKlasses);
        registerControllers(ioc, componentKlasses);

        return ioc.getBean(WebConfig.class);
    }

    private static Set<Class<?>> discoveryComponents(List<String> packageNames) {
        List<Class<? extends Annotation>> annotationList = new ArrayList<Class<? extends Annotation>>();
        annotationList.add(IocBean.class);
        annotationList.add(Controller.class);
        annotationList.add(Managed.class);
        return ClassFinder.getClasses(packageNames, true, annotationList, true);
    }

    private static void registerManagedComponments(Ioc ioc, Collection<Class<?>> klasses) {
        ResultHandlerResolver resultHandlerResolver = ioc.getBean(ResultHandlerResolver.class);
        ViewHandlerResolver viewHandlerResolver = ioc.getBean(ViewHandlerResolver.class);
        ArgumentGetterResolver argumentGetterResolver = ioc.getBean(ArgumentGetterResolver.class);

        for (Class<?> klass : klasses) {
            for (Annotation annotation : klass.getAnnotations()) {
                if (annotation instanceof Managed) {
                    // 跳过框架自带的 Class，这些class已经被自动注册了。
                    if (klass.getName().startsWith("jetbrick.web.mvc.")) {
                        continue;
                    }
                    if (ResultHandler.class.isAssignableFrom(klass)) {
                        Class<?>[] managedClasses = ((Managed) annotation).value();

                        if (managedClasses.length == 0) {
                            Class<?> type = TypeResolverUtils.getRawType(ResultHandler.class.getTypeParameters()[0], klass);
                            resultHandlerResolver.register(type, klass);
                        } else {
                            for (Class<?> type : managedClasses) {
                                resultHandlerResolver.register(type, klass);
                            }
                        }
                    } else if (ViewHandler.class.isAssignableFrom(klass)) {
                        viewHandlerResolver.register(klass);
                    } else if (ArgumentGetter.class.isAssignableFrom(klass)) {
                        Class<?>[] managedClasses = ((Managed) annotation).value();
                        if (managedClasses.length == 0) {
                            Class<?> type = TypeResolverUtils.getRawType(ArgumentGetter.class.getTypeParameters()[0], klass);
                            argumentGetterResolver.register(type, klass);
                        } else {
                            for (Class<?> type : managedClasses) {
                                argumentGetterResolver.register(type, klass);
                            }
                        }
                    } else {
                        throw new IllegalStateException("@Managed annotation is illegal in class: " + klass.getName());
                    }
                }
            }
        }
    }

    private static void registerControllers(Ioc ioc, Set<Class<?>> klasses) {
        WebConfig webConfig = ioc.getBean(WebConfig.class);
        Router router = webConfig.getRouter();
        for (Class<?> klass : klasses) {
            Controller controller = klass.getAnnotation(Controller.class);
            if (controller != null) {
                router.registerController(klass);
            }
        }
    }
}
