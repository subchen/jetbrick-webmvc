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
package jetbrick.util.fastjson;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.*;

public final class JSON {

    @SuppressWarnings("unchecked")
    public static JSONArray toJSON(List<?> list) {
        return new JSONArray((List<Object>) list);
    }

    @SuppressWarnings("unchecked")
    public static JSONObject toJSON(Map<?, ?> map) {
        return new JSONObject((Map<String, Object>) map);
    }

    public static JSONAware ok() {
        JSONObject json = new JSONObject();
        json.put("succ", Boolean.TRUE);
        return json;
    }

    public static JSONAware ok(String message) {
        JSONObject json = new JSONObject();
        json.put("succ", Boolean.TRUE);
        json.put("message", message);
        return json;
    }

    public static JSONAware fail(String message) {
        JSONObject json = new JSONObject();
        json.put("succ", Boolean.FALSE);
        json.put("message", message);
        return json;
    }

    public static JSONAware fail(Throwable e) {
        JSONObject json = new JSONObject();
        json.put("succ", Boolean.FALSE);
        json.put("message", e.getMessage());
        return json;
    }

    public static JSONAware fail(String message, Throwable e) {
        JSONObject json = new JSONObject();
        json.put("succ", Boolean.FALSE);
        json.put("message", message + ", " + e.getMessage());
        return json;
    }
}
