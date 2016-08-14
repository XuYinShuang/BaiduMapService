package com.zhiyuan3g.baidumapservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ServiceClass extends Service {
    LocationClient client;
    private String LoCity = "";
    private MyBind binder = new MyBind();

    private static Handler handle; ///线程同步

    public static void setHandle(Handler handle) {
        ServiceClass.handle = handle;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocationCity();
    }

    private void LocationCity() {
        try {
            client = new LocationClient(this);
            client.registerNotifyLocationListener(new MyLocationListener());
            LocationClientOption OPTION = new LocationClientOption();
            OPTION.setOpenGps(true);
            //定位结果中包含位置信息
            OPTION.setAddrType("all");
            OPTION.setCoorType("bd0911");
            OPTION.setScanSpan(1000);
            client.setLocOption(OPTION);
            client.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                Toast.makeText(getApplicationContext(), "无法连接网络，定位失败", Toast.LENGTH_SHORT).show();

                return;
            }
            /*
            定位方式：1.GPS 2.网络 3.基站
             */

            StringBuffer buffer = new StringBuffer(256);

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                buffer.append(bdLocation.getCity()); //追加城市名称
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                buffer.append(bdLocation.getCity());
            }
            //判断是否有数据
            // TextUtils:当传入的参数为空，或者空字符串的情况下 返回true
            if (!TextUtils.isEmpty(buffer.toString())) {
                if (handle != null) {
                    LoCity = buffer.toString();
                    handle.sendEmptyMessage(0);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (handle != null) {
            handle.removeMessages(0);
        }
        if (client != null) {
            client.stop();
        }

        super.onDestroy();

    }

    //Binder的内部类
    public class MyBind extends Binder {
        public MyBind() {

        }

        public String getLocation() {
            return LoCity;
        }
    }
}
