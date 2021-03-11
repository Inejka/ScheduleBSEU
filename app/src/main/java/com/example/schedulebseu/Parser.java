package com.example.schedulebseu;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    Document toParse;

    public Parser(Document toParse) {
        this.toParse = toParse;
    }

    public void parse(Context context, Fragment fragment) {
        Schedule toSave = new Schedule();

        toSave.startWeek = new GregorianCalendar().get(Calendar.WEEK_OF_YEAR) - Integer.parseInt
                (toParse.select("#sched > caption > span > strong:nth-child(1)").get(0).text());
        Elements rows = toParse.select("#sched > tbody:nth-child(3)").get(0).getElementsByTag("tr");
        rows.remove(0);//убирает говнецо
        rows.remove(0);//убирает понедельник(эх,жаль не в реале)
        for (Element i : rows) {
            if (isSubject(i) && isTime(i.getElementsByTag("td").get(1).text()))
                toSave.weeksCount = Math.max(toSave.weeksCount, getMax(getWeeksOfSubject(
                        i.getElementsByTag("td").get(1).text())));
            //if (isTime(i.text())) Log.i("Hah", i.text());
            //if (isTime(i.select("td:nth-child(1)").text()))
            //    Log.i("Hah", "1");
            //else Log.i("Hah", "0");
        }
        int day = 0;
        toSave.createWeeks();
        Iterator<Element> rowsITERATOR = rows.iterator();
        while (rowsITERATOR.hasNext()) {
            Element toWorkWith = rowsITERATOR.next();
            if (!isSubject(toWorkWith)) {
                day++;
                continue;
            }
            if (hasSubgroups(toWorkWith)) {

            } else {
                addNormakSub(toSave, toWorkWith, day);
            }
        }
        save(toSave,context,fragment);
    }

    private void save(Schedule toSave,Context mcoContext,Fragment fragment) {

        FileOutputStream fos = null;

        try {
            fos = mcoContext.openFileOutput("FILE_NAME", Context.MODE_PRIVATE);
            ObjectOutputStream test = new ObjectOutputStream(fos);
            test.writeObject(toSave);
            fragment.getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            fragment.getActivity().finish();
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

    private void addNormakSub(Schedule toAdd, Element toParse, int day) {
        Elements tds = toParse.getElementsByTag("td");
        simpleSubject sub = new simpleSubject();
        sub.time = tds.get(0).text();
        sub.classroom = tds.get(3).text();
        sub.subjectName = tds.get(2).getAllElements().get(0).text();
        sub.lecturer = tds.get(2).getElementsByClass("teacher dd").get(0).text();
        sub.type = tds.get(2).getElementsByClass("distype").get(0).text();
        sub.subjectName = sub.subjectName.replace(",", "");
        sub.subjectName = sub.subjectName.replace(sub.lecturer, "");
        sub.subjectName = sub.subjectName.replace(sub.type, "");
        sub.subjectName = sub.subjectName.trim();
        for (Integer i : getWeeksOfSubject(tds.get(1).text()))
            toAdd.weeks.get(i - 1).days.get(day).subjects.add(sub.copy());
    }


    private boolean hasSubgroups(Element toCheck) {
        int test = 0;
        for (Element i : toCheck.getElementsByTag("td"))
            test++;
        return test == 3;
    }

    private boolean isSubject(Element toCheck) {
        int test = 0;
        for (Element i : toCheck.getElementsByTag("td"))
            test++;
        return test >= 2;
    }

    private boolean isTime(String str) {
        for (int i = 0; i < str.length(); i++)
            if (!(str.charAt(i) == ':' || str.charAt(i) == '-' || (str.charAt(i) >= '0' &&
                    str.charAt(i) <= '9') || str.charAt(i) == '(' || str.charAt(i) == ')'
                    || str.charAt(i) == ','))
                return false;
        return true;
    }

    private List<Integer> getWeeksOfSubject(String str) {
        List<Integer> toReturn = new LinkedList<>();
        int i = 1;
        String from = "", to = "";
        while (true) {
            if (str.charAt(i) == ')') {
                toReturn.add(Integer.parseInt(from));
                break;
            }
            if (str.charAt(i) == ',') {
                toReturn.add(Integer.parseInt(from));
                i++;
                from = "";
                continue;
            }
            if (str.charAt(i) == '-') {
                i++;
                while (str.charAt(i) != ',' && str.charAt(i) != ')') {
                    to += str.charAt(i);
                    i++;
                }
                for (int j = Integer.parseInt(from); j <= Integer.parseInt(to); j++)
                    toReturn.add(j);
                if (str.charAt(i) == ')') break;
                i++;
                to = "";
                from = "";
                continue;
            }
            from += str.charAt(i);
            i++;
        }
        return toReturn;
    }

    Integer getMax(List<Integer> list) {
        Integer test = list.get(0);
        for (Integer i : list)
            test = Math.max(i, test);
        return test;
    }
}
