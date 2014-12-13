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
package jetbrick.web.mvc.result;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import jetbrick.web.mvc.RequestContext;

/**
 * 自定义输出文本.
 *
 * @author Guoqiang Chen
 */
public final class RawTextResultHandler implements ResultHandler<RawText> {

    @Override
    public void handle(RequestContext ctx, RawText result) throws IOException {
        HttpServletResponse response = ctx.getResponse();

        String contentType = result.getMimetype() + "; charset=" + response.getCharacterEncoding();
        response.setContentType(contentType);

        PrintWriter out = response.getWriter();
        out.write(result.getText());
        out.flush();
    }
}
