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

import jetbrick.bean.MethodInfo;
import jetbrick.util.concurrent.ConcurrentInitializer;
import jetbrick.util.concurrent.LazyInitializer;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.ResultInfo;
import jetbrick.web.mvc.router.UrlTemplate;

public final class ActionInfo {
    private final ControllerInfo controller;
    private final MethodInfo method;
    private final UrlTemplate urlTemplate;

    private final ConcurrentInitializer<ActionMethodInjector> methodInjector = new LazyInitializer<ActionMethodInjector>() {
        @Override
        protected ActionMethodInjector initialize() {
            return ActionMethodInjector.create(method, controller.getType());
        }
    };

    public ActionInfo(ControllerInfo controller, MethodInfo method, String url) {
        this.controller = controller;
        this.method = method;
        this.urlTemplate = new UrlTemplate(url);
    }

    // 和实际的 URL 进行匹配，并返回成功匹配的参数(pathVariables)
    public boolean match(String[] urlSegments, PathVariables pathVariables) {
        return urlTemplate.match(urlSegments, pathVariables);
    }

    public ResultInfo execute(RequestContext ctx) throws Exception {
        Object object = controller.getObject();
        Object result = methodInjector.get().invoke(object, ctx);
        return new ResultInfo(method.getRawReturnType(controller.getType()), result);
    }

    public MethodInfo getMethod() {
        return method;
    }

    public Class<?> getControllerClass() {
        return controller.getType();
    }
}
