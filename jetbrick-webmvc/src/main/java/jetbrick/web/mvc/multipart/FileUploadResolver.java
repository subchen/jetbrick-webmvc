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
package jetbrick.web.mvc.multipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import jetbrick.ioc.Ioc;
import jetbrick.ioc.annotation.Inject;
import jetbrick.ioc.annotation.IocInit;
import jetbrick.web.mvc.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUploadResolver {
    private final Logger log = LoggerFactory.getLogger(FileUploadResolver.class);
    private final List<FileUpload> uploads = new ArrayList<FileUpload>();

    public void initialize() {
        register(HTML5FileUpload.class);
    }

    public void register(Class<?> implementClass) {
        log.debug("register FileUpload: {}", implementClass.getName());

        Ioc ioc = WebConfig.getIoc();
        FileUpload fileUpload = (FileUpload) ioc.newInstance(implementClass);
        ioc.injectSetters(fileUpload);
        ioc.initialize(fileUpload);

        uploads.add(fileUpload);
    }

    public HttpServletRequest transform(HttpServletRequest request) throws IOException {
        for (FileUpload upload : uploads) {
            MultipartRequest req = upload.transform(request);
            if (req != null) {
                return req;
            }
        }
        // 没有找到返回 原始对象
        return request;
    }
}
