package com.example.dailyjournalgroup9;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.DateFormatSymbols;
import java.time.YearMonth;
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

    public void setEntries(HashSet<Entry> entries) {
        this.entries = entries;
    }

    public void addEntry (Entry entry) {
        this.entries.add(entry);
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
        } else if (mediaFilter != null) {
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
        } else {
            for (Entry e : entries) {
                filtered.put(e.date.getDate(), e.emotion);
            }
        }
//        if (name.equals("November")) {
//            if (mediaFilter == null) {
//                if (moodFilter == null) {
//                    filtered.put(1,"content");
//                    filtered.put(4,"content");
//                    filtered.put(14,"content");
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(7,"veryhappy");
//                    filtered.put(10,"notgreat");
//                    filtered.put(11,"notgreat");
//                    filtered.put(27,"neutral");
//                    filtered.put(17,"sad");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("content")) {
//                    filtered.put(1,"content");
//                    filtered.put(4,"content");
//                    filtered.put(14,"content");
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("veryhappy")) {
//                    filtered.put(7,"veryhappy");
//                } else if (moodFilter.equals("notgreat")) {
//                    filtered.put(10,"notgreat");
//                    filtered.put(11,"notgreat");
//                } else if (moodFilter.equals("sad")) {
//                    filtered.put(17,"sad");
//                } else {
//                    filtered.put(27,"neutral");
//                }
//            } else if (mediaFilter.equals("text")) {
//                if (moodFilter == null) {
//                    filtered.put(1, "content");
//                    filtered.put(4, "content");
//                    filtered.put(7, "veryhappy");
//                    filtered.put(10, "notgreat");
//                } else if (moodFilter.equals("content")) {
//                    filtered.put(1, "content");
//                    filtered.put(4, "content");
//                } else if (moodFilter.equals("veryhappy")) {
//                    filtered.put(7, "veryhappy");
//                } else if (moodFilter.equals("notgreat")) {
//                    filtered.put(10, "notgreat");
//                } else {
//
//                }
//            } else if (mediaFilter.equals("picture")) {
//                if (moodFilter == null) {
//                    filtered.put(11,"notgreat");
//                    filtered.put(14,"content");
//                    filtered.put(27,"neutral");
//                } else if (moodFilter.equals("content")) {
//                    filtered.put(14,"content");
//                } else if (moodFilter.equals("netural")) {
//                    filtered.put(27,"neutral");
//                } else if (moodFilter.equals("notgreat")) {
//                    filtered.put(11,"notgreat");
//                } else {
//
//                }
//            } else if (mediaFilter.equals("audio")) {
//                if (moodFilter == null) {
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(17,"sad");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("content")) {
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("sad")) {
//                    filtered.put(17,"sad");
//                } else {
//                }
//            } else {
//                if (moodFilter == null) {
//                    filtered.put(1,"content");
//                    filtered.put(4,"content");
//                    filtered.put(14,"content");
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(7,"veryhappy");
//                    filtered.put(10,"notgreat");
//                    filtered.put(11,"notgreat");
//                    filtered.put(27,"neutral");
//                    filtered.put(17,"sad");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("content")) {
//                    filtered.put(1,"content");
//                    filtered.put(4,"content");
//                    filtered.put(14,"content");
//                    filtered.put(12,"content");
//                    filtered.put(15,"content");
//                    filtered.put(30,"content");
//                } else if (moodFilter.equals("veryhappy")) {
//                    filtered.put(7,"veryhappy");
//                } else if (moodFilter.equals("notgreat")) {
//                    filtered.put(10,"notgreat");
//                    filtered.put(11,"notgreat");
//                } else if (moodFilter.equals("sad")) {
//                    filtered.put(17,"sad");
//                } else {
//                    filtered.put(27,"neutral");
//                }
//            }
//        }
//
//        if (name.equals("December")) {
//            if (mediaFilter == null) {
//                if (moodFilter == null) {
//                    filtered.put(8, "sad");
//                    filtered.put(14, "sad");
//                    filtered.put(27, "veryhappy");
//                    filtered.put(30, "veryhappy");
//                }
//            }
//        }


        return filtered;
    }

    public void readDataForMonth(Date currDay, Context context) {
        int yearInt = currDay.getYear() + 1900;
        int monthInt = currDay.getMonth() + 1;
        YearMonth yearMonthObject = YearMonth.of(yearInt, monthInt);
        //not correct...hmm
        int daysInMonth = yearMonthObject.lengthOfMonth();
        String dirNameDate;
        File directory;
        HashSet<Entry> entries = new HashSet<>();
        for (int i = 1; i <= daysInMonth; i++) {
            String day = "" + i;
            String month = "" + monthInt;
            if (i < 10) {
                day = "0" + i;
            }
            if (monthInt < 10) {
                month = "0" + monthInt;
            }
            dirNameDate = day + "-" + month + "-" + yearInt;
//            directory = new File(Environment.getExternalStorageDirectory(), dirNameDate + "/");
            directory = new File(context.getExternalFilesDir(null), dirNameDate + "/");
            if (directory.exists() && directory.isDirectory()) {

                File textFile = new File(directory, context.getResources().getString(R.string.text_file));

                StringBuilder text = new StringBuilder();
                String line = null;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(textFile));
                    line = br.readLine();
                    while (line != null) {
                        text.append(line);
                        text.append("\n");
                        line = br.readLine();
                    }
                    br.close();
                } catch (Exception e) {

                }

                File emotionFile = new File(directory, context.getResources().getString(R.string.emotion_file));

                StringBuilder emotion = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(emotionFile));
                    line = br.readLine();
                    while (line != null) {
                        emotion.append(line);
                        line = br.readLine();
                    }
                    br.close();
                } catch (Exception e) {

                }

                Entry entry = new Entry(emotion.toString(), text.toString(), new Date(currDay.getYear(), currDay.getMonth(), i), null, null);
                entries.add(entry);
            }
        }
        this.entries = entries;
    }

    public HashSet<Entry> getAllEntries() {
        return entries;
    }

    public Entry getDay (Date day) {
        for (Entry entry : entries) {
            if (entry.date.getMonth() == day.getMonth() && entry.date.getYear() == day.getYear() &&
            entry.date.getDay() == day.getDay()) {
                return entry;
            }
        }
        return null;
    }
}
