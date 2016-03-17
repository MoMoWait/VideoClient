package momo.cn.edu.fjnu.videoclient.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import momo.cn.edu.fjnu.androidutils.base.BaseActivity;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.service.LocationListener;

/**
 * APP基本Activity,所有的Activity'都要继承此类
 * Created by GaoFei on 2016/3/8.
 */
public class AppBaseActivity extends BaseActivity {
    /**
     * 定位客户端
     */
    private AMapLocationClient mLocationClient;
    /**
     * 定位选项
     */
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化操作栏
        initActionBar();
        //初始化定位
        initLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止定位
        mLocationClient.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注意资源释放
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    /**
     * 初始化定位
     */
    public void initLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位选项
        mLocationClient.setLocationOption(mLocationOption);
        // 设置是否需要显示地址信息
        mLocationOption.setNeedAddress(true);
        //设置定位间隔时间
        mLocationOption.setInterval(AppConst.LOCATE_TIME);
        //设置是否为单次定位
        mLocationOption.setOnceLocation(false);
        //设置定位监听器
        mLocationClient.setLocationListener(new LocationListener());
    }

    /**
     * 初始化操作栏
     */
    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //设置操作栏的背景颜色
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //设置操作栏的布局
            actionBar.setCustomView(R.layout.actionbar_custom);
            //设置操作栏背景
            actionBar.setBackgroundDrawable(new ColorDrawable(ResourceUtils.getColor(R.color.actionbar_background)));
        }
    }

}
