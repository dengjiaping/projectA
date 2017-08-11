package com.hzease.tomeet.utils.autoUpdate;

import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hzease.tomeet.PTApplication;
import com.hzease.tomeet.data.AppVersionBean;
import com.orhanobut.logger.Logger;

public class AutoUpdateService extends AVersionService {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        Logger.e("response: " + response);
        AppVersionBean appVersionBean = new Gson().fromJson(response, AppVersionBean.class);
        if (appVersionBean.isSuccess() && !TextUtils.isEmpty(PTApplication.appVersion) && !appVersionBean.getData().getVersion().equals(PTApplication.appVersion)) {
            service.showVersionDialog(appVersionBean.getData().getDownUrl(), "检测到新版本", appVersionBean.getData().getMessage());
        }
    }
}