package jp.pigumer;

import org.junit.Test;

import java.util.List;
import java.util.zip.ZipEntry;

public class ClasspathScannerTest {

    ClasspathScanner sut = new ClasspathScanner();

    @Test
    public void test() throws Exception {
        List<ZipEntry> list = sut.scan(Thread.currentThread().getContextClassLoader(), "org.junit");
        for(ZipEntry entry : list) {
            System.out.println(entry.getName());
        }
    }
}
