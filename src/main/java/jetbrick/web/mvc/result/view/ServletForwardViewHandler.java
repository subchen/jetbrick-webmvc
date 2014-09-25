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
package jetbrick.web.mvc.result.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.ioc.annotation.Managed;
import jetbrick.util.PathUtils;
import jetbrick.web.mvc.RequestContext;

@Managed
public final class ServletForwardViewHandler implements ViewHandler {
    @Override
    public String getType() {
        return "forward";
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public void render(RequestContext ctx, String viewPathName) throws Exception {
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        // 转换相对路径为绝对路径
        viewPathName = PathUtils.getRelativePath(ctx.getPathInfo(), viewPathName);
        request.getRequestDispatcher(viewPathName).forward(request, response);
    }
}
