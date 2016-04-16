package momo.cn.edu.fjnu.videoclient.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.activity.VideoRtMonitorActivity;
import momo.cn.edu.fjnu.videoclient.adapter.AllUsersAdapter;
import momo.cn.edu.fjnu.videoclient.data.AppConst;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.GetAllOnlineUserTask;
import momo.cn.edu.fjnu.videoclient.pojo.OnlineUser;
import momo.cn.edu.fjnu.vlclib.activity.PlayerActivity;


/**
 * 显示所有的用户页面
 */
@ContentView(R.layout.fragment_all_users)
public class AllUsersFragment extends BaseFragment {

    public final String TAG = AllUsersFragment.class.getSimpleName();

    private GetAllOnlineUserTask mUsersTask;
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
        //设置标题为在线用户
        mTextTitle.setText(ResourceUtils.getString(R.string.online_users));
        mTextOption.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        refreshView();
    }

    @Override
    public void initEvent() {
        mListAllUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                Intent localIntent = new Intent(getContext(), VideoRtMonitorActivity.class);
                try{
                    intent.putExtra(AppConst.VIDEO_URL,AppConst.RTSP_HEAD +  ((OnlineUser)(parent.getAdapter().getItem(position))).getUser_id());
                    localIntent.putExtra(AppConst.USER_ID, ((OnlineUser)(parent.getAdapter().getItem(position))).getUser_id());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 21 ){
                    startActivity(localIntent);
                }else
                    startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_enter_left, R.anim.activity_enter_right);
            }
        });

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_exit_left, R.anim.activity_exit_right);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mUsersTask != null && mUsersTask.getStatus() == AsyncTask.Status.RUNNING)
            mUsersTask.cancel(true);
    }


    public void refreshView(){
        if(mUsersTask != null && mUsersTask.getStatus() == AsyncTask.Status.RUNNING)
            mUsersTask.cancel(true);
        mUsersTask = new GetAllOnlineUserTask(new GetAllOnlineUserTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                List<OnlineUser> onlineUsers = new ArrayList<>();
                try{
                    JSONArray userArrays = jsonObject.getJSONArray("online_users");
                    for(int i = 0; i != userArrays.length(); ++i){
                        JSONObject itemObject = userArrays.getJSONObject(i);
                        OnlineUser onlineUser = (OnlineUser)(JsonUtils.jsonToObject(OnlineUser.class, itemObject));
                        Log.i(TAG, "在线用户：" + onlineUser);
                        onlineUsers.add(onlineUser);
                    }
                }catch (Exception e){
                    Log.i(TAG, "" + e);
                }
                if(getContext() != null){
                    AllUsersAdapter allUsersAdapter = new AllUsersAdapter(getContext(), R.layout.adapter_all_users, onlineUsers);
                    mListAllUsers.setAdapter(allUsersAdapter);
                   //allUsersAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailed(AppException exception) {
                if(getContext() != null)
                    Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
                if(exception.getErrorMsg().equals("暂无在线用户")){
                    if(getContext() != null){
                        AllUsersAdapter allUsersAdapter = new AllUsersAdapter(getContext(), R.layout.adapter_all_users, new ArrayList<OnlineUser>());
                        mListAllUsers.setAdapter(allUsersAdapter);
                        //allUsersAdapter.notifyDataSetChanged();
                    }

                }

            }
        });
        mUsersTask.execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new OnlineReceiver(), new IntentFilter(AppConst.USERONLINE_MESSAGE_RECEIVED));
       LocalBroadcastManager.getInstance(getContext()).registerReceiver(new OfflineReceiver(), new IntentFilter(AppConst.USEROFFLINE_MESSAGE_RECEIVED));
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(new OnlineReceiver());
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(new OfflineReceiver());
    }

    class OnlineReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView();
        }
    }

    class OfflineReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshView();
        }
    }

}
