package momo.cn.edu.fjnu.videoclient.fragment;

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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.UUID;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.BitmapUtils;
import momo.cn.edu.fjnu.androidutils.utils.DeviceInfoUtils;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.activity.AllUsersActivity;
import momo.cn.edu.fjnu.videoclient.activity.PhotoRtMonitorActivity;
import momo.cn.edu.fjnu.videoclient.activity.VideoRtUploadActivity;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.data.SharedKeys;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.FileUploadTask;
import momo.cn.edu.fjnu.videoclient.service.LocationService;
import momo.cn.edu.fjnu.videoclient.view.PersonDataView;

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
    @ViewInject(R.id.btn_photo_rt_upload)
    private Button mBtnPhotoRtUpload;

    /**视频录制上传按钮*/
    @ViewInject(R.id.btn_photo_rt_monitor)
    private Button mBtnPhotoRtMonitor;

    /**图片存储目录*/
    private String mPhotoRawName;

    private SlidingMenu mSlidMenu;

    private PersonDataView mPersonDataView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void initView() {
        AQuery imgHeadQuery = new AQuery(mImgBack);
        try {
            JSONObject userObject = new JSONObject(StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO));
            imgHeadQuery.image(userObject.getString("head_photo"));
        }catch (Exception e){

        }
        mTextOption.setVisibility(View.GONE);
        //设置标题
        mTextTitle.setText(ResourceUtils.getString(R.string.main_menu));
        String currUserInfo = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
        int userType = 1;
        try{
            JSONObject userObject = new JSONObject(currUserInfo);
            userType = userObject.getInt("type");
        }catch (Exception e){

        }
        if (userType == AppConst.UserType.NORMAL){
            //隐藏监控按钮
            mBtnVideoRtMonitor.setVisibility(View.GONE);
            mBtnPhotoRtMonitor.setVisibility(View.GONE);
        }

        mSlidMenu = new SlidingMenu(getContext());
        mSlidMenu.setMode(SlidingMenu.LEFT);
        mSlidMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidMenu.setShadowWidth(0);
        mSlidMenu.setBehindOffset(DeviceInfoUtils.getScreenWidth(getContext()) / 5);
        mSlidMenu.setFadeDegree(0.35f);
        mSlidMenu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        mPersonDataView = new PersonDataView(getContext());
        mSlidMenu.setMenu(mPersonDataView);
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
                getActivity().overridePendingTransition(R.anim.activity_enter_left, R.anim.activity_enter_right);
            }
        });

        //视频实时监控监听事件
        mBtnVideoRtMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllUsersActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_enter_left, R.anim.activity_enter_right);
            }
        });

        //拍照上传按钮监听事件
        mBtnPhotoRtUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dirFile = new File(AppConst.PHOTO_DIRECTORY);
                if (!dirFile.exists())
                    dirFile.mkdirs();
                mPhotoRawName = UUID.randomUUID().toString() + ".jpg";
                File photoFile = new File(dirFile, mPhotoRawName);
                startCamera(photoFile.getAbsolutePath());
            }
        });

        //图片实时监控按钮监听事件
        mBtnPhotoRtMonitor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PhotoRtMonitorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_enter_left, R.anim.activity_enter_right);
            }
        });

        mSlidMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                mImgBack.setVisibility(View.GONE);
                //刷新页面
              //  mPersonDataView.refreshData();
            }
        });

        mSlidMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                mImgBack.setVisibility(View.VISIBLE);
            }
        });

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSlidMenu.isMenuShowing())
                    mSlidMenu.showMenu();
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
        BitmapUtils.saveScaledBitmap(photoFile.getAbsolutePath(), AppConst.CAMERA_SCALE_WIDTH, AppConst.CAMERA_SCALE_HEIGHT, saveScaleFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, 80);
        saveScaleFile = new File(dirFile, saveScalePhotoName);
        DialogUtils.showLoadingDialog(getContext(), false);
        AjaxCallback.setTimeout(AppConst.MAX_UPLOAD_PHOTO_TIME);
        String userId = String.valueOf(-1);
        String currInfo = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
        try{
            JSONObject userObject = new JSONObject(currInfo);
            userId = userObject.getString("id");
        }catch (Exception e){

        }
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
        }).execute(userId, "" + AppConst.FileType.PHOTO, "" + saveScaleFile.length() / 1024,
                saveScaleFile.getAbsolutePath(), "" + LocationService.lng, "" + LocationService.lat, LocationService.address);
    }


    @Override
    public void onStart() {
        super.onStart();
        mPersonDataView.refreshData();
        String userInfo =StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_INFO);
        try{
            JSONObject userObject = new JSONObject(userInfo);
            AQuery aQuery = new AQuery(mImgBack);
            aQuery.image(userObject.getString("head_photo"));

        }catch (Exception e){

        }
    }
}
