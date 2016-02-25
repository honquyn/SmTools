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
public class IntroductionActivity extends Activity {
    private static final String[] introduction = {
            "编辑框内的数据都是十六进制数。",
            "使用规范推荐的参数进行计算(ID:1234567812345678)。",
            "密钥都是明文，公钥长度64字节，私钥长度32字节。",
            "SM4加解密的输入数据必须是16字节的倍数,目前只支持ECB算法。",
            "SM2加密结果不包含标识符'04'，输入数据已兼容标识符处理。"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_info);

        TextView tv = (TextView) findViewById(R.id.tv_info);
        tv.setText(getIntroductionInfo());
    }

    private String getIntroductionInfo(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < introduction.length; i++) {
            builder.append(String.format("%d.", i+1)).append(introduction[i]).append("\n");
        }
        return builder.toString();
    }

}
