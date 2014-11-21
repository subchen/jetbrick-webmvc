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

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import jetbrick.config.Config;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;

/**
 * Settings for Freemarker.
 *
 * <h2>Set template loader.</h2>
 * <p>
 * <strong>webapp</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /WEB-INF/templates</li>
 * </ul>
 * <p>
 * <strong>file</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /opt/templates</li>
 * </ul>
 * <p>
 * <strong>classpath</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /META-INF/templates</li>
 * </ul>
 * <p>
 * <strong>customize</strong>
 * <ul>
 *   <li>freemarker.template_loader = $templateLoader</li>
 *   <li>$templateLoader = demo.app.TemplaterLoader</li>
 *   <li>$templateLoader.root = ...</li>
 *   <li>$templateLoader.xxx = ...</li>
 * </ul>
 *
 * <h2>Following keys are freemarker buildin config.</h2>
 * <ul>
 *   <li>freemarker.cache_storage</li>
 *   <li>freemarker.template_update_delay</li>
 *   <li>freemarker.auto_import</li>
 *   <li>freemarker.auto_include</li>
 *   <li>freemarker.whitespace_stripping</li>
 *   <li>freemarker.tag_syntax</li>
 *   <li>freemarker.default_encoding</li>
 *   <li>freemarker.localized_lookup</li>
 *   <li>freemarker.strict_syntax</li>
 *   <li>freemarker.datetime_format</li>
 *   <li>freemarker.date_format</li>
 *   <li>freemarker.time_format</li>
 *   <li>freemarker.number_format</li>
 *   <li>freemarker.boolean_format</li>
 *   <li>freemarker.output_encoding</li>
 *   <li>freemarker.locale</li>
 *   <li>freemarker.time_zone</li>
 *   <li>freemarker.classic_compatible</li>
 *   <li>freemarker.template_exception_handler</li>
 *   <li>freemarker.arithmetic_engine</li>
 *   <li>freemarker.object_wrapper</li>
 *   <li>freemarker.url_escaping_charset</li>
 *   <li>freemarker.strict_bean_models</li>
 *   <li>freemarker.auto_flush</li>
 *   <li>freemarker.new_builtin_class_resolver</li>
 * </ul>
 *
 * @author Andy Yin
 * @author Guoqiang Chen
 */
public final class FreemarkerSettings {
    private static final String KEY_PREFIX = "freemarker.";
    private static final String TEMPLATE_LOADER = KEY_PREFIX + "template_loader";
    private static final String TEMPLATE_LOADER_PATH_PREFIX = KEY_PREFIX + "template_loader_path_prefix";

    private static Configuration config = new Configuration();

    public static Configuration getConfig() {
        return config;
    }

    public void initialize(ServletContext sc, Config cfg) throws TemplateException, IOException {
        for (String key : cfg.keySet(KEY_PREFIX)) {
            String value = cfg.asString(key);
            if (TEMPLATE_LOADER.equals(key)) {
                String pathPrefix = cfg.asString(TEMPLATE_LOADER_PATH_PREFIX);
                if ("file".equals(value)) {
                    config.setDirectoryForTemplateLoading(new File(pathPrefix));
                } else if ("classpath".equals(value)) {
                    config.setClassForTemplateLoading(getClass(), pathPrefix);
                } else if ("webapp".equals(value)) {
                    config.setServletContextForTemplateLoading(sc, pathPrefix);
                } else {
                    TemplateLoader loader = cfg.asObject(key, TemplateLoader.class);
                    config.setTemplateLoader(loader);
                }
            } else if (TEMPLATE_LOADER_PATH_PREFIX.equals(key)) {
                continue;
            } else {
                // buildin config
                String name = key.substring(KEY_PREFIX.length());
                config.setSetting(name, value);
            }
        }
    }

    public Template getTemplate(String name) throws IOException {
        return config.getTemplate(name);
    }
}
