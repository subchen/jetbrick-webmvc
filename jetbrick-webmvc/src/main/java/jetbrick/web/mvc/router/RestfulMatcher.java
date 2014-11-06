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
import jetbrick.collection.SoftHashMap;
import jetbrick.collection.multimap.MultiValueHashMap;
import jetbrick.collection.multimap.MultiValueMap;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.RouteInfo;
import jetbrick.web.mvc.action.ActionInfo;
import jetbrick.web.mvc.action.PathVariables;

/*
 * <h2>分组匹配算法</h2>
 * <ol>
 *   <li>按照 HttpMethod 分组</li>
 *   <li>按照静态/动态 URL 分组(cache)</li>
 *   <li>动态 URL 先按照 path 长度分组，再按照 group 分组</li>
 * </ol>
 */
final class RestfulMatcher {
    private static final int MAX_PATH_PARTS = 20;
    private Map<String, RouteInfo> staticUrls = new HashMap<String, RouteInfo>(128);
    private Map<String, RouteInfo> cachedUrls = new SoftHashMap<String, RouteInfo>(256);
    private OneByOneMatcher[] matchers = new OneByOneMatcher[MAX_PATH_PARTS]; // 按照长度分组

    public void register(ActionInfo action, String url) {
        if (url.indexOf('{') == -1) {
            staticUrls.put(url, new RouteInfo(action));
        } else {
            String[] urlSegments = StringUtils.split(url.substring(1), '/');
            if (urlSegments.length >= MAX_PATH_PARTS) {
                throw new IllegalStateException("exceed max url parts: " + url);
            }

            OneByOneMatcher matcher = matchers[urlSegments.length];
            if (matcher == null) {
                matcher = new OneByOneMatcher();
                matchers[urlSegments.length] = matcher;
            }

            matcher.register(action, urlSegments);
        }
    }

    public RouteInfo lookup(String url) {
        // 1. 查询静态路由
        RouteInfo info = staticUrls.get(url);

        if (info != null) {
            return info;
        }

        // 2. 查询动态路由缓存
        info = cachedUrls.get(url);

        if (info != null) {
            return info;
        }

        // 3. 开始执行动态路由匹配 (分组匹配)
        String[] urlSegments = StringUtils.split(url.substring(1), '/');
        if (urlSegments.length >= MAX_PATH_PARTS) {
            throw new IllegalStateException("exceed max url parts: " + url);
        }

        OneByOneMatcher matcher = matchers[urlSegments.length];
        if (matcher != null) {
            info = matcher.lookup(urlSegments);
        }

        // 4. 加入缓存
        if (info == null) {
            info = RouteInfo.NOT_FOUND;
        }

        cachedUrls.put(url, info);

        // 5. 返回
        return info;
    }

    // 动态路由匹配(逐个匹配)
    static final class OneByOneMatcher {
        private final MultiValueMap<String, ActionInfo> groups = new MultiValueHashMap<String, ActionInfo>(256);
        private final List<ActionInfo> ungroupList = new ArrayList<ActionInfo>(32);

        // 添加路由信息（按照 URL 前缀分组）
        public void register(ActionInfo action, String[] urlSegments) {
            String group = urlSegments[0];
            if (group.indexOf('{') == -1) {
                groups.put(group, action);
            } else {
                ungroupList.add(action);
            }
        }

        public RouteInfo lookup(String[] urlSegments) {
            // 1. 先 new 出一个存放 URL PathVariables 的对象
            PathVariables pathVariables = new PathVariables();

            // 2. 分组查询
            String group = urlSegments[0];
            List<ActionInfo> actions = groups.getList(group);
            if (actions != null) {
                RouteInfo info = doLookup(actions, urlSegments, pathVariables);
                if (info != null) {
                    return info;
                }
            }

            // 3. 查找未分组的内容
            return doLookup(ungroupList, urlSegments, pathVariables);
        }

        private RouteInfo doLookup(List<ActionInfo> actions, String[] urlSegments, PathVariables pathVariables) {
            for (ActionInfo action : actions) {
                if (action.match(urlSegments, pathVariables)) {
                    return new RouteInfo(action, pathVariables);
                }
            }
            return null;
        }
    }
}
