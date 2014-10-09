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
import jetbrick.io.IoUtils;
import jetbrick.io.file.FileCopyUtils;
import jetbrick.io.file.FileMoveUtils;
import jetbrick.util.FilenameUtils;
import jetbrick.util.builder.ToStringBuilder;

public final class FilePart {
    private static final String MESSAGE_FILE_MOVED = "File has been moved - cannot be read again";

    private final String fieldName;
    private final String originalFileName;
    private final String originalFileExt;
    private final File diskFile;
    private final long size;

    public FilePart(String fieldName, String originalFileName, File diskFile) {
        this.fieldName = fieldName;
        this.originalFileName = originalFileName;
        this.originalFileExt = FilenameUtils.getFileExtension(originalFileName);
        this.diskFile = diskFile;
        this.size = diskFile.length();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getOriginalFileExt() {
        return originalFileExt;
    }

    public File getDiskFile() {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }
        return diskFile;
    }

    public long getSize() {
        return size;
    }

    public void delete() {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }
        diskFile.delete();
    }

    public void moveTo(File destFile) {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }

        try {
            File dir = destFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileMoveUtils.moveFile(diskFile, destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeTo(File destFile) {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }

        try {
            File dir = destFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileCopyUtils.copyFile(diskFile, destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeTo(OutputStream os) {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }

        try {
            IoUtils.copy(new FileInputStream(diskFile), os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream openStream() {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }

        try {
            return new FileInputStream(diskFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileContent(String charset) {
        if (!available()) {
            throw new IllegalStateException(MESSAGE_FILE_MOVED);
        }
        return IoUtils.toString(diskFile, charset);
    }

    private boolean available() {
        return diskFile.exists() && (diskFile.length() == size);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflection(this);
    }
}
