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

import java.util.IdentityHashMap;
import java.util.Map;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.*;
import jetbrick.util.ClassUtils;
import jetbrick.util.Validate;
import jetbrick.web.mvc.result.*;
import com.alibaba.fastjson.JSONAware;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局 ResultHandler 管理器
 */
public final class ResultHandlerResolver {
    private final Logger log = LoggerFactory.getLogger(ResultHandlerResolver.class);
    private final Map<Class<?>, ResultHandler<?>> mapping = new IdentityHashMap<Class<?>, ResultHandler<?>>();

    @Inject
    private Ioc ioc;

    @IocInit
    public void initialize() {
        register(Void.TYPE, VoidResultHandler.class);
        register(Object.class, ObjectResultHandler.class);
        register(String.class, StringResultHandler.class);
        register(HttpStatus.class, HttpStatusResultHandler.class);
        register(RawText.class, RawTextResultHandler.class);
        register(RawData.class, RawDataResultHandler.class);
        register(RawDownload.class, RawDownloadResultHandler.class);

        if (ClassUtils.available("com.alibaba.fastjson.JSONAware")) {
            register(JSONAware.class, FastjsonResultHandler.class);
        }
        if (ClassUtils.available("com.google.gson.JsonElement")) {
            register(JsonElement.class, GsonResultHandler.class);
        }
    }

    public void register(Class<?> resultClass, Class<?> resultHandlerClass) {
        Validate.isAssignableFrom(ResultHandler.class, resultHandlerClass);

        log.info("register ResultHandler: {} -> {}", resultClass.getName(), resultHandlerClass.getName());

        ResultHandler<?> resultHandler = (ResultHandler<?>) ioc.newInstance(resultHandlerClass);
        ioc.injectSetters(resultHandler);
        ioc.initialize(resultHandler);
        mapping.put(resultClass, resultHandler);
    }

    @SuppressWarnings("unchecked")
    public ResultHandler<Object> lookup(Class<?> resultClass) {
        ResultHandler<Object> result = (ResultHandler<Object>) mapping.get(resultClass);

        if (result == null) {
            // Special code for Object.class as result
            for (Map.Entry<Class<?>, ResultHandler<?>> entry : mapping.entrySet()) {
                Class<?> targetClass = entry.getKey();
                if ((targetClass != Object.class) && targetClass.isAssignableFrom(resultClass)) {
                    return (ResultHandler<Object>) entry.getValue();
                }
            }

            throw new IllegalStateException("Unsupported result class: " + resultClass.getName());
        }

        return result;
    }

    // 是否支持该 resultClass
    public boolean validate(Class<?> resultClass) {
        // 查找：已经注册的类
        if (mapping.containsKey(resultClass)) {
            return true;
        }

        // 查找：用 annotation 标注，但是没有注册的 ResultHandler
        ManagedWith with = resultClass.getAnnotation(ManagedWith.class);
        if ((with != null) && ResultHandler.class.isAssignableFrom(with.value())) {
            register(resultClass, with.value()); // 发现后注册
            return true;
        }

        // 查找：使用了已经注册的类的子类
        for (Map.Entry<Class<?>, ResultHandler<?>> entry : mapping.entrySet()) {
            Class<?> targetClass = entry.getKey();
            if ((targetClass != Object.class) && targetClass.isAssignableFrom(resultClass)) {
                mapping.put(resultClass, entry.getValue()); // 发现后关联
                return true;
            }
        }

        return false;
    }
}
