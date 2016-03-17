package momo.cn.edu.fjnu.videoclient.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.UUID;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ValidUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.data.SharedKeys;

/**
 * 视频录制页面
 */

@ContentView(R.layout.fragment_video_recorder)
public class VideoRecorderFragment extends BaseFragment {

    @ViewInject(R.id.img_btn_record)
    private ImageButton imgBtnRecord;

    @ViewInject(R.id.img_btn_stop)
    private ImageButton imgBtnStop;

    @ViewInject(R.id.view_surface_display)
    private SurfaceView viewSurfaceDisplay;

    private File mVideoFile;
    private MediaRecorder mRecorder;
    private boolean mIsRecording;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);

    }


    @Override
    public void initView() {
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        imgBtnStop.setEnabled(false);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        imgBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(ValidUtils.isEmpty(StorageUtils.getDataFromSharedPreference(SharedKeys.CAMERA_WIDTH))){
                        Camera tmpCam = Camera.open();
                        Camera.Parameters params = tmpCam.getParameters();
                        final List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
                        int i = prevSizes.size()-1;
                        int width = prevSizes.get(i / 2).width;
                        int height = prevSizes.get(i / 2).height;
                        StorageUtils.saveDataToSharedPreference(SharedKeys.CAMERA_WIDTH, "" + width);
                        StorageUtils.saveDataToSharedPreference(SharedKeys.CAMERA_HEIGHT, "" + height);
                        tmpCam.release();
                       // tmpCam = null;
                    }
                    File dirFile = new File(AppConst.VIDEO_SAVE_DIRECTORY);
                    if (!dirFile.exists())
                        dirFile.mkdirs();
                    // 创建保存录制视频的视频文件
                    mVideoFile = new File(dirFile, UUID.randomUUID().toString() + ".mp4");
                    // 创建MediaPlayer对象
                    mRecorder = new MediaRecorder();
                    // 设置音频录入源
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    // 设置视频图像的录入源
                    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    // 设置录入媒体的输出格式
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    // 设置音频的编码格式
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    // 设置视频的编码格式
                    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    // 设置视频的采样率,每秒对应的帧数
                 //   mRecorder.setVideoFrameRate(AppConst.VIDEO_CAPTURE_FRAME_RATE);
                    // 设置录制视频文件的输出路径
                    mRecorder.setOutputFile(mVideoFile.getAbsolutePath());
                    // 设置捕获视频图像的预览界面
                    mRecorder.setPreviewDisplay(viewSurfaceDisplay.getHolder().getSurface());
                    //录制时间随意
                    mRecorder.setMaxDuration(-1);
                    int cameraWidth = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.CAMERA_WIDTH));
                    int cameraHeight = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.CAMERA_HEIGHT));
                   // mRecorder.setVideoSize(cameraWidth, cameraHeight);
                    viewSurfaceDisplay.getHolder().setFixedSize(cameraWidth, cameraHeight);
                    mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                        @Override
                        public void onError(MediaRecorder mr, int what, int extra) {
                            // 发生错误，停止录制
                            mRecorder.stop();
                            mRecorder.release();
                            mRecorder = null;
                            mIsRecording = false;
                            imgBtnRecord.setEnabled(true);
                            imgBtnStop.setEnabled(false);
                            Toast.makeText(getContext(), "录制出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mRecorder.prepare();
                    // 开始录制
                    mRecorder.start();
                    // 让record按钮不可用。
                    imgBtnRecord.setEnabled(false);
                    // 让stop按钮可用。
                    imgBtnStop.setEnabled(true);
                    mIsRecording = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果正在进行录制
                if (mIsRecording) {
                    // 停止录制
                    mRecorder.stop();
                    // 释放资源
                    mRecorder.release();
                    mRecorder = null;
                    // 让record按钮可用。
                    imgBtnRecord.setEnabled(true);
                    // 让stop按钮不可用。
                    imgBtnStop.setEnabled(false);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(AppConst.VIDEO_PATH, mVideoFile.getAbsolutePath());
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });
    }

}
