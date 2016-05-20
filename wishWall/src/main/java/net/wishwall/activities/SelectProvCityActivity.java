package net.wishwall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import net.wishwall.R;
import net.wishwall.adapter.ExpandableListViewAdapter;
import net.wishwall.domain.ProvCityAreaDTO;
import net.wishwall.service.ApiClient;
import net.wishwall.views.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 
 *@Description: 城市选择 、定位
 *@author panRongFu pan@ipushan.com
 *@date 2015年9月29日 下午4:39:38
 */
public class SelectProvCityActivity extends BaseActivity  {

	private TextView choose_city_back;
	private TextView gps_local;
	private ExpandableListView expandableListView;
	private ExpandableListViewAdapter expandableListViewAdapter;
	private CustomProgressDialog progressDialog ;
	private List<ProvCityAreaDTO.ResultBean> provinceLists = new ArrayList<ProvCityAreaDTO.ResultBean>();
    private List<ProvCityAreaDTO.ResultBean> allCityList = new ArrayList<ProvCityAreaDTO.ResultBean>();
	private List<List<ProvCityAreaDTO.ResultBean>> pCitysLists = new ArrayList<List<ProvCityAreaDTO.ResultBean>>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.choose_citys);
		initViewUI();
	}

	/**
	 * 初始化控件
	 */
	private void initViewUI() {
		Intent intent = getIntent();
		String local = intent.getStringExtra("location");
        progressDialog = CustomProgressDialog.createDialog(this);
		progressDialog.setMessage("加载中...").show();
		choose_city_back = (TextView) findViewById(R.id.choose_city_back);
		gps_local = (TextView) findViewById(R.id.gps_local);
		gps_local.setOnClickListener(new ChooseCityBtnClickListener());
		choose_city_back.setOnClickListener(new ChooseCityBtnClickListener());
		expandableListView = (ExpandableListView) findViewById(R.id.choose_city_list);
		expandableListView.requestFocusFromTouch();
		expandableListView.setOnChildClickListener(new ChildItemsClickListener());

		gps_local.setText(local==null?"广州市":local);

        ApiClient.findAllProv(new Callback<ProvCityAreaDTO>() {
            @Override
            public void onResponse(Call<ProvCityAreaDTO> call, Response<ProvCityAreaDTO> response) {
                ProvCityAreaDTO body = response.body();
                if(body.getCode() == 200){
                    provinceLists = body.getResult();
                    ApiClient.getAllCity(new Callback<ProvCityAreaDTO>() {
                        @Override
                        public void onResponse(Call<ProvCityAreaDTO> call, Response<ProvCityAreaDTO> response) {
                            ProvCityAreaDTO cityBody = response.body();
                            if(cityBody.getCode() == 200){
                                allCityList = cityBody.getResult();
                                for(ProvCityAreaDTO.ResultBean prov : provinceLists){
                                    List<ProvCityAreaDTO.ResultBean> cityList = new ArrayList<ProvCityAreaDTO.ResultBean>();
                                    for(ProvCityAreaDTO.ResultBean city : allCityList){
                                        if(prov.getCode() == city.getParentId()){
                                            cityList.add(city);
                                        }
                                    }
                                    pCitysLists.add(cityList);
                                }
                                expandableListViewAdapter = new ExpandableListViewAdapter(SelectProvCityActivity.this,provinceLists,pCitysLists);
                                expandableListView.setAdapter(expandableListViewAdapter);
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Call<ProvCityAreaDTO> call, Throwable t) {
                                     t.printStackTrace();
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<ProvCityAreaDTO> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
	}

	class ChooseCityBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.choose_city_back:
				SelectProvCityActivity.this.finish();
				break;
			}
		}
	}

	class ChildItemsClickListener implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
			String cityName = (String)expandableListViewAdapter.getChild(groupPosition,childPosition);
			String cityCode = pCitysLists.get(groupPosition).get(childPosition).getCode()+"";
			if(cityName.contains("辖")||cityName.contains("县")){
                cityName = provinceLists.get(groupPosition).getName();
			}
            //localSputil.setKeyValue("cityCode", cityCode);
            //localSputil.setKeyValue("cityName", cityName);
			Intent intentCity = new Intent();
            intentCity.putExtra("cityName", cityName);
			SelectProvCityActivity.this.setResult(RESULT_OK, intentCity);
			SelectProvCityActivity.this.finish();
			return true;
		}
	}
}
