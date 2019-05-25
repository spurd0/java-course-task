package com.babenko;

import com.babenko.testkg.annotation.After;
import com.babenko.testkg.annotation.Before;
import com.babenko.testkg.annotation.Test;
import com.babenko.testkg.asserts.Assert;

public class ExampleTest {
    public ExampleTest() {
        System.out.println("constructor");
    }

    @Before
    public void setUp() {
        System.out.println("setUp");
    }

    @Test(expected = NullPointerException.class)
    public void test1() {
        System.out.println("test1");
        throw new IllegalArgumentException();
    }

    @Test
    public void test2() {
        System.out.println("test2");
        Assert.assertsEquals("", 1L, 1L);
    }

    @Test
    public void test3() {
        System.out.println("test3");
        Assert.assertsEquals("test3 message", 1L, 12);
    }

    @After
    public void tearDown() {
        System.out.println("tearDown");
    }

}
