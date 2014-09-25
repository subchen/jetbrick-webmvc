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
package jetbrick.web.mvc;

import java.io.File;
import javax.servlet.ServletContext;
import jetbrick.web.servlet.ServletUtils;

public final class WebContext {
    private static ServletContext sc = null;
    private static File webroot = null;

    // 由容器启动时候设置
    protected static void setServletContext(ServletContext sc) {
        WebContext.sc = sc;
    }

    public static ServletContext getServletContext() {
        return sc;
    }

    public static File getWebroot() {
        if (webroot == null) {
            webroot = ServletUtils.getWebroot(sc);
        }
        return webroot;
    }
}
