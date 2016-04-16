package momo.cn.edu.fjnu.videoclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.ResourceUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.data.AppConst;

/**
 * 图片放大浏览页面
 * Created by GaoFei on 2016/3/29.
 */
@ContentView(R.layout.fragment_photo_view)
public class PhotoViewFragment extends BaseFragment{

    @ViewInject(R.id.img_big_photo)
    private ImageView mImgBigPhoto;

    @ViewInject(R.id.img_back)
    private ImageView mImgBack;
    @ViewInject(R.id.text_title)
    private TextView mTextTitle;
    @ViewInject(R.id.text_option)
    private TextView mTextOption;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void initView() {
        mTextOption.setVisibility(View.GONE);
        mTextTitle.setText(ResourceUtils.getString(R.string.photo_view));
        AQuery aQuery = new AQuery(mImgBigPhoto);
        aQuery.image(getActivity().getIntent().getStringExtra(AppConst.BIG_PHOTO_URL));
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_exit_left, R.anim.activity_exit_right);
            }
        });
    }
}
