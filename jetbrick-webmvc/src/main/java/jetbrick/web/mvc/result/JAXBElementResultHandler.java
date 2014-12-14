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
import javax.xml.bind.*;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.WebConfig;

/**
 * 输出  JAXMElement response.
 *
 * @author Guoqiang Chen
 */
public final class JAXBElementResultHandler implements ResultHandler<JAXBElement<?>> {

    @Override
    public void handle(RequestContext ctx, JAXBElement<?> jaxbElement) throws Exception {
        HttpServletResponse response = ctx.getResponse();
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();

        JAXBContext jc = JAXBContext.newInstance(jaxbElement.getDeclaredType());
        Marshaller marshaler = jc.createMarshaller();
        marshaler.setProperty(Marshaller.JAXB_ENCODING, WebConfig.getHttpEncoding());
        if (WebConfig.isDevelopment()) {
            marshaler.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }
        marshaler.marshal(jaxbElement, out);

        out.flush();
    }

}
