/**
 * 
 */
package org.webreformatter.resources.encoding;


import org.webreformatter.resources.adapters.encoding.HtmlEncodingDetector;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class CharcodeDetectionTest extends TestCase {

    /**
     * @param name
     */
    public CharcodeDetectionTest(String name) {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        testEncoding(
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n",
            "ISO-8859-1");
        testEncoding("<meta content = "
            + "       ' text/html;charset=iso-8859-1 '"
            + " http-equiv = 'Content-Type'>\n", "ISO-8859-1");
    }

    private void testEncoding(String str, String encoding) {
        HtmlEncodingDetector detector = new HtmlEncodingDetector();
        String test = detector.detectEncoding(str);
        assertEquals(encoding, test);
    }

}
