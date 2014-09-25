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

import jetbrick.web.mvc.RequestContext;

/**
 * 用来注入参数，获取对应的参数值.
 *
 * @param <T> 可以处理的类型(返回值)
 */
public interface ArgumentGetter<T> {

    public static final ArgumentGetter<?>[] EMPTY_ARRAY = new ArgumentGetter[0];

    public T get(RequestContext ctx) throws Exception;
}
