package momo.cn.edu.fjnu.videoclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import momo.cn.edu.fjnu.videoclient.R;

/**
 * 选择所有用户的Adapter,使用JSON处理
 * Created by GaoFei on 2016/3/16.
 */
public class AllUsersAdapter extends ArrayAdapter<String>{

    private Context mContext;
    private List<String> mObjects;
    public AllUsersAdapter(Context context, int resource, List<String> objects) {
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
    public String getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView textUserName;
        if(view != null){
           textUserName = (TextView)(view.findViewById(R.id.text_user_name));
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_all_users, parent, false);
            textUserName = (TextView)(view.findViewById(R.id.text_user_name));
        }
        try{
            JSONObject jsonObject = new JSONObject(getItem(position));
            textUserName.setText(jsonObject.getString("user_name"));
        }catch (Exception e){
            //服务端保证此处不会发生异常
        }
        return view;
    }
}
