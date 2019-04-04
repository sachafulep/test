package com.sss.test;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class InterestsAdapter extends BaseAdapter {
    private List<String> interests;
    private Context context;

    InterestsAdapter(List<String> interests, Context context) {
        this.interests = interests;
        this.context = context;
    }

    @Override
    public int getCount() {
        return interests.size();
    }

    @Override
    public Object getItem(int position) {
        return interests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(context);
        Button button = new Button(context);
        button.setText(interests.get(position));
        button.setBackgroundResource(R.drawable.button_inactive);
        button.setAllCaps(false);
        layout.setGravity(Gravity.CENTER);
        layout.addView(button);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        return layout;
    }
}
