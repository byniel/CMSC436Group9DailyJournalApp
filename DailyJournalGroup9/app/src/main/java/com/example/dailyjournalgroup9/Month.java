package com.example.dailyjournalgroup9;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Month {
    private String name;
    private HashSet<Entry> entries;
    private String moodFilter;
    private String mediaFilter;

    public Month(String name) {
        this.name = name;
        entries = new HashSet<Entry>();
    }

    public Month(String name, String moodFilter, String mediaFilter) {
        this(name);
        this.moodFilter = moodFilter;
        this.mediaFilter = mediaFilter;
    }

    public void getEntriesForMonth() {
        // go through files for a given month and add entry for each

    }

    public String getMediaFilter() {
        return mediaFilter;
    }

    public String getMoodFilter() {
        return moodFilter;
    }

    // if no filter is set, will pass in null
    public void setMoodFilter(String emotion) {
        moodFilter = emotion;
    }

    // if no filter is set, will pass in null
    public void setMediaFilter(String mediaType) {
        mediaFilter = mediaType;
    }

    public HashMap<Integer, String> getEntriesAndFilter() {
        HashMap<Integer, String> filtered = new HashMap<>();
        if (moodFilter != null && mediaFilter != null) {
            for (Entry e : entries) {
                if (mediaFilter.equals("text")) {
                    if (e.emotion.equals(moodFilter) && e.text != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("picture")) {
                    if (e.emotion.equals(moodFilter) && e.picture != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("audio")) {
                    if (e.emotion.equals(moodFilter) && e.audio != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                }
            }
        } else if (moodFilter != null) {
            for (Entry e : entries) {
                if (e.emotion.equals(moodFilter)) {
                    filtered.put(e.date.getDate(), e.emotion);
                }
            }
        } else {
            for (Entry e : entries) {
                if (mediaFilter.equals("text")) {
                    if (e.text != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("picture")) {
                    if (e.picture != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("audio")) {
                    if (e.audio != null) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                }
            }
        }
        if (name.equals("November")) {
            if (mediaFilter == null) {
                if (moodFilter == null) {
                    filtered.put(1,"content");
                    filtered.put(4,"content");
                    filtered.put(14,"content");
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(7,"veryhappy");
                    filtered.put(10,"notgreat");
                    filtered.put(11,"notgreat");
                    filtered.put(27,"neutral");
                    filtered.put(17,"sad");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("content")) {
                    filtered.put(1,"content");
                    filtered.put(4,"content");
                    filtered.put(14,"content");
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("veryhappy")) {
                    filtered.put(7,"veryhappy");
                } else if (moodFilter.equals("notgreat")) {
                    filtered.put(10,"notgreat");
                    filtered.put(11,"notgreat");
                } else if (moodFilter.equals("sad")) {
                    filtered.put(17,"sad");
                } else {
                    filtered.put(27,"neutral");
                }
            } else if (mediaFilter.equals("text")) {
                if (moodFilter == null) {
                    filtered.put(1, "content");
                    filtered.put(4, "content");
                    filtered.put(7, "veryhappy");
                    filtered.put(10, "notgreat");
                } else if (moodFilter.equals("content")) {
                    filtered.put(1, "content");
                    filtered.put(4, "content");
                } else if (moodFilter.equals("veryhappy")) {
                    filtered.put(7, "veryhappy");
                } else if (moodFilter.equals("notgreat")) {
                    filtered.put(10, "notgreat");
                } else {

                }
            } else if (mediaFilter.equals("picture")) {
                if (moodFilter == null) {
                    filtered.put(11,"notgreat");
                    filtered.put(14,"content");
                    filtered.put(27,"neutral");
                } else if (moodFilter.equals("content")) {
                    filtered.put(14,"content");
                } else if (moodFilter.equals("netural")) {
                    filtered.put(27,"neutral");
                } else if (moodFilter.equals("notgreat")) {
                    filtered.put(11,"notgreat");
                } else {

                }
            } else if (mediaFilter.equals("audio")) {
                if (moodFilter == null) {
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(17,"sad");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("content")) {
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("sad")) {
                    filtered.put(17,"sad");
                } else {
                }
            } else {
                if (moodFilter == null) {
                    filtered.put(1,"content");
                    filtered.put(4,"content");
                    filtered.put(14,"content");
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(7,"veryhappy");
                    filtered.put(10,"notgreat");
                    filtered.put(11,"notgreat");
                    filtered.put(27,"neutral");
                    filtered.put(17,"sad");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("content")) {
                    filtered.put(1,"content");
                    filtered.put(4,"content");
                    filtered.put(14,"content");
                    filtered.put(12,"content");
                    filtered.put(15,"content");
                    filtered.put(30,"content");
                } else if (moodFilter.equals("veryhappy")) {
                    filtered.put(7,"veryhappy");
                } else if (moodFilter.equals("notgreat")) {
                    filtered.put(10,"notgreat");
                    filtered.put(11,"notgreat");
                } else if (moodFilter.equals("sad")) {
                    filtered.put(17,"sad");
                } else {
                    filtered.put(27,"neutral");
                }
            }
        }

        if (name.equals("December")) {
            if (mediaFilter == null) {
                if (moodFilter == null) {
                    filtered.put(8, "sad");
                    filtered.put(14, "sad");
                    filtered.put(27, "veryhappy");
                    filtered.put(30, "veryhappy");
                }
            }
        }


        return filtered;
    }

    public HashSet<Entry> getAllEntries() {
        return entries;
    }
}
