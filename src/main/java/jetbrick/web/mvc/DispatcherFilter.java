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

import java.io.IOException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.util.VersionUtils;
import jetbrick.util.JdkUtils;
import jetbrick.web.mvc.action.HttpMethod;
import jetbrick.web.mvc.config.WebConfig;
import jetbrick.web.mvc.config.WebConfigBuilder;
import jetbrick.web.mvc.interceptor.Interceptor;
import jetbrick.web.mvc.interceptor.InterceptorChainImpl;
import jetbrick.web.mvc.plugin.Plugin;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.servlet.RequestUtils;
import jetbrick.web.servlet.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DispatcherFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(DispatcherFilter.class);
    private WebConfig config;
    private BypassRequestUrls bypassUrls;
    private Router router;
    private ResultHandlerResolver resultHandlerResolver;
    private ExceptionHandler exceptionHandler;
    private String encoding;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.info("DispatcherFilter starting ...");
        log.info("java.version = {}", JdkUtils.JAVA_VERSION);
        log.info("jetbrick.version = {}", VersionUtils.getVersion(getClass()));
        log.info("user.dir = {}", System.getProperty("user.dir"));
        log.info("java.io.tmpdir = {}", System.getProperty("java.io.tmpdir"));
        log.info("user.timezone = {}", System.getProperty("user.timezone"));
        log.info("file.encoding = {}", System.getProperty("file.encoding"));

        WebContext.setServletContext(fc.getServletContext());

        try {
            config = WebConfigBuilder.build(fc);
            encoding = config.getHttpEncoding();
            bypassUrls = config.getBypassRequestUrls();
            router = config.getRouter();
            resultHandlerResolver = config.getResultHandlerResolver();
            exceptionHandler = config.getExceptionHandler();

            log.info("router = {}", router.getClass().getName());
            log.info("exception.handler = {}", (exceptionHandler == null) ? null : exceptionHandler.getClass().getName());

            for (Plugin plugin : config.getPlugins()) {
                log.info("load plugin: {}", plugin.getClass().getName());
                plugin.init(config);
            }

            for (Interceptor interceptor : config.getInterceptors()) {
                log.info("load interceptor: {}", interceptor.getClass().getName());
                interceptor.init(config);
            }
        } catch (Exception e) {
            log.error("DispatcherFilter init error.", e);
            log.error("************************************");
            log.error("       System.exit() !");
            log.error("************************************");
            System.exit(1);
        }

        log.info("development = {}", config.isDevelopment());
        log.info("web.root = {}", config.getWebroot());
        log.info("DispatcherFilter loaded successfully.");
    }

    @Override
    public void destroy() {
        log.info("DispatcherFilter destroy...");

        for (Interceptor interceptor : config.getInterceptors()) {
            log.info("destroy interceptor: {}", interceptor.getClass().getName());
            interceptor.destory();
        }

        for (Plugin plugin : config.getPlugins()) {
            log.info("destroy plugin: {}", plugin.getClass().getName());
            plugin.destory();
        }

        log.info("DispatcherFilter exit.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);

        String path = RequestUtils.getPathInfo(request);

        if (bypassUrls != null && bypassUrls.accept(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        if (config.isHttpCache() == false) {
            ResponseUtils.setBufferOff(response);
        }

        RequestContext ctx = null;
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
            RouteInfo route = router.lookup(request, path, httpMethod);
            ctx = new RequestContext(request, response, path, httpMethod, route);

            List<Interceptor> interceptors = config.getInterceptors();
            InterceptorChainImpl interceptorChain = new InterceptorChainImpl(interceptors, ctx);
            interceptorChain.invoke();

            ResultInfo result = interceptorChain.getResult();
            if (result != null) {
                ResultHandler<Object> handler = resultHandlerResolver.lookup(result.getResultClass());
                handler.handle(ctx, result.getResultObject());
            }
        } catch (Exception e) {
            request.setAttribute(ExceptionHandler.KEY_IN_REQUEST, e);

            if (exceptionHandler != null) {
                try {
                    exceptionHandler.handleError(ctx, e);
                    return;
                } catch (Exception ex) {
                    e = ex;
                }
            }

            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }

            throw new ServletException(e);
        } finally {
            if (ctx != null) {
                ctx.destory();
            }
        }
    }
}
