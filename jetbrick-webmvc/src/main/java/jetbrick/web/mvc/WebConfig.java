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
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Config;
import jetbrick.ioc.annotation.Inject;
import jetbrick.util.Validate;
import jetbrick.web.mvc.action.ArgumentGetterResolver;
import jetbrick.web.mvc.interceptor.Interceptor;
import jetbrick.web.mvc.multipart.DelegatedFileUpload;
import jetbrick.web.mvc.multipart.FileUpload;
import jetbrick.web.mvc.plugin.Plugin;

// 单例模式，启动的时候自动初始化，由 IoC 容器进行管理.
public final class WebConfig {
    private static WebConfig instance;

    public static WebConfig getInstance() {
        return instance;
    }

    @Inject
    private WebConfig() {
        Validate.isTrue(instance == null);
        instance = this;
    }

    @Inject
    private Ioc ioc;

    @Config(value = "web.development", defaultValue = "true")
    private boolean development;

    @Config(value = "web.http.encoding", defaultValue = "utf-8")
    private String httpEncoding;

    @Config(value = "web.http.cache", defaultValue = "false")
    private boolean httpCache;

    @Config("web.upload.dir")
    private File uploaddir;

    @Config("web.root")
    private File webroot;

    @Inject
    private ServletContext servletContext;

    @Config(value = "web.urls.bypass", required = false)
    private BypassRequestUrls bypassUrls;

    @Config("web.urls.router")
    private Router router;

    @Config(value = "web.exception.handler", required = false)
    private ExceptionHandler exceptionHandler;

    @Inject
    private DelegatedFileUpload fileUpload;

    @Inject
    private ResultHandlerResolver resultHandlerResolver;

    @Inject
    private ViewHandlerResolver viewHandlerResolver;

    @Inject
    private ArgumentGetterResolver argumentGetterResolver;

    @Config("web.interceptors")
    private List<Interceptor> interceptors;

    @Config("web.plugins")
    private List<Plugin> plugins;

    public Ioc getIoc() {
        return ioc;
    }

    public boolean isDevelopment() {
        return development;
    }

    public String getHttpEncoding() {
        return httpEncoding;
    }

    public boolean isHttpCache() {
        return httpCache;
    }

    public File getUploaddir() {
        return uploaddir;
    }

    public File getWebroot() {
        return webroot;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public BypassRequestUrls getBypassRequestUrls() {
        return bypassUrls;
    }

    public Router getRouter() {
        return router;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public ResultHandlerResolver getResultHandlerResolver() {
        return resultHandlerResolver;
    }

    public ViewHandlerResolver getViewHandlerResolver() {
        return viewHandlerResolver;
    }

    public ArgumentGetterResolver getArgumentGetterResolver() {
        return argumentGetterResolver;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
