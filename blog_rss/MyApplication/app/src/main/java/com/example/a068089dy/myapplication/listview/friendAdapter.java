package com.example.a068089dy.myapplication.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.a068089dy.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 068089dy on 2016/11/4.
 */
public class friendAdapter extends ArrayAdapter<friend> {
    private int resourceId;
    public friendAdapter(Context context, int textViewResourceId, List<friend> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    public View getView(int position, View converView, ViewGroup parent){
        friend frid = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView friendblog = (TextView) view.findViewById(R.id.blog);
        TextView friendartical = (TextView) view.findViewById(R.id.artical);
        friendblog.setText(frid.getName());
        friendartical.setText(frid.getArtical());
        return view;
    }
}
