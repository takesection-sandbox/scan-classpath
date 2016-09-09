package jp.pigumer;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClasspathScannerTest {

    ClasspathScanner sut = new ClasspathScanner();

    @Test
    public void test() throws Exception {
        List<ZipEntry> urls = sut.scan(Package.getPackage("org.junit"),
                (path, stream) -> stream.map(entry -> {
                    System.out.println(entry.getName());
                    return entry;
                }).collect(Collectors.toList()));
        assertThat(urls.isEmpty(), is(false));
    }
}
