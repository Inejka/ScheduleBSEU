package com.example.schedulebseu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    List<String> weeks;
    LayoutInflater inflator;

    public SpinnerAdapter(Context context, List<String> weeks) {
        inflator = LayoutInflater.from(context);
        this.weeks = weeks;
    }

    @Override
    public int getCount() {
        return weeks.size();
    }

    @Override
    public Object getItem(int position) {
        return weeks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflator.inflate(R.layout.spinner_item, null);
        TextView tv = convertView.findViewById(R.id.tvCust);
        if(weeks.size()>position)
        tv.setText(weeks.get(position)); else tv.setText("Поздравляю,но это было не так уж и сложно,не так ли?");
        return convertView;
    }
}
