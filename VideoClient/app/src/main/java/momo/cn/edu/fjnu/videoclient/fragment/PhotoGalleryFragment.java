package momo.cn.edu.fjnu.videoclient.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.base.BaseFragment;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.videoclient.BuildConfig;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.GetUploadPhotoTask;
import momo.cn.edu.fjnu.videoclient.pojo.FileUpload;
import momo.cn.edu.fjnu.videoclient.utils.ImageCache;
import momo.cn.edu.fjnu.videoclient.utils.ImageFetcher;
import momo.cn.edu.fjnu.videoclient.utils.Utils;
import momo.cn.edu.fjnu.videoclient.view.RecyclingImageView;


/**
 * Created by searover on 3/15/15.
 * The main fragment that powers the PictureGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load
 * children asynchronously, keeping the UI nice and smooth and caching thumbnails for quick
 * retrieval. The cache is retained over configuration changes like orientation change so
 * the images are populated quickly if, for example, the user rotates the device.
 */
@ContentView(R.layout.photo_gallery_fragment)
public class PhotoGalleryFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final  String TAG = "PhotoGalleryFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private List<FileUpload> mFiles;
    @ViewInject(R.id.gridview)
    private GridView mPhotoGridView;
    /**
    * Empty constractor as per the Fragment documentation
    */
    public PhotoGalleryFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelOffset(R.dimen.image_thumbnail_spacing);
        mFiles = new ArrayList<>();
        mAdapter = new ImageAdapter(getActivity(), mFiles);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return x.view().inject(this, inflater, container);
    }

    

    @Override
    public void initView() {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(),IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(),mImageThumbSize);
        mImageFetcher.setLoadingImage(R.mipmap.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
        mPhotoGridView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int rqTime = 0;
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            rqTime = (int)(date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new GetUploadPhotoTask(new GetUploadPhotoTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                //成功获取图片之后进行加载
                try{
                    JSONArray photosArray = jsonObject.getJSONArray("photos");
                    mFiles =  (List<FileUpload>)(JsonUtils.arrayToList(FileUpload.class, photosArray));
                  //  mAdapter = new ImageAdapter(getActivity(), mFiles);
                //    mPhotoGridView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    Log.i(TAG, "获取的文件列表" + mFiles);
                    //mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    Log.i(TAG, "转换JSONArray发生异常:" + e);
                }

            }

            @Override
            public void onFailed(AppException exception) {
                Toast.makeText(getContext(), exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        }).execute("" +  rqTime);
    }

    @Override
    public void initEvent() {
        mPhotoGridView.setOnItemClickListener(this);
        //GridView滑动监听事件
        mPhotoGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if(!Utils.hasHoneycomb()){
                        mImageFetcher.setPauseWork(true);
                    }
                }else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode = columnWidth. The column width is used to set the
        // height of each view so we get nice square thumbnails.
        mPhotoGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(mAdapter.getNumColumns() == 0){
                    final int numColumns = (int)Math.floor(mPhotoGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                    if(numColumns > 0){
                        final int columnWidth = (mPhotoGridView.getWidth() / numColumns) - mImageThumbSpacing;
                        mAdapter.setNumColumns(numColumns);
                        mAdapter.setItemHeight(columnWidth);
                        if(BuildConfig.DEBUG){
                            Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                        }
                        if(Utils.hasJellyBean()){
                            mPhotoGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            mPhotoGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mImageFetcher.setExitTaskEarly(false);
   //     mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTaskEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
//        final Intent i = new Intent()
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_photo,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.clear_cache){
            mImageFetcher.clearCache();
            Toast.makeText(getActivity(),"Caches have been cleared",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off coverd by it.
     */
    private class ImageAdapter extends BaseAdapter{

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private List<FileUpload> mFiles;
        public ImageAdapter(Context context, List<FileUpload> files){
            super();
            this.mFiles = files;
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            //获取状态栏的高度
            if(context.getTheme().resolveAttribute(android.R.attr.actionBarSize,tv,true)){
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
            }
        }

        public int getCount() {
            // If columns here yet to be determined, return no items
            if(getNumColumns() == 0){
                return 0;
            }

            // Size + number of columns for top empty row
            return mFiles.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ? null : mFiles.get(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount(){
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds(){
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // First check if this is the top view
            if(position < mNumColumns){
                if(convertView == null){
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if(convertView == null){
                // If it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            }else {
                // Otherwise, re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if(imageView.getLayoutParams().height != mItemHeight){
                imageView.setLayoutParams(mImageViewLayoutParams);
            }
            // Finally load the image asynchronously into the ImageView, this also take care of
            // setting a placeholder image while the background thread runs
            FileUpload itemFile = mFiles.get(position - mNumColumns);
            Log.i(TAG, "" + itemFile);
            mImageFetcher.loadImage(itemFile.getUrl(), imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column
         * width so the height can be set to match.
         * @param height
         */
        public void setItemHeight(int height){
            if(height == mItemHeight){
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns){
            mNumColumns = numColumns;
        }

        public int getNumColumns(){
            return mNumColumns;
        }
    }
}
