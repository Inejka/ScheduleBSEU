package com.example.schedulebseu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class ScheduleChooseActivity extends SingleFragmentActivity {

    boolean test = false;

    @Override
    protected Fragment createFragment() {
        return new ScheduleChooseFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}