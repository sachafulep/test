package com.sss.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.SeekBar;

public class ColorPickerDialogFragment extends DialogFragment {
    ColorView colorView;
    SeekBar sbRed;
    SeekBar sbGreen;
    SeekBar sbBlue;
    int position;
    int color = 0;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        position = args.getInt("position");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_color, null))
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Message msg = Message.obtain();
                        Bundle bdl = new Bundle();
                        bdl.putInt("position", position);
                        bdl.putInt("color", color);
                        msg.setData(bdl);
                        InterestsActivity.handler.sendMessage(msg);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ColorPickerDialogFragment.this.getDialog().cancel();
                    }
                });

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
    }

    private SeekBar.OnSeekBarChangeListener sbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.argb(
                    100,
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            );

            colorView.setBackgroundPaint(
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}

