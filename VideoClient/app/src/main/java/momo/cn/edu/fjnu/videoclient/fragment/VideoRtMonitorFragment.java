package momo.cn.edu.fjnu.videoclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.VideoView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.data.AppConst;

/**
 * 视频实时监控页面
 * Created by GaoFei on 2016/3/13.
 */
@ContentView(R.layout.fragment_video_rt_monitor)
public class VideoRtMonitorFragment extends BaseFragment{

    @ViewInject(R.id.video_monitor)
    private VideoView mVideoMonitor;

    private int mUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void initView() {
        //保持屏幕常亮
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void initData() {
        mUserId =getActivity().getIntent().getIntExtra(AppConst.USER_ID, 1);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mVideoMonitor.setVideoPath(AppConst.RTSP_HEAD + mUserId);
        mVideoMonitor.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoMonitor.stopPlayback();
    }


}
