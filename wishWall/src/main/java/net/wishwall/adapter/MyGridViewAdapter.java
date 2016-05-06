package net.wishwall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.wishwall.R;

import java.util.List;
import java.util.Map;

public class MyGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private List<Map<String,String>> hotCityLists;
	
	public MyGridViewAdapter(Context context,List<Map<String,String>> list) {
		this.mContext = context;
		this.hotCityLists = list;
	}
	public MyGridViewAdapter(Context context){
		this.mContext = context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.choose_citys_items, null);
		}			
		return convertView;
	}
	
}
