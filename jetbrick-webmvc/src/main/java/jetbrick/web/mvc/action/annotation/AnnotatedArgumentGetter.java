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
package jetbrick.web.mvc.action.annotation;

import java.lang.annotation.Annotation;
import jetbrick.bean.KlassInfo;
import jetbrick.bean.ParameterInfo;
import jetbrick.typecast.Convertor;
import jetbrick.typecast.TypeCastUtils;
import jetbrick.util.Validate;

/**
 * 根据 annotation 来注入参数
 *
 * @param <A> － 代表的 Annotation
 * @param <T> － 要返回的类型
 */
public interface AnnotatedArgumentGetter<A extends Annotation, T> extends ArgumentGetter<T> {

    public void initialize(ArgumentContext<A> ctx);

    public static class ArgumentContext<T extends Annotation> {
        private final KlassInfo declaringKlass;
        private final ParameterInfo parameter;
        private final T annotation;

        public ArgumentContext(KlassInfo declaringKlass, ParameterInfo parameter, T annotation) {
            this.declaringKlass = declaringKlass;
            this.parameter = parameter;
            this.annotation = annotation;
        }

        public KlassInfo getDeclaringKlass() {
            return declaringKlass;
        }

        public ParameterInfo getParameter() {
            return parameter;
        }

        public T getAnnotation() {
            return annotation;
        }

        public String getParameterName() {
            return parameter.getName();
        }

        public Class<?> getRawParameterType() {
            return parameter.getRawType(declaringKlass);
        }

        public Class<?> getRawParameterComponentType(int index) {
            return parameter.getRawComponentType(declaringKlass.getType(), index);
        }

        /**
         * 获取参数类型的转换器，如果是 String，那么无需转换，返回 null.
         */
        public Convertor<?> getTypeConvertor() {
            Class<?> clazz = getRawParameterType();
            if (clazz == String.class) {
                return null;
            }
            return TypeCastUtils.lookup(clazz);
        }

        public Convertor<?> getComponentTypeConvertor() {
            Class<?> clazz = parameter.getRawType(declaringKlass);
            Validate.isTrue(clazz.isArray(), "parameter is not an array");

            if (clazz == String.class) {
                return null;
            }
            return TypeCastUtils.lookup(clazz.getComponentType());
        }
    }
}
