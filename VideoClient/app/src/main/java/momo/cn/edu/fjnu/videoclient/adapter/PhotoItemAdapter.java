package momo.cn.edu.fjnu.videoclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.utils.DeviceInfoUtils;
import momo.cn.edu.fjnu.androidutils.utils.SizeUtils;
import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.model.db.UserService;
import momo.cn.edu.fjnu.videoclient.pojo.FileUpload;
import momo.cn.edu.fjnu.videoclient.pojo.User;

/**
 * 图片浏览页面
 * Created by GaoFei on 2016/3/27.
 */
public class PhotoItemAdapter extends ArrayAdapter<FileUpload>{
    private Context mContext;
    private List<FileUpload> mObjects;
    private int mLayoutResource;

    public PhotoItemAdapter(Context context, int resource, List<FileUpload> objects) {
        super(context, resource, objects);
        this.mObjects = objects;
        this.mContext = context;
        mLayoutResource = resource;
    }

    @Override
    public FileUpload getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null)
            return  convertView;
        else{
            convertView = LayoutInflater.from(mContext).inflate(mLayoutResource, null, false);
            ImageView photoImg = (ImageView)(convertView.findViewById(R.id.img_photo));
            int itemWidth = (int)((DeviceInfoUtils.getScreenWidth(mContext) - SizeUtils.dp2px(mContext, 4))/3);
            convertView.setLayoutParams(new AbsListView.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            photoImg.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemWidth));
            photoImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }
        FileUpload fileUpload = getItem(position);
        UserService userService = new UserService();
        User user = userService.getObjectById(User.class, fileUpload.getUid());
        AQuery imgQuery = new AQuery(convertView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTime = simpleDateFormat.format(new Date(fileUpload.getCreate_time() * 1000L));
        imgQuery.id(R.id.img_photo).image(fileUpload.getUrl())
                .id(R.id.text_time).text(strTime).id(R.id.text_address).text(fileUpload.getAddress())
                .id(R.id.text_user_name).text(user.getUser_name());
        return convertView;
    }
}
