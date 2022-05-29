package com.ctrip.xpipe.redis.keeper.applier.sequence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Slight
 * <p>
 * Feb 20, 2022 6:40 PM
 */
public class DefaultSequenceControllerTest {

    ApplierSequenceController controller = new DefaultSequenceController();

    @Before
    public void setUp() throws Exception {
        controller.initialize();
    }

    @After
    public void tearDown() throws Exception {
        controller.dispose();
    }

    @Test
    public void twoCommandsOnSameKey() throws ExecutionException, InterruptedException {

        TestSetCommand first = new TestSetCommand(100, "SET", "Key", "V1");
        TestSetCommand second = new TestSetCommand(200, "SET", "Key", "V2");

        assertEquals(first.key(), second.key());

        controller.submit(first);
        controller.submit(second);

        first.future().get();
        second.future().get();

        assertTrue(second.startTime >= first.endTime);
    }
}