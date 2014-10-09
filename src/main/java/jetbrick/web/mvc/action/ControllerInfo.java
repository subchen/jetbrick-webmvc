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
package jetbrick.web.mvc.action;

import jetbrick.ioc.object.*;
import jetbrick.web.mvc.config.WebConfig;

public final class ControllerInfo {
    private final Class<?> type;
    private final IocObject iocObject;

    public ControllerInfo(Class<?> type, Controller annotation) {
        this.type = type;

        if (annotation.singleton()) {
            iocObject = new ClassSingletonObject(WebConfig.getInstance().getIoc(), type);
        } else {
            iocObject = new ClassInstanceObject(WebConfig.getInstance().getIoc(), type);
        }
    }

    // 获取一个 Controller 实例
    public Object getObject() throws Exception {
        return iocObject.getObject();
    }

    public Class<?> getType() {
        return type;
    }
}
