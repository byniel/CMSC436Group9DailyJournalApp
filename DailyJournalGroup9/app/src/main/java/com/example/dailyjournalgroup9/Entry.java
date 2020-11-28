package com.example.dailyjournalgroup9;

import java.util.Date;

public class Entry {
    String emotion;
    String text;
    Date date;
    //how are pictures stored and retrieved?
    String picture;
    String audio;

    public Entry (String emotion, String text, Date date, String picture, String audio) {
        this.emotion = emotion;
        this.text = text;
        this.date = date;
        this.picture = picture;
        this.audio = audio;
    }

}
