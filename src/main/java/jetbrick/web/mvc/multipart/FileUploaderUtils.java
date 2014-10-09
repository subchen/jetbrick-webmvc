/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 * Email: subchen@gmail.com
 * URL: http://subchen.github.io/
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import jetbrick.io.IoUtils;
import jetbrick.util.*;
import jetbrick.web.mvc.config.WebConfig;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public final class FileUploaderUtils {
    public static boolean supported(HttpServletRequest request) {
        return isMultipartContent(request) || isHtml5FileUploadContent(request);
    }

    private static boolean isHtml5FileUploadContent(HttpServletRequest request) {
        String contentDisposition = request.getHeader("content-disposition");
        return contentDisposition != null;
    }

    private static boolean isMultipartContent(HttpServletRequest request) {
        String contextType = request.getHeader("Content-Type");
        return (contextType != null) && (contextType.startsWith("multipart/form-data"));
    }

    public static HttpServletRequest asRequest(HttpServletRequest request) {
        try {
            if (isMultipartContent(request)) {
                return asMultipartRequest(request);
            } else if (isHtml5FileUploadContent(request)) {
                return asHtml5Request(request);
            }
            return request;
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    // multipart/form-data
    private static MultipartRequest asMultipartRequest(HttpServletRequest request) throws Exception {
        String encoding = request.getCharacterEncoding();

        MultipartRequest req = new MultipartRequest(request);
        ServletFileUpload upload = new ServletFileUpload();
        upload.setHeaderEncoding(encoding);
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
                    File diskFile = getTempFile(originalFilename);
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

        return req;
    }

    // application/octet-stream
    private static MultipartRequest asHtml5Request(HttpServletRequest request) throws Exception {
        String originalFilename = request.getHeader("content-disposition");
        if (originalFilename == null) {
            throw new ServletException("The request is not a html5 file upload request.");
        }

        originalFilename = new String(originalFilename.getBytes("iso8859-1"), request.getCharacterEncoding());
        originalFilename = StringUtils.substringAfter(originalFilename, "; filename=");
        originalFilename = StringUtils.remove(originalFilename, "\"");
        originalFilename = URLDecoder.decode(originalFilename, "utf-8");

        File diskFile = getTempFile(originalFilename);
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

    private static File getTempFile(String originalFilename) {
        String fileExt = FilenameUtils.getFileExtension(originalFilename);
        String fileName = RandomStringUtils.randomAlphanumeric(16);

        if (StringUtils.isNotEmpty(fileExt)) {
            fileName = fileName + "." + fileExt;
        }

        File uploaddir = WebConfig.getInstance().getUploaddir();
        return new File(uploaddir, fileName);
    }
}
