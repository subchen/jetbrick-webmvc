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
package jetbrick.web.mvc;

import java.util.HashMap;
import java.util.Map;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Inject;
import jetbrick.ioc.annotation.IocInit;
import jetbrick.util.*;
import jetbrick.web.mvc.result.view.*;

/**
 * 全局 ViewHandler 管理器
 */
public class ViewHandlerResolver {
    private final Map<String, ViewHandler> mapping = new HashMap<String, ViewHandler>();

    @Inject
    private Ioc ioc;

    @IocInit
    public void initialize() {
        register(ServletForwardViewHandler.class);
        register(ServletRedirectViewHandler.class);
        register(HttpStatusViewHandler.class);
        register(PlainDataViewHandler.class);
        register(HtmlDataViewHandler.class);
        register(XmlDataViewHandler.class);
        register(JsDataViewHandler.class);
        register(CssDataViewHandler.class);
        register(JsonDataViewHandler.class);
        register(JspTemplateViewHandler.class);

        if (ClassUtils.available("jetbrick.template.JetEngine")) {
            register(JetxTemplateViewHandler.class);
        }
    }

    public void register(Class<?> viewHandlerClass) {
        Validate.isAssignableFrom(ViewHandler.class, viewHandlerClass);

        ViewHandler viewHandler = (ViewHandler) ioc.newInstance(viewHandlerClass);
        ioc.injectSetters(viewHandler);
        ioc.initialize(viewHandler);
        mapping.put(viewHandler.getType(), viewHandler);

        String suffix = viewHandler.getSuffix();
        if (suffix != null) {
            suffix = StringUtils.removeStart(suffix, ".");
            mapping.put(suffix, viewHandler);
        }
    }

    public ViewHandler lookup(String type) {
        return mapping.get(type);
    }
}
