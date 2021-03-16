package com.example.schedulebseu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScheduleChooseFragment extends Fragment {

    private Map<String, String> mapFaculties = new LinkedHashMap<>();
    private List<String> listFaculties = new LinkedList<>();

    Spinner fuculties_spinner;

    private Map<String, String> mapForms = new LinkedHashMap<>();
    private List<String> listForms = new LinkedList<>();
    SpinnerAdapter form_spinner_a;
    Spinner form_spinner;

    private Map<String, String> mapGroup = new LinkedHashMap<>();
    private List<String> listGroup = new LinkedList<>();
    SpinnerAdapter group_spinner_a;
    Spinner group_spinner;

    private Map<String, String> mapCourse = new LinkedHashMap<>();
    private List<String> listCourse = new LinkedList<>();
    SpinnerAdapter course_spinner_a;
    Spinner course_spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_choose_fragment, container,
                false);
        faculty_init(view);
        form_init(view);
        course_init(view);
        group_init(view);

        Button init = view.findViewById(R.id.init_button);
        init.setOnClickListener(v -> {
                    if ((String) fuculties_spinner.getSelectedItem() != "Выберите факультет" &&
                            (String) form_spinner.getSelectedItem() != "Выберите форму обучения" &&
                            (String) course_spinner.getSelectedItem() != "Выберите курс" &&
                            (String) group_spinner.getSelectedItem() != "Выберите группу") {
                        HTML_SCHEDULE_PARSER test = new HTML_SCHEDULE_PARSER(getContext(), this);
                        test.sendPost(mapFaculties.get((String) fuculties_spinner.getSelectedItem()),
                                mapForms.get((String) form_spinner.getSelectedItem()),
                                mapCourse.get((String) course_spinner.getSelectedItem()),
                                mapGroup.get((String) group_spinner.getSelectedItem()),
                                "__id.25.main.inpFldsA.GetSchedule__sp.7.results__fp.4.main"
                        );
                    }
                }
        );

        return view;
    }

    private void group_init(View view) {
        listGroup.add("Выберите группу");
        group_spinner = view.findViewById(R.id.group_spinner);
        //group_spinner_a = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, listGroup);
        //group_spinner_a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group_spinner_a = new SpinnerAdapter(view.getContext(), listGroup);
        group_spinner.setAdapter(group_spinner_a);
        group_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
            }
        });
    }

    private void course_init(View view) {
        listCourse.add("Выберите курс");
        course_spinner = view.findViewById(R.id.course_spinner);
        //course_spinner_a = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, listCourse);
        //course_spinner_a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course_spinner_a = new SpinnerAdapter(view.getContext(), listCourse);
        course_spinner.setAdapter(course_spinner_a);
        course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (!((String) parent.getItemAtPosition(pos)).equals("Выберите курс")) {
                    JSON_ALL_PARSER test = new JSON_ALL_PARSER(mapGroup, listGroup, group_spinner_a);
                    test.sendPost(mapFaculties.get((String) fuculties_spinner.getSelectedItem()),
                            mapForms.get((String) form_spinner.getSelectedItem()),
                            mapCourse.get((String) parent.getItemAtPosition(pos)), "",
                            "__id.23.main.inpFldsA.GetGroups");
                }
            }
        });

        form_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                listGroup.clear();
                mapGroup.clear();
                listGroup.add("Выберите группу");
                group_spinner_a.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void form_init(View view) {
        listForms.add("Выберите форму обучения");
        form_spinner = view.findViewById(R.id.form_spinner);
        //form_spinner_a = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, listForms);
        //form_spinner_a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        form_spinner_a = new SpinnerAdapter(view.getContext(), listForms);
        form_spinner.setAdapter(form_spinner_a);
        form_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (!((String) parent.getItemAtPosition(pos)).equals("Выберите форму обучения")) {
                    JSON_ALL_PARSER test = new JSON_ALL_PARSER(mapCourse, listCourse, course_spinner_a);
                    test.sendPost(mapFaculties.get((String) fuculties_spinner.getSelectedItem()),
                            mapForms.get((String) parent.getItemAtPosition(pos)),
                            "", "", "__id.23.main.inpFldsA.GetCourse");
                }
            }
        });
        form_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                listCourse.clear();
                mapCourse.clear();
                listCourse.add("Выберите курс");
                course_spinner_a.notifyDataSetChanged();

                listGroup.clear();
                mapGroup.clear();
                listGroup.add("Выберите группу");
                group_spinner_a.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void faculty_init(View view) {
        listFaculties.add("Выберите факультет");
        fuculties_spinner = view.findViewById(R.id.fuculties_spinner);
        fuculties_spinner.setAdapter(new SpinnerAdapter(view.getContext(), listFaculties));
        SpinnerAdapter adapter = new SpinnerAdapter(view.getContext(), listFaculties);
        fuculties_spinner.setAdapter(adapter);

        fuculties_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (!((String) parent.getItemAtPosition(pos)).equals("Выберите факультет")) {
                    JSON_ALL_PARSER test = new JSON_ALL_PARSER(mapForms, listForms, form_spinner_a);
                    test.sendPost(mapFaculties.get((String) parent.getItemAtPosition(pos)), "",
                            "", "", "__id.22.main.inpFldsA.GetForms");
                }
            }
        });
        fuculties_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                listForms.clear();
                mapForms.clear();
                listForms.add("Выберите форму обучения");
                form_spinner_a.notifyDataSetChanged();

                listCourse.clear();
                mapCourse.clear();
                listCourse.add("Выберите курс");
                course_spinner_a.notifyDataSetChanged();

                listGroup.clear();
                mapGroup.clear();
                listGroup.add("Выберите группу");
                group_spinner_a.notifyDataSetChanged();
                return false;
            }
        });
        new HTML_FUCULTY_PARSER(mapFaculties, listFaculties, adapter);
    }


}
