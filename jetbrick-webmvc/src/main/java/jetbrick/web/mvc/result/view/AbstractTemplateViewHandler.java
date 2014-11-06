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

import jetbrick.util.PathUtils;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.RequestContext;

public abstract class AbstractTemplateViewHandler implements ViewHandler {

    public abstract String getPrefix();

    @Override
    public void render(RequestContext ctx, String viewPathName) throws Exception {
        // 转换相对路径为绝对路径
        String view = PathUtils.getRelativePath(ctx.getPathInfo(), viewPathName);
        if (view.endsWith("/")) {
            view = view.concat("index");
        }

        String prefix = getPrefix();
        if (prefix != null) {
            view = StringUtils.prefix(view, prefix);
        }

        view = StringUtils.suffix(view, getSuffix());

        doRender(ctx, view);
    }

    protected abstract void doRender(RequestContext ctx, String viewPathName) throws Exception;
}
