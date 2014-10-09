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
package jetbrick.web.mvc.result;

import java.io.*;
import jetbrick.io.stream.UnsafeByteArrayInputStream;
import jetbrick.ioc.annotation.ManagedWith;

/**
 * 负责文件下载.
 *
 * @author Guoqiang Chen
 */
@ManagedWith(RawDownloadResultHandler.class)
public final class RawDownload {
    public static final String MIME_APPLICATION_X_DOWNLOAD = "application/x-download";
    public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";
    private final InputStream is;
    private final String fileName;
    private final String contentType;
    private final int contentLength;

    public RawDownload(InputStream is, String fileName) {
        this(is, fileName, MIME_APPLICATION_OCTET_STREAM);
    }

    public RawDownload(InputStream is, String fileName, String contentType) {
        this.is = is;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = 0;
    }

    public RawDownload(File file, String fileName) {
        this(file, fileName, MIME_APPLICATION_OCTET_STREAM);
    }

    public RawDownload(File file, String fileName, String contentType) {
        try {
            this.is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = (int) file.length();
    }

    public RawDownload(byte[] data, String fileName, String contentType) {
        this.is = new UnsafeByteArrayInputStream(data);
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = data.length;
    }

    public InputStream getInputStream() {
        return is;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }
}
