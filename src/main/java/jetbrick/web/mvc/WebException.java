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
package jetbrick.web.mvc;

/**
 * 默认 Webapp 抛出的异常.
 */
public class WebException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static RuntimeException uncheck(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new WebException(e);
    }

    public WebException() {
        super();
    }

    public WebException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebException(String message) {
        super(message);
    }

    public WebException(Throwable cause) {
        super(cause);
    }
}
