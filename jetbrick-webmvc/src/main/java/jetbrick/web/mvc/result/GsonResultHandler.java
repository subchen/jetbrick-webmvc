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
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrick.ioc.annotation.*;
import jetbrick.web.mvc.RequestContext;
import com.google.gson.*;

@Managed(JsonElement.class)
public class GsonResultHandler implements ResultHandler<JsonElement> {

    @Inject(required = false)
    private Gson gson;

    @IocInit
    public void initialize() {
        if (gson == null) {
            gson = new Gson();
        }
    }

    @Override
    public void handle(RequestContext ctx, JsonElement result) throws IOException {
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        if (result == null) {
            JsonObject json = new JsonObject();
            Enumeration<String> e = request.getAttributeNames();

            while (e.hasMoreElements()) {
                String name = e.nextElement();
                Object value = request.getAttribute(name);
                json.add(name, gson.toJsonTree(value));
            }

            for (Map.Entry<String, Object> entry : ctx.getModel().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                json.add(key, gson.toJsonTree(value));
            }

            result = json;
        }

        String characterEncoding = request.getCharacterEncoding();
        response.setCharacterEncoding(characterEncoding);

        String mimetype = MimetypeUtils.getJSON(request);
        response.setContentType(mimetype + "; charset=" + characterEncoding);

        PrintWriter out = response.getWriter();
        out.write(result.toString());
        out.flush();
    }
}
