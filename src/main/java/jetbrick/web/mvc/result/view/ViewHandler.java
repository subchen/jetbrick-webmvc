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
package jetbrick.web.mvc.result.view;

import jetbrick.web.mvc.RequestContext;

// 所有子类都是单例
public interface ViewHandler {
    /**
     * 返回 ViewHandler 的类型.
     */
    public String getType();

    /**
     * 返回 ViewHandler 默认的扩展名.
     */
    public String getSuffix();

    /**
     * 如果 Resource 不存在，那么应该 throw java.io.ResourceNotFoundException.
     */
    public void render(RequestContext ctx, String viewPathName) throws Exception;
}
