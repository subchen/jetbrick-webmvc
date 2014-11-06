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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.ioc.annotation.Config;
import jetbrick.web.mvc.RequestContext;

public final class JspTemplateViewHandler extends AbstractTemplateViewHandler {
    @Config(value = "web.view.jsp.prefix", required = false)
    private String prefix;

    @Override
    public String getType() {
        return "jsp";
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return ".jsp";
    }

    @Override
    protected void doRender(RequestContext ctx, String viewPathName) throws Exception {
        if (ctx.getServletContext().getResource(viewPathName) == null) {
            throw new ResourceNotFoundException(viewPathName);
        }

        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        for (Map.Entry<String, Object> entry : ctx.getModel().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        response.setContentType("text/html; charset=" + response.getCharacterEncoding());
        request.getRequestDispatcher(viewPathName).forward(request, response);
    }
}
