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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jetbrick.util.*;
import jetbrick.web.mvc.action.PathVariables;

/**
 * 按照不同的方式，进行 URL Segment 匹配.
 */
public abstract class UrlSegmentMatcher {
    private static final Map<String, UrlSegmentMatcher> cache = new HashMap<String, UrlSegmentMatcher>();

    public abstract boolean match(String urlSegment, PathVariables pathVariables);

    public static UrlSegmentMatcher create(String urlSegment) {
        UrlSegmentMatcher matcher = cache.get(urlSegment);
        if (matcher == null) {
            matcher = doCreate(urlSegment);
            cache.put(urlSegment, matcher);
        }
        return matcher;
    }

    private static UrlSegmentMatcher doCreate(String urlSegment) {
        int ipos = urlSegment.indexOf('{');
        if (ipos != -1) {
            if (urlSegment.indexOf(':') != -1) {
                return RegexUrlSegmentMatcher.create(urlSegment);
            } else if ((ipos == 0) && (urlSegment.indexOf('}') == (urlSegment.length() - 1))) {
                String name = urlSegment.substring(1, urlSegment.length() - 1);
                return new AnyUrlSegmentMatcher(name);
            } else {
                return RegexUrlSegmentMatcher.create(urlSegment);
            }
        } else if (urlSegment.indexOf('*') != -1) {
            if (urlSegment.length() == 1) {
                return new AnyUrlSegmentMatcher("*");
            } else {
                return new WildcharUrlSegmentMatcher(urlSegment);
            }
        } else if (urlSegment.indexOf('?') != -1) {
            return new WildcharUrlSegmentMatcher(urlSegment);
        } else {
            return new EqualsUrlSegmentMatcher(urlSegment);
        }
    }

    // 直接 equals 匹配
    static final class EqualsUrlSegmentMatcher extends UrlSegmentMatcher {
        private final String pattern;

        public EqualsUrlSegmentMatcher(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean match(String urlSegment, PathVariables pathVariables) {
            return pattern.equals(urlSegment);
        }
    }

    // 任意匹配 (*)
    static final class AnyUrlSegmentMatcher extends UrlSegmentMatcher {
        private final String name;

        public AnyUrlSegmentMatcher(String name) {
            this.name = name;
        }

        @Override
        public boolean match(String urlSegment, PathVariables pathVariables) {
            pathVariables.add(name, urlSegment);

            return true;
        }
    }

    // 通配符匹配
    static final class WildcharUrlSegmentMatcher extends UrlSegmentMatcher {
        private final String pattern;

        public WildcharUrlSegmentMatcher(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean match(String urlSegment, PathVariables pathVariables) {
            return WildcharUtils.match(urlSegment, pattern);
        }
    }

    // 正则表达式匹配
    static abstract class RegexUrlSegmentMatcher extends UrlSegmentMatcher {
        private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{[^}]+\\}");

        public static RegexUrlSegmentMatcher create(String urlSegment) {
            if (JdkUtils.IS_AT_LEAST_JAVA_7) {
                return new Jdk7RegexUrlSegmentMatcher(urlSegment);
            } else {
                return new Jdk6RegexUrlSegmentMatcher(urlSegment);
            }
        }

        protected final String toPattern(String urlSegment, List<String> namedGroupList) {
            StringBuilder sb = new StringBuilder(urlSegment.length() + 16);
            Matcher m = PATH_PARAM_PATTERN.matcher(urlSegment);
            int lastpos = 0;

            while (m.find()) {
                String s = urlSegment.substring(lastpos, m.start());
                sb.append(StringEscapeUtils.escapeJavaRegexPattern(s));

                String name = m.group(1);
                String regex = "[^/]+";
                int pos = name.indexOf(':');
                if (pos > 0) {
                    regex = name.substring(pos + 1).trim();
                    name = name.substring(0, pos).trim();
                }

                sb.append("(?<").append(name).append('>').append(regex).append(')');

                if (namedGroupList != null) {
                    namedGroupList.add(name);
                }

                lastpos = m.end() + 1;
            }

            if (lastpos < urlSegment.length()) {
                String s = urlSegment.substring(lastpos);
                sb.append(StringEscapeUtils.escapeJavaRegexPattern(s));
            }

            return sb.toString();
        }
    }

    // 正则表达式匹配(JDK6)
    static final class Jdk6RegexUrlSegmentMatcher extends RegexUrlSegmentMatcher {
        private final jetbrick.regex.jdk6.Pattern pattern;

        public Jdk6RegexUrlSegmentMatcher(String urlSegment) {
            this.pattern = jetbrick.regex.jdk6.Pattern.compile(toPattern(urlSegment, null));
        }

        @Override
        public boolean match(String urlSegment, PathVariables pathVariables) {
            jetbrick.regex.jdk6.Matcher matcher = pattern.matcher(urlSegment);

            if (matcher.matches()) {
                for (String name : pattern.groupNames()) {
                    String value = matcher.group(name);
                    pathVariables.add(name, value);
                }
                return true;
            }

            return false;
        }
    }

    // 正则表达式匹配(JDK7)
    static final class Jdk7RegexUrlSegmentMatcher extends RegexUrlSegmentMatcher {
        private final Pattern pattern;
        private final List<String> namedGroupList;

        public Jdk7RegexUrlSegmentMatcher(String urlSegment) {
            this.namedGroupList = new ArrayList<String>(8);
            this.pattern = Pattern.compile(toPattern(urlSegment, namedGroupList));
        }

        @Override
        public boolean match(String urlSegment, PathVariables pathVariables) {
            Matcher matcher = pattern.matcher(urlSegment);

            if (matcher.matches()) {
                for (String name : namedGroupList) {
                    String value = matcher.group(name);
                    pathVariables.add(name, value);
                }
                return true;
            }

            return false;
        }
    }
}
