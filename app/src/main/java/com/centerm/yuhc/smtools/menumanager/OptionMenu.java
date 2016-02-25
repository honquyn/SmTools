package com.centerm.yuhc.smtools.menumanager;

import android.content.Context;
import android.content.Intent;

import com.centerm.yuhc.smtools.activitymanager.AppVersionActivity;
import com.centerm.yuhc.smtools.activitymanager.IntroductionActivity;

/**
 * Created by yuhc on 16-2-25.
 *
 */
public class OptionMenu {
    private Context context;
    public OptionMenu(Context mainContext){
        context = mainContext;
    }

    /**
     * 显示应用版本信息
     */
    public void ShowAppVersionInfo(){
        Intent intent = new Intent(context,AppVersionActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示用法说明
     */
    public void ShowIntroduction(){
        Intent intent = new Intent(context,IntroductionActivity.class);
        context.startActivity(intent);
    }
}
