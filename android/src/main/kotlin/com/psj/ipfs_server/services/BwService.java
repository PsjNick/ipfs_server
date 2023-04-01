package com.psj.ipfs_server.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.psj.ipfs_server.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.ipfsbox.library.IpfsBox;
import org.ipfsbox.library.entity.Stats_bw;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BwService extends Service {
    Timer timer;

    public BwService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new IpfsBox().stats().bw(new Callback<Stats_bw>() {
                    @Override
                    public void onResponse(Call<Stats_bw> call, Response<Stats_bw> response) {
                        EventBus.getDefault().post(response.body());
                    }

                    @Override
                    public void onFailure(Call<Stats_bw> call, Throwable t) {
                        if (timer != null) {
                            timer.cancel();
                        }
                        Stats_bw stats_bw = new Stats_bw();
                        stats_bw.setRateIn(0);
                        stats_bw.setRateOut(0);
                        EventBus.getDefault().post(stats_bw);

                        stopService(new Intent(BwService.this, BwService.class));
                        LogUtils.e(t.getMessage() + "false");

                    }
                });
            }
        }, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

}
