package net.wishwall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.ProvCityAreaDTO;

import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
	private List<ProvCityAreaDTO.ResultBean> provinceLists ;
	private List<List<ProvCityAreaDTO.ResultBean>> pCitysLists;
	private Context mContext;
	
	public ExpandableListViewAdapter(Context context, List<ProvCityAreaDTO.ResultBean> provinces, List<List<ProvCityAreaDTO.ResultBean>> citys ){
		this.mContext = context;
		this.provinceLists = provinces;
		this.pCitysLists = citys;
		
	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return provinceLists.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		Log.i("getChildrenCount", pCitysLists.get(groupPosition).size()+"");
		return pCitysLists.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return provinceLists.get(groupPosition).getName();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.i("getChild", pCitysLists.get(groupPosition).get(childPosition).getName());
		return pCitysLists.get(groupPosition).get(childPosition).getName();
	}

	@Override
	public long getGroupId(int groupPosition) {
	
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Log.i("getChildId", groupPosition+"@@@@@@@"+childPosition+"");
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_group_view, null);
		}
		TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
		groupName.setText(getGroup(groupPosition).toString());
		ImageView indicator = (ImageView) convertView.findViewById(R.id.group_indicator);
		if (isExpanded) {
			indicator.setImageResource(R.mipmap.indicator_expanded);
		} else {
			indicator.setImageResource(R.mipmap.indicator_unexpanded);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_view, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.contact_list_item_name);
		tv.setText(getChild(groupPosition, childPosition).toString());			
		return convertView;
	}


}
