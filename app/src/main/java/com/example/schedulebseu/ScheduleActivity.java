package com.example.schedulebseu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

public class ScheduleActivity extends AppCompatActivity {

    Schedule mSchedule;
    Calendar mCalendar = new GregorianCalendar();
    int currentWeer;

    private List<String> weeks = new LinkedList<>();
    ArrayAdapter<String> weeksA;
    Spinner weeksS;

    List<String> days;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isScheduleGenerated())
            startActivity(new Intent(ScheduleActivity.this, ScheduleChooseActivity.class));
        else {
            init();
        }

    }

    private void init() {
        for (int i = 0; i < mSchedule.weeks.size(); i++)
            weeks.add(String.valueOf(i + 1) + " неделя");
        weeksS = findViewById(R.id.spinner);
        weeksA = new ArrayAdapter<String>(ScheduleActivity.this, android.R.layout.simple_spinner_item, weeks);
        weeksA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weeksS.setAdapter(weeksA);
        weeksS.setSelection(mCalendar.get(Calendar.WEEK_OF_YEAR) - mSchedule.startWeek - 1);
        currentWeer = mCalendar.get(Calendar.WEEK_OF_YEAR) - mSchedule.startWeek - 1;

        weeksS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar testC = new GregorianCalendar();
                testC.add(Calendar.WEEK_OF_YEAR, -1 * mSchedule.startWeek);
                testC.add(Calendar.WEEK_OF_YEAR, position + 2);
                testC.add(Calendar.DAY_OF_MONTH, -1 * mCalendar.get(Calendar.DAY_OF_WEEK) + 2);
                ((TextView) findViewById(R.id.pnDate)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                testC.add(Calendar.DAY_OF_MONTH,1);
                ((TextView) findViewById(R.id.vtDate)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                testC.add(Calendar.DAY_OF_MONTH,1);
                ((TextView) findViewById(R.id.srDay)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                testC.add(Calendar.DAY_OF_MONTH,1);
                ((TextView) findViewById(R.id.chDay)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                testC.add(Calendar.DAY_OF_MONTH,1);
                ((TextView) findViewById(R.id.ptDay)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                testC.add(Calendar.DAY_OF_MONTH,1);
                ((TextView) findViewById(R.id.sbDay)).setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        LinearLayout first = findViewById(R.id.pn);
        first.setOnClickListener(e->{
            first.setBackgroundColor(Color.RED);
        });
    }

    private void clear(){
        ((LinearLayout)findViewById(R.id.pn)).setBackgroundColor(Color.WHITE);
        ((LinearLayout)findViewById(R.id.vt)).setBackgroundColor(Color.WHITE);
        ((LinearLayout)findViewById(R.id.sr)).setBackgroundColor(Color.WHITE);
        ((LinearLayout)findViewById(R.id.ch)).setBackgroundColor(Color.WHITE);
        ((LinearLayout)findViewById(R.id.pt)).setBackgroundColor(Color.WHITE);
        ((LinearLayout)findViewById(R.id.sb)).setBackgroundColor(Color.WHITE);
    }

    /*class testAdapter extends BaseAdapter {
        private Context mContext;
        List<String> days;

        public testAdapter(Context context, List<String> days) {
            mContext = context;
            this.days = days;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return days.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View test = convertView;
            if (test == null) {
                test = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.grid_item_layout, parent, false);
            }
            TextView dayWeek = test.findViewById(R.id.dayWeek);
            TextView number = test.findViewById(R.id.number);
            Log.i("Hah", String.valueOf(position));
            switch (position) {
                case 0:
                    dayWeek.setText("ПН");
                    break;
                case 1:
                    dayWeek.setText("ВТ");
                    break;
                case 2:
                    dayWeek.setText("СР");
                    break;
                case 3:
                    dayWeek.setText("ЧТ");
                    break;
                case 4:
                    dayWeek.setText("ПТ");
                    break;
                case 5:
                    dayWeek.setText("СБ");
                    break;
            }
            //((TextView)test.findViewById(R.id.day)).setText(days.get(position));
            return test;
        }
    }
       */
    boolean isScheduleGenerated() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("FILE_NAME");
            ObjectInputStream test = new ObjectInputStream(fis);
            mSchedule = (Schedule) test.readObject();
            test.close();
            fis.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}