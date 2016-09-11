package jp.pigumer;

import org.junit.Test;

import java.util.List;

public class ClasspathScannerTest {

    ClasspathScanner sut = new ClasspathScanner();

    @Test
    public void test() throws Exception {
        List<String> list = sut.scan(Thread.currentThread().getContextClassLoader(), "org.junit");
        list.forEach(path -> System.out.println(path));
    }
}
