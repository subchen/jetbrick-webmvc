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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import jetbrick.ioc.annotation.IocInit;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.BypassRequestUrls;

// 用来过滤静态文件等非 mvc filter 需要处理的文件, 使用正则表达式匹配算法
public final class RegexBypassRequestUrls implements BypassRequestUrls {
    public static final String DEFAULT_PATTERNS = "^(.+[.])(jsp|js|css|jpg|png|gif|ico|swf)$, ^(/assets/|/static/).+$";

    private String patterns = DEFAULT_PATTERNS;
    private List<Pattern> patternList = new ArrayList<Pattern>(8);

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

    @IocInit
    private void initialize() {
        if (patterns != null && patterns.length() > 0) {
            for (String pattern : StringUtils.split(patterns, ',')) {
                pattern = StringUtils.trimToNull(pattern);
                if (pattern != null) {
                    patternList.add(Pattern.compile(pattern));
                }
            }
        }
    }

    @Override
    public boolean accept(HttpServletRequest request, String path) {
        for (Pattern pattern : patternList) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }
}
