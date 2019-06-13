package com.sss.wearable.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sss.wearable.InterestsActivity;
import com.sss.wearable.R;
import com.sss.wearable.Views.ColorView;

public class ColorPickerDialogFragment extends DialogFragment {
    private ColorView colorView;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;
    private int position;
    private int color;
    private int previousColor;
    private String mode;
    private String name;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        name = args.getString("name");
        mode = args.getString("mode");
        if (mode != null && mode.equals("edit")) {
            previousColor = args.getInt("color");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        color = Color.rgb(0, 0, 0);

        final Message msg = Message.obtain();
        final Bundle bdl = new Bundle();

        bdl.putString("name", name);
        bdl.putString("mode", mode);

        builder.setView(inflater.inflate(R.layout.dialog_color, null))
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ColorPickerDialogFragment.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // -1 will not change the background color even though it is a valid color.
                        if (color == -1) {
                            color = -2;
                        }

                        bdl.putInt("color", color);
                        InterestsActivity.handler.sendMessage(msg);
                    }
                });

        if (mode.equals("edit")) {
            builder.setNegativeButton("delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    bdl.putInt("color", 0);
                    InterestsActivity.handler.sendMessage(msg);
                }
            });
        }

        msg.setData(bdl);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        LinearLayout container = getDialog().findViewById(R.id.colorViewContainer);
        TextView tvInstructions1 = getDialog().findViewById(R.id.tvInstructions1);
        TextView tvInstructions2 = getDialog().findViewById(R.id.tvInstructions2);
        colorView = new ColorView(requireActivity(), null);
        colorView.setName(name, getTextColor(previousColor));
        container.addView(colorView);

        sbRed = getDialog().findViewById(R.id.sbRed);
        sbGreen = getDialog().findViewById(R.id.sbGreen);
        sbBlue = getDialog().findViewById(R.id.sbBlue);
        sbRed.setOnSeekBarChangeListener(sbListener);
        sbGreen.setOnSeekBarChangeListener(sbListener);
        sbBlue.setOnSeekBarChangeListener(sbListener);
        if (mode.equals("edit")) {
            tvInstructions1.setText(R.string.dialogInstructionEdit1);
            tvInstructions2.setText(R.string.dialogInstructionEdit2);
            sbRed.setProgress(Color.red(previousColor));
            sbGreen.setProgress(Color.green(previousColor));
            sbBlue.setProgress(Color.blue(previousColor));
            colorView.setBackgroundPaint(previousColor);
        } else {
            tvInstructions1.setText(R.string.dialogInstructionSet1);
            tvInstructions2.setText(R.string.dialogInstructionSet2);
        }
    }

    private SeekBar.OnSeekBarChangeListener sbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.rgb(
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            );

            colorView.setBackgroundPaint(color);
            colorView.setName(name, getTextColor(color));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

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
                Color.parseColor("#EEEEEE");
    }
}