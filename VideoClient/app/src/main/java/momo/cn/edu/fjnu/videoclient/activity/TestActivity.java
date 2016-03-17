package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.TestFragment;


/**
 * 测试Activity
 * Created by GaoFei on 2016/3/16.
 */
public class TestActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new TestFragment();
    }
}
