package momo.cn.edu.fjnu.videoclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.TextUtils;
import momo.cn.edu.fjnu.androidutils.utils.ValidUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.activity.MainActivity;
import momo.cn.edu.fjnu.videoclient.data.SharedKeys;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.LoginTask;

/**
 * 登录页面及相关处理
 * Created by GaoFei on 2016/3/7.
 */
@ContentView(R.layout.fragment_login)
public class LoginFragment extends BaseFragment{

    public final String TAG = LoginFragment.class.getSimpleName();

    @ViewInject(R.id.img_back)
    private ImageView mImgBack;

    @ViewInject(R.id.text_title)
    private TextView mTextTitle;

    @ViewInject(R.id.text_option)
    private TextView mTextOption;
    /**登陆按钮*/
    @ViewInject(R.id.btn_login)
    private TextView mBtnLogin;

    @ViewInject(R.id.edt_user_name)
    private EditText mEdtUserName;

    @ViewInject(R.id.edt_password)
    private EditText mEdtPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void initView() {
        //隐藏按钮
        mImgBack.setVisibility(View.GONE);
        mTextOption.setVisibility(View.GONE);
        //设置标题
        mTextTitle.setText(ResourceUtils.getString(R.string.user_login));
    }

    @Override
    public void initData() {
        String userName = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_NAME);
        String password = StorageUtils.getDataFromSharedPreference(SharedKeys.CURR_USER_PASSWORD);
        mEdtUserName.setText(userName);
        mEdtPassword.setText(password);
        login();
    }

    @Override
    public void initEvent() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * 登录事件
     */
    public void login(){
        String userName = mEdtUserName.getText().toString();
        String password = mEdtPassword.getText().toString();
        if(ValidUtils.isEmpty(userName) || ValidUtils.isEmpty(password)){
            Toast.makeText(getContext(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtils.showLoadingDialog(getContext(), false);
        new LoginTask(new LoginTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    StorageUtils.saveDataToSharedPreference(SharedKeys.CURR_USERID, "" + jsonObject.getInt("uid"));
                    StorageUtils.saveDataToSharedPreference(SharedKeys.CURR_USER_TYPE, "" + jsonObject.getInt("type"));
                    Log.i(TAG, "用户ID:" + jsonObject.getInt("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //存储用户名
                StorageUtils.saveDataToSharedPreference(SharedKeys.CURR_USER_NAME, mEdtUserName.getText().toString());
                //存储密码
                StorageUtils.saveDataToSharedPreference(SharedKeys.CURR_USER_PASSWORD, mEdtPassword.getText().toString());
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                DialogUtils.closeLoadingDialog();
                getActivity().finish();
            }

            @Override
            public void onFailed(AppException exception) {
                Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
                DialogUtils.closeLoadingDialog();
            }
        }).execute(userName, TextUtils.str2MD5(password));
    }
}
