package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.calendar.Database.PeriodDatabaseHelper;
import com.example.calendar.Model.PeriodEntry;
import com.example.calendar.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    FloatingActionButton floatingActionButton;
    PeriodDatabaseHelper periodDatabaseHelper;
    CalendarView calendarView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initViews(activityMainBinding);

        periodDatabaseHelper = new PeriodDatabaseHelper(getApplicationContext());

        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SaveCycle.class));
        });

        customizeCalendarView();

        // Retrieve period entries from the database
        List<PeriodEntry> periodEntries = periodDatabaseHelper.getPeriodEntries();

        // Calculate ovulation day
        LocalDate ovulationDay = calculateOvulationDay(periodEntries);

        // Calculate average cycle length
        int averageCycleLength = calculateAverageCycleLength(periodEntries);

        // Calculate fertile window
        Pair<LocalDate, LocalDate> fertileWindow = calculateFertileWindow(ovulationDay, averageCycleLength);

        // Calculate next period date
        LocalDate nextPeriodDate = calculateNextPeriodDate(periodEntries, averageCycleLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void customizeCalendarView() {
        // Retrieve period entries from the database
        List<PeriodEntry> periodEntries = periodDatabaseHelper.getPeriodEntries();

        // Calculate ovulation day
        LocalDate ovulationDay = calculateOvulationDay(periodEntries);

        // Calculate average cycle length
        int averageCycleLength = calculateAverageCycleLength(periodEntries);

        // Calculate fertile window
        Pair<LocalDate, LocalDate> fertileWindow = calculateFertileWindow(ovulationDay, averageCycleLength);

        // Calculate next period date
        LocalDate nextPeriodDate = calculateNextPeriodDate(periodEntries, averageCycleLength);

        // Create a list to store the event days for customization
        List<EventDay> eventDays = new ArrayList<>();

        // Add ovulation day event
        if (ovulationDay != null) {
            Calendar ovulationCalendar = convertLocalDateToCalendar(ovulationDay);
            eventDays.add(new EventDay(ovulationCalendar, R.drawable.ovulation, Color.parseColor("#D92121")));
        }

        // Add fertile window events
        // Add fertile window events
        if (fertileWindow != null) {
            Calendar fertileStartCalendar = convertLocalDateToCalendar(fertileWindow.first);
            Calendar fertileEndCalendar = convertLocalDateToCalendar(fertileWindow.second);

            // Add all days between the start and end of the fertile window
            Calendar currentDay = (Calendar) fertileStartCalendar.clone();
            while (currentDay.before(fertileEndCalendar) || currentDay.equals(fertileEndCalendar)) {
                eventDays.add(new EventDay(currentDay, R.drawable.fertile, Color.parseColor("#ff4500")));//add aka image in drawable folder to represent fertile days
                currentDay.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Add the start and end days of the fertile window
            eventDays.add(new EventDay(fertileStartCalendar, R.drawable.fertile, Color.parseColor("#ffb347")));
            eventDays.add(new EventDay(fertileEndCalendar, R.drawable.fertile, Color.parseColor("#ffb347")));
        }


        // Add next period date event
        if (nextPeriodDate != null) {
            Calendar nextPeriodCalendar = convertLocalDateToCalendar(nextPeriodDate);
            eventDays.add(new EventDay(nextPeriodCalendar, R.drawable.next, Color.parseColor("#056608")));
        }

        // Set the event days to the calendar view
        calendarView.setEvents(eventDays);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Calendar convertLocalDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate calculateOvulationDay(List<PeriodEntry> periodEntries) {
        if (periodEntries.size() < 2) {
            return null; // Insufficient data to calculate ovulation day
        }

        // Get the last two period entries
        PeriodEntry secondLastEntry = periodEntries.get(periodEntries.size() - 2);

        // Calculate the average cycle length
        int averageCycleLength = calculateAverageCycleLength(periodEntries);

        // Convert the date strings to LocalDate objects
        LocalDate secondLastEndDate = LocalDate.parse(secondLastEntry.getEndDate());

        // Calculate the estimated ovulation day

        return secondLastEndDate.plusDays(averageCycleLength - 14);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculateAverageCycleLength(List<PeriodEntry> periodEntries) {
        int totalDays = 0;
        int count = 0;
        for (PeriodEntry periodEntry : periodEntries) {
            // Convert the date strings to LocalDate objects
            LocalDate startDate = LocalDate.parse(periodEntry.getStartDate());

            LocalDate endDate = LocalDate.parse(periodEntry.getEndDate());

            // Calculate the number of days between the start and end dates
            int days = 0;
            days = Period.between(startDate, endDate).getDays() +
                    (Period.between(startDate, endDate).getMonths() * 30) +
                    (Period.between(startDate, endDate).getYears() * 365);
            totalDays += days;
            count++;
        }

        if (count > 0) {
            return totalDays / count;
        } else {
            return 28; // Default average cycle length if no entries are available
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Pair<LocalDate, LocalDate> calculateFertileWindow(LocalDate ovulationDay, int averageCycleLength) {
        if (ovulationDay == null) {
            Toast.makeText(this, "Insufficient data to calculate ovulation day", Toast.LENGTH_SHORT).show();
            return null; // Ovulation day not available
        }

        // Calculate the start and end dates of the fertile window
        LocalDate startDate = ovulationDay.minusDays(5);
        LocalDate endDate = startDate.plusDays(10);


        return new Pair<>(startDate, endDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate calculateNextPeriodDate(List<PeriodEntry> periodEntries, int averageCycleLength) {
        if (periodEntries.isEmpty()) {
            return null; // No period entries available
        }

        // Get the last period entry
        PeriodEntry lastEntry = periodEntries.get(periodEntries.size() - 1);

        // Convert the date string to a LocalDate object
        LocalDate lastEndDate = LocalDate.parse(lastEntry.getEndDate());

        // Calculate the next period date
        return lastEndDate.plusDays(averageCycleLength);
    }


    private void initViews(ActivityMainBinding activityMainBinding) {
        floatingActionButton = activityMainBinding.fab;
        calendarView = (CalendarView) activityMainBinding.calendarView;
    }
}
