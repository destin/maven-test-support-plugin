package test;

import org.junit.Ignore;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: dawid
 * Date: 09.11.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    @org.junit.Test
    public void testSuccess() throws Exception {
        System.out.println("test");
    }

    @org.junit.Test
    public void testFailure() throws Exception {
        System.out.println("failed test - out");
        System.err.println("failed test - err");
        System.out.println("failed test - out2");
        fail("Fail");
    }

    @org.junit.Test
    public void testError() throws Exception {
        System.out.println("NPE test");
        //throw new NullPointerException();
    }

    @Ignore("ignore message")
    @org.junit.Test
    public void testIgnoredWithMessage() throws Exception {

    }

    @Ignore
    @org.junit.Test
    public void testIgnored() throws Exception {

    }
}
