package net.wishwall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.wishwall.R;
import net.wishwall.adapter.ProvinceAdapter;
import net.wishwall.domain.ProvCityAreaDTO;
import net.wishwall.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProvCityAreaActivity extends BaseActivity
					implements View.OnClickListener{
    
	private TextView back;
	private TextView save;
	private String proUrl;
	private String cityUrl;
	private String disUrl;
	private String proviceName;
	private String cityName;
	private String areaName;
	private String proCode;
	private String cityCode;
	private String areaCode;
	private Spinner province_spinner;
	private Spinner city_spinner;
	private Spinner area_spinner;
	private List<ProvCityAreaDTO.ResultBean> provinceLists = new ArrayList<ProvCityAreaDTO.ResultBean>();
	private List<ProvCityAreaDTO.ResultBean> citysLists;
	private List<ProvCityAreaDTO.ResultBean> areaLists;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.pro_city_area);
		initViewUI();
	}

	/**
	 * 初始化布局、控件
	 */
	private void initViewUI() {
		back = (TextView)findViewById(R.id.pro_city_area_back);
		save = (TextView)findViewById(R.id.pro_city_area_save);
		province_spinner = (Spinner) findViewById(R.id.province_spinner);
		city_spinner = (Spinner) findViewById(R.id.city_spinner);
        area_spinner = (Spinner) findViewById(R.id.county_spinner);
		city_spinner = (Spinner) findViewById(R.id.city_spinner);
		back.setOnClickListener(this);
		save.setOnClickListener(this);
		province_spinner.setOnItemSelectedListener(new ProviceSpinnerItemClickListener());
		city_spinner.setOnItemSelectedListener(new CitySpinnerItemClickListener());
        area_spinner.setOnItemSelectedListener(new DisSpinnerItemClickListener());
		//获取所有省

        ApiClient.findAllProv(new Callback<ProvCityAreaDTO>() {
            @Override
            public void onResponse(Call<ProvCityAreaDTO> call, Response<ProvCityAreaDTO> response) {
                ProvCityAreaDTO body = response.body();
				if(body.getCode() == 200){
					provinceLists = body.getResult();
                    ProvinceAdapter province_adapter = new ProvinceAdapter(ProvCityAreaActivity.this, provinceLists);
					province_spinner.setAdapter(province_adapter);
					province_spinner.setPrompt("请选择省份");
				}
            }

            @Override
            public void onFailure(Call<ProvCityAreaDTO> call, Throwable t) {
                    t.printStackTrace();
            }
        });

	}	
	/**
	 * 省下拉列表item点击监听
	 */
	class ProviceSpinnerItemClickListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {		
			proviceName = provinceLists.get(position).getName();
			proCode = provinceLists.get(position).getCode()+""	;
			//获取当前省下的所有城市
		//	jsonRequestUtil.getStringObjects(newUrl, new CityListener(), new CityErrorListener());
            ApiClient.findProvCity(proCode, new Callback<ProvCityAreaDTO>() {
                @Override
                public void onResponse(Call<ProvCityAreaDTO> call, Response<ProvCityAreaDTO> response) {
                    ProvCityAreaDTO body = response.body();
                    if(body.getCode() == 200){
                        citysLists = new ArrayList<ProvCityAreaDTO.ResultBean>();
                        citysLists = body.getResult();
                        ProvinceAdapter province_adapter  = new ProvinceAdapter(ProvCityAreaActivity.this, citysLists);
                        city_spinner.setAdapter(province_adapter);
                        city_spinner.setPrompt("请选择城市");
                    }
                }

                @Override
                public void onFailure(Call<ProvCityAreaDTO> call, Throwable t) {
							t.printStackTrace();
                }
            });
		}		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}	
	}
	/**
	 * 市下拉列表item点击监听
	 */
	class CitySpinnerItemClickListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {		
			cityName = citysLists.get(position).getName();
			cityCode = citysLists.get(position).getCode()+""; 						

			//获取当前城市下的所有地区
			ApiClient.findCityArea(cityCode, new Callback<ProvCityAreaDTO>() {
                @Override
                public void onResponse(Call<ProvCityAreaDTO> call, Response<ProvCityAreaDTO> response) {
                    ProvCityAreaDTO body = response.body();
                    if(body.getCode() == 200){
                        areaLists = new ArrayList<ProvCityAreaDTO.ResultBean>();
                        areaLists = body.getResult();
                        ProvinceAdapter province_adapter  = new ProvinceAdapter(ProvCityAreaActivity.this, areaLists);
                        area_spinner.setAdapter(province_adapter);
                        area_spinner.setPrompt("请选择地区");
                    }
                }
                @Override
                public void onFailure(Call<ProvCityAreaDTO> call, Throwable t) {
						t.printStackTrace();
                }
            });
		}		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}	
	}
	/**
	 * 区下拉列表item点击监听
	 */
	class DisSpinnerItemClickListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {		
			areaName = areaLists.get(position).getName();
			areaCode = areaLists.get(position).getCode()+"";
		}		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {}	
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pro_city_area_back:
                this.finish();
                break;
            case R.id.pro_city_area_save:
                Toast.makeText(
                        this,
                        "省:"+proviceName+":"+proCode+ 
                        "市:"+cityName+":"+cityCode+
                        "区:"+areaName+":"+areaCode,Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra("provName",proviceName);
				intent.putExtra("provCode",proCode);
				intent.putExtra("cityName",cityName);
				intent.putExtra("cityCode",cityCode);
				intent.putExtra("areaName",areaName);
				intent.putExtra("areaCode",areaCode);
				setResult(RESULT_OK,intent);
				finish();
                break;
        }
    }
}