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

import javax.servlet.http.HttpServletRequest;
import jetbrick.bean.KlassInfo;
import jetbrick.bean.MethodInfo;
import jetbrick.util.*;
import jetbrick.util.annotation.ValueConstants;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.action.*;
import jetbrick.web.mvc.config.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h2>URL 映射规则：</h2>
 * <ul>
 *   <li>/users + (null)  == /users/(method)</li>
 *   <li>/users + (empty) == /users/(method)</li>
 *   <li>/users + /       == /users/</li>
 *   <li>/users + /add    == /users/add</li>
 * </ul>
 */
public final class RestfulRouter implements Router {
    private final Logger log = LoggerFactory.getLogger(RestfulRouter.class);
    private final RestfulMatcher[] matchers = new RestfulMatcher[HttpMethod.METHOD_LENGTH];

    /**
     * 根据 annotation，获取所有的 Action
     */
    @Override
    public void registerController(Class<?> clazz) {
        Controller controller = clazz.getAnnotation(Controller.class);
        Validate.notNull(controller);

        String ctrlPath = ValueConstants.trimToEmpty(controller.value());
        ControllerInfo ctrlInfo = new ControllerInfo(clazz, controller);

        ResultHandlerResolver resultHandlerResolver = WebConfig.getInstance().getResultHandlerResolver();
        KlassInfo klass = KlassInfo.create(clazz);

        for (MethodInfo actionMethod : klass.getMethods()) {
            if (!klass.isPublic() || actionMethod.isStatic()) {
                continue;
            }

            Action action = actionMethod.getAnnotation(Action.class);
            if (action == null) {
                continue;
            }

            String actionPath = ValueConstants.defaultValue(action.value(), actionMethod.getName());
            String url = StringUtils.removeEnd(ctrlPath, "/") + StringUtils.prefix(actionPath, "/");

            // validate the action result type
            Class<?> returnClass = actionMethod.getRawReturnType(clazz);
            if (!resultHandlerResolver.validate(returnClass)) {
                throw new IllegalStateException("Unsupported result class: " + returnClass.getName() + " of " + actionMethod);
            }

            HttpMethod[] httpMethods = action.method();
            Validate.isTrue(httpMethods.length > 0);

            if (log.isDebugEnabled()) {
                log.debug("found action: {} {}", ArrayUtils.toString(httpMethods), url);
            }

            ActionInfo actionInfo = new ActionInfo(ctrlInfo, actionMethod, url);
            for (HttpMethod method : httpMethods) {
                RestfulMatcher matcher = matchers[method.getIndex()];
                if (matcher == null) {
                    matcher = new RestfulMatcher();
                    matchers[method.getIndex()] = matcher;
                }
                matcher.register(actionInfo, url);
            }
        }
    }

    @Override
    public RouteInfo lookup(HttpServletRequest request, String path, HttpMethod method) {
        RestfulMatcher matcher = matchers[method.getIndex()];
        if (matcher != null) {
            return matcher.lookup(path);
        }
        return RouteInfo.NOT_FOUND;
    }
}
