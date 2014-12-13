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

import javax.xml.parsers.*;
import jetbrick.bean.ParameterInfo;
import jetbrick.web.mvc.RequestContext;
import org.w3c.dom.Document;

public final class XmlDocumentRequestBodyGetter implements RequestBodyGetter<Document> {
    private static final DocumentBuilder builder;

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Document get(RequestContext ctx, ParameterInfo parameter) throws Exception {
        return builder.parse(ctx.getRequest().getInputStream());
    }
}
