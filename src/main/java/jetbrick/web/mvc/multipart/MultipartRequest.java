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

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p> This class functions as a wrapper around HttpServletRequest to provide
 * working getParameter methods for multipart requests. </p>
 */
public final class MultipartRequest extends HttpServletRequestWrapper {
    private Map<String, String[]> parameters = new HashMap<String, String[]>();
    private List<FilePart> files = new ArrayList<FilePart>(8);

    public MultipartRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * <p> Sets a parameter for this request.  The parameter is actually
     * separate from the request parameters, but calling on the getParameter()
     * methods of this class will work as if they weren't. </p>
     */
    public void setParameter(String name, String value) {
        String[] values = parameters.get(name);
        if (values == null) {
            values = new String[] { value };
            parameters.put(name, values);
        } else {
            String[] newValues = new String[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[values.length] = value;
            parameters.put(name, newValues);
        }
    }

    /**
     * <p> Attempts to get a parameter for this request.  It first looks in
     * the underlying HttpServletRequest object for the parameter, and if that
     * doesn't exist it looks for the parameters retrieved from the multipart
     * request </p>
     */
    @Override
    public String getParameter(String name) {
        String value = getRequest().getParameter(name);
        if (value == null) {
            String[] values = parameters.get(name);
            if (values != null && values.length > 0) {
                value = values[0];
            }
        }

        return value;
    }

    /**
     * <p> Returns the names of the parameters for this request. The
     * enumeration consists of the normal request parameter names plus the
     * parameters read from the multipart request </p>
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Enumeration<String> baseParams = getRequest().getParameterNames();
        List<String> list = new ArrayList<String>();
        while (baseParams.hasMoreElements()) {
            list.add(baseParams.nextElement());
        }

        Collection<String> multipartParams = parameters.keySet();
        Iterator<String> iterator = multipartParams.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return Collections.enumeration(list);
    }

    /**
     * <p> Returns the values of a parameter in this request. It first looks
     * in the underlying HttpServletRequest object for the parameter, and if
     * that doesn't exist it looks for the parameter retrieved from the
     * multipart request. </p>
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = getRequest().getParameterValues(name);
        if (values == null) {
            values = parameters.get(name);
        }
        return values;
    }

    /**
     * <p> Combines the parameters stored here with those in the underlying
     * request. If paramater values in the underlying request take precedence
     * over those stored here. </p>
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<String, String[]>(parameters);
        map.putAll(getRequest().getParameterMap());
        return map;
    }

    public void addFile(FilePart file) {
        files.add(file);
    }

    public FilePart getFile(String name) {
        for (FilePart file : files) {
            if (file.getFieldName().equals(name)) {
                return file;
            }
        }
        return null;
    }

    public List<FilePart> getFiles() {
        return Collections.unmodifiableList(files);
    }
}
