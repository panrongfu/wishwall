package net.wishwall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.domain.ProvCityAreaDTO;

import java.util.List;


public class ProvinceAdapter extends BaseAdapter {
	private Context mContext;
	private List<ProvCityAreaDTO.ResultBean> proLists;

	public ProvinceAdapter(Context context,List<ProvCityAreaDTO.ResultBean> lists) {
		this.mContext = context;
		this.proLists = lists;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return proLists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (null == convertView) {
			vHolder = new ViewHolder();			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.prov_city_area_items, null);
			convertView.setTag(vHolder);
		}else{			
			vHolder = (ViewHolder)convertView.getTag();
		}
		vHolder.mTitel = (TextView)convertView.findViewById(R.id.title_name);
		vHolder.mTitel.setText(proLists.get(position).getName());
		return convertView;
	}
	public class ViewHolder {		
		public TextView mTitel;							       
	}
}
