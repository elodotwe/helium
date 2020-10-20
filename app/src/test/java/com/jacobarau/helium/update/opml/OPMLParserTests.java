package com.jacobarau.helium.update.opml;

import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.rss.ParseException;

import org.junit.Assert;
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
        Assert.assertEquals(2, subscriptions.size());
        Assert.assertEquals("http://news.com.com/2547-1_3-0-5.xml", subscriptions.get(0).url);
        Assert.assertEquals("http://www.washingtonpost.com/wp-srv/politics/rssheadlines.xml", subscriptions.get(1).url);
    }
}
