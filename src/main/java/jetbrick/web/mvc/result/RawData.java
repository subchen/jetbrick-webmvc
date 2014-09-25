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
package jetbrick.web.mvc.result;

import java.io.*;
import jetbrick.io.stream.UnsafeByteArrayInputStream;
import jetbrick.ioc.annotation.ManagedWith;

/**
 * 自定义输出二进制数据.
 *
 * @author Guoqiang Chen
 */
@ManagedWith(RawDataResultHandler.class)
public final class RawData {
    private final InputStream is;
    private final String contentType;
    private final int contentLength;

    public RawData(InputStream is, String contentType) {
        this.is = is;
        this.contentType = contentType;
        this.contentLength = 0;
    }

    public RawData(File file, String contentType) {
        try {
            this.is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.contentType = contentType;
        this.contentLength = (int) file.length();
    }

    public RawData(byte[] data, String contentType) {
        this.is = new UnsafeByteArrayInputStream(data);
        this.contentType = contentType;
        this.contentLength = data.length;
    }

    public InputStream getInputStream() {
        return is;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }
}
