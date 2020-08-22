package com.jacobarau.helium.update.rss;

import android.util.Log;
import android.util.Xml;

import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RssParser {
    private static String TAG = RssParser.class.getSimpleName();

    //TODO more robust unhappy path handling
    public ParseResult parseRSS(InputStream inputStream, String encoding, @NotNull String url) throws XmlPullParserException, IOException, ParseException {
        ParseResult result = new ParseResult();
        Subscription subscription = new Subscription(url);
        result.subscription = subscription;
        result.items = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, encoding);
        // Skip the START_DOCUMENT event
        parser.next();

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

    private void skipInto(String tag, XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        if (parser.getEventType() == XmlPullParser.END_DOCUMENT) {
            throw new ParseException("End of document not expected but reached");
        }

        boolean foundTag = false;
        do {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if (!parser.getName().equals(tag)) {
                    skipTag(parser);
                } else {
                    foundTag = true;
                    // Go into the child nodes of the tag.
                    parser.next();
                }
            } else {
                // Consumes things like text nodes and such.
                parser.next();
            }
        } while (parser.getEventType() != XmlPullParser.END_DOCUMENT
                && parser.getEventType() != XmlPullParser.END_TAG
                && !foundTag);

        if (!foundTag) {
            throw new ParseException("No <" + tag + "> tag found");
        }
    }

    private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
            }
        }
        parser.next();
    }

    private String processTextTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        parser.next();
        if (parser.getEventType() != XmlPullParser.TEXT) {
            //TODO: act more gracefully when we hit tags we don't expect. Maybe gather all
            //TEXT nodes from the children of the node we started at. Can't be arsed just now.
            skipTag(parser);
            return "";
        }
        String result = parser.getText();
        skipTag(parser);
        return result.trim();
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
