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
package jetbrick.web.mvc.action.annotation;

import java.util.List;
import jetbrick.ioc.annotation.Managed;
import jetbrick.web.mvc.RequestContext;
import jetbrick.web.mvc.multipart.FilePart;

@Managed
public final class FilePartsArgumentGetter implements TypedArgumentGetter<FilePart[]> {
    private static final FilePart[] EMPTY_ARRAY = new FilePart[0];

    @Override
    public FilePart[] get(RequestContext ctx) {
        List<FilePart> parts = ctx.getFileParts();
        if (parts.size() == 0) {
            return EMPTY_ARRAY;
        } else {
            return parts.toArray(new FilePart[parts.size()]);
        }
    }
}
