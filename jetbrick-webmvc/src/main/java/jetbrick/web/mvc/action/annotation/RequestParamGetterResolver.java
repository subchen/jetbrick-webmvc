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
import jetbrick.web.mvc.multipart.FilePart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public final class RequestParamGetterResolver {
    private final Logger log = LoggerFactory.getLogger(RequestParamGetterResolver.class);
    private final Map<Class<?>, RequestParamGetter<?>> getters = new IdentityHashMap<Class<?>, RequestParamGetter<?>>();

    public void initialize() {
        register(FilePart.class, FilePartRequestParamGetter.class);
        register(Document.class, XmlDocumentRequestParamGetter.class);
        register(JAXBElement.class, JAXBElementRequestParamGetter.class);
    }

    public <T> void register(Class<T> cls, Class<?> getterCls) {
        log.debug("register RequestParamGetter: {} -> {}", cls.getName(), getterCls.getName());

        Ioc ioc = WebConfig.getIoc();
        RequestParamGetter<?> getter = (RequestParamGetter<?>) ioc.newInstance(getterCls);
        ioc.injectSetters(getter);
        ioc.initialize(getter);

        RequestParamGetter<?> old = getters.put(cls, getter);
        if (old != null) {
            throw new IllegalStateException("duplicate RequestParamGetter for " + cls.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> RequestParamGetter<T> resolve(Class<T> cls) {
        return (RequestParamGetter<T>) getters.get(cls);
    }
}
