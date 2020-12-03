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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

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
                    if (e.emotion.equals(moodFilter) && e.picture) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("audio")) {
                    if (e.emotion.equals(moodFilter) && e.audio) {
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
                    if (e.picture) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                } else if (mediaFilter.equals("audio")) {
                    if (e.audio) {
                        filtered.put(e.date.getDate(), e.emotion);
                    }
                }
            }
        } else {
            for (Entry e : entries) {
                filtered.put(e.date.getDate(), e.emotion);
            }
        }


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
        String path = context.getExternalFilesDir(null).toString();
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


                File pictureFile = new File(path, dirNameDate + ".jpg");
                boolean picture = false;
                if (pictureFile.exists()) {
                    picture = true;
                }

                File audioFile = new File(path, dirNameDate + ".mp3");
                boolean audio = false;
                if (audioFile.exists()) {
                    audio = true;
                }


                Entry entry = new Entry(emotion.toString(), text.toString(), new Date(currDay.getYear(), currDay.getMonth(), i), picture, audio);
                entries.add(entry);
            }
        }
        this.entries = entries;
    }

    public HashSet<Entry> getAllEntries() {
        return entries;
    }

    public Entry getDay (Date day) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(day);
        for (Entry entry : entries) {
            Calendar entryCal = Calendar.getInstance(Locale.getDefault());
            entryCal.setTime(entry.date);
            if (entryCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                    entryCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                    entryCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                return entry;
            }
        }
        return null;
    }
}
