package com.example.schedulebseu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

public class ScheduleActivity extends AppCompatActivity {

    Schedule mSchedule;
    Calendar mCalendar = new GregorianCalendar();

    private List<String> weeks = new LinkedList<>();
    ArrayAdapter<String> weeksA;
    Spinner weeksS;

    FragmentManager fragmentManager;

    ViewPager viewPager;

    daysContainer vies = new daysContainer();

    boolean buttonClickes = false;


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
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.pager);
        vies.init();

        weeksS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar testC = new GregorianCalendar();
                testC.add(Calendar.WEEK_OF_YEAR, -1 * mSchedule.startWeek);
                testC.add(Calendar.WEEK_OF_YEAR, position + 1);
                testC.add(Calendar.DAY_OF_MONTH, -1 * mCalendar.get(Calendar.DAY_OF_WEEK) + 2);
                for (int i = 0; i < 6; i++) {
                    vies.viesDate[i].setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                    testC.add(Calendar.DAY_OF_WEEK, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            int prevPos = 0;

            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (!buttonClickes) {
                    if ((prevPos > position)) {
                        vies.dec();
                    } else {
                        vies.inc();
                    }
                } else buttonClickes = false;
                prevPos = position;
                return new RecycleViewFragment(mSchedule.get(position));
            }

            @Override
            public int getCount() {
                return mSchedule.weeksCount * 6;
            }
        });
        int startDay = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        vies.vies[(startDay == 7) ? 5 : (startDay - 2)].callOnClick();

    }

    private class daysContainer {
        private int chosen;
        public LinearLayout[] vies = new LinearLayout[6];
        public TextView[] viesDate = new TextView[6];

        public void clear() {
            for (int i = 0; i < 6; i++)
                vies[i].setBackgroundColor(Color.WHITE);
        }

        public int getPosSelected() {
            return chosen;
        }

        public void dec() {
            if (chosen == 0) {
                chosen = 5;
                weeksDec();
            } else chosen = chosen - 1;
            clear();
            vies[chosen].setBackgroundColor(Color.RED);
        }

        public void inc() {
            if (chosen == 5) {
                chosen = 0;
                weeksInc();
            } else chosen = chosen + 1;
            clear();
            vies[chosen].setBackgroundColor(Color.RED);
        }

        public void init() {
            vies[0] = findViewById(R.id.pn);
            vies[1] = findViewById(R.id.vt);
            vies[2] = findViewById(R.id.sr);
            vies[3] = findViewById(R.id.ch);
            vies[4] = findViewById(R.id.pt);
            vies[5] = findViewById(R.id.sb);
            viesDate[0] = findViewById(R.id.pnDate);
            viesDate[1] = findViewById(R.id.vtDate);
            viesDate[2] = findViewById(R.id.srDay);
            viesDate[3] = findViewById(R.id.chDay);
            viesDate[4] = findViewById(R.id.ptDay);
            viesDate[5] = findViewById(R.id.sbDay);

            for (int i = 0; i < 6; i++) {
                LinearLayout test = vies[i];
                int finalI = i;
                test.setOnClickListener(e -> {
                    chosen = finalI;
                    clear();
                    test.setBackgroundColor(Color.RED);
                    buttonClickes = true;
                    viewPager.setCurrentItem(weeksS.getSelectedItemPosition() * 6 + chosen);
                    //((RecycleViewFragment) recView).updateSub(mSchedule.weeks.get(weeksS.getSelectedItemPosition()).days.get(chosen).subjects);
                });
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FileOutputStream fos = null;
        try {
            fos = ScheduleActivity.this.openFileOutput("FILE_NAME", Context.MODE_PRIVATE);
            ObjectOutputStream test = new ObjectOutputStream(fos);
            test.writeObject(mSchedule);
            test.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void weeksDec() {
        int pos = weeksS.getSelectedItemPosition();
        if (pos != 0)
            weeksS.setSelection(pos - 1);
    }

    private void weeksInc() {
        int pos = weeksS.getSelectedItemPosition();
        if (pos != mSchedule.weeksCount - 1)
            weeksS.setSelection(pos + 1);
    }


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