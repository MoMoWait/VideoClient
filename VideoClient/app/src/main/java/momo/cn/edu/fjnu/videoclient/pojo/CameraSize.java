package momo.cn.edu.fjnu.videoclient.pojo;

/**
 * 摄像机的分辨率
 * Created by GaoFei on 2016/3/17.
 */
public class CameraSize {
    int width;
    int height;

    public CameraSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
