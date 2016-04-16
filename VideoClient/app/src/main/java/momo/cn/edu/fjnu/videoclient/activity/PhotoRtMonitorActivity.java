package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.PhotoRtMonitorFragment;


/**
 * 视频录制Activity
 * Created by GaoFei on 2016/3/12.
 */
public class PhotoRtMonitorActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new PhotoRtMonitorFragment();
    }
}
