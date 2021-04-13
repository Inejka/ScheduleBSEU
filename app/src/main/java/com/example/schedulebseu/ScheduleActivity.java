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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.http.PUT;

public class ScheduleActivity extends AppCompatActivity {
    boolean initIsNeeded = true;
    Schedule mSchedule;
    Calendar mCalendar = new GregorianCalendar();

    private List<String> weeks = new LinkedList<>();
    SpinnerAdapter weeksA;
    Spinner weeksS;

    FragmentManager fragmentManager;

    ViewPager viewPager;

    daysContainer vies = new daysContainer();
    int buttonPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
    }


    protected void onResume() {
        super.onResume();
        if (!isScheduleGenerated())
            startActivity(new Intent(ScheduleActivity.this, ScheduleChooseActivity.class));
        else if (initIsNeeded) {
            init();
        }
    }


    private void init() {
        loadSchedule();
        for (int i = 0; i < mSchedule.weeks.size(); i++)
            weeks.add(String.valueOf(i + 1) + " неделя");
        weeksS = findViewById(R.id.spinner);
        weeksA = new SpinnerAdapter(ScheduleActivity.this, weeks);
        //weeksA = new ArrayAdapter<String>(ScheduleActivity.this, android.R.layout.simple_spinner_item, weeks);
        //weeksA.setDropDownViewResource(R.layout.spinner_item);
        weeksS.setAdapter(weeksA);
        weeksS.setSelection(mCalendar.get(Calendar.WEEK_OF_YEAR) - mSchedule.startWeek - 1);
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.pager);
        vies.init();

        weeksS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar testC = new GregorianCalendar();
                testC.set(Calendar.WEEK_OF_YEAR,mSchedule.startWeek+1);
                testC.add(Calendar.WEEK_OF_YEAR, position );
                testC.add(Calendar.DAY_OF_MONTH, -1 * mCalendar.get(Calendar.DAY_OF_WEEK) + 2);
                for (int i = 0; i < 6; i++) {
                    vies.viesDate[i].setText(String.valueOf(testC.get(Calendar.DAY_OF_MONTH)));
                    testC.add(Calendar.DAY_OF_WEEK, 1);
                    vies.click();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewPager.addOnPageChangeListener(mOnPageChangeListener);
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == mSchedule.weeksCount * 6) {
                    return new RecycleViewFragment(new ArrayList<>(100));
                }
                return new RecycleViewFragment(mSchedule.get(position));
            }

            @Override
            public int getCount() {
                return mSchedule.weeksCount * 6 + 1;
            }
        });
        int startDay = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        vies.vies[(startDay == 1) ? 5 : (startDay - 2)].callOnClick();
        initIsNeeded = false;
    }

    private class daysContainer {
        public boolean buttonClicked;
        private int chosen;
        public LinearLayout[] vies = new LinearLayout[6];
        public TextView[] viesDate = new TextView[6];

        public void clear() {
            for (int i = 0; i < 6; i++)
                vies[i].setBackgroundResource(R.drawable.layout_bg);
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
            updateButtons();
        }

        public void click() {
            vies[chosen].callOnClick();
        }

        public void inc() {
            if (chosen == 5) {
                chosen = 0;
                weeksInc();
            } else chosen = chosen + 1;
            clear();
            updateButtons();
        }

        public void updateHat(int pos) {
            chosen = pos % 6;
            weeksS.setSelection(pos / 6);
            clear();
            updateButtons();
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
                    buttonClicked = true;
                    chosen = finalI;
                    clear();
                    updateButtons();
                    buttonPosition = weeksS.getSelectedItemPosition() * 6 + chosen;
                    mOnPageChangeListener.prevPos = buttonPosition;
                    viewPager.setCurrentItem(buttonPosition);
                    //((RecycleViewFragment) recView).updateSub(mSchedule.weeks.get(weeksS.getSelectedItemPosition()).days.get(chosen).subjects);
                });
            }
        }

        public void updateButtons() {
            vies[chosen].setBackgroundResource(R.drawable.layoyt_chosen_bg);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isScheduleGenerated()) return;
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
            fis.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    void loadSchedule() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("FILE_NAME");
            ObjectInputStream test = new ObjectInputStream(fis);
            mSchedule = (Schedule) test.readObject();
            test.close();
            fis.close();
        } catch (Exception e) {
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        public int prevPos = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (vies.buttonClicked && positionOffsetPixels != 0) {
                return;
            } else vies.buttonClicked = false;
            vies.updateHat(position);
            /*    if (positionOffsetPixels != 0) return;
            if (prevPos == position) return;
            if (Math.abs(prevPos - position) == 1) {
                if ((prevPos > position)) {
                    vies.dec();
                } else {
                    vies.inc();
                }
            }
        */
            prevPos = position;
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    private MyPageChangeListener mOnPageChangeListener = new MyPageChangeListener();
}