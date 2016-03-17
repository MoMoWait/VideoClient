package momo.cn.edu.fjnu.videoclient.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import momo.cn.edu.fjnu.videoclient.R;
import momo.cn.edu.fjnu.videoclient.exception.AppException;
import momo.cn.edu.fjnu.videoclient.model.net.GetAllUserTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    private final String TAG = TestFragment.class.getSimpleName();

    public TestFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new GetAllUserTask(new GetAllUserTask.CallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.i(TAG, "" + jsonObject.toString());
            }

            @Override
            public void onFailed(AppException exception) {
                Log.i(TAG, "" + exception.toString());
            }
        }).execute();
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }


}
