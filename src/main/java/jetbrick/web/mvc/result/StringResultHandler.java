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
package jetbrick.web.mvc.result;

import jetbrick.ioc.annotation.*;
import jetbrick.util.FilenameUtils;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.result.view.ViewHandler;

@Managed
public final class StringResultHandler implements ResultHandler<String> {
    @Inject
    private ViewHandlerResolver viewHandlerResolver;

    @Config(value = "web.view.default", defaultValue = "jetx")
    private String defaultViewType;

    private ViewHandler defaultViewHandler;

    @IocInit
    private void initialize() {
        defaultViewHandler = viewHandlerResolver.lookup(defaultViewType);
        if (defaultViewHandler == null) {
            throw new IllegalStateException("Cannot find the default view resolver: " + defaultViewType);
        }
    }

    @Override
    public void handle(RequestContext ctx, String result) throws Exception {
        ViewHandler viewHandler = null;
        String url;

        if (result == null) {
            // 使用默认的 ViewPathName
            url = ctx.getPathInfo();
        } else {
            int ipos = result.indexOf(':');
            if (ipos > 0) {
                // 根据 URL 前缀查找 view
                String type = result.substring(0, ipos);
                url = result.substring(ipos + 1);
                viewHandler = viewHandlerResolver.lookup(type);
                if (viewHandler == null) {
                    throw new WebException("Can't find view resolver for path: " + result);
                }
            } else {
                url = result;
            }
        }

        if (viewHandler == null) {
            // 根据后缀名查找 view
            String suffix = FilenameUtils.getFileExtension(result);
            if (suffix != null) {
                viewHandler = viewHandlerResolver.lookup(suffix);
                if (viewHandler == null) {
                    throw new WebException("Can't find view resolver for path: " + result);
                }
            }
        }

        if (viewHandler == null) {
            // 使用默认配置 view
            viewHandler = defaultViewHandler;
        }

        viewHandler.render(ctx, url);
    }
}
