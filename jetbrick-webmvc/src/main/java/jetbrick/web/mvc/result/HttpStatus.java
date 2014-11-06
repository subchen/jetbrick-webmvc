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

import javax.servlet.http.HttpServletResponse;
import jetbrick.web.mvc.ManagedWith;

@ManagedWith(HttpStatusResultHandler.class)
public final class HttpStatus {
    public static final HttpStatus SC_CONTINUE = new HttpStatus(HttpServletResponse.SC_CONTINUE);
    public static final HttpStatus SC_SWITCHING_PROTOCOLS = new HttpStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
    public static final HttpStatus SC_OK = new HttpStatus(HttpServletResponse.SC_OK);
    public static final HttpStatus SC_CREATED = new HttpStatus(HttpServletResponse.SC_CREATED);
    public static final HttpStatus SC_ACCEPTED = new HttpStatus(HttpServletResponse.SC_ACCEPTED);
    public static final HttpStatus SC_NON_AUTHORITATIVE_INFORMATION = new HttpStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
    public static final HttpStatus SC_NO_CONTENT = new HttpStatus(HttpServletResponse.SC_NO_CONTENT);
    public static final HttpStatus SC_RESET_CONTENT = new HttpStatus(HttpServletResponse.SC_RESET_CONTENT);
    public static final HttpStatus SC_PARTIAL_CONTENT = new HttpStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
    public static final HttpStatus SC_MULTIPLE_CHOICES = new HttpStatus(HttpServletResponse.SC_MULTIPLE_CHOICES);
    public static final HttpStatus SC_MOVED_PERMANENTLY = new HttpStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    public static final HttpStatus SC_MOVED_TEMPORARILY = new HttpStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    public static final HttpStatus SC_FOUND = new HttpStatus(HttpServletResponse.SC_FOUND);
    public static final HttpStatus SC_SEE_OTHER = new HttpStatus(HttpServletResponse.SC_SEE_OTHER);
    public static final HttpStatus SC_NOT_MODIFIED = new HttpStatus(HttpServletResponse.SC_NOT_MODIFIED);
    public static final HttpStatus SC_USE_PROXY = new HttpStatus(HttpServletResponse.SC_USE_PROXY);
    public static final HttpStatus SC_TEMPORARY_REDIRECT = new HttpStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    public static final HttpStatus SC_BAD_REQUEST = new HttpStatus(HttpServletResponse.SC_BAD_REQUEST);
    public static final HttpStatus SC_UNAUTHORIZED = new HttpStatus(HttpServletResponse.SC_UNAUTHORIZED);
    public static final HttpStatus SC_PAYMENT_REQUIRED = new HttpStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
    public static final HttpStatus SC_FORBIDDEN = new HttpStatus(HttpServletResponse.SC_FORBIDDEN);
    public static final HttpStatus SC_NOT_FOUND = new HttpStatus(HttpServletResponse.SC_NOT_FOUND);
    public static final HttpStatus SC_METHOD_NOT_ALLOWED = new HttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    public static final HttpStatus SC_NOT_ACCEPTABLE = new HttpStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
    public static final HttpStatus SC_PROXY_AUTHENTICATION_REQUIRED = new HttpStatus(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
    public static final HttpStatus SC_REQUEST_TIMEOUT = new HttpStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
    public static final HttpStatus SC_CONFLICT = new HttpStatus(HttpServletResponse.SC_CONFLICT);
    public static final HttpStatus SC_GONE = new HttpStatus(HttpServletResponse.SC_GONE);
    public static final HttpStatus SC_LENGTH_REQUIRED = new HttpStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
    public static final HttpStatus SC_PRECONDITION_FAILED = new HttpStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
    public static final HttpStatus SC_REQUEST_ENTITY_TOO_LARGE = new HttpStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
    public static final HttpStatus SC_REQUEST_URI_TOO_LONG = new HttpStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
    public static final HttpStatus SC_UNSUPPORTED_MEDIA_TYPE = new HttpStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
    public static final HttpStatus SC_REQUESTED_RANGE_NOT_SATISFIABLE = new HttpStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
    public static final HttpStatus SC_EXPECTATION_FAILED = new HttpStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
    public static final HttpStatus SC_INTERNAL_SERVER_ERROR = new HttpStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    public static final HttpStatus SC_NOT_IMPLEMENTED = new HttpStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    public static final HttpStatus SC_BAD_GATEWAY = new HttpStatus(HttpServletResponse.SC_BAD_GATEWAY);
    public static final HttpStatus SC_SERVICE_UNAVAILABLE = new HttpStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    public static final HttpStatus SC_GATEWAY_TIMEOUT = new HttpStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
    public static final HttpStatus SC_HTTP_VERSION_NOT_SUPPORTED = new HttpStatus(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED);
    private final int status;
    private final String message;

    public HttpStatus(int status) {
        this.status = status;
        this.message = null;
    }

    public HttpStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
