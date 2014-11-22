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
import java.lang.annotation.Annotation;
import java.util.*;
import javax.servlet.ServletContext;
import jetbrick.config.Config;
import jetbrick.io.finder.ClassFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;

/**
 * Settings for Freemarker.
 *
 * <h2>Set template loader.</h2>
 *
 * <strong>webapp</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /WEB-INF/templates</li>
 * </ul>
 *
 * <strong>file</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /opt/templates</li>
 * </ul>
 *
 * <strong>classpath</strong>
 * <ul>
 *   <li>freemarker.template_loader = webapp</li>
 *   <li>freemarker.template_loader_path_prefix = /META-INF/templates</li>
 * </ul>
 *
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
 * <h2>Extension keys for freemarker config.</h2>
 * <ul>
 *   <li>freemarker.auto_scan_packages = com.app.demo</li>
 *   <li>freemarker.auto_scan_skiperrors = false</li>
 * </ul>
 *
 * @author Guoqiang Chen
 * @author Andy Yin
 *         add Extension config keys:freemarker.auto_scan_packages
 */
public final class FreemarkerSettings {
    private final Logger log = LoggerFactory.getLogger(FreemarkerSettings.class);

    private static final String KEY_PREFIX = "freemarker.";
    private static final String TEMPLATE_LOADER = KEY_PREFIX + "template_loader";
    private static final String TEMPLATE_LOADER_PATH_PREFIX = KEY_PREFIX + "template_loader_path_prefix";
    private static final String TEMPLATE_AUTO_SCAN_PACKAGES = KEY_PREFIX + "auto_scan_packages";
    private static final String TEMPLATE_AUTO_SCAN_SKIPERRORS = KEY_PREFIX + "auto_scan_skiperrors";

    private static Configuration config = new Configuration();

    public static Configuration getConfig() {
        return config;
    }

    public void initialize(ServletContext sc, Config cfg) throws Exception {
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
            } else if (TEMPLATE_AUTO_SCAN_PACKAGES.equals(key)) {
                boolean skiperrors = cfg.asBoolean(TEMPLATE_AUTO_SCAN_SKIPERRORS, "false");
                autoScanPackages(cfg.asStringList(key), skiperrors);
            } else if (TEMPLATE_AUTO_SCAN_SKIPERRORS.equals(key)) {
                continue;
            } else {
                // buildin config
                String name = key.substring(KEY_PREFIX.length());
                config.setSetting(name, value);
            }
        }
    }

    /**
     * 自动扫描 annotation
     */
    public void autoScanPackages(List<String> packageNames, boolean skipErrors) throws IllegalAccessException, InstantiationException {
        if (packageNames == null || packageNames.size() == 0) {
            return;
        }

        //@formatter:off
        @SuppressWarnings("unchecked")
        List<Class<? extends Annotation>> annotations = Arrays.asList(
                Freemarker.Method.class,
                Freemarker.Directive.class
        );
        //@formatter:on

        log.info("Scanning @Freemarker.Method, @Freemarker.Directive from " + packageNames + " ...");

        long ts = System.currentTimeMillis();
        Set<Class<?>> classes = ClassFinder.getClasses(packageNames, true, annotations, skipErrors);
        log.info("Found {} annotated classes, time elapsed {} ms.", classes.size(), System.currentTimeMillis() - ts);

        for (Class<?> cls : classes) {
            for (Annotation anno : cls.getAnnotations()) {
                if (anno instanceof Freemarker.Method) {
                    config.setSharedVariable(((Freemarker.Method) anno).value(), (TemplateModel) cls.newInstance());
                    break;
                } else if (anno instanceof Freemarker.Directive) {
                    config.setSharedVariable(((Freemarker.Directive) anno).value(), (TemplateModel) cls.newInstance());
                    break;
                }
            }
        }
    }

    public Template getTemplate(String name) throws IOException {
        return config.getTemplate(name);
    }
}
