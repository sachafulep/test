package com.sss.wearable.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.sss.wearable.Classes.Interest;

import java.util.List;

public class InterestsAdapter extends BaseAdapter {
    public List<Interest> interests;

    public InterestsAdapter(List<Interest> interests) {
        this.interests = interests;
    }

    @Override
    public int getCount() {
        return interests.size();
    }

    @Override
    public Interest getItem(int position) {
        return interests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return interests.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return interests.get(position).getView();
    }
}
