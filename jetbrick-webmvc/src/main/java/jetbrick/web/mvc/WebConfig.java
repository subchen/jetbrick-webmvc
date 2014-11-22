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
import java.util.List;
import javax.servlet.ServletContext;
import jetbrick.config.Config;
import jetbrick.ioc.Ioc;
import jetbrick.util.VersionUtils;
import jetbrick.web.mvc.action.*;
import jetbrick.web.mvc.action.annotation.RequestBodyGetterResolver;
import jetbrick.web.mvc.action.annotation.RequestParamGetterResolver;
import jetbrick.web.mvc.interceptor.Interceptor;
import jetbrick.web.mvc.multipart.FileUploadResolver;
import jetbrick.web.mvc.plugin.Plugin;

public final class WebConfig {
    public static final String DEFAULT_CONFIG_FILE = "/WEB-INF/jetbrick-webmvc.properties";
    public static final String VERSION = VersionUtils.getVersion(WebConfig.class);

    protected static ServletContext servletContext;
    protected static File webroot;
    protected static Ioc ioc;
    protected static Config config;
    protected static boolean development;
    protected static String httpEncoding;
    protected static boolean httpCache;
    protected static File uploaddir;
    protected static BypassRequestUrls bypassRequestUrls;
    protected static Router router;
    protected static ExceptionHandler exceptionHandler;
    protected static FileUploadResolver fileUploadResolver;
    protected static ResultHandlerResolver resultHandlerResolver;
    protected static ViewHandlerResolver viewHandlerResolver;
    protected static ArgumentGetterResolver argumentGetterResolver;
    protected static RequestParamGetterResolver requestParamGetterResolver;
    protected static RequestBodyGetterResolver requestBodyGetterResolver;
    protected static List<Interceptor> interceptors;
    protected static List<Plugin> plugins;

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean isDevelopment() {
        return development;
    }

    public static String getHttpEncoding() {
        return httpEncoding;
    }

    public static boolean isHttpCache() {
        return httpCache;
    }

    public static File getWebroot() {
        return webroot;
    }

    public static File getUploaddir() {
        return uploaddir;
    }

    public static Ioc getIoc() {
        return ioc;
    }

    public static BypassRequestUrls getBypassRequestUrls() {
        return bypassRequestUrls;
    }

    public static Router getRouter() {
        return router;
    }

    public static ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public static FileUploadResolver getFileUploadResolver() {
        return fileUploadResolver;
    }

    public static ResultHandlerResolver getResultHandlerResolver() {
        return resultHandlerResolver;
    }

    public static ViewHandlerResolver getViewHandlerResolver() {
        return viewHandlerResolver;
    }

    public static ArgumentGetterResolver getArgumentGetterResolver() {
        return argumentGetterResolver;
    }

    public static RequestParamGetterResolver getRequestParamGetterResolver() {
        return requestParamGetterResolver;
    }

    public static RequestBodyGetterResolver getRequestBodyGetterResolver() {
        return requestBodyGetterResolver;
    }

    public static List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public static List<Plugin> getPlugins() {
        return plugins;
    }
}
