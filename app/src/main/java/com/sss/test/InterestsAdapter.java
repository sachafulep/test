package com.sss.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterestsAdapter extends BaseAdapter {
    private List<String> interests;
    private Context context;
    private FragmentManager fragmentManager;
    private List<Button> interestButtons = new ArrayList<>();
    public SparseIntArray colors = new SparseIntArray();

    InterestsAdapter(List<String> interests, Context context, FragmentManager fragmentManager) {
        this.interests = interests;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return interests.size();
    }

    @Override
    public Button getItem(int position) {
        return interestButtons.get(position);
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

        int color;
        if ((color = colors.get(position, -1)) != -1) {
            button.setBackgroundColor(color);
        } else {
            button.setBackgroundResource(R.drawable.button_inactive);
        }

        button.setAllCaps(false);
        layout.setGravity(Gravity.CENTER);
        layout.addView(button);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        final Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
                dialog.setArguments(bundle);
                dialog.show(fragmentManager, "ColorPickerDialog");
            }
        });

        interestButtons.add(position, button);

        return layout;
    }
}
