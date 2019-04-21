package com.sss.wearable.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.SeekBar;

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

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        position = args.getInt("position");
        mode = args.getString("mode");
        if (mode != null && mode.equals("edit")) {
            previousColor = args.getInt("color");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        color = Color.rgb(0, 0, 0);

        builder.setView(inflater.inflate(R.layout.dialog_color, null))
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ColorPickerDialogFragment.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();
                        bdl.putInt("position", position);

                        // -1 will not change the background color even though it is a valid color.
                        if (color == -1) {
                            color = -2;
                        }

                        bdl.putInt("color", color);
                        msg.setData(bdl);
                        InterestsActivity.handler.sendMessage(msg);
                    }
                });

        if (mode.equals("edit")) {
            color = previousColor;

            builder.setNegativeButton("delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Message msg = Message.obtain();
                    Bundle bdl = new Bundle();
                    bdl.putInt("position", position);
                    bdl.putInt("color", 0);
                    msg.setData(bdl);
                    InterestsActivity.handler.sendMessage(msg);
                }
            });
        }

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        colorView = getDialog().findViewById(R.id.colorView);
        sbRed = getDialog().findViewById(R.id.sbRed);
        sbGreen = getDialog().findViewById(R.id.sbGreen);
        sbBlue = getDialog().findViewById(R.id.sbBlue);
        sbRed.setOnSeekBarChangeListener(sbListener);
        sbGreen.setOnSeekBarChangeListener(sbListener);
        sbBlue.setOnSeekBarChangeListener(sbListener);
        if (mode.equals("edit")) {
            sbRed.setProgress(Color.red(previousColor));
            sbGreen.setProgress(Color.green(previousColor));
            sbBlue.setProgress(Color.blue(previousColor));
            colorView.setBackgroundPaint(previousColor);
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
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}

