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
package jetbrick.web.mvc.interceptor;

import java.util.List;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.ResultInfo;
import jetbrick.web.mvc.action.ActionInfo;

/**
 * 依次执行所有的 Intercepter，完成后在执行 action
 */
public final class InterceptorChainImpl implements InterceptorChain {
    private final List<Interceptor> interceptors;
    private final RequestContext ctx;
    private int currentIndex = 0;
    private ResultInfo result;

    public InterceptorChainImpl(List<Interceptor> interceptors, RequestContext ctx) {
        this.interceptors = interceptors;
        this.ctx = ctx;
    }

    @Override
    public void invoke() throws Exception {
        if (currentIndex < interceptors.size()) {
            Interceptor interceptor = interceptors.get(currentIndex++);
            interceptor.intercept(ctx, this);
        } else {
            executeAction(ctx);
        }
    }

    public ResultInfo getResult() {
        return result;
    }

    private void executeAction(RequestContext ctx) throws Exception {
        ActionInfo action = ctx.getRouteInfo().getAction();
        result = action.execute(ctx);
    }
}
