package com.jacobarau.helium.update.rss;

import android.util.Log;

import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.XMLParser;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RssParser extends XMLParser {
    private static String TAG = RssParser.class.getSimpleName();

    //TODO more robust unhappy path handling
    public ParseResult parseRSS(InputStream inputStream, String encoding, @NotNull String url) throws XmlPullParserException, IOException, ParseException {
        ParseResult result = new ParseResult();
        Subscription subscription = new Subscription(url);
        result.subscription = subscription;
        result.items = new ArrayList<>();
        XmlPullParser parser = initialize(inputStream, encoding);

        skipInto("rss", parser);
        skipInto("channel", parser);

        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "title":
                        subscription.title = processTextTag(parser);
                        break;
                    case "link":
                        subscription.link = processTextTag(parser);
                        break;
                    case "description":
                        subscription.description = processTextTag(parser);
                        break;
                    case "image":
                        processImageTag(parser, subscription);
                        break;
                    case "item":
                        result.items.add(processItem(parser));
                        break;
                    default:
                        skipTag(parser);
                }
            } else {
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT &&
                 parser.getEventType() != XmlPullParser.END_TAG);


        return result;
    }

    private Item processItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        parser.next();

        Item item = new Item();

        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "title":
                        item.title = processTextTag(parser);
                        break;
                    case "description":
                        item.description = processTextTag(parser);
                        break;
                    case "enclosure":
                        String length = parser.getAttributeValue(null, "length");
                        if (length != null) {
                            item.enclosureLengthBytes = Integer.parseInt(length);
                        }

                        item.enclosureUrl = parser.getAttributeValue(null, "url");

                        item.enclosureMimeType = parser.getAttributeValue(null, "type");

                        skipTag(parser);
                        break;
                    case "pubDate":
                        String dateStr = processTextTag(parser);
                        item.publishDate = null;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM y H:m:s Z", Locale.US);
                        try {
                            item.publishDate = simpleDateFormat.parse(dateStr);
                        } catch (java.text.ParseException e) {
                            Log.e(TAG, "processItem: Date string of '" + dateStr + "' not parseable", e);
                        }

                        break;
                    default:
                        skipTag(parser);
                }
            } else {
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT &&
                parser.getEventType() != XmlPullParser.END_TAG);
        parser.next();
        return item;
    }

    private void processImageTag(XmlPullParser parser, Subscription subscription) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        parser.next();

        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "url":
                        subscription.imageUrl = processTextTag(parser);
                        break;
                    default:
                        skipTag(parser);
                }
            } else {
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT &&
                parser.getEventType() != XmlPullParser.END_TAG);
        parser.next();
    }
}
