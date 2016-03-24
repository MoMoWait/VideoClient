package momo.cn.edu.fjnu.videoclient.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.adapter.AllUsersAdapter;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.GetAllUserTask;
import momo.cn.edu.fjnu.vlclib.activity.PlayerActivity;


/**
 * 显示所有的用户页面
 */
@ContentView(R.layout.fragment_all_users)
public class AllUsersFragment extends BaseFragment {

    public final String TAG = AllUsersFragment.class.getSimpleName();

    private GetAllUserTask mUsersTask;
    @ViewInject(R.id.list_all_users)
    private ListView mListAllUsers;

    @ViewInject(R.id.img_back)
    private ImageView mImgBack;

    @ViewInject(R.id.text_title)
    private TextView mTextTitle;

    @ViewInject(R.id.text_option)
    private TextView mTextOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }


    @Override
    public void initView() {
        mTextTitle.setText(ResourceUtils.getString(R.string.user_list));
        mTextOption.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        mUsersTask = new GetAllUserTask(new GetAllUserTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                List<String> useList =new ArrayList<>();
                try{
                    JSONArray  userAray = jsonObject.getJSONArray("users");
                    Log.i(TAG, "返回的jsonArray:" + userAray);
                    for(int i = 0; i < userAray.length(); ++i){
                        useList.add(userAray.getString(i));
                    }
                }catch (Exception e){
                    Log.i(TAG, "发生异常:" + e);
                }
                AllUsersAdapter adapter = new AllUsersAdapter(getContext(), R.layout.adapter_all_users, useList);
                mListAllUsers.setAdapter(adapter);
            }

            @Override
            public void onFailed(AppException exception) {
                Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        mUsersTask.execute();
    }

    @Override
    public void initEvent() {
        mListAllUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             /*   Intent intent = new Intent(getContext(), VideoRtMonitorActivity.class);
                try{
                    JSONObject jsonObject = new JSONObject(parent.getAdapter().getItem(position).toString());
                    intent.putExtra(AppConst.USER_ID,  jsonObject.getInt("id"));
                }catch (Exception e){
                    e.printStackTrace();
                }*/
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                try{
                    JSONObject jsonObject = new JSONObject(parent.getAdapter().getItem(position).toString());
                    intent.putExtra(AppConst.VIDEO_URL,AppConst.RTSP_HEAD +  jsonObject.getInt("id"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //启动视频监控页面
                startActivity(intent);
            }
        });

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mUsersTask != null && mUsersTask.getStatus() == AsyncTask.Status.RUNNING)
            mUsersTask.cancel(true);
    }
}
