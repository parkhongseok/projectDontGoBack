//package com.dontgoback.dontgo.tests;
//
//import org.junit.jupiter.api.*;
//
//public class JUnitCycleTest {
//    @BeforeAll      // 전체 테스트를 시작하기 전에 1회 실행되므로, 메서드는 static으로 선언
//    static void beforeAll(){
//        System.out.println("@BeforeAll is gonna running");
//    }
//
//    @BeforeEach //TestCase 시작 전마다 매번 실행
//    public void beforeEach(){
//        System.out.println("@BeforeEach");
//    }
//
//    @Test
//    public void Test1(){
//        System.out.println("test1");
//    }
//
//    @Test
//    public void Test2(){
//        System.out.println("test2");
//    }
//
//    @Test
//    public void Test3(){
//        System.out.println("test3");
//    }
//
//    @AfterAll
//    static void afterAll(){
//        System.out.println("@AfterAll");
//    }
//
//    @AfterEach
//    public void afterEach(){
//        System.out.println("@AfterEach");
//    }
//}
