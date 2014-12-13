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
import javax.servlet.http.HttpServletRequest;
import jetbrick.io.IoUtils;
import jetbrick.web.mvc.Managed;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

@Managed
public final class CommonsFileUpload implements FileUpload {

    @Override
    public MultipartRequest transform(HttpServletRequest request) throws IOException {
        String contextType = request.getHeader("Content-Type");
        if (contextType == null || !contextType.startsWith("multipart/form-data")) {
            return null;
        }

        String encoding = request.getCharacterEncoding();

        MultipartRequest req = new MultipartRequest(request);
        ServletFileUpload upload = new ServletFileUpload();
        upload.setHeaderEncoding(encoding);

        try {
            FileItemIterator it = upload.getItemIterator(request);
            while (it.hasNext()) {
                FileItemStream item = it.next();
                String fieldName = item.getFieldName();
                InputStream stream = item.openStream();
                try {
                    if (item.isFormField()) {
                        req.setParameter(fieldName, Streams.asString(stream, encoding));
                    } else {
                        String originalFilename = item.getName();
                        if (originalFilename == null || originalFilename.length() == 0) {
                            continue;
                        }
                        File diskFile = UploadUtils.getUniqueTemporaryFile(originalFilename);
                        OutputStream fos = new FileOutputStream(diskFile);

                        try {
                            IoUtils.copy(stream, fos);
                        } finally {
                            IoUtils.closeQuietly(fos);
                        }

                        FilePart filePart = new FilePart(fieldName, originalFilename, diskFile);
                        req.addFile(filePart);
                    }
                } finally {
                    IoUtils.closeQuietly(stream);
                }
            }
        } catch (FileUploadException e) {
            throw new IllegalStateException(e);
        }

        return req;
    }

}
