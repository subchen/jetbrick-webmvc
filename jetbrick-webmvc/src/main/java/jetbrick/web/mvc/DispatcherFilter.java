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
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.util.JdkUtils;
import jetbrick.web.mvc.action.HttpMethod;
import jetbrick.web.mvc.interceptor.Interceptor;
import jetbrick.web.mvc.interceptor.InterceptorChainImpl;
import jetbrick.web.mvc.multipart.FileUploadResolver;
import jetbrick.web.mvc.plugin.Plugin;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.servlet.RequestUtils;
import jetbrick.web.servlet.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DispatcherFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(DispatcherFilter.class);

    private String httpEncoding;
    private boolean httpCache;
    private Router router;
    private BypassRequestUrls bypassRequestUrls;
    private CORSRequestProcessor corsRequestProcessor;
    private ResultHandlerResolver resultHandlerResolver;
    private FileUploadResolver fileUploadResolver;
    private ExceptionHandler exceptionHandler;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.info("DispatcherFilter starting ...");
        log.info("java.version = {}", JdkUtils.JAVA_VERSION);
        log.info("webmvc.version = {}", WebConfig.VERSION);
        log.info("user.dir = {}", System.getProperty("user.dir"));
        log.info("java.io.tmpdir = {}", System.getProperty("java.io.tmpdir"));
        log.info("user.timezone = {}", System.getProperty("user.timezone"));
        log.info("file.encoding = {}", System.getProperty("file.encoding"));

        try {
            long ts = System.currentTimeMillis();

            ServletContext sc = fc.getServletContext();
            String configLocation = fc.getInitParameter("configLocation");
            WebInitializer.initialize(sc, configLocation);

            httpEncoding = WebConfig.getHttpEncoding();
            httpCache = WebConfig.isHttpCache();
            router = WebConfig.getRouter();
            bypassRequestUrls = WebConfig.getBypassRequestUrls();
            corsRequestProcessor = WebConfig.getCORSRequestProcessor();
            resultHandlerResolver = WebConfig.getResultHandlerResolver();
            fileUploadResolver = WebConfig.getFileUploadResolver();
            exceptionHandler = WebConfig.getExceptionHandler();

            log.info("web.root = {}", WebConfig.getWebroot());
            log.info("web.development = {}", WebConfig.isDevelopment());
            log.info("web.upload.dir = {}", WebConfig.getUploaddir());
            log.info("web.urls.router = {}", router.getClass().getName());
            log.info("web.urls.bypass = {}", (bypassRequestUrls == null) ? null : bypassRequestUrls.getClass().getName());
            log.info("web.urls.cors = {}", (corsRequestProcessor == null) ? null : corsRequestProcessor.getClass().getName());

            for (Plugin plugin : WebConfig.getPlugins()) {
                log.info("load plugin: {}", plugin.getClass().getName());
                plugin.initialize();
            }

            for (Interceptor interceptor : WebConfig.getInterceptors()) {
                log.info("load interceptor: {}", interceptor.getClass().getName());
                interceptor.initialize();
            }

            log.info("DispatcherFilter initialize successfully, Time elapsed: {} ms.", System.currentTimeMillis() - ts);

        } catch (Exception e) {
            log.error("Failed to initialize DispatcherFilter", e);
            log.error("*************************************");
            log.error("          System.exit(1)             ");
            log.error("*************************************");
            System.exit(1);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        request.setCharacterEncoding(httpEncoding);
        response.setCharacterEncoding(httpEncoding);

        String path = RequestUtils.getPathInfo(request);

        if (httpCache == false) {
            ResponseUtils.setBufferOff(response);
        }

        if (bypassRequestUrls != null && bypassRequestUrls.accept(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());

        if (corsRequestProcessor != null) {
            corsRequestProcessor.setHeaders(request, response);
            if (httpMethod == HttpMethod.OPTIONS) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        RouteInfo route = router.lookup(request, path, httpMethod);
        request = fileUploadResolver.transform(request);
        RequestContext ctx = new RequestContext(request, response, path, httpMethod, route);

        try {
            if (route == null || route == RouteInfo.NOT_FOUND) {
                throw new ActionNotFoundException(path);
            }

            InterceptorChainImpl interceptorChain = new InterceptorChainImpl(WebConfig.getInterceptors(), ctx);
            interceptorChain.invoke();

            ResultInfo result = interceptorChain.getResult();
            if (result != null) {
                ResultHandler<Object> handler = resultHandlerResolver.lookup(result.getResultClass());
                handler.handle(ctx, result.getResultObject());
            }
        } catch (Exception e) {
            handleError(ctx, e);
        } finally {
            if (ctx != null) {
                ctx.destory();
            }
        }
    }

    private void handleError(RequestContext ctx, Exception e) throws IOException, ServletException {
        ctx.getRequest().setAttribute(ExceptionHandler.KEY_IN_REQUEST, e);

        if (exceptionHandler != null) {
            try {
                exceptionHandler.handleError(ctx, e);
                return;
            } catch (Exception ex) {
                e = ex;
            }
        }

        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        if (e instanceof IOException) {
            throw (IOException) e;
        }
        if (e instanceof ServletException) {
            throw (ServletException) e;
        }

        throw new ServletException(e);
    }

    @Override
    public void destroy() {
        log.info("DispatcherFilter destroy...");

        for (Interceptor interceptor : WebConfig.getInterceptors()) {
            log.info("destroy interceptor: {}", interceptor.getClass().getName());
            interceptor.destory();
        }

        for (Plugin plugin : WebConfig.getPlugins()) {
            log.info("destroy plugin: {}", plugin.getClass().getName());
            plugin.destory();
        }

        log.info("DispatcherFilter exit.");
    }
}
