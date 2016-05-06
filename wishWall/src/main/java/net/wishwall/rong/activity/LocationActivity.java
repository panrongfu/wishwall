package net.wishwall.rong.activity;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;

import net.wishwall.App;
import net.wishwall.R;

import io.rong.message.LocationMessage;

/**
 * Created by DragonJ on 14/11/21.
 */

public class LocationActivity extends BaseActivity
        implements AMapLocationListener,LocationSource,View.OnClickListener {


    Button mButton = null;
    LocationMessage mMsg;
   // Handler mHandler;
  //  Handler mWorkHandler;
    TextView mTitle;
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    /**
     * 当前地图地址的poi
     */
    private HandlerThread mHandlerThread;

    private final static int RENDER_POI = 1;
    private final static int SHWO_TIPS = 2;

    @Override
    /**
     *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_soso_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mButton = (Button)findViewById(android.R.id.button1);
        mButton.setOnClickListener(this);
        init();

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.moveCamera(CameraUpdateFactory.zoomIn());
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    ///    mMsg = LocationMessage.obtain(poiItem.getLat() / 1E6,
    //poiItem.getLng() / 1E6, poiItem.name, uri);
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                Uri uri= Uri
                        .parse("http://lbs.amap.com/api/webservice/reference/staticmaps/").buildUpon()
                        .appendQueryParameter("size","240*240")
                        .appendQueryParameter("key","6481d9d2bcb94d6b790e495ebc62fbbe")
                        .appendQueryParameter("zoom","16")
                        .appendQueryParameter("center",amapLocation.getLatitude()/1E6+","+amapLocation.getLongitude()/1E6)
                        .build();
                mMsg = LocationMessage.obtain(
                        amapLocation.getLatitude(),
                        amapLocation.getLongitude(),
                        amapLocation.getPoiName(),
                        uri);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," +amapLocation.getErrorCode()+ ": "
                                + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {

        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }

    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onClick(View v) {
        if (mMsg != null) {
            App.getLastLocationCallback().onSuccess(mMsg);
          //  DemoContext.getInstance().setLastLocationCallback(null);
            LocationActivity.this.finish();
        } else {
            App.getLastLocationCallback().onFailure("定位失败");
        }
    }
}


