package com.jacobarau.helium.model;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;

public class Item {
    /**
     * Internal database ID for this item. Not tied to any value in the RSS. Null if not previously saved in the database.
     */
    @Nullable
    public Long id;

    /**
     * Database ID of parent Subscription object.
     */
    @Nullable
    public Long subscriptionId;

    /**
     * Human-readable title of this item, if present in RSS.
     *
     * Either title or description will be present at a bare minimum.
     */
    @Nullable
    public String title;

    /**
     * Human-readable description of this item, if present in RSS.
     *
     * Either title or description will be present at a bare minimum.
     */
    @Nullable
    public String description;

    /**
     * Date-timestamp of when this item was published; null if not present in the RSS
     */
    @Nullable
    public Date publishDate;

    /**
     * If present in the RSS, the URL to the enclosure (typically the podcast audio goes here)
     */
    @Nullable
    public String enclosureUrl;

    /**
     * MIME type of the enclosure.
     *
     * If enclosure URL is not null, this will be not null as well.
     */
    @Nullable
    public String enclosureMimeType;

    /**
     * Length in bytes of the enclosure.
     *
     * If enclosure URL is not null, this will be not null as well.
     */
    @Nullable
    public Integer enclosureLengthBytes;

    @NotNull
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", subscriptionId=" + subscriptionId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publishDate=" + publishDate +
                ", enclosureUrl='" + enclosureUrl + '\'' +
                ", enclosureMimeType='" + enclosureMimeType + '\'' +
                ", enclosureLengthBytes=" + enclosureLengthBytes +
                '}';
    }

    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(subscriptionId, item.subscriptionId) &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(publishDate, item.publishDate) &&
                Objects.equals(enclosureUrl, item.enclosureUrl) &&
                Objects.equals(enclosureMimeType, item.enclosureMimeType) &&
                Objects.equals(enclosureLengthBytes, item.enclosureLengthBytes);
    }

    @SuppressLint("NewApi")
    @Override
    public int hashCode() {
        return Objects.hash(id, subscriptionId, title, description, publishDate, enclosureUrl, enclosureMimeType, enclosureLengthBytes);
    }
}
