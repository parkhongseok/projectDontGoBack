package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JUnitTest {
    @DisplayName("test1")
    @Test
    public void junitTest(){
        int a = 1;
        int b = 2;
        int sum = 3;

        Assertions.assertEquals(sum, a+b);
    }

    @DisplayName("test2")
    @Test
    public void junitTest2(){
        int a = 1;
        int b = 2;
        int sum = 3121435;
        Assertions.assertEquals(sum, a+b);
    }


}
