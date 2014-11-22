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
import jetbrick.util.StringUtils;
import jetbrick.util.Validate;
import jetbrick.web.mvc.result.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局 ViewHandler 管理器
 */
public final class ViewHandlerResolver {
    private final Logger log = LoggerFactory.getLogger(ViewHandlerResolver.class);
    private final Map<String, ViewHandler> mapping = new HashMap<String, ViewHandler>();

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
    }

    public void register(Class<?> viewHandlerClass) {
        Validate.isAssignableFrom(ViewHandler.class, viewHandlerClass);

        Ioc ioc = WebConfig.getIoc();
        ViewHandler viewHandler = (ViewHandler) ioc.newInstance(viewHandlerClass);
        ioc.injectSetters(viewHandler);
        ioc.initialize(viewHandler);
        mapping.put(viewHandler.getType(), viewHandler);

        log.debug("register ViewHandler: {} -> {}", viewHandler.getType(), viewHandlerClass.getName());

        String suffix = viewHandler.getSuffix();
        if (suffix != null) {
            suffix = StringUtils.removeStart(suffix, ".");
            if (mapping.put(suffix, viewHandler) == null) {
                log.debug("register ViewHandler: {} -> {}", suffix, viewHandlerClass.getName());
            }
        }
    }

    public ViewHandler lookup(String type) {
        return mapping.get(type);
    }
}
