package com.example.dailyjournalgroup9;

import java.util.Date;

// Class to keep track of individual logs
public class Entry {
    String emotion;
    String text;
    Date date;
    boolean picture;
    boolean audio;

    public Entry (String emotion, String text, Date date, boolean picture, boolean audio) {
        this.emotion = emotion;
        this.text = text;
        this.date = date;
        this.picture = picture;
        this.audio = audio;
    }

    public Date getDate() {
        return date;
    }

    public boolean getAudio() {
        return audio;
    }

    public String getEmotion() {
        return emotion;
    }

    public boolean getPicture() {
        return picture;
    }

    public String getText() {
        return text;
    }
}

