package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.VideoRtUploadFragment;


/**
 * 视频实时上传页面
 * Created by GaoFei on 2016/3/13.
 */
public class VideoRtUploadActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new VideoRtUploadFragment();
    }
}
