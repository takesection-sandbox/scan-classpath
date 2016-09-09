package jp.pigumer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class ClasspathScanner {

    private static final String JAR_URL_SEPARATOR = "!/";

    URL getUrl(URL url) throws MalformedURLException {
        String file = url.getFile();
        int index = file.indexOf(JAR_URL_SEPARATOR);
        if (index != -1) {
            String jarFile = file.substring(0, index);
            try {
                return new URL(jarFile);
            } catch (MalformedURLException e) {
                if (jarFile.startsWith("/")) {
                    return new URL("file:" + jarFile);
                }
                return new URL("file:/" + jarFile);
            }
        }
        return url;
    }

    Optional<JarFile> getJarFile(URL url) {
        try {
            URI uri = getUrl(url).toURI();
            return Optional.of(new JarFile(uri.getSchemeSpecificPart()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public <R> R scan(ClassLoader classLoader, String path, BiFunction<String, Stream<ZipEntry>, R> mapper) throws IOException {
        Enumeration<URL> urls = classLoader.getResources(path);
        List<JarFile> list = new ArrayList<>();
        while (urls.hasMoreElements()) {
            getJarFile(urls.nextElement()).ifPresent(jarFile -> list.add(jarFile));
        }
        return mapper.apply(path, list.stream().flatMap(jarFile -> {
            Stream<JarEntry> entry = jarFile.stream();
            return entry.filter(item -> item.getName().startsWith(path));
        }));
    }

    public <R> R scan(Package rootPackage, BiFunction<String, Stream<ZipEntry>, R> mapper) throws IOException {
        String rootName = rootPackage.getName();
        String path = rootName.replaceAll("\\.", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return scan(classLoader, path, mapper);
    }
}
