package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.AllUsersFragment;


/**
 * 监控所有的用户,显示所有的用户列表
 * Created by GaoFei on 2016/3/16.
 */
public class AllUsersActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new AllUsersFragment();
    }
}
