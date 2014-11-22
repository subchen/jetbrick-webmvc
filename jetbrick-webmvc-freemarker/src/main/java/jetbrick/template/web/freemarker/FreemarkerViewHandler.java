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
package jetbrick.template.web.freemarker;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import jetbrick.config.ConfigLoader;
import jetbrick.ioc.annotation.Config;
import jetbrick.ioc.annotation.IocInit;
import jetbrick.util.StringUtils;
import jetbrick.web.mvc.*;
import jetbrick.web.mvc.result.view.AbstractTemplateViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * View Handler for Freemarker.
 *
 * @author Andy Yin
 * @author Guoqiang Chen
 */
@Managed
public final class FreemarkerViewHandler extends AbstractTemplateViewHandler {
    private final Logger log = LoggerFactory.getLogger(FreemarkerViewHandler.class);
    private final String KEY_CONFIG_LOCATION = "freemarker-config-location";

    @Config(value = "web.view.ftl.prefix", required = false)
    private String prefix;

    @Config(value = "web.view.ftl.suffix", defaultValue = ".ftl")
    private String suffix;

    private FreemarkerSettings freemarkerSettings;

    @Override
    public String getType() {
        return "ftl";
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @IocInit
    private void initialize() throws Exception {
        freemarkerSettings = new FreemarkerSettings();

        ServletContext sc = WebConfig.getServletContext();
        String configLocation = sc.getInitParameter(KEY_CONFIG_LOCATION);
        if (StringUtils.isNotEmpty(configLocation)) {
            log.debug("Loadding Freemarker config from {} ...", configLocation);
            jetbrick.config.Config config = new ConfigLoader().load(configLocation, sc).asConfig();
            freemarkerSettings.initialize(sc, config);
        } else {
            log.debug("Loadding Freemarker config from jetbrick-mvc config file ... ");
            freemarkerSettings.initialize(sc, WebConfig.getConfig());
        }
    }

    @Override
    protected void doRender(RequestContext ctx, String viewPathName) throws IOException, TemplateException {
        HttpServletResponse response = ctx.getResponse();
        response.setContentType("text/html; charset=" + response.getCharacterEncoding());

        Template template = freemarkerSettings.getTemplate(viewPathName);
        WebContextMap context = new WebContextMap(ctx.getRequest(), response, ctx.getModel());

        Writer out = response.getWriter();
        template.process(context, out);
        out.flush();
    }
}
