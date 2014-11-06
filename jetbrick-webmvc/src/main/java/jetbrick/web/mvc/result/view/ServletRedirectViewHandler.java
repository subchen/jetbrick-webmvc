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

import java.io.IOException;
import jetbrick.util.PathUtils;
import jetbrick.web.mvc.RequestContext;

public final class ServletRedirectViewHandler implements ViewHandler {
    @Override
    public String getType() {
        return "redirect";
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public void render(RequestContext ctx, String viewPathName) throws IOException {
        // 转换相对路径为绝对路径
        viewPathName = PathUtils.getRelativePath(ctx.getPathInfo(), viewPathName);
        if (viewPathName.charAt(0) == '/') {
            // 添加 context path
            viewPathName = ctx.getContextPath() + viewPathName;
        }

        ctx.getResponse().sendRedirect(viewPathName);
    }
}
