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
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.WebException;

public final class HttpStatusViewHandler implements ViewHandler {

    @Override
    public String getType() {
        return "status";
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public void render(RequestContext ctx, String value) {
        String code = value;
        String message = null;

        int ipos = value.indexOf(':');
        if (ipos > 0) {
            code = value.substring(0, ipos);
            message = value.substring(ipos + 1);
        }

        int status = Integer.valueOf(code);
        if (status >= 400) {
            try {
                if (message == null) {
                    ctx.getResponse().sendError(status);
                } else {
                    ctx.getResponse().sendError(status, message);
                }
            } catch (IOException e) {
                throw WebException.uncheck(e);
            }
        } else {
            ctx.getResponse().setStatus(status);
        }
    }
}
