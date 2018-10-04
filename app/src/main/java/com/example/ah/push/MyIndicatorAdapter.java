package com.example.ah.push;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyIndicatorAdapter extends ArrayAdapter<MyMainListItem>{
    private Context mContext;
    private List<MyMainListItem> deviceList = new ArrayList<>();

    public MyIndicatorAdapter(@NonNull Context context, @LayoutRes ArrayList<MyMainListItem> list) {
        super(context, 0 , list);
        mContext = context;
        deviceList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.my_listitem,parent,false);

        MyMainListItem currentDevice = deviceList.get(position);

        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
        image.setImageResource(currentDevice.getmImageDrawable());

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(currentDevice.getmName());

        return listItem;
    }
}
