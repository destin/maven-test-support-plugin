package insuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created with IntelliJ IDEA.
 * User: dawid
 * Date: 21.11.13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestInSuite1.class,
        TestInSuite2.class
})
public class TestSuite {
}
