package org.axway.grapes.server.db.datamodel;

import org.axway.grapes.server.db.DataUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DataUtilsStripTest {

    @Rule
    public ExpectedException exc = ExpectedException.none();

    @Test
    public void testOneToken() {
        assertEquals("a:b:c",
                DataUtils.strip("a:b:c:jar", 1));
    }

    @Test
    public void testTwoTokens() {
        assertEquals("org.apache.activemq:apache-activemq:5.14.1",
                DataUtils.strip("org.apache.activemq:apache-activemq:5.14.1::jar", 2));
    }

    @Test
    public void testRemovedTokensGreaterThanInputTokens() {
        assertEquals("a:b:c:d",
                DataUtils.strip("a:b:c:d", 5));
    }

    @Test
    public void testRemovedTokensEqualsInputTokens() {
        assertEquals("a:b:c:d",
                DataUtils.strip("a:b:c:d", 4));
    }

    @Test
    public void nullInputGeneratesException() {
        exc.expect(IllegalArgumentException.class);
        DataUtils.strip(null, 3);
    }

    @Test
    public void zeroStripCountGeneratesException() {
        exc.expect(IllegalArgumentException.class);
        DataUtils.strip("some:string", 0);
    }

    @Test
    public void negativeStripCountGeneratesException() {
        exc.expect(IllegalArgumentException.class);
        DataUtils.strip("some:string", -4);
    }
}
