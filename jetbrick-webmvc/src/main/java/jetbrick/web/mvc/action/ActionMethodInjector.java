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
import java.lang.reflect.Method;
import java.util.List;
import jetbrick.bean.*;
import jetbrick.ioc.Ioc;
import jetbrick.util.ArrayUtils;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.action.annotation.*;
import jetbrick.web.mvc.action.annotation.AnnotatedArgumentGetter.ArgumentContext;

final class ActionMethodInjector {
    private final Method method;
    private final ArgumentGetter<?>[] resolvers;

    @SuppressWarnings("unchecked")
    public static ActionMethodInjector create(MethodInfo method, Class<?> declaringClass) {
        List<ParameterInfo> parameters = method.getParameters();
        if (parameters.size() == 0) {
            return new ActionMethodInjector(method.getMethod(), ArgumentGetter.EMPTY_ARRAY);
        }

        Ioc ioc = WebConfig.getInstance().getIoc();
        ArgumentGetterResolver resolver = WebConfig.getInstance().getArgumentGetterResolver();
        ArgumentGetter<?>[] resolvers = new ArgumentGetter[parameters.size()];

        for (int i = 0; i < resolvers.length; i++) {
            ParameterInfo parameter = parameters.get(i);
            ArgumentGetter<?> getter = null;
            for (Annotation annotation : parameter.getAnnotations()) {
                Class<?> argumentGetterClass = resolver.lookup(annotation);
                if (argumentGetterClass == null) {
                    // 没有注册 annotation， 那么尝试自动发现
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    ManagedWith managedWith = annotationType.getAnnotation(ManagedWith.class);
                    if (managedWith != null) {
                        argumentGetterClass = managedWith.value();
                        if (AnnotatedArgumentGetter.class.isAssignableFrom(argumentGetterClass)) {
                            // 找到一个没有注册的 annotation，在这里注册
                            resolver.register(annotationType, argumentGetterClass);
                        } else {
                            argumentGetterClass = null; // 不是我们要的，恢复成 null
                        }
                    }
                }

                if (argumentGetterClass != null) {
                    getter = (ArgumentGetter<?>) ioc.newInstance(argumentGetterClass);
                    ioc.injectSetters(getter);
                    ioc.initialize(getter);

                    ArgumentContext<Annotation> ctx = new ArgumentContext<Annotation>(KlassInfo.create(argumentGetterClass), parameter, annotation);
                    ((AnnotatedArgumentGetter<Annotation, ?>) getter).initialize(ctx);

                    break;
                }
            }

            if (getter == null) {
                // 没有找到标注，那么尝试根据参数类型来查找
                Class<?> type = parameter.getRawType(declaringClass);
                getter = resolver.lookup(type);
            }

            if (getter == null) {
                throw new IllegalStateException("cannot inject parameter: " + parameter);
            }

            resolvers[i] = getter;
        }

        return new ActionMethodInjector(method.getMethod(), resolvers);
    }

    public ActionMethodInjector(Method method, ArgumentGetter<?>[] resolvers) {
        this.method = method;
        this.resolvers = resolvers;
    }

    public Object invoke(Object action, RequestContext ctx) throws Exception {
        Object[] parameters = ArrayUtils.EMPTY_OBJECT_ARRAY;
        int length = resolvers.length;
        if (length > 0) {
            parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                parameters[i] = resolvers[i].get(ctx);
            }
        }
        return method.invoke(action, parameters);
    }
}
