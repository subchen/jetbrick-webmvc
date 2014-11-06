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

import java.io.*;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import jetbrick.io.IoUtils;
import jetbrick.util.StringUtils;

public final class HTML5FileUpload implements FileUpload {

    // application/octet-stream
    @Override
    public MultipartRequest transform(HttpServletRequest request) throws IOException {
        String originalFilename = request.getHeader("content-disposition");
        if (originalFilename == null) {
            return null;
        }

        originalFilename = new String(originalFilename.getBytes("ISO-8859-1"), request.getCharacterEncoding());
        originalFilename = StringUtils.substringAfter(originalFilename, "; filename=");
        originalFilename = StringUtils.remove(originalFilename, "\"");
        originalFilename = URLDecoder.decode(originalFilename, "UTF-8");

        File diskFile = UploadUtils.getUniqueTemporaryFile(originalFilename);
        InputStream fis = request.getInputStream();
        OutputStream fos = new FileOutputStream(diskFile);

        try {
            IoUtils.copy(fis, fos);
        } finally {
            IoUtils.closeQuietly(fis);
            IoUtils.closeQuietly(fos);
        }

        MultipartRequest req = new MultipartRequest(request);
        FilePart filePart = new FilePart("file", originalFilename, diskFile);
        req.addFile(filePart);

        return req;
    }

}
