package com.airtec.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GenericExpandableListAdaptor<T1, T2> extends BaseExpandableListAdapter {
	public ArrayList<T1> groups;
	public ArrayList<ArrayList<T2>> children;
	public ArrayList<ArrayList<T2>> originalChildrenList;
	public ArrayList<T1> originalGroups;
	public Context context;
	int groupLayoutResourceId, childLayoutResourceId;

	/**
	 * R.id.optionImageView,R.drawable.arrow_up,R.drawable.arrow_down
	 */
	public int[] resources;
	@SuppressWarnings("rawtypes")
	FTADataBinder groupDatabinder, childDatabinder;
	boolean boolExpandexpandList = false;
	public boolean setPadding;

	public GenericExpandableListAdaptor(ArrayList<T1> groups, ArrayList<ArrayList<T2>> children, Context context, int groupLayoutResourceId, int childLayoutResourceId, int[] resources, FTADataBinder<T1> groupDatabinder,
                                        FTADataBinder<T2> childDatabinder) {
		super();
		this.groups = groups;
		this.originalGroups = new ArrayList<T1>();
		this.originalGroups.addAll(groups);
		this.children = children;
		this.originalChildrenList = new ArrayList<ArrayList<T2>>();
		this.originalChildrenList.addAll(children);
		this.context = context;
		this.groupDatabinder = groupDatabinder;
		this.childDatabinder = childDatabinder;
		this.groupLayoutResourceId = groupLayoutResourceId;
		this.childLayoutResourceId = childLayoutResourceId;
		this.resources = resources;
	}

	public GenericExpandableListAdaptor(ArrayList<T1> groups, ArrayList<ArrayList<T2>> children, Context context, int groupLayoutResourceId, int childLayoutResourceId, int[] resources, FTADataBinder<T1> groupDatabinder,
                                        FTADataBinder<T2> childDatabinder, boolean boolExpandexpandList) {
		this(groups, children, context, groupLayoutResourceId, childLayoutResourceId, resources, groupDatabinder, childDatabinder);
		this.boolExpandexpandList = boolExpandexpandList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return (children.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(childLayoutResourceId, parent, false);
		}

		T2 model = this.children.get(groupPosition).get(childPosition);
		if (children.get(groupPosition).indexOf(model) == 0) {
			setPadding = true;
		} else {
			setPadding = false;
		}
		if (convertView != null)
			childDatabinder.bind(model, convertView,groupPosition);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(groupLayoutResourceId, parent, false);
		if (resources != null) {
			View imageView = convertView.findViewById(resources[0]);
			if (imageView != null) {
				ImageView image = (ImageView) imageView;

				int imageResourceId = isExpanded ? resources[1] : resources[2];
				image.setImageResource(imageResourceId);

				image.setVisibility(View.VISIBLE);
			}
		}

		T1 model = this.groups.get(groupPosition);

		groupDatabinder.bind(model, convertView, groupPosition);

		if (boolExpandexpandList) {
			ExpandableListView eLV = (ExpandableListView) parent;
			eLV.expandGroup(groupPosition);
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public void update(ArrayList<T1> newGroups, ArrayList<ArrayList<T2>> newChildren) {
		groups = newGroups;
		children = newChildren;
	}

}