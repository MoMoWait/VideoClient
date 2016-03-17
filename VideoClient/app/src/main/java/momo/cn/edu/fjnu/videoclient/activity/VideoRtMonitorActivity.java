package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.VideoRtMonitorFragment;


/**
 * 视频实时监控页面
 * Created by GaoFei on 2016/3/13.
 */
public class VideoRtMonitorActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new VideoRtMonitorFragment();
    }
}
