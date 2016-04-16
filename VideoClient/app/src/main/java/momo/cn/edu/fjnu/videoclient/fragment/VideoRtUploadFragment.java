package momo.cn.edu.fjnu.videoclient.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ValidUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.data.SharedKeys;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.RegisterOnlineTask;
import momo.cn.edu.fjnu.videoclient.model.net.RequestOfflineTask;
import momo.cn.edu.fjnu.videoclient.pojo.CameraSize;
import momo.cn.edu.fjnu.videoclient.service.LocationService;
import momo.cn.edu.fjnu.videoclient.view.VideoServerDialog;
import momo.cn.edu.fjnu.videoclient.view.VideoSettingDialog;


/**
 * 视频实时上传页面
 */
@ContentView(R.layout.fragment_video_rt_upload)
public class VideoRtUploadFragment extends BaseFragment implements
        View.OnClickListener,
        RtspClient.Callback,
        Session.Callback,
        SurfaceHolder.Callback{
    public final static String TAG = VideoRtUploadFragment.class.getSimpleName();
    @ViewInject(R.id.start)
    private ImageButton mButtonStart;
    @ViewInject(R.id.flash)
    private ImageButton mButtonFlash;
    @ViewInject(R.id.camera)
    private ImageButton mButtonCamera;
    @ViewInject(R.id.video_setting)
    private ImageButton mButtonVideoSetting;
    @ViewInject(R.id.server_setting)
    private ImageButton mButtonServerSetting;
    @ViewInject(R.id.surface)
    private SurfaceView mSurfaceView;
    @ViewInject(R.id.bitrate)
    private TextView mTextBitrate;
    @ViewInject(R.id.progress_bar)
    private ProgressBar mProgressBar;
    private Session mSession;
    private RtspClient mClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }


    @Override
    public void initView() {
        mButtonFlash.setTag("off");



    }

    @Override
    public void initData() {
        mSession = SessionBuilder.getInstance()
                .setContext(getContext().getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(AppConst.AUDIO_SAMPLING, AppConst.AUDIO_BITRATE))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(0)
                .setCallback(this)
                .setTimeToLive(75)
                .build();

        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);
        mSurfaceView.getHolder().addCallback(this);

        //获取设备摄像头的一些信息
        if(ValidUtils.isEmpty(StorageUtils.getDataFromSharedPreference(SharedKeys.CAMERA_SIZES))){
            //打开后置摄像头
            Camera camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            List<CameraSize> cameraSizes = new ArrayList<>();
            for(Camera.Size size : supportedPreviewSizes){
                cameraSizes.add(new CameraSize(size.width, size.height));
            }
            StorageUtils.saveDataToSharedPreference(SharedKeys.CAMERA_SIZES, JsonUtils.listToJsonArray(cameraSizes).toString());
            camera.release();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                toggleStream();
                break;
            case R.id.flash:
                if (mButtonFlash.getTag().equals("on")) {
                    mButtonFlash.setTag("off");
                    mButtonFlash.setImageResource(R.mipmap.ic_flash_on_holo_light);
                } else {
                    mButtonFlash.setImageResource(R.mipmap.ic_flash_off_holo_light);
                    mButtonFlash.setTag("on");
                }
                mSession.toggleFlash();
                break;
            case R.id.camera:
                mSession.switchCamera();
                break;
            case R.id.video_setting:
                VideoSettingDialog videoSettingDialog = new VideoSettingDialog(getContext());
                videoSettingDialog.show();
                break;
            case R.id.server_setting:
                VideoServerDialog videoServerDialog = new VideoServerDialog(getContext());
                videoServerDialog.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);
    }

    private void selectQuality() {
        String videoWidthStr = StorageUtils.getDataFromSharedPreference(SharedKeys.VIDEO_WIDTH);
        if(ValidUtils.isEmpty(videoWidthStr)){
            Toast.makeText(getContext(), ResourceUtils.getString(R.string.server_config), Toast.LENGTH_SHORT).show();
            return;
        }
        int videoWidth = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.VIDEO_WIDTH));
        int videoHeight = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.VIDEO_HEIGHT));
        int frameRate = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.VIDEO_FRAME_RATE));
        int bitrate = Integer.parseInt(StorageUtils.getDataFromSharedPreference(SharedKeys.VIDEO_BITRATE));
        mSession.setVideoQuality(new VideoQuality(videoWidth, videoHeight, frameRate, bitrate));
    }

    private void enableUI() {
        mButtonStart.setEnabled(true);
        mButtonCamera.setEnabled(true);
    }

    // Connects/disconnects to the RTSP server and starts/stops the stream
    public void toggleStream() {
        if (!mClient.isStreaming()) {
            String serverUserName = StorageUtils.getDataFromSharedPreference(SharedKeys.SERVER_USER_NAME);
            String serverPassword = StorageUtils.getDataFromSharedPreference(SharedKeys.SERVER_PASSWORD);
            String serverIp = StorageUtils.getDataFromSharedPreference(SharedKeys.SERVER_IP);
            if(ValidUtils.isEmpty(serverUserName)){
                Toast.makeText(getContext(), ResourceUtils.getString(R.string.server_config), Toast.LENGTH_SHORT).show();
                return;
            }
            mClient.setCredentials(serverUserName, serverPassword);
            mClient.setServerAddress(serverIp, AppConst.SERVER_PORT);
            String userInfo = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
            String userId = "-1";
            try {
                JSONObject userObject = new JSONObject(userInfo);
                userId =  userObject.getString("id");
            }catch (Exception e){

            }
            mClient.setStreamPath("/" + AppConst.VIDEO_MONITOR + "/" + userId);
            //尝试使用UDP模式
            mClient.setTransportMode(RtspClient.TRANSPORT_UDP);
            //设置视频质量
            selectQuality();
            //显示环形进度条
            mProgressBar.setVisibility(View.VISIBLE);
            //开始捕获视频流并且发送
            mClient.startStream();

        } else {
            mClient.stopStream();
        }
    }

    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        // Displays a popup to report the eror to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        mTextBitrate.setText("" + bitrate / 1000 + " kbps");
    }

    @Override
    public void onPreviewStarted() {
        if (mSession.getCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mButtonFlash.setEnabled(false);
            mButtonFlash.setTag("off");
            mButtonFlash.setImageResource(R.mipmap.ic_flash_on_holo_light);
        } else {
            mButtonFlash.setEnabled(true);
        }
    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        enableUI();
        mButtonStart.setImageResource(R.mipmap.ic_switch_video_active);
        mProgressBar.setVisibility(View.GONE);
        //获取当前用户信息
        String userInfo = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
        String userId = String.valueOf(-1);
        try {
            JSONObject userObject = new JSONObject(userInfo);
            userId = userObject.getString("id");
        }catch (Exception e){
            Log.i(TAG, "" + e);
        }
        Log.i(TAG, "RTSP回话开始");
        //尝试注册在线
        new RegisterOnlineTask(new RegisterOnlineTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onFailed(AppException exception) {

            }
        }).execute(userId, LocationService.address, "" + LocationService.lng, "" + LocationService.lat);
    }

    @Override
    public void onSessionStopped() {
        enableUI();
        mButtonStart.setImageResource(R.mipmap.ic_switch_video);
        mProgressBar.setVisibility(View.GONE);
        Log.i(TAG, "RTSP回话开始");
        //获取当前用户信息
        String userInfo = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
        String userId = String.valueOf(-1);
        try {
            JSONObject userObject = new JSONObject(userInfo);
            userId = userObject.getString("id");
        }catch (Exception e){
            Log.i(TAG, "" + e);
        }
        //请求下线
        new RequestOfflineTask(new RequestOfflineTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onFailed(AppException exception) {

            }
        }).execute(userId);
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mButtonFlash.setImageResource(R.mipmap.ic_flash_on_holo_light);
                mButtonFlash.setTag("off");
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                VideoQuality quality = mSession.getVideoTrack().getVideoQuality();
                logError("The following settings are not supported on this phone: " +
                        quality.toString() + " " +
                        "(" + e.getMessage() + ")");
                e.printStackTrace();
                return;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            logError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRtspUpdate(int message, Exception e) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                mProgressBar.setVisibility(View.GONE);
                enableUI();
                logError(e.getMessage());
                e.printStackTrace();
                break;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mClient.stopStream();
    }

    @Override
    public void initEvent() {
        mButtonStart.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);
        mButtonCamera.setOnClickListener(this);
        mButtonServerSetting.setOnClickListener(this);
        mButtonVideoSetting.setOnClickListener(this);
    }
}
