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
package jetbrick.web.mvc.action;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.InjectParameterWith;
import jetbrick.util.ExceptionUtils;
import jetbrick.util.Validate;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.action.annotation.*;
import jetbrick.web.mvc.multipart.FilePart;
import jetbrick.web.servlet.map.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局 ArgumentGetter 管理器.
 */
public final class ArgumentGetterResolver {
    private final Logger log = LoggerFactory.getLogger(ArgumentGetterResolver.class);
    private final Map<Class<?>, TypedArgumentGetter<?>> typedMaps = new IdentityHashMap<Class<?>, TypedArgumentGetter<?>>();
    private final Map<Class<?>, Class<AnnotatedArgumentGetter<?, ?>>> annotatedMaps = new IdentityHashMap<Class<?>, Class<AnnotatedArgumentGetter<?, ?>>>();

    public void initialize() {
        // typed
        register(RequestContext.class, RequestContextArgumentGetter.class);
        register(Model.class, ModelArgumentGetter.class);
        register(HttpServletRequest.class, HttpServletRequestArgumentGetter.class);
        register(HttpServletResponse.class, HttpServletResponseArgumentGetter.class);
        register(HttpSession.class, HttpSessionArgumentGetter.class);
        register(ServletContext.class, ServletContextArgumentGetter.class);
        register(FilePart.class, FilePartArgumentGetter.class);
        register(FilePart[].class, FilePartsArgumentGetter.class);
        register(RequestAttributeMap.class, RequestAttributeMapArgumentGetter.class);
        register(SessionAttributeMap.class, SessionAttributeMapArgumentGetter.class);
        register(ServletContextAttributeMap.class, ServletContextAttributeMapArgumentGetter.class);
        register(RequestParameterMap.class, RequestParameterMapArgumentGetter.class);
        register(RequestParameterValuesMap.class, RequestParameterValuesMapArgumentGetter.class);
        register(RequestHeaderMap.class, RequestHeaderMapArgumentGetter.class);
        register(RequestHeaderValuesMap.class, RequestHeaderValuesMapArgumentGetter.class);
        register(RequestCookieMap.class, RequestCookieMapArgumentGetter.class);
        register(ServletContextInitParameterMap.class, ServletContextInitParameterMapArgumentGetter.class);

        // annotated
        register(PathVariable.class, PathVariableArgumentGetter.class);
        register(RequestParam.class, RequestParamArgumentGetter.class);
        register(RequestForm.class, RequestFormArgumentGetter.class);
        register(RequestBody.class, RequestBodyArgumentGetter.class);
        register(RequestHeader.class, RequestHeaderArgumentGetter.class);
        register(RequestCookie.class, RequestCookieArgumentGetter.class);
        register(RequestAttribute.class, RequestAttributeArgumentGetter.class);
        register(SessionAttribute.class, SessionAttributeArgumentGetter.class);
        register(ServletContextAttribute.class, ServletContextAttributeArgumentGetter.class);
        register(InitParameter.class, InitParameterArgumentGetter.class);
    }

    @SuppressWarnings("unchecked")
    public void register(Class<?> type, Class<?> argumentGetterClass) {
        log.debug("register ArgumentGetter: {} -> {}", type.getName(), argumentGetterClass.getName());

        if (TypedArgumentGetter.class.isAssignableFrom(argumentGetterClass)) {
            // singleton
            try {
                Ioc ioc = WebConfig.getIoc();
                TypedArgumentGetter<?> getter = (TypedArgumentGetter<?>) ioc.newInstance(argumentGetterClass);
                ioc.injectSetters(getter);
                ioc.initialize(getter);
                typedMaps.put(type, getter);
            } catch (Exception e) {
                throw ExceptionUtils.unchecked(e);
            }
        } else if (AnnotatedArgumentGetter.class.isAssignableFrom(argumentGetterClass)) {
            annotatedMaps.put(type, (Class<AnnotatedArgumentGetter<?, ?>>) argumentGetterClass);
        } else {
            throw new IllegalStateException("Invalid class " + argumentGetterClass);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> TypedArgumentGetter<T> lookup(Class<T> type) {
        return (TypedArgumentGetter<T>) typedMaps.get(type);
    }

    @SuppressWarnings("rawtypes")
    public <A extends Annotation> Class<? extends AnnotatedArgumentGetter> lookup(A annotation) {
        Validate.notNull(annotation);

        // Special code
        if (annotation.annotationType().isAnnotationPresent(InjectParameterWith.class)) {
            return IocBeanArgumentGetter.class;
        }

        return annotatedMaps.get(annotation.annotationType());
    }
}
