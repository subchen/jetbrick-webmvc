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
package jetbrick.web.mvc.action.annotation;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import jetbrick.bean.ParameterInfo;
import jetbrick.io.IoUtils;
import jetbrick.web.mvc.RequestContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;

public final class JSONAwareRequestBodyGetter implements RequestBodyGetter<JSONAware> {

    @Override
    public JSONAware get(RequestContext ctx, ParameterInfo parameter) throws IOException {
        HttpServletRequest request = ctx.getRequest();
        InputStream is = null;
        try {
            is = request.getInputStream();
            String body = IoUtils.toString(is, request.getCharacterEncoding());
            return (JSONAware) JSON.parse(body);
        } finally {
            IoUtils.closeQuietly(is);
        }
    }

}
