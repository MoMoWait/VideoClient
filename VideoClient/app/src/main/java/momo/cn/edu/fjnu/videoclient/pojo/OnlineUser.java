package momo.cn.edu.fjnu.videoclient.pojo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 在线用户
 * Created by GaoFei on 2016/3/25.
 */
@Table(name = "OnlineUser")
public class OnlineUser {
    @Column(name = "id", isId = true, autoGen = false)
    private int id;
    @Column(name = "user_id")
    private int user_id;
    @Column(name = "address")
    private String address;
    @Column(name = "lng")
    private double lng;
    @Column(name = "lat")
    private double lat;
    @Column(name = "time")
    private int time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OnlineUser{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", address='" + address + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", time=" + time +
                '}';
    }
}
