package com.example.eisen.sesam.userinterface;

import android.content.Context;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.eisen.sesam.R;
import com.example.eisen.sesam.data.TimeWindow;

import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public final static int TIMEWINDOW_SKILLS=5;

    private Context _context;
    private List<TimeWindow> timeWindows;    //timeWindows

    public ExpandableListAdapter(Context context, List<TimeWindow>  content) {
        this._context = context;
        this.timeWindows = content;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        TimeWindow timeWindow = timeWindows.get(groupPosition);

        switch(childPosititon){
            case 0:
                return "am " + timeWindow.getFromDate();
            case 1:
                return "bis " + timeWindow.getTillDate();
            case 2:
                return "ab " + timeWindow.getFromTime() + " Uhr";
            case 3:
                return "bis "+ timeWindow.getTillTime() + " Uhr";
            case 4:
                return "Klingeln: " + timeWindow.getRingNumber() + " Mal";
            default:
                return "?";
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.general_settings_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return TIMEWINDOW_SKILLS;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.timeWindows.get(groupPosition).getTitle();
    }

    @Override
    public int getGroupCount() {
        return this.timeWindows.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.general_settings_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        Button button = (Button) convertView.findViewById(R.id.buttonDeleteWindow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bttn",groupPosition+"");
                timeWindows.remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public List<TimeWindow> getTimeWindows() {
        return timeWindows;
    }

    public void setTimeWindows(List<TimeWindow> timeWindows) {
        this.timeWindows = timeWindows;
        notifyDataSetChanged();
    }

    public void clearTimeWindows() {
        this.timeWindows.clear();
        notifyDataSetChanged();
    }

    public void addTimeWindow(TimeWindow timeWindow) {
        this.timeWindows.add(timeWindow);
        notifyDataSetChanged();
    }

}