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
package jetbrick.web.mvc.router;

import jetbrick.util.StringUtils;
import jetbrick.util.Validate;
import jetbrick.web.mvc.action.PathVariables;

// 代表 Action 配置的 URL 模板
public final class UrlTemplate {
    private final String url;
    private final UrlSegmentMatcher[] matchers;

    public UrlTemplate(String url) {
        Validate.notEmpty(url);
        this.url = url;

        Validate.isTrue(url.charAt(0) == '/');

        String[] urlSegments = StringUtils.split(url.substring(1), '/');
        this.matchers = new UrlSegmentMatcher[urlSegments.length];
        for (int i = 0; i < urlSegments.length; i++) {
            matchers[i] = UrlSegmentMatcher.create(urlSegments[i]);
        }
    }

    public String getUrl() {
        return url;
    }

    // 和实际的 URL 进行匹配，并返回成功匹配的参数(pathVariables)
    public boolean match(String[] urlSegments, PathVariables pathVariables) {
        Validate.isTrue(urlSegments.length == matchers.length);

        for (int i = 0; i < matchers.length; i++) {
            if (!matchers[i].match(urlSegments[i], pathVariables)) {
                pathVariables.clear(); // 注意：不匹配的情况下，需要清除此次匹配的内容
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return url;
    }
}
