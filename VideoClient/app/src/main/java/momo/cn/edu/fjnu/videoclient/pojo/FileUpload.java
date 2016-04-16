package momo.cn.edu.fjnu.videoclient.pojo;

/**
 * 文件上传类
 * Created by GaoFei on 2016/3/27.
 */
public class FileUpload {
    private int id;
    private int uid;
    private int type;
    private String url;
    private String file_name;
    private int file_size;
    private double lng;
    private double lat;
    private String address;
    private int create_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "id=" + id +
                ", uid=" + uid +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", file_name='" + file_name + '\'' +
                ", file_size=" + file_size +
                ", lng=" + lng +
                ", lat=" + lat +
                ", address='" + address + '\'' +
                ", create_time=" + create_time +
                '}';
    }
}
