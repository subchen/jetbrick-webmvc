package jetbrick.web.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import jetbrick.io.IoUtils;
import jetbrick.io.finder.ClassFinder;
import jetbrick.util.ClassLoaderUtils;
import jetbrick.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImplementsScanner {
    private static final String DEFAULT_PLUGINS_FILE = "META-INF/jetbrick-plugins.properties";
    private final Logger log = LoggerFactory.getLogger(ImplementsScanner.class);
    private final Map<String, List<Class<?>>> implementsMap = new HashMap<String, List<Class<?>>>();

    public void loadFromConfig() {
        ClassLoader loader = ClassLoaderUtils.getDefault();
        Enumeration<URL> files = null;
        try {
            files = loader.getResources(DEFAULT_PLUGINS_FILE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        while (files.hasMoreElements()) {
            URL url = files.nextElement();
            log.debug("found {} at: {}", DEFAULT_PLUGINS_FILE, url);

            InputStream is = null;
            Properties props = null;
            try {
                is = url.openStream();
                props = new Properties();
                props.load(is);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } finally {
                IoUtils.closeQuietly(is);
            }

            for (Entry<Object, Object> entry : props.entrySet()) {
                String valueList = entry.getValue().toString();
                String[] values = StringUtils.split(valueList, ',');
                for (String value : values) {
                    value = StringUtils.trimToNull(value);
                    if (value != null) {
                        Class<?> cls = null;
                        try {
                            cls = loader.loadClass(value);
                        } catch (ClassNotFoundException e) {
                            throw new IllegalStateException(e);
                        }

                        String key = entry.getKey().toString();
                        addImplementClass(key, cls);
                    }
                }
            }
        }
    }

    /**
     * 自动扫描 Annotation.
     */
    public void autoscan(Collection<String> packageNames, Collection<Class<? extends Annotation>> annotations) {
        Set<Class<?>> classes = ClassFinder.getClasses(packageNames, true, annotations, true);
        for (Class<?> cls : classes) {
            for (Annotation annotation : cls.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotations.contains(annotationType)) {
                    addImplementClass(annotationType.getName(), cls);
                }
            }
        }
    }

    private void addImplementClass(String key, Class<?> cls) {
        List<Class<?>> impls = implementsMap.get(key);
        if (impls == null) {
            impls = new ArrayList<Class<?>>(8);
            implementsMap.put(key, impls);
        }
        impls.add(cls);
    }

    /**
     * 获取某个 key 对应的插件实现 List.
     */
    public List<Class<?>> getList(String key) {
        List<Class<?>> impls = implementsMap.get(key);
        return (impls == null) ? Collections.<Class<?>> emptyList() : impls;
    }

    /**
     * 获取某个 cls 对应的插件实现 List.
     *
     * @param cls Annotation/Interface/Abstract Class
     */
    public List<Class<?>> getList(Class<?> cls) {
        return getList(cls.getName());
    }

}
