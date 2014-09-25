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
package jetbrick.web.mvc.router;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import jetbrick.ioc.annotation.IocInit;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.BypassRequestUrls;

// 用来过滤静态文件等非 mvc filter 需要处理的文件, 使用前缀/后缀匹配算法
public final class PrefixSuffixBypassRequestUrls implements BypassRequestUrls {
    public static final String DEFAULT_PATTERNS = "*.jsp, *.js, *.css, *.jpg, *.png, *.gif, *.ico, *.swf, /assets/*, /static/*";

    private String patterns = DEFAULT_PATTERNS;
    private List<String> prefixList;
    private List<String> suffixList;
    private List<String> staticList;

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

    @IocInit
    private void initialize() {
        if ((patterns != null) && (patterns.length() > 0)) {
            for (String pattern : StringUtils.split(patterns, ',')) {
                pattern = StringUtils.trimToNull(pattern);
                if (pattern != null) {
                    if (pattern.charAt(0) == '*') {
                        if (suffixList == null) {
                            suffixList = new ArrayList<String>(8);
                        }
                        suffixList.add(pattern.substring(1));
                    } else if (pattern.charAt(pattern.length() - 1) == '*') {
                        if (prefixList == null) {
                            prefixList = new ArrayList<String>(8);
                        }
                        prefixList.add(pattern.substring(0, pattern.length() - 1));
                    } else {
                        if (staticList == null) {
                            staticList = new ArrayList<String>(8);
                        }
                        staticList.add(pattern);
                    }
                }
            }
        }
    }

    @Override
    public boolean accept(HttpServletRequest request, String path) {
        if (prefixList != null) {
            for (String prefix : prefixList) {
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }

        if (suffixList != null) {
            for (String suffix : suffixList) {
                if (path.endsWith(suffix)) {
                    return true;
                }
            }
        }

        if (staticList != null) {
            for (String full : staticList) {
                if (path.equals(full)) {
                    return true;
                }
            }
        }

        return false;
    }
}
