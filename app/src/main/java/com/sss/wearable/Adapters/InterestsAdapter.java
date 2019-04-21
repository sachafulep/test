package com.sss.wearable.Adapters;

import android.content.Context;
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
            button.setBackgroundResource(R.drawable.button_inactive);
        } else {
            button.setBackgroundColor(interest.getColor());
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
}
