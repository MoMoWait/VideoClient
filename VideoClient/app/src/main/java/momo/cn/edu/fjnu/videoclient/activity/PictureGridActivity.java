package momo.cn.edu.fjnu.videoclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import momo.cn.edu.fjnu.videoclient.BuildConfig;
import momo.cn.edu.fjnu.videoclient.fragment.PhotoGalleryFragment;
import momo.cn.edu.fjnu.videoclient.utils.Utils;


/**
 * Simple FragmentActivity to hold the main {@link PhotoGalleryFragment}
 * and not much else.
 */
public class PictureGridActivity extends FragmentActivity {

    private static final String TAG = "PictureGridActivity";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG){
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);
        if(getSupportFragmentManager().findFragmentByTag(TAG) == null){
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, new PhotoGalleryFragment(), TAG);
            transaction.commit();
        }
    }
}
