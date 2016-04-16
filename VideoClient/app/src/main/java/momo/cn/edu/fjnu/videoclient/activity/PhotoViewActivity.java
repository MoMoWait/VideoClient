package momo.cn.edu.fjnu.videoclient.activity;

import android.support.v4.app.Fragment;

import momo.cn.edu.fjnu.videoclient.fragment.PhotoViewFragment;

/**
 * 图片放大浏览页面
 * Created by GaoFei on 2016/3/29.
 */
public class PhotoViewActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new PhotoViewFragment();
    }
}
