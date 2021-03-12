package com.example.schedulebseu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.io.ObjectInputStream;
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

    daysContainer vies = new daysContainer();

    RecyclerView mRecyclerView;
    subjectAdapter mSubjectAdapter;

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
        recycleViewInit();
        vies.init();
        int startDay = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 2;
        vies.vies[(startDay == 7) ? 5 : (startDay - 2)].callOnClick();
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

        ((Button) findViewById(R.id.dec)).setOnClickListener(e -> {
            vies.dec();
        });
        ((Button) findViewById(R.id.inc)).setOnClickListener(e -> {
            vies.inc();
        });
    }

    private void recycleViewInit() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this));
        mSubjectAdapter = new subjectAdapter();
        mRecyclerView.setAdapter(mSubjectAdapter);
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
            vies[chosen].callOnClick();
        }

        public void inc() {
            if (chosen == 5) {
                chosen = 0;
                weeksInc();
            } else chosen = chosen + 1;
            vies[chosen].callOnClick();
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
                    mSubjectAdapter.updateSub(mSchedule.weeks.get(weeksS.getSelectedItemPosition()).days.get(chosen).subjects);
                });
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

    private class subjectHolder extends RecyclerView.ViewHolder {
        TextView time, nameAndType, lecturer, classroom, customInfo;

        public subjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.subject_layout, parent, false));
            time = itemView.findViewById(R.id.time);
            nameAndType = itemView.findViewById(R.id.nameAndType);
            lecturer = itemView.findViewById(R.id.lecturer);
            classroom = itemView.findViewById(R.id.classroom);
            customInfo = itemView.findViewById(R.id.customInfo);
        }

        public void bind(simpleSubject subject) {
            time.setText(subject.time);
            nameAndType.setText(subject.subjectName + subject.type);
            lecturer.setText(subject.lecturer);
            classroom.setText(subject.classroom);
            customInfo.setText(subject.customInfo);
        }
    }

    private class subjectAdapter extends RecyclerView.Adapter<subjectHolder> {
        List<simpleSubject> mSubjects;

        public subjectAdapter() {
        }

        public subjectAdapter(List<simpleSubject> subjects) {
            mSubjects = subjects;
        }

        public void updateSub(List<simpleSubject> subjects) {
            mSubjects = subjects;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public subjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ScheduleActivity.this);
            return new subjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull subjectHolder holder, int position) {
            holder.bind(mSubjects.get(position));
        }

        @Override
        public int getItemCount() {
            return mSubjects.size();
        }
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