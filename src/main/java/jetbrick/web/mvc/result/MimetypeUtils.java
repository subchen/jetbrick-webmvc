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
package jetbrick.web.mvc.result;

import javax.servlet.http.HttpServletRequest;

public final class MimetypeUtils {

    // IE 10 以下的版本不支持 application/json
    public static String getJSON(HttpServletRequest request) {
        return isOldIEBrowser(request, 10) ? "text/html" : "application/json";
    }

    // IE 9  以下的版本不支持 application/javscript
    public static String getJavaScript(HttpServletRequest request) {
        return isOldIEBrowser(request, 9) ? "text/html" : "application/javascript";
    }

    private static boolean isOldIEBrowser(HttpServletRequest request, int expectedVersion) {
        try {
            String agent = request.getHeader("user-agent");
            int ipos = agent.indexOf("MSIE");
            if (ipos > 0) {
                ipos = ipos + 4;
                int jpos = agent.indexOf(';', ipos);
                String version = agent.substring(ipos, jpos);
                return Float.parseFloat(version) < expectedVersion;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
