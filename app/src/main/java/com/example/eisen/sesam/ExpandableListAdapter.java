package com.example.eisen.sesam;

import android.content.Context;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public final static int TIMEWINDOW_SKILLS=6;

    private Context _context;
    private List<String> _listDataHeader; //title
    private List<TimeWindow> _listDataChild;    //timewindows
    private Button btn;

    public ExpandableListAdapter(Context context, List<TimeWindow>  content) {
        this._context = context;
        this._listDataHeader = generateTimeWindowsTitleList(content);
        this._listDataChild = content;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        TimeWindow timeWindow = _listDataChild.get(groupPosition);

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
                return timeWindow.getOpenDuration()+ " Sekunde(n) öffnen";
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
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
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
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.general_settings_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

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

    public static ArrayList<String> generateTimeWindowsTitleList(List<TimeWindow> timeWindows){
        final ArrayList<String> titleList = new ArrayList<>();
        timeWindows.stream().forEach(timeWindow -> titleList.add(timeWindow.getTitle()));
        return titleList;
    }

}