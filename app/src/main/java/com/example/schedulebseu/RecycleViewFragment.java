package com.example.schedulebseu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecycleViewFragment extends Fragment {

    RecyclerView mRecyclerView;
    subjectAdapter mSubjectAdapter;

    LayoutInflater inflater;

    Context viewContest;

    List<simpleSubject> subjects;

    public RecycleViewFragment(List<simpleSubject> subjects) {
        this.subjects = subjects;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        if(subjects instanceof ArrayList)
            return inflater.inflate(R.layout.secret_layout, container, false);
        if (subjects.size() == 0)
            return inflater.inflate(R.layout.no_subject_day, container, false);
        View v = inflater.inflate(R.layout.recycle_view_fragment, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mSubjectAdapter = new subjectAdapter(subjects);
        mRecyclerView.setAdapter(mSubjectAdapter);
        viewContest = v.getContext();
        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void updateSub(List<simpleSubject> subjects) {
        mSubjectAdapter.updateSub(subjects);
    }

    private class subjectHolder extends RecyclerView.ViewHolder {
        TextView time, nameAndType, lecturer, classroom = null, customInfo = null;
        simpleSubject subject;
        LinearLayout layout;

        public subjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.subject_layout, parent, false));
            time = itemView.findViewById(R.id.time);
            nameAndType = itemView.findViewById(R.id.nameAndType);
            lecturer = itemView.findViewById(R.id.lecturer);
            layout = itemView.findViewById(R.id.sublay);
            //classroom = itemView.findViewById(R.id.classroom);
            //customInfo = itemView.findViewById(R.id.customInfo);
            itemView.findViewById(R.id.sublay).setOnClickListener(
                    e -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), android.R.style.Theme_Holo_Dialog_NoActionBar);
                        builder.setTitle("Title");
// Set up the input
                        final EditText input = new EditText(itemView.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setTextColor(getResources().getColor(R.color.textColor));
                        builder.setView(input);
                        if (customInfo != null)
                            input.setText(customInfo.getText());

// Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (customInfo == null) {
                                    customInfo = textInit(layout.getContext(), layout);
                                    customInfo.setTextSize(18);
                                    customInfo.setTextColor(getResources().getColor(R.color.customTextColor));
                                }
                                customInfo.setText(input.getText());
                                subject.customInfo = input.getText().toString();
                                if (subject.customInfo.isEmpty())
                                    layout.removeView(customInfo);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
            );
        }

        public void bind(simpleSubject subject) {
            time.setText(subject.time);
            nameAndType.setText(subject.subjectName + subject.type);
            lecturer.setText(subject.lecturer);
            //classroom.setText(subject.classroom);
            if (!subject.classroom.isEmpty()) {
                if (classroom == null) {
                    classroom = textInit(layout.getContext(), layout);
                    classroom.setTextSize(24);
                }
                classroom.setText(subject.classroom);
            }
            //customInfo.setText(subject.customInfo);
            if (!subject.customInfo.isEmpty()) {
                if (customInfo == null) {
                    customInfo = textInit(layout.getContext(), layout);
                    customInfo.setTextSize(18);
                    customInfo.setTextColor(getResources().getColor(R.color.customTextColor));
                }
                customInfo.setText(subject.customInfo);
            }
            this.subject = subject;
        }

        private TextView textInit(Context context, LinearLayout toAdd) {
            TextView toReturn = new TextView(context);
            toAdd.addView(toReturn);
            toReturn.setTextColor(getResources().getColor(R.color.textColor));
            toReturn.setGravity(Gravity.CENTER);
            toReturn.setPadding(16, 0, 16, 0);
            return toReturn;

        }
    }


    private class subjectAdapter extends RecyclerView.Adapter<subjectHolder> {
        List<simpleSubject> mSubjects = new LinkedList<>();

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
            return new subjectHolder(inflater, parent);
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
}
