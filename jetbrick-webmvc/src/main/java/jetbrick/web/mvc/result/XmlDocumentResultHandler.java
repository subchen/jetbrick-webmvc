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

import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.WebConfig;
import org.w3c.dom.Document;

/**
 * 输出 XML response.
 *
 * @author Guoqiang Chen
 */
public final class XmlDocumentResultHandler implements ResultHandler<Document> {
    private Transformer outputTransformer;

    @Override
    public void handle(RequestContext ctx, Document document) throws Exception {
        HttpServletResponse response = ctx.getResponse();
        response.setContentType("application/xml");

        Transformer transformer = outputTransformer;
        if (transformer == null) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, WebConfig.getHttpEncoding());
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            if (WebConfig.isDevelopment()) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            }
            outputTransformer = transformer;
        }

        PrintWriter out = response.getWriter();
        DOMSource xmlSource = new DOMSource(document);
        StreamResult outputTarget = new StreamResult(out);
        transformer.transform(xmlSource, outputTarget);

        out.flush();
    }

}
