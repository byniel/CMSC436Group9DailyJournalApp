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
package com.example.dailyjournalgroup9.ui.calendar;

import android.content.Context;
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

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

import com.example.dailyjournalgroup9.Month;
import com.example.dailyjournalgroup9.R;


/**
 * The roboto calendar view
 *
 * @author Marco Hernaiz Cao
 */

// external library with changes by the group that allow for calendar view customization
public class RobotoCalendarView extends LinearLayout {

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
            markDayAsCurrentDay();

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

            currMonth.readDataForMonth(calendar.getTime(), getContext());
//            updateView();

            return true;
        }
    };
    private boolean shortWeekDays = false;

    public RobotoCalendarView(Context context) {
        super(context);
        init(null);
    }

    public RobotoCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public RobotoCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RobotoCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        // Mark current day as selected
        ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(calendar);
        dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring_background);
    }

    public void clearSelectedDay() {
        if (lastSelectedDayCalendar != null) {
            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(lastSelectedDayCalendar);

            // If it's today, keep the current day style
            Calendar nowCalendar = Calendar.getInstance();
            if (nowCalendar.get(Calendar.YEAR) == lastSelectedDayCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.DAY_OF_YEAR) == lastSelectedDayCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
                markDayAsCurrentDay();
            } else {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
            }

            TextView dayOfTheMonth = getDayOfMonthText(lastSelectedDayCalendar);
            dayOfTheMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.roboto_calendar_day_of_the_month_font));
        }
    }

    public void setShortWeekDays(boolean shortWeekDays) {
        this.shortWeekDays = shortWeekDays;
    }

    // adds emoji denoting very happy under specific date
    public void markVeryHappy(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ImageView veryHappy = getVeryHappyImage(calendar);
        veryHappy.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(veryHappy.getDrawable(), Color.rgb(115,239,157));
//            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
        }
    }

    // Added by group: adds emoji denoting mark content under specific date
    public void markContentImage(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ImageView content = getContentImage(calendar);
        content.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(content.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(content.getDrawable(), Color.rgb(8,184,242));
        }
    }

    // Added by group: adds emoji denoting neutral under specific date
    public void markNeutral(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ImageView neutral = getNeutralImage(calendar);
        neutral.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(neutral.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(neutral.getDrawable(), Color.rgb(243,221,78));
//            DrawableCompat.setTint(veryHappy.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_circle_1));
        }
    }

    // Added by group: adds emoji denoting not great under specific date
    public void markNotGreat(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ImageView notGreatImage = getNotGreatImage(calendar);
        notGreatImage.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(notGreatImage.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(notGreatImage.getDrawable(), Color.rgb(255,165,0));
        }
    }

    // Added by group: adds emoji denoting sad under specific date
    public void markSad(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        ImageView sadImage = getSadImage(calendar);
        sadImage.setVisibility(View.VISIBLE);
        if (lastSelectedDayCalendar != null && areInTheSameDay(calendar, lastSelectedDayCalendar)) {
            DrawableCompat.setTint(sadImage.getDrawable(), ContextCompat.getColor(getContext(), R.color.roboto_calendar_selected_day_font));
        } else {
            DrawableCompat.setTint(sadImage.getDrawable(), Color.RED);
        }
    }

    public void showDateTitle(boolean show) {
        if (show) {
            robotoCalendarMonthLayout.setVisibility(VISIBLE);
        } else {
            robotoCalendarMonthLayout.setVisibility(GONE);
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

        leftButton.setOnClickListener(view -> {
            if (robotoCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
            }

            // Decrease month
            clearMoods();
            currentCalendar.add(Calendar.MONTH, -1);
            lastSelectedDayCalendar = null;
            updateView();
            robotoCalendarListener.onLeftButtonClick();
        });

        rightButton.setOnClickListener(view -> {
            if (robotoCalendarListener == null) {
                throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
            }

            // Increase month
            clearMoods();
            currentCalendar.add(Calendar.MONTH, 1);
            lastSelectedDayCalendar = null;
            updateView();
            robotoCalendarListener.onRightButtonClick();
        });
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

    private void setUpDaysOfMonthLayout() {

        TextView dayOfTheMonthText;
        View circleImage1;
        View circleImage2;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthBackground;

        for (int i = 1; i < 43; i++) {

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


    private void setUpDaysInCalendar() {

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthLayout;

        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthContainer = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + dayOfTheMonthIndex);
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);
            if (dayOfTheMonthText == null) {
                break;
            }
            dayOfTheMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfTheMonthContainer.setOnLongClickListener(onDayOfMonthLongClickListener);
            dayOfTheMonthText.setVisibility(View.VISIBLE);
            dayOfTheMonthText.setText(String.valueOf(i));
        }

        for (int i = 36; i < 43; i++) {
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            dayOfTheMonthLayout = rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            if (dayOfTheMonthText.getVisibility() == INVISIBLE) {
                dayOfTheMonthLayout.setVisibility(GONE);
            } else {
                dayOfTheMonthLayout.setVisibility(VISIBLE);
            }
        }
        drawMoodsFromFilter();
    }

    //Adapted external code to make current day specific color
    private void markDayAsCurrentDay() {
        // If it's the current month, mark current day
        Calendar nowCalendar = Calendar.getInstance();
        if (nowCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(nowCalendar.getTime());


            TextView dayOfTheMonth = getDayOfMonthText(currentCalendar);

            //sets current day to purple
            dayOfTheMonth.setTextColor(Color.rgb(115, 0, 238));
        }
    }

    private void updateView() {
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

    // Gets a map of entries depending on the mood and media filters.
    // Based on the map of days and emotions, displays the emotions per day
    public void drawMoodsFromFilter() {

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        Calendar markMood = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        currMonth.readDataForMonth(auxCalendar.getTime(), getContext());
        markMood.setTime(currentCalendar.getTime());
        markMood.set(Calendar.DAY_OF_MONTH, 1);
        int month = auxCalendar.get(Calendar.MONTH);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;

        HashMap<Integer,String> entries = currMonth.getEntriesAndFilter();


        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);
            if (dayOfTheMonthText == null) {
                break;
            }
            if(entries.get(i) != null) {
                markMood.set(Calendar.MONTH, month);
                markMood.set(Calendar.DAY_OF_MONTH, i);
                if (entries.get(i).equals("content")) {
                    markContentImage(markMood.getTime());
                } else if (entries.get(i).equals("veryhappy")){
                    markVeryHappy(markMood.getTime());
                } else if (entries.get(i).equals("neutral")) {
                    markNeutral(markMood.getTime());
                } else if (entries.get(i).equals("notgreat")) {
                    markNotGreat(markMood.getTime());
                } else if (entries.get(i).equals("sad")) {
                    markSad(markMood.getTime());
                }
            }
        }
    }

    // Goes through the moods that are displayed and clears them when changing pages
    public void clearMoods() {
        HashMap<Integer, String> entries = currMonth.getEntriesAndFilter();

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        Calendar markMood = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        markMood.setTime(currentCalendar.getTime());
        markMood.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;

        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthText = rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);
            if (dayOfTheMonthText == null) {
                break;
            }
            if (entries.get(i) != null) {
                markMood.set(Calendar.MONTH, auxCalendar.get(Calendar.MONTH));
                markMood.set(Calendar.DAY_OF_MONTH, i);
                if (entries.get(i).equals("content")) {
                    ImageView contentImage = getContentImage(markMood);
                    if (contentImage.getVisibility() == VISIBLE) {
                        contentImage.setVisibility(INVISIBLE);
                    }
                } else if (entries.get(i).equals("veryhappy")) {
                    ImageView veryHappyImage = getVeryHappyImage(markMood);
                    if (veryHappyImage.getVisibility() == VISIBLE) {
                        veryHappyImage.setVisibility(INVISIBLE);
                    }
                } else if (entries.get(i).equals("neutral")) {
                    ImageView neutral = getNeutralImage(markMood);
                    if (neutral.getVisibility() == VISIBLE) {
                        neutral.setVisibility(INVISIBLE);
                    }
                } else if (entries.get(i).equals("notgreat")) {
                    ImageView notgreat = getNotGreatImage(markMood);
                    if (notgreat.getVisibility() == VISIBLE) {
                        notgreat.setVisibility(INVISIBLE);
                    }
                } else if (entries.get(i).equals("sad")) {
                    ImageView sad = getSadImage(markMood);
                    if (sad.getVisibility() == VISIBLE) {
                        sad.setVisibility(INVISIBLE);
                    }
                }
            }
        }
    }




    public interface RobotoCalendarListener {

        void onDayClick(Date date);

        void onDayLongClick(Date date);

        void onRightButtonClick();

        void onLeftButtonClick();
    }

}
