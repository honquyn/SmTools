package com.centerm.yuhc.smtools.activitymanager;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.centerm.yuhc.smtools.R;

/**
 * Created by yuhc on 16-2-25.
 * 应用版本信息显示
 */
public class AppVersionActivity extends Activity {
    private static final String APP_NAME = "国密算法工具";
    private static final String APP_VERSION = "V0.90";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_version);

        TextView tv = (TextView) findViewById(R.id.tv_version);
        tv.setText(getVersionInfo());
        tv = (TextView) findViewById(R.id.tv_change_log);
        tv.setText(getChangeLogInfo());
    }

    private String getVersionInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("应用名称：" + APP_NAME + "\n");
        builder.append("应用版本：" + APP_VERSION + "\n");
        builder.append("发布日期：" + "2016-02-25");
        builder.append("作者：" + "yuhonqin@163.com");
        return builder.toString();
    }

    private String getVersionFromApk(){
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(),0);
            String version = info.versionName;
            return getString(R.string.app_name) + version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本信息获取失败！";
        }

    }

    private String getChangeLogInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("【更新记录】" + "\n");
        builder.append("Version：" + APP_VERSION + "\n");
        builder.append("首个发布版本，实现国密基本功能。");
        return builder.toString();
    }
}
