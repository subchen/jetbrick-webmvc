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

import jetbrick.ioc.annotation.Managed;
import jetbrick.typecast.Convertor;
import jetbrick.typecast.TypeCastUtils;
import jetbrick.util.ArrayUtils;
import jetbrick.util.StringUtils;
import jetbrick.util.annotation.ValueConstants;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.multipart.FilePart;

@Managed
public final class RequestParamArgumentGetter implements AnnotatedArgumentGetter<RequestParam, Object> {
    // 区分不同的场景
    private static final int SCENARIO_FILE = 1;
    private static final int SCENARIO_ARRAY = 2;
    private static final int SCENARIO_ELEMENT = 3;

    private int scenario;
    private Class<?> type; // 参数类型
    private String name;
    private boolean required;
    private String defaultValue;
    private Convertor<?> cast;

    @Override
    public void initialize(ArgumentContext<RequestParam> ctx) {
        type = ctx.getRawParameterType();

        if (FilePart.class.isAssignableFrom(type)) {
            scenario = SCENARIO_FILE;
            cast = null;
        } else if (type.isArray()) {
            scenario = SCENARIO_ARRAY;
            type = type.getComponentType();
            cast = null;
        } else {
            scenario = SCENARIO_ELEMENT;
            cast = ctx.getTypeConvertor();
        }

        RequestParam annotation = ctx.getAnnotation();
        name = annotation.value();
        if (ValueConstants.isEmptyOrNull(name)) {
            name = ctx.getParameterName();
        }
        required = annotation.required();
        defaultValue = ValueConstants.trimToNull(annotation.defaultValue());
    }

    @Override
    public Object get(RequestContext ctx) {
        switch (scenario) {
        case SCENARIO_ELEMENT: {
            String value = ctx.getParameter(name);
            if (value == null) {
                value = defaultValue;
            }

            if (value == null) {
                if (required) {
                    throw new IllegalStateException("request parameter is not found: " + name);
                }
                return null;
            }

            if (cast != null) {
                return cast.convert(value);
            }

            return value;
        }

        case SCENARIO_ARRAY: {
            String[] values = ctx.getParameterValues(name);
            if (values == null) {
                if (defaultValue != null) {
                    values = StringUtils.split(defaultValue, ',');
                }
            }

            if (values == null) {
                values = ArrayUtils.EMPTY_STRING_ARRAY;
            }

            return TypeCastUtils.convertToArray(values, type);
        }

        case SCENARIO_FILE: {
            Object value = ctx.getFilePart(name);
            if (value == null && required) {
                throw new IllegalStateException("upload file object is not found: " + name);
            }
            return value;
        }
        }

        throw new IllegalStateException("unreachable");
    }
}
