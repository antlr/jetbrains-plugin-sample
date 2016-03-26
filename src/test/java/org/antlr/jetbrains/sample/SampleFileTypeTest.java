package org.antlr.jetbrains.sample;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class SampleFileTypeTest {

    private SampleFileType fileType;

    @Before
    public void setUp() throws Exception {
        fileType = new SampleFileType();
    }

    @Test
    public void name() throws Exception {
        Assert.assertEquals("sample", fileType.getDefaultExtension());
    }

}