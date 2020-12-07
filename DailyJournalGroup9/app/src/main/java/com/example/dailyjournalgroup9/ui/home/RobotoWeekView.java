/*
 * Copyright (C) 2016 Marco Hernaiz Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dailyjournalgroup9.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.dailyjournalgroup9.LoggedActivity;
import com.example.dailyjournalgroup9.Month;
import com.example.dailyjournalgroup9.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


/**
 * The roboto calendar view
 *
 * @author Marco Hernaiz Cao
 */

// Adapted the month RobotoCalendarView to fit for one week
public class RobotoWeekView extends LinearLayout {

    private static final String DAY_OF_THE_WEEK_TEXT = "dayOfTheWeekText";
    private static final String DAY_OF_THE_WEEK_LAYOUT = "dayOfTheWeekLayout";
    private static final String DAY_OF_THE_MONTH_LAYOUT = "dayOfTheMonthLayout";
    private static final String DAY_OF_THE_MONTH_TEXT = "dayOfTheMonthText";
    private static final String DAY_OF_THE_MONTH_BACKGROUND = "dayOfTheMonthBackground";
    private static final String DAY_OF_THE_MONTH_CONTENT_IMAGE = "dayOfTheMonthContentImage";
    private static final String DAY_OF_THE_MONTH_VERYHAPPY_IMAGE = "dayOfTheMonthVeryHappyImage";
    private static final String DAY_OF_THE_MONTH_NEUTRAL_IMAGE = "dayOfTheMonthNeutralImage";
    private static final String DAY_OF_THE_MONTH_NOTGREAT_IMAGE = "dayOfTheMonthNotGreatImage";
    private static final String DAY_OF_THE_MONTH_SAD_IMAGE = "dayOfTheMonthSadImage";

    private TextView dateTitle;
    private ImageView leftButton;
    private ImageView rightButton;
    private View rootView;
    private ViewGroup robotoCalendarMonthLayout;
    private RobotoCalendarListener robotoCalendarListener;
    @NonNull
    private Calendar currentCalendar = Calendar.getInstance();
    @Nullable
    private Calendar lastSelectedDayCalendar;
    private Month currMonth;
    private final OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            // Extract day selected
            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            // Extract the day from the text
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfTheMonthText.getText().toString()));

            markDayAsSelectedDay(calendar.getTime());

            // Fire event
            if (robotoCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
            } else {
                robotoCalendarListener.onDayClick(calendar.getTime());
            }
        }
    };
    private final OnLongClickListener onDayOfMonthLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            // Extract day selected
            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            // Extract the day from the text
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfTheMonthText.getText().toString()));

            markDayAsSelectedDay(calendar.getTime());

            // Fire event
            if (robotoCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
            } else {
                robotoCalendarListener.onDayLongClick(calendar.getTime());
            }
            return true;
        }
    };
    private boolean shortWeekDays = false;

    public RobotoWeekView(Context context) {
        super(context);
        init(null);
    }

    public RobotoWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public RobotoWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RobotoWeekView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public Month getCurrentMonth() {
        return currMonth;
    }

    private static String checkSpecificLocales(String dayOfTheWeekString, int i) {
        // Set Wednesday as "X" in Spanish Locale.getDefault()
        if (i == 4 && "ES".equals(Locale.getDefault().getCountry())) {
            dayOfTheWeekString = "X";
        } else {
            dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase();
        }
        return dayOfTheWeekString;
    }


    private static int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        return currentDay + monthOffset;
    }

    private static int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private static int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    private static boolean areInTheSameDay(@NonNull Calendar calendarOne, @NonNull Calendar calendarTwo) {
        return calendarOne.get(Calendar.YEAR) == calendarTwo.get(Calendar.YEAR) && calendarOne.get(Calendar.DAY_OF_YEAR) == calendarTwo.get(Calendar.DAY_OF_YEAR);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void init(@Nullable AttributeSet set) {

        if (isInEditMode()) {
            return;
        }

        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflate.inflate(R.layout.roboto_calendar_view_layout, this, true);
        findViewsById(rootView);
        setUpEventListeners();

        currentCalendar = Calendar.getInstance();
        setDate(currentCalendar.getTime());

        ViewPump.init(ViewPump.builder()
                              .addInterceptor(new CalligraphyInterceptor(
                                      new CalligraphyConfig.Builder()
                                              .setFontAttrId(R.attr.fontPath)
                                              .build()))
                              .build());
    }

    /**
     * Set an specific calendar to the view and update de view
     *
     * @param date, the selected date
     */
    public void setDate(@NonNull Date date) {
        currentCalendar.setTime(date);
        updateView();
    }

    public void markDayAsSelectedDay(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Clear previous current day mark
        clearSelectedDay();

        // Store current values as last values
        lastSelectedDayCalendar = calendar;

    }

    public void clearSelectedDay() {
        if (lastSelectedDayCalendar != null) {
            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(lastSelectedDayCalendar);

            // If it's today, keep the current day style
            Calendar nowCalendar = Calendar.getInstance();
            if (nowCalendar.get(Calendar.YEAR) == lastSelectedDayCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.DAY_OF_YEAR) == lastSelectedDayCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
            } else {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
            }

            TextView dayOfTheMonth = getDayOfMonthText(lastSelectedDayCalendar);
            dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_day_of_the_month_font));
        }
    }


    // If log indicates given day is very happy, mark emoji on calendar
    public void markVeryHappy(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.WEEK_OF_MONTH, 1);
        ImageView veryHappy = getVeryHappyImage(calendar);
        veryHappy.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(veryHappy.getDrawable(), Color.rgb(115,239,157));
//            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
        }
    }

    // If log indicates given day is content, mark emoji on calendar
    public void markContentImage(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.WEEK_OF_MONTH, 1);
        ImageView content = getContentImage(calendar);
        content.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(content.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(content.getDrawable(), Color.rgb(8,184,242));
        }
    }

    // If log indicates given day is neutral, mark emoji on calendar
    public void markNeutral(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.WEEK_OF_MONTH, 1);
        ImageView neutral = getNeutralImage(calendar);
        neutral.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(neutral.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(neutral.getDrawable(), Color.rgb(243,221,78));
//            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
        }
    }

    // If log indicates given day is not great, mark emoji on calendar
    public void markNotGreat(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.WEEK_OF_MONTH, 1);
        ImageView notGreatImage = getNotGreatImage(calendar);
        notGreatImage.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(notGreatImage.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(notGreatImage.getDrawable(), Color.rgb(255,165,0));
        }
    }

    // If log indicates given day is sad, mark emoji on calendar
    public void markSad(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.WEEK_OF_MONTH, 1);
        ImageView sadImage = getSadImage(calendar);
        sadImage.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(sadImage.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(sadImage.getDrawable(), Color.RED);
        }
    }


    public void setRobotoCalendarListener(RobotoCalendarListener robotoCalendarListener) {
        this.robotoCalendarListener = robotoCalendarListener;
    }

    private void findViewsById(View view) {

        robotoCalendarMonthLayout = view.findViewById(R.id.robotoCalendarDateTitleContainer);
        leftButton = view.findViewById(R.id.leftButton);
        rightButton = view.findViewById(R.id.rightButton);
        dateTitle = view.findViewById(R.id.monthText);

        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < 42; i++) {

            int weekIndex = (i % 7) + 1;
            ViewGroup dayOfTheWeekLayout = view.findViewWithTag(DAY_OF_THE_WEEK_LAYOUT + weekIndex);

            // Create day of the month
            View dayOfTheMonthLayout = inflate.inflate(R.layout.roboto_calendar_day_of_the_month_layout, null);
            View dayOfTheMonthText = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_TEXT);
            View dayOfTheMonthBackground = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND);
            View dayOfTheMonthContentImage = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CONTENT_IMAGE);
            View dayOfTheMonthVeryHappyImage = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_VERYHAPPY_IMAGE);
            View dayOfTheMonthNeutralImage = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_NEUTRAL_IMAGE);
            View dayOfTheMonthNotGreatImage = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_NOTGREAT_IMAGE);
            View dayOfTheMonthSadImage = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_SAD_IMAGE);


            // Set tags to identify them
            int viewIndex = i + 1;
            dayOfTheMonthLayout.setTag(DAY_OF_THE_MONTH_LAYOUT + viewIndex);
            dayOfTheMonthText.setTag(DAY_OF_THE_MONTH_TEXT + viewIndex);
            dayOfTheMonthBackground.setTag(DAY_OF_THE_MONTH_BACKGROUND + viewIndex);
            dayOfTheMonthContentImage.setTag(DAY_OF_THE_MONTH_CONTENT_IMAGE + viewIndex);
            dayOfTheMonthVeryHappyImage.setTag(DAY_OF_THE_MONTH_VERYHAPPY_IMAGE + viewIndex);
            dayOfTheMonthNeutralImage.setTag(DAY_OF_THE_MONTH_NEUTRAL_IMAGE + viewIndex);
            dayOfTheMonthNotGreatImage.setTag(DAY_OF_THE_MONTH_NOTGREAT_IMAGE + viewIndex);
            dayOfTheMonthSadImage.setTag(DAY_OF_THE_MONTH_SAD_IMAGE + viewIndex);

            dayOfTheWeekLayout.addView(dayOfTheMonthLayout);
        }
    }

    private void setUpEventListeners() {

    }

    private void setUpMonthLayout() {
        String dateText = new DateFormatSymbols(Locale.getDefault()).getMonths()[currentCalendar.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
        Calendar calendar = Calendar.getInstance();
        if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            dateTitle.setText(dateText);
        } else {
            dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
        }
        if (currMonth != null) {
            String prevMoodFilter = currMonth.getMoodFilter();
            String prevMediaFilter = currMonth.getMediaFilter();
            currMonth = new Month(dateText, prevMoodFilter, prevMediaFilter);
        } else {
            currMonth = new Month(dateText);
        }
        currMonth.readDataForMonth(currentCalendar.getTime(), super.getContext());
    }

    private void setUpWeekDaysLayout() {
        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        int length = weekDaysArray.length;
        for (int i = 1; i < length; i++) {
            dayOfWeek = rootView.findViewWithTag(DAY_OF_THE_WEEK_TEXT + getWeekIndex(i, currentCalendar));
            dayOfTheWeekString = weekDaysArray[i];
            if (shortWeekDays) {
                dayOfTheWeekString = checkSpecificLocales(dayOfTheWeekString, i);
            } else {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase() + dayOfTheWeekString.substring(1, 3);
            }

            dayOfWeek.setText(dayOfTheWeekString);
        }
    }

    // Set up days in current week
    private void setUpDaysOfMonthLayout() {

        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthBackground;

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.getFirstDayOfWeek());
        int firstDayOfWeek = auxCalendar.get(Calendar.DAY_OF_MONTH);

        for (int i = firstDayOfWeek; i < firstDayOfWeek + 7; i++) {

            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            dayOfTheMonthBackground = rootView.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND + i);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);

            dayOfTheMonthText.setVisibility(View.INVISIBLE);

            // Apply styles
            dayOfTheMonthText.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthText.setTypeface(null, Typeface.NORMAL);
            dayOfTheMonthText.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_day_of_the_month_font));
            dayOfTheMonthContainer.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthContainer.setOnClickListener(null);
            dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
        }
    }


    // For given day, set up days before and after current day in week by entering date
    // For calendar day that is current day, change text color
    private void setUpDaysInCalendar() {
        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());

        Calendar today = Calendar.getInstance(Locale.getDefault());
        today.setTime(currentCalendar.getTime());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i <= 7 ; i++) {
            auxCalendar = Calendar.getInstance(Locale.getDefault());
            auxCalendar.setTime(currentCalendar.getTime());
            auxCalendar.add(Calendar.DAY_OF_MONTH, i - dayOfWeek);
            int day = auxCalendar.get(Calendar.DAY_OF_MONTH);


            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            if (dayOfTheMonthText == null) {
                break;
            }
            dayOfTheMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfTheMonthContainer.setOnLongClickListener(onDayOfMonthLongClickListener);
            dayOfTheMonthText.setVisibility(View.VISIBLE);
            dayOfTheMonthText.setText(String.valueOf(day));
            if (i - dayOfWeek == 0) {
                dayOfTheMonthText.setTextColor(Color.rgb(115, 0, 238));
            }
        }
        drawMoodsFromFilter();
    }

    // Replaced by previous method
    private void markDayAsCurrentDay() {
    }

    private void updateView() {
        ImageView left_arrow = findViewById(R.id.leftButton);
        left_arrow.setVisibility(INVISIBLE);
        ImageView right_arrow = findViewById(R.id.rightButton);
        right_arrow.setVisibility(INVISIBLE);
        setUpMonthLayout();
        setUpWeekDaysLayout();
        setUpDaysOfMonthLayout();
        setUpDaysInCalendar();
        markDayAsCurrentDay();
    }

    private ViewGroup getDayOfMonthBackground(Calendar currentCalendar) {
        return (ViewGroup) getView(DAY_OF_THE_MONTH_BACKGROUND, currentCalendar);
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        return (TextView) getView(DAY_OF_THE_MONTH_TEXT, currentCalendar);
    }

    private ImageView getContentImage(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CONTENT_IMAGE, currentCalendar);
    }

    private ImageView getVeryHappyImage(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_VERYHAPPY_IMAGE, currentCalendar);
    }

    private ImageView getNeutralImage(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_NEUTRAL_IMAGE, currentCalendar);
    }

    private ImageView getNotGreatImage(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_NOTGREAT_IMAGE, currentCalendar);
    }

    private ImageView getSadImage(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_SAD_IMAGE, currentCalendar);
    }

    private View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);
        return rootView.findViewWithTag(key + index);
    }

    // Fetches mood for each day in current week and sets mood for days with entries
    public void drawMoodsFromFilter() {

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());

        Calendar today = Calendar.getInstance(Locale.getDefault());
        today.setTime(currentCalendar.getTime());
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);

        Calendar markMood = Calendar.getInstance(Locale.getDefault());


        for (int i = 1; i <= 7 ; i++) {
            auxCalendar = Calendar.getInstance(Locale.getDefault());
            auxCalendar.setTime(currentCalendar.getTime());
            auxCalendar.add(Calendar.DAY_OF_MONTH, i - dayOfWeek);
            Date day = auxCalendar.getTime();

            String dirNameDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(day);
            File directory = new File(getContext().getExternalFilesDir(null), dirNameDate+"/");
            StringBuilder text = new StringBuilder();
            if (directory.exists() && directory.isDirectory()) {
                StringBuilder emotion = new StringBuilder();
                File emotionFile = new File(directory, getResources().getString(R.string.emotion_file));
                try {
                    BufferedReader br = new BufferedReader(new FileReader(emotionFile));
                    String line = br.readLine();

                    while (line != null) {
                        emotion.append(line);
                        line = br.readLine();
                    }
                    br.close();
                } catch (IOException e) {

                }
                    markMood.setTime(auxCalendar.getTime());
                    if (emotion.toString().equals("content")) {
                        markContentImage(markMood.getTime());
                    } else if (emotion.toString().equals("veryhappy")){
                        markVeryHappy(markMood.getTime());
                    } else if (emotion.toString().equals("neutral")) {
                        markNeutral(markMood.getTime());
                    } else if (emotion.toString().equals("notgreat")) {
                        markNotGreat(markMood.getTime());
                    } else if (emotion.toString().equals("sad")) {
                        markSad(markMood.getTime());
                    }

            }
        }
    }


    public interface RobotoCalendarListener {

        void onDayClick(Date date);

        void onDayLongClick(Date date);
    }

}
