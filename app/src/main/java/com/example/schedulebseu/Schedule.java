package com.example.schedulebseu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    public class simpleDay implements Serializable {
        private static final long serialVersionUID = 2L;
        public List<simpleSubject> subjects;
        public simpleDay(){
            subjects= new LinkedList<>();
        }

    }

    public class Week implements Serializable {
        private static final long serialVersionUID = 3L;
        public List<simpleDay> days;

        public Week(){
            days = new ArrayList<>();
            for(int i = 0 ; i < 7 ; i++)
                days.add(new simpleDay());
        }
    }

    public List<Week> weeks;
    public int startWeek, weeksCount = 17;

    public void createWeeks() {
        weeks = new ArrayList<>();

        for (int i = 0; i < weeksCount; i++)
            weeks.add(new Week());

    }
}
