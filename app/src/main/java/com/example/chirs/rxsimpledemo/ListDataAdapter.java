package com.example.chirs.rxsimpledemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.ActivityClass;

import java.util.List;

/**
 * Created by jianjianhong on 2016/5/25.
 */
public class ListDataAdapter extends BaseAdapter {

    private List<ActivityClass> dataList;
    private Context context;

    public ListDataAdapter(Context context, List<ActivityClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
        TextView nameTv = (TextView)itemView.findViewById(R.id.item_name);

        ActivityClass activityClass = dataList.get(position);
        nameTv.setText(activityClass.getActivityName());

        return itemView;
    }
}
