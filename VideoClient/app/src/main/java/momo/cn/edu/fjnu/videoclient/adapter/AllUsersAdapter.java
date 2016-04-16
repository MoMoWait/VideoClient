package momo.cn.edu.fjnu.videoclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.model.db.UserService;
import momo.cn.edu.fjnu.videoclient.pojo.OnlineUser;
import momo.cn.edu.fjnu.videoclient.pojo.User;

/**
 * 选择所有用户的Adapter,使用JSON处理
 * Created by GaoFei on 2016/3/16.
 */
public class AllUsersAdapter extends ArrayAdapter<OnlineUser>{

    private Context mContext;
    private List<OnlineUser> mObjects;
    public AllUsersAdapter(Context context, int resource, List<OnlineUser> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public OnlineUser getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_all_users, parent, false);
        }
        OnlineUser onlineUser =  getItem(position);
        UserService userService = new UserService();
        User user = userService.getObjectById(User.class, onlineUser.getUser_id());
        //用户名字和在线登录时间
        TextView textNameTime = (TextView)(view.findViewById(R.id.text_user_name_time));
        //用户地址
        TextView textAddress = (TextView)(view.findViewById(R.id.text_address));
        //用户头像
        ImageView userHeadImg = (ImageView)(view.findViewById(R.id.img_user_head));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String strTime = dateFormat.format(new Date(onlineUser.getTime() * 1000L));
        textNameTime.setText(user.getUser_name() + "    " + strTime);
        textAddress.setText(onlineUser.getAddress());
        AQuery aQuery = new AQuery(userHeadImg);
        aQuery.image(user.getHead_photo());
        return view;
    }
}
