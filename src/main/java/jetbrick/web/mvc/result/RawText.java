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

import jetbrick.ioc.annotation.ManagedWith;
import jetbrick.web.mvc.RequestContext;

/**
 * 自定义输出文本.
 *
 * @author Guoqiang Chen
 */
@ManagedWith(RawTextResultHandler.class)
public final class RawText {
    private final String text;
    private final String mimetype;

    public static RawText html(String text) {
        return new RawText(text, "text/html");
    }

    public static RawText text(String text) {
        return new RawText(text, "text/plain");
    }

    public static RawText xml(String text) {
        return new RawText(text, "text/xml");
    }

    public static RawText json(String text) {
        RequestContext ctx = RequestContext.getCurrent();
        String mimetype = MimetypeUtils.getJSON(ctx.getRequest());
        return new RawText(text, mimetype);
    }

    public static RawText js(String text) {
        RequestContext ctx = RequestContext.getCurrent();
        String mimetype = MimetypeUtils.getJavaScript(ctx.getRequest());
        return new RawText(text, mimetype);
    }

    public static RawText css(String text) {
        return new RawText(text, "text/css");
    }

    public RawText(String text, String mimetype) {
        this.text = text;
        this.mimetype = mimetype;
    }

    public String getText() {
        return text;
    }

    public String getMimetype() {
        return mimetype;
    }
}
