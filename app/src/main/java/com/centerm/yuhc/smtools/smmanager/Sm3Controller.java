package com.centerm.yuhc.smtools.smmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.centerm.yuhc.smtools.R;
import com.security.cipher.sm.SM2Utils;
import com.security.cipher.sm.SM3Digest;
import com.security.cipher.sm.Util;

import java.lang.ref.WeakReference;

/**
 * Created by yuhc on 16-2-23.
 *
 */
public class Sm3Controller implements View.OnClickListener {
    private static final String TAG = Sm3Controller.class.getSimpleName();
    Context context;

    public static final int TYPE_HASH = 1;

    public static final int MSG_OK = 1;
    public static final int MSG_ERR = 2;

    public static final String TAG_HASH_DATA = "result_hash";
    private Handler communication;

    private EditText etUserData;
    private EditText etHashData;
    private String userData;

    public Sm3Controller(Context context){
        this.context = context;
    }

    public View getSm3View(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sm3_activity, null);
        Button button = (Button) view.findViewById(R.id.cal_hash_btn);
        button.setOnClickListener(this);

        etUserData = (EditText) view.findViewById(R.id.et_input);
        etHashData = (EditText) view.findViewById(R.id.et_hash_result);

        initCommunication();
        return view;
    }

    private void initCommunication() {
        communication = new ExchangeData(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cal_hash_btn:
                if (checkHashActivityData(R.id.cal_hash_btn)) {
                    StarHashTask();
                }
                break;
        }
    }

    //签名操作
    private void StarHashTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] data = Util.hexStringToBytes(userData);
                byte[] hash = SM3Digest.calHash(data);

                Message msg = Message.obtain();
                msg.what = TYPE_HASH;
                msg.arg1 = MSG_OK;
                Bundle bundle = new Bundle();
                bundle.putByteArray(TAG_HASH_DATA, hash);
                msg.setData(bundle);
                communication.sendMessage(msg);
            }
        }.start();
    }

    //检查编辑框的输入数据
    private boolean checkHashActivityData(int btnId){
        userData = etUserData.getText().toString().trim();
        switch (btnId)
        {
            case R.id.cal_hash_btn:
                if (TextUtils.isEmpty(userData))
                {
                    Toast.makeText(context, "请输入数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userData.length() & 0x00000001) == 1)
                {
                    Toast.makeText(context, "请输入偶数个字符！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    //后台处理线程与前台界面的通讯
    static class ExchangeData extends Handler {
        WeakReference<Sm3Controller> view;
        public ExchangeData(Sm3Controller context){
            view = new WeakReference<Sm3Controller>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            Sm3Controller context = view.get();
            switch (msg.what){
                case TYPE_HASH:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"计算失败！",Toast.LENGTH_SHORT).show();
                    }else {
                        byte[] hashData = msg.getData().getByteArray(TAG_HASH_DATA);
                        if (hashData != null) {
                            context.etHashData.setText(Util.getHexString(hashData));
                        }
                    }
                    break;
            }
        }
    }
}
