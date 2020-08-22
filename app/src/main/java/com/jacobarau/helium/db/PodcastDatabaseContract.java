package com.jacobarau.helium.db;

import android.provider.BaseColumns;

final class PodcastDatabaseContract {
    static class Subscriptions implements BaseColumns {
        static final String TABLE_NAME = "subscriptions";
        static final String COLUMN_NAME_URL = "url";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_LINK = "link";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        static final String COLUMN_NAME_LAST_UPDATED = "lastUpdated";
    }

    static class Items implements BaseColumns {
        static final String TABLE_NAME = "items";
        static final String COLUMN_NAME_SUBSCRIPTION_ID = "subscription_id";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "description";
        static final String COLUMN_NAME_PUBLISH_DATE = "publishDate";
        static final String COLUMN_NAME_ENCLOSURE_URL = "enclosureUrl";
        static final String COLUMN_NAME_ENCLOSURE_MIME_TYPE = "enclosureMimeType";
        static final String COLUMN_NAME_ENCLOSURE_LENGTH_BYTES = "enclosureLengthBytes";
    }
}
