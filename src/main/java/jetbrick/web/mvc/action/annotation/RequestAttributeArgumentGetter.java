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
package jetbrick.web.mvc.action.annotation;

import jetbrick.ioc.annotation.Managed;
import jetbrick.web.mvc.RequestContext;

@Managed
public final class RequestAttributeArgumentGetter implements AnnotatedArgumentGetter<RequestAttribute, Object> {
    private String name;
    private boolean required;

    @Override
    public void initialize(ArgumentContext<RequestAttribute> ctx) {
        RequestAttribute annotation = ctx.getAnnotation();
        this.name = annotation.value();
        this.required = annotation.required();
    }

    @Override
    public Object get(RequestContext ctx) {
        Object value = ctx.getRequest().getAttribute(name);
        if (value == null && required) {
            throw new IllegalStateException("request attribute is not found: " + name);
        }
        return value;
    }
}
