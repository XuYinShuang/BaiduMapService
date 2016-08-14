package com.zhiyuan3g.baidumapservice.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhiyuan3g.baidumapservice.R;
import com.zhiyuan3g.baidumapservice.service.ServiceClass;

public class MainActivity extends AppCompatActivity {
    Button btnMAP, btnUnbind;
    TextView txtCity;
    Intent intent;

    private boolean isBinder = false;

    private ServiceClass.MyBind binder;

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (binder != null) {
                    txtCity.setText(binder.getLocation());
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMAP = (Button) findViewById(R.id.MAP);
        btnUnbind = (Button) findViewById(R.id.btnUnbind);
        txtCity = (TextView) findViewById(R.id.txtCity);

        btnMAP.setOnClickListener(btnMAPListener);
        btnUnbind.setOnClickListener(btnUnbindListener);

        intent = new Intent(this, ServiceClass.class);

    }

    View.OnClickListener btnMAPListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isBinder) {
                isBinder = true;
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                ServiceClass.setHandle(handle);
            }

        }
    };
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ServiceClass.MyBind) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    View.OnClickListener btnUnbindListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isBinder == true) {
                unbindService(connection);
                isBinder = false;
            }

        }
    };
}
