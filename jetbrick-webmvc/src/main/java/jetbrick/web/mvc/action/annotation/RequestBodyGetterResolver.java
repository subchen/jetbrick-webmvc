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
package jetbrick.web.mvc.action.annotation;

import java.util.IdentityHashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import jetbrick.ioc.Ioc;
import jetbrick.web.mvc.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public final class RequestBodyGetterResolver {
    private final Logger log = LoggerFactory.getLogger(RequestBodyGetterResolver.class);
    private final Map<Class<?>, RequestBodyGetter<?>> getters = new IdentityHashMap<Class<?>, RequestBodyGetter<?>>();

    public void initialize() {
        register(Document.class, XmlDocumentRequestBodyGetter.class);
        register(JAXBElement.class, JAXBElementRequestBodyGetter.class);
    }

    public void register(Class<?> cls, Class<?> getterCls) {
        log.debug("register RequestBodyGetter: {}", getterCls.getName());

        Ioc ioc = WebConfig.getIoc();
        RequestBodyGetter<?> getter = (RequestBodyGetter<?>) ioc.newInstance(getterCls);
        ioc.injectSetters(getter);
        ioc.initialize(getter);

        RequestBodyGetter<?> old = getters.put(cls, getter);
        if (old != null) {
            throw new IllegalStateException("duplicate RequestBodyGetter for " + cls.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> RequestBodyGetter<T> resolve(Class<T> cls) {
        return (RequestBodyGetter<T>) getters.get(cls);
    }
}
