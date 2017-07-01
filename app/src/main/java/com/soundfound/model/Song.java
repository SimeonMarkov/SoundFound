package com.soundfound.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * Created by Simeon Markov on 6/29/2017.
 */

@DatabaseTable(tableName = "songs")
public class Song {

    public static final String USER_ID_COLUMN = "userId";

    @DatabaseField(canBeNull = false)
    private String artist;
    @DatabaseField(canBeNull = false)
    private String track;
    @DatabaseField(canBeNull = false)
    private String duration;
    @DatabaseField(canBeNull = false)
    private String userId;

    public Song() {}

    public Song(String artist, String track, String duration, String userId) {
        this.artist = artist;
        this.track = track;
        this.duration = duration;
        this.userId = userId;

    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return artist + " - " + track + "\t\t\t\t" + duration;
    }
}
