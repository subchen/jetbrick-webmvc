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
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import jetbrick.bean.MethodInfo;
import jetbrick.ioc.Ioc;
import jetbrick.web.mvc.action.HttpMethod;
import jetbrick.web.mvc.multipart.FilePart;
import jetbrick.web.mvc.multipart.MultipartRequest;
import jetbrick.web.mvc.result.ResultHandler;
import jetbrick.web.servlet.RequestIntrospectUtils;

public class RequestContext {
    private final static ThreadLocal<RequestContext> threadContext = new InheritableThreadLocal<RequestContext>();
    private final HttpServletResponse response;
    private final HttpServletRequest request;
    private final String pathInfo;
    private final HttpMethod httpMethod;
    private final RouteInfo routeInfo;

    protected RequestContext(HttpServletRequest request, HttpServletResponse response, String pathInfo, HttpMethod httpMethod, RouteInfo routeInfo) {
        this.request = request;
        this.response = response;
        this.pathInfo = pathInfo;
        this.httpMethod = httpMethod;
        this.routeInfo = routeInfo;

        threadContext.set(this);
    }

    //--- thread context -------------------------------------------------
    protected void destory() {
        threadContext.remove();
    }

    public static RequestContext getCurrent() {
        return threadContext.get();
    }

    //----- servlet ------------------------------------------
    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    public ServletContext getServletContext() {
        return WebConfig.getServletContext();
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }

    public Class<?> getController() {
        return routeInfo.getAction().getControllerClass();
    }

    public MethodInfo getAction() {
        return routeInfo.getAction().getMethod();
    }

    //---- parameters ------------------------------------------------
    public <T> T getForm(T form) {
        RequestIntrospectUtils.introspect(form, request);

        return form;
    }

    public String getParameter(String key) {
        return request.getParameter(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = request.getParameter(key);
        return (value == null) ? defaultValue : value;
    }

    public Integer getParameterAsInt(String key) {
        return getParameterAsInt(key, null);
    }

    public Integer getParameterAsInt(String key, Integer defaultValue) {
        String value = request.getParameter(key);
        return (value == null) ? defaultValue : Integer.valueOf(value);
    }

    public Long getParameterAsLong(String key) {
        return getParameterAsLong(key, null);
    }

    public Long getParameterAsLong(String key, Long defaultValue) {
        String value = request.getParameter(key);
        return (value == null) ? defaultValue : Long.valueOf(value);
    }

    public String[] getParameterValues(String key) {
        return request.getParameterValues(key);
    }

    public String getHeader(String key) {
        return request.getHeader(key);
    }

    public List<String> getHeaders(String key) {
        return Collections.list(request.getHeaders(key));
    }

    public Cookie[] getCookies() {
        return request.getCookies();
    }

    public Cookie getCookie(String name) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public FilePart getFilePart(String name) {
        if (request instanceof MultipartRequest) {
            return ((MultipartRequest) request).getFile(name);
        }
        return null;
    }

    public FilePart getFilePart() {
        Collection<FilePart> files = getFileParts();
        if (files.size() > 0) {
            return files.iterator().next();
        }
        return null;
    }

    public List<FilePart> getFileParts() {
        if (request instanceof MultipartRequest) {
            return ((MultipartRequest) request).getFiles();
        }
        return Collections.emptyList();
    }

    public String getPathVariable(String name) {
        return routeInfo.getPathVariable(name);
    }

    //---- model ------------------------------------------------
    public Model getModel() {
        Model model = (Model) request.getAttribute(Model.NAME_IN_REQUEST);
        if (model == null) {
            model = new Model();
            request.setAttribute(Model.NAME_IN_REQUEST, model);
        }
        return model;
    }

    //---- url ------------------------------------------------
    public File getWebroot() {
        return WebConfig.getWebroot();
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    //---- result ------------------------------------------------
    public void handleResult(Object result) throws Throwable {
        handleResult(result.getClass(), result);
    }

    public void handleResult(Class<?> resultClass, Object result) throws Throwable {
        ResultHandlerResolver resolver = WebConfig.getIoc().getBean(ResultHandlerResolver.class);
        ResultHandler<Object> handler = resolver.lookup(resultClass);
        handler.handle(this, result);
    }
}
