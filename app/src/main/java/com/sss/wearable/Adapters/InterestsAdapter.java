package com.sss.wearable.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import com.sss.wearable.Classes.Interest;
import com.sss.wearable.Fragments.ColorPickerDialogFragment;
import com.sss.wearable.R;

import java.util.ArrayList;
import java.util.List;

public class InterestsAdapter extends BaseAdapter {
    public List<Interest> interests;
    private Context context;
    private FragmentManager fragmentManager;
    private List<Button> interestButtons = new ArrayList<>();
    private int counter = 0;

    public InterestsAdapter(List<Interest> interests,
                            Context context,
                            FragmentManager fragmentManager
    ) {
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
        Interest interest = interests.get(position);
        setInterestButtonStyling(layout, button, interest);

        final Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("name", interest.getName());
        if (interests.get(position).getColor() == 0) {
            bundle.putString("mode", "set");
        } else {
            bundle.putString("mode", "edit");
            bundle.putInt("color", interests.get(position).getColor());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 5) {
                    ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
                    dialog.setArguments(bundle);
                    dialog.show(fragmentManager, "ColorPickerDialog");
                }
            }
        });

        interestButtons.add(position, button);
        return layout;
    }

    private void setInterestButtonStyling(LinearLayout layout, Button button, Interest interest) {
        button.setText(interest.getName());

        if (interest.getColor() == 0) {
            button.setBackgroundResource(R.drawable.button_interest_);
        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(90);
            shape.setColor(interest.getColor());
            button.setBackground(shape);
            button.setTextColor(getTextColor(interest.getColor()));
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
    }

    public void setInterestColor(int position, int color) {
        interests.get(position).setColor(color);
        counter++;
    }

    public void moveInterestToFront(int position) {
        Interest interest = interests.get(position);
        interest.setPosition(position);
        interests.remove(interest);
        interests.add(0, interest);
    }

    public void resetInterestPosition(int position) {
        Interest interest = interests.get(position);
        interests.remove(interest);
        interests.add(interest.getPosition(), interest);
    }

    private int getTextColor(int color) {
        double temp;

        temp = Color.red(color) / 255.0;
        double red = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        temp = Color.green(color) / 255.0;
        double green = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        temp = Color.blue(color) / 255.0;
        double blue = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        double L = 0.2126 * red + 0.7152 * green + 0.0722 * blue;

        return L > 0.179 ? Color.parseColor("#000000") :
                context.getColor(R.color.background);
    }
}
