package com.jacobarau.helium.update;

import android.util.Xml;

import com.jacobarau.helium.update.rss.ParseException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class XMLParser {
    public XmlPullParser initialize(InputStream inputStream, String encoding) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, encoding);
        // Skip the START_DOCUMENT event
        parser.next();
        return parser;
    }

    public void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    public void skipInto(String tag, XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
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

    public String processTextTag(XmlPullParser parser) throws XmlPullParserException, IOException {
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
}
