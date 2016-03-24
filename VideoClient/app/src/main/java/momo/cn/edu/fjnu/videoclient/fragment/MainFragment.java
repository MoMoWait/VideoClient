package momo.cn.edu.fjnu.videoclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.UUID;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.BitmapUtils;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.activity.AllUsersActivity;
import momo.cn.edu.fjnu.videoclient.activity.VideoRecorderActivity;
import momo.cn.edu.fjnu.videoclient.activity.VideoRtUploadActivity;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.data.SharedKeys;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.FileUploadTask;
import momo.cn.edu.fjnu.videoclient.service.LocationService;

/**
 * 主目录页面
 * Created by GaoFei on 2016/3/10.
 */
@ContentView(R.layout.fragment_main)
public class MainFragment extends BaseFragment{

    public static final int REQUEST_GET_VIDEO = 3000;

    @ViewInject(R.id.img_back)
    private ImageView mImgBack;

    @ViewInject(R.id.text_title)
    private TextView mTextTitle;

    @ViewInject(R.id.text_option)
    private TextView mTextOption;

    /**视频实时上传按钮*/
    @ViewInject(R.id.btn_video_rt_upload)
    private Button mBtnVideoRtUpload;

    /**视频实时监控按钮*/
    @ViewInject(R.id.btn_video_rt_monitor)
    private Button mBtnVideoRtMonitor;

    /**拍照上传按钮*/
    @ViewInject(R.id.btn_photo_capture_upload)
    private Button mBtnPhtotoCapture;

    /**视频录制上传按钮*/
    @ViewInject(R.id.btn_video_recorder_upload)
    private Button mBtnVideoRecorder;

    /**图片存储目录*/
    private String mPhotoRawName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void initView() {
        mImgBack.setVisibility(View.GONE);
        mTextOption.setVisibility(View.GONE);
        //设置标题
        mTextTitle.setText(ResourceUtils.getString(R.string.main_menu));
        //普通用户,隐藏监控按钮
        if(StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_TYPE).equals("" + AppConst.UserType.NORMAL))
            mBtnVideoRtMonitor.setVisibility(View.GONE);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        //视频实时上传监听事件
        mBtnVideoRtUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VideoRtUploadActivity.class);
                startActivity(intent);
            }
        });

        //视频实时监控监听事件
        mBtnVideoRtMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllUsersActivity.class);
                startActivity(intent);
            }
        });

        //拍照上传按钮监听事件
        mBtnPhtotoCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                File dirFile = new File(AppConst.PHOTO_DIRECTORY);
                if(!dirFile.exists())
                    dirFile.mkdirs();
                mPhotoRawName = UUID.randomUUID().toString() + ".jpg";
                File photoFile = new File(dirFile, mPhotoRawName);
                startCamera(photoFile.getAbsolutePath());
            }
        });

        //视频录制按钮上传事件
        mBtnVideoRecorder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VideoRecorderActivity.class);
                startActivityForResult(intent, REQUEST_GET_VIDEO);
            }
        });

    }

    /**
     * 拍照回调事件
     */
    @Override
    public void onTakePicture() {
        File dirFile = new File(AppConst.PHOTO_DIRECTORY);
        File photoFile = new File(dirFile, mPhotoRawName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //获取原始图片的宽和高
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
        //待保存的图片文件名字
        String saveScalePhotoName = UUID.randomUUID().toString() + ".jpg";
        File saveScaleFile = new File(dirFile, saveScalePhotoName);
        //图片的宽和高压缩为原来的一半
        BitmapUtils.saveScaledBitmap(photoFile.getAbsolutePath(), options.outWidth / 2, options.outHeight / 2 , saveScaleFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, 80);
        saveScaleFile = new File(dirFile, saveScalePhotoName);
        DialogUtils.showLoadingDialog(getContext(), false);
        AjaxCallback.setTimeout(AppConst.MAX_UPLOAD_PHOTO_TIME);
        new FileUploadTask(new FileUploadTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                DialogUtils.closeLoadingDialog();
                Toast.makeText(getContext(), "图片上传成功", Toast.LENGTH_SHORT).show();
                AjaxCallback.setTimeout(AppConst.DEFAULT_UPLOAD_TIME);
            }

            @Override
            public void onFailed(AppException exception) {
                DialogUtils.closeLoadingDialog();
                Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
                AjaxCallback.setTimeout(AppConst.DEFAULT_UPLOAD_TIME);
            }
        }).execute(StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USERID), "" + AppConst.FileType.PHOTO, "" + saveScaleFile.length() / 1024,
                saveScaleFile.getAbsolutePath(), "" + LocationService.lng, "" + LocationService.lat, LocationService.address);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_GET_VIDEO && resultCode == Activity.RESULT_OK){
            String videoPath = data.getStringExtra(AppConst.VIDEO_PATH);
            File videoFile = new File(videoPath);
            new FileUploadTask(new FileUploadTask.CallBack() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    DialogUtils.closeLoadingDialog();
                    Toast.makeText(getContext(), "视频上传成功", Toast.LENGTH_SHORT).show();
                    AjaxCallback.setTimeout(AppConst.DEFAULT_UPLOAD_TIME);
                }

                @Override
                public void onFailed(AppException exception) {
                    DialogUtils.closeLoadingDialog();
                    Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    AjaxCallback.setTimeout(AppConst.DEFAULT_UPLOAD_TIME);
                }
            }).execute(StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USERID), "" + AppConst.FileType.VIDEO, "" + videoFile.length() / 1024,
                    videoFile.getAbsolutePath(), "" + LocationService.lng, "" +LocationService.lat, LocationService.address);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
