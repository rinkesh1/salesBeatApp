package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandableMenuAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> groupTitles;
    private Map<String, List<MenuItem>> itemMap;

    public ExpandableMenuAdapter(Context context, Map<String, List<MenuItem>> itemMap) {
        this.context = context;
        this.itemMap = itemMap;
        this.groupTitles = new ArrayList<>(itemMap.keySet());
    }

    @Override
    public int getGroupCount() {
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String group = groupTitles.get(groupPosition);
        return itemMap.get(group).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String group = groupTitles.get(groupPosition);
        return itemMap.get(group).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Group view
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        String groupTitle = (String) getGroup(groupPosition);
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
//        }
//
//        TextView groupTitleView = convertView.findViewById(R.id.tvGroupTitle);
//        groupTitleView.setText(groupTitle);
//
//        return convertView;
//    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        }

        TextView groupTitleView = convertView.findViewById(R.id.tvGroupTitle);
        ImageView arrow = convertView.findViewById(R.id.ivArrow);

        groupTitleView.setText(groupTitles.get(groupPosition));

        // Rotate arrow based on expand state
        arrow.setRotation(isExpanded ? 90 : 0); // right arrow when collapsed, down when expanded

        return convertView;
    }


    // Child view (no States shown)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        MenuItem item = (MenuItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false);
        }

        TextView menuNameView = convertView.findViewById(R.id.tvMenuName);
        menuNameView.setText(item.menuName);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
