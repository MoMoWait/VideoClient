package momo.cn.edu.fjnu.androidutils.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

import momo.cn.edu.fjnu.androidutils.data.RequestCodeForActivity;

/**
 * 基本的Fragment
 * Created by GaoFei on 2016/1/3.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    public abstract void initView();

    public abstract void initData();

    public abstract void initEvent();

    public void startCamera(String rawPath){
        File rawFile = new File(rawPath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(rawFile));
        startActivityForResult(intent, RequestCodeForActivity.RQC_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == RequestCodeForActivity.RQC_CAMERA)
                onTakePicture();

        }
    }

    /**
     * 拍照回调函数
     */
    public void onTakePicture(){
    }
}
