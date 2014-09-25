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
package jetbrick.web.mvc.action;

import java.util.*;

/**
 * 为了优化性能，没有使用 Map&lt;String, String&gt;，而是自己实现的容器.
 */
public final class PathVariables {
    private String[] items;
    private int size;

    public PathVariables() {
        this.items = new String[16];
        this.size = 0;
    }

    public void add(String name, String value) {
        if ((items.length - size) < 2) {
            items = Arrays.copyOf(items, items.length + 16);
        }

        items[size] = name;
        items[size + 1] = value;
        size += 2;
    }

    public void clear() {
        // 没有必要将 items 的内容设置为 null，设置 size 就可以了.
        size = 0;
    }

    public String getValue(String name) {
        for (int i = 0; i < size; i += 2) {
            if (items[i].equals(name)) {
                return items[i + 1];
            }
        }
        return null;
    }

    // 返回最终的 map
    public Map<String, String> map() {
        Map<String, String> map = new HashMap<String, String>();
        int i = 0;
        while (i < size) {
            map.put(items[i], items[i + 1]);
            i += 2;
        }
        return map;
    }
}
