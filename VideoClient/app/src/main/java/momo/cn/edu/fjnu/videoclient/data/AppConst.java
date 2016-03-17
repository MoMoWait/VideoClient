package momo.cn.edu.fjnu.videoclient.data;

import android.os.Environment;

/**
 * APP常量数据
 * Created by GaoFei on 2016/3/8.
 */
public class AppConst {
    /**数据库名称*/
    public static final String DB_NAME = "videocontrol.db";
    /**数据库存储目录*/
    public static final String DB_DIRECTORY = Environment.getExternalStorageDirectory() + "/momo/cn.edu.fjnu.videomonitor/db";
    /**数据库版本号*/
    public static final int DB_VERSION = 1;
    /**定位间隔时间*/
    public static final long LOCATE_TIME = 60L * 1000;
    /**请求的URl地址前缀*/
    public static final String URL_HEAD = "http://192.168.1.203:8080/VideoAppService/";
    /**RTSP流的前缀*/
    public static final String RTSP_HEAD = "rtsp://120.24.210.186:1935/VideoControl/";
    /**图片存储目录*/
    public static final String PHOTO_DIRECTORY = Environment.getExternalStorageDirectory() + "/momo/cn.edu.fjnu.videomonitor/photo";
    /**录制视频的采样率*/
    public static final  int VIDEO_CAPTURE_FRAME_RATE = 15;
    /**音频采样率*/
    public static final int AUDIO_SAMPLING = 8000;
    /**音频比特率*/
    public static final int AUDIO_BITRATE = 16000;
    /**录制视频的存储目录*/
    public static final  String VIDEO_SAVE_DIRECTORY = Environment.getExternalStorageDirectory() + "/momo/cn.edu.fjnu.videomonitor/video";
    /**图片上传的超时设定*/
    public static final int MAX_UPLOAD_PHOTO_TIME = 2 * 60 * 1000;
    /**Aquey默认上传时间*/
    public static final int DEFAULT_UPLOAD_TIME = 30 * 1000;
    /**录制视频存储路径*/
    public static final String VIDEO_PATH = "video_path";
    /**包名*/
    public static final String PACKAGE = "package";
    /**类名字*/
    public static final String CLASS_NAME = "class_name";
    /**用户ID'*/
    public static final String USER_ID = "user_id";
    /**服务器端口*/
    public static final int SERVER_PORT = 1935;
    /**VideoMonitor Application*/
    public static final String VIDEO_MONITOR = "VideoControl";
    /**
     * 请求结果
     */
    public interface RetResult{
        int SUCC = 1;
        int FAILED = 2;
    }

    /**
     * 用户类型
     */
    public interface UserType{
        /**普通用户*/
        int NORMAL = 1;
        /**管理员*/
        int MANAGER = 2;
    }

    /**
     * 文件类型
     */
    public interface FileType{
        //图片
        public int PHOTO = 1;
        //视频
        public int VIDEO = 2;
    }

}
