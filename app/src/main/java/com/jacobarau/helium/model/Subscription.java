package com.jacobarau.helium.model;

import android.annotation.SuppressLint;

import com.jacobarau.helium.jdata.Copyable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;

public class Subscription implements Copyable<Subscription> {
    /**
     * Database ID of this Subscription, or null if not previously saved in the database.
     */
    @Nullable
    public Long id;

    /**
     * URL of the RSS feed associated with this subscription.
     */
    @NotNull
    public String url;

    /**
     * Title of this subscription, from the RSS feed.
     */
    @Nullable
    public String title;

    /**
     * Link to the feed's website, from the RSS.
     */
    @Nullable
    public String link;

    /**
     * Phrase/sentence describing the feed, from the RSS.
     */
    @Nullable
    public String description;

    /**
     * If present in the RSS, a URL to an associated image.
     */
    @Nullable
    public String imageUrl;

    /**
     * The instant at which the podcast app has last fetched and parsed the RSS for this subscription.
     * This does not come from the RSS itself.
     * <p>
     * It can be null if, for instance, a feed was just added--the new subscription will be added to
     * the database before we've fetched the RSS and filled out any fields yet.
     */
    @Nullable
    public Date lastUpdated;

    @NotNull
    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id) &&
                url.equals(that.url) &&
                Objects.equals(title, that.title) &&
                Objects.equals(link, that.link) &&
                Objects.equals(description, that.description) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(lastUpdated, that.lastUpdated);
    }

    @SuppressLint("NewApi")
    @Override
    public int hashCode() {
        return Objects.hash(id, url, title, link, description, imageUrl, lastUpdated);
    }

    public Subscription(@NotNull String url) {
        this.url = url;
    }

    @Override
    public Subscription copy() {
        Subscription result = new Subscription(url);
        result.title = title;
        result.lastUpdated = lastUpdated;
        result.description = description;
        result.id = id;
        result.imageUrl = imageUrl;
        result.link = link;
        return result;
    }
}