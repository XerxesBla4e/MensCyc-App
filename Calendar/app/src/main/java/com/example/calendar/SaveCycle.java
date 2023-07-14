package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.calendar.Database.PeriodDatabaseHelper;
import com.example.calendar.databinding.ActivitySaveCycleBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SaveCycle extends AppCompatActivity {
    private ActivitySaveCycleBinding activitySaveCycleBinding;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Button pickStart, pickEnd, submit;
    private PeriodDatabaseHelper periodDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySaveCycleBinding = ActivitySaveCycleBinding.inflate(getLayoutInflater());
        setContentView(activitySaveCycleBinding.getRoot());

        initViews();

        periodDatabaseHelper = new PeriodDatabaseHelper(SaveCycle.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFields()) {
                    return;
                } else {
                    // Save the information to the database
                    String startDate = formatDate(startCalendar);
                    String endDate = formatDate(endCalendar);
                    String periodFlow = activitySaveCycleBinding.edtTxtPeriodFlow.getText().toString();

                    periodDatabaseHelper.addPeriodEntry(startDate, endDate, periodFlow);

                    // Clear the form fields
                    activitySaveCycleBinding.edtTxtFinishDate.setText("");
                    activitySaveCycleBinding.edtTxtInitDate.setText("");
                    activitySaveCycleBinding.edtTxtPeriodFlow.setText("");

                    Toast.makeText(SaveCycle.this, R.string.period_entry_saved, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        pickStart = activitySaveCycleBinding.btnPickInitDate;
        pickEnd = activitySaveCycleBinding.btnPickFinishDate;
        submit = activitySaveCycleBinding.btnAddCycle;

        pickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartDate();
            }
        });

        pickEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndDate();
            }
        });
    }

    private void setStartDate() {
        new DatePickerDialog(SaveCycle.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, monthOfYear);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                },
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setEndDate() {
        new DatePickerDialog(SaveCycle.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, monthOfYear);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                },
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validateFields() {
        String periodFlow = activitySaveCycleBinding.edtTxtPeriodFlow.getText().toString();

        if (TextUtils.isEmpty(periodFlow)) {
            activitySaveCycleBinding.edtTxtPeriodFlow.setError(getString(R.string.error_fill_period_flow));
            return false;
        }

        if (startCalendar.after(endCalendar)) {
            Toast.makeText(SaveCycle.this, R.string.error_invalid_date_range, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
