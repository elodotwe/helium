package com.jacobarau.helium.update.opml;

import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.rss.ParseException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class OPMLParserTests {
    @Test
    public void testParseOPML() throws ParseException, XmlPullParserException, IOException {
        OPMLParser parser = new OPMLParser();
        List<Subscription> subscriptions = parser.parseOPML(ClassLoader.getSystemResourceAsStream("subscriptionList.opml"), "UTF-8");
        System.out.println(subscriptions);
    }
}
