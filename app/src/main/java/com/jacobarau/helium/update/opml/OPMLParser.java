package com.jacobarau.helium.update.opml;

import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.XMLParser;
import com.jacobarau.helium.update.rss.ParseException;
import com.jacobarau.helium.update.rss.RssParser;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OPMLParser extends XMLParser {
    private static String TAG = RssParser.class.getSimpleName();

    public List<Subscription> parseOPML(InputStream inputStream, String encoding) throws XmlPullParserException, IOException, ParseException {
        List<Subscription> subscriptions = new ArrayList<>();
        XmlPullParser parser = initialize(inputStream, encoding);

        skipInto("opml", parser);
        skipInto("body", parser);

        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if ("outline".equals(parser.getName())) {
                    processOutline(parser, subscriptions);
                } else {
                    skipTag(parser);
                }
            } else {
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT &&
                parser.getEventType() != XmlPullParser.END_TAG);


        return subscriptions;
    }

    private void processOutline(XmlPullParser parser, List<Subscription> subscriptions) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        String url = parser.getAttributeValue(null, "xmlUrl");
        if (url != null) {
            subscriptions.add(new Subscription(url));
        }
        parser.next();

        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if ("outline".equals(parser.getName())) {
                    processOutline(parser, subscriptions);
                } else {
                    skipTag(parser);
                }
            } else {
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT &&
                parser.getEventType() != XmlPullParser.END_TAG);
    }
}
