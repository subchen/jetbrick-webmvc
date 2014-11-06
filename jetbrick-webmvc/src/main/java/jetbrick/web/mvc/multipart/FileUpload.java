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
import javax.servlet.http.HttpServletRequest;

public interface FileUpload {

    /**
     * 将一个 普通的 request 对象转为 MultipartRequest.
     *
     * @param request 普通的 request 对象
     * @return 如果不支持，返回 null.
     * @throws IOException
     */
    public MultipartRequest transform(HttpServletRequest request) throws IOException;

}
