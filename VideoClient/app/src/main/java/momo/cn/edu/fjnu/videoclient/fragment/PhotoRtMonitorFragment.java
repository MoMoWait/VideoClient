package momo.cn.edu.fjnu.videoclient.fragment;


import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.activity.PhotoViewActivity;
import momo.cn.edu.fjnu.videoclient.adapter.PhotoItemAdapter;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.GetUploadPhotoTask;
import momo.cn.edu.fjnu.videoclient.pojo.FileUpload;

/**
 * 视频录制页面
 */

@ContentView(R.layout.fragment_photo_rt_monitor)
public class PhotoRtMonitorFragment extends BaseFragment {

    private final String TAG = PhotoRtMonitorFragment.class.getSimpleName();

    @ViewInject(R.id.img_back)
    private ImageView mImgBack;

    @ViewInject(R.id.text_title)
    private TextView mTextTitle;

    @ViewInject(R.id.text_option)
    private TextView mTextOption;

    @ViewInject(R.id.grid_photo)
    private GridView mPhotoGrid;

    private GetUploadPhotoTask mPhotoTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);

    }


    @Override
    public void initView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strTime = simpleDateFormat.format(new Date());
        mTextOption.setText(strTime);
        mTextTitle.setText(ResourceUtils.getString(R.string.photo_view));
    }

    @Override
    public void initData() {
        refreshView();
    }

    @Override
    public void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_exit_left, R.anim.activity_exit_right);
            }
        });

        mPhotoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileUpload fileUpload = (FileUpload) (parent.getAdapter().getItem(position));
                Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                intent.putExtra(AppConst.BIG_PHOTO_URL, fileUpload.getUrl());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_enter_left, R.anim.activity_enter_right);
            }
        });

        mTextOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try{
                    Date currDate = simpleDateFormat.parse(mTextOption.getText().toString());
                    calendar.setTime(currDate);
                }catch (Exception e){

                }

                DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Log.i(TAG, "选择日期=" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        Calendar selectCalendar = Calendar.getInstance();
                        selectCalendar.set(Calendar.YEAR, year);
                        selectCalendar.set(Calendar.MONTH, monthOfYear);
                        selectCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mTextOption.setText(new SimpleDateFormat("yyyy-MM-dd").format(selectCalendar.getTime()));
                        //刷新页面
                        refreshView();

                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });
    }

    /**
     * 刷新界面
     */
    public void refreshView(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int rqTime = 0;
        try {
            Date date = simpleDateFormat.parse(mTextOption.getText().toString());
            rqTime = (int)(date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(mPhotoTask != null && mPhotoTask.getStatus() == AsyncTask.Status.RUNNING)
            mPhotoTask.cancel(true);
        mPhotoTask  = new GetUploadPhotoTask(new GetUploadPhotoTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                //成功获取图片之后进行加载
                try{
                    JSONArray photosArray = jsonObject.getJSONArray("photos");
                    List<FileUpload> mFiles =  (List<FileUpload>)(JsonUtils.arrayToList(FileUpload.class, photosArray));
                    Log.i(TAG, "获取文件列表0" + mFiles);
                    PhotoItemAdapter adapter = new PhotoItemAdapter(getContext(), R.layout.adapter_photo_item, mFiles);
                    mPhotoGrid.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    Log.i(TAG, "获取的文件列表1" + mFiles);
                }catch (Exception e){
                    Log.i(TAG, "转换JSONArray发生异常:" + e);
                }
            }

            @Override
            public void onFailed(AppException exception) {
                Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        mPhotoTask.execute("" + rqTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new PushMessageReceiver(), new IntentFilter(AppConst.PHOTO_MESSAGE_RECEIVED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(new PushMessageReceiver());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPhotoTask != null && mPhotoTask.getStatus() == AsyncTask.Status.RUNNING)
            mPhotoTask.cancel(true);
    }

    class PushMessageReceiver extends  BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "收到推送广播");
            //需要和本地时间进行对比
            Log.i(TAG, "receive1:" + mTextOption.getText().toString());
            Log.i(TAG, "receive2:" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            if(mTextOption.getText().toString().equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())))
                refreshView();
        }
    }
}
