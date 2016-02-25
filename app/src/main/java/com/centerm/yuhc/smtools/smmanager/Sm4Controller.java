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
import com.security.cipher.sm.SM4Utils;
import com.security.cipher.sm.Util;

import java.lang.ref.WeakReference;

/**
 * Created by yuhc on 16-2-23.
 *
 */
public class Sm4Controller implements View.OnClickListener {
    private static final String TAG = Sm4Controller.class.getSimpleName();
    Context context;

    public static final int SM4_KEY_STR_LEN = 32;   //密钥长度为16字节

    public static final int TYPE_ENCRYPT = 1;
    public static final int TYPE_DECRYPT = 2;

    public static final int MSG_OK = 1;
    public static final int MSG_ERR = 2;

    public static final String TAG_ENCRYPT_DATA = "result_encrypt";
    public static final String TAG_DECRYPT_DATA = "result_decrypt";

    private EditText etUserData;
    private EditText etKey;
    private EditText etResult;

    private String userKey;
    private String userData;

    private Handler communication;

    public Sm4Controller(Context context){
        this.context = context;
    }

    public View getSm4View(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sm4_activity, null);
        Button button = (Button) view.findViewById(R.id.bt_encrypt);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.bt_decrypt);
        button.setOnClickListener(this);

        etUserData = (EditText) view.findViewById(R.id.et_input);
        etKey = (EditText) view.findViewById(R.id.et_key);
        etResult = (EditText) view.findViewById(R.id.et_result);

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
            case R.id.bt_encrypt:
                if (checkEncryptActivityData(R.id.bt_encrypt)) {
                    StarEncryptTask();
                }
                break;
            case R.id.bt_decrypt:
                if (checkEncryptActivityData(R.id.bt_decrypt)) {
                    StarDecryptTask();
                }
                break;
        }
    }

    private void StarEncryptTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] key = Util.hexStringToBytes(userKey);
                byte[] data = Util.hexStringToBytes(userData);
                byte[] encrypt = SM4Utils.encryptData(key,data);

                Message msg = Message.obtain();
                msg.what = TYPE_ENCRYPT;
                if (encrypt == null){
                    msg.arg1 = MSG_ERR;
                }else {
                    msg.arg1 = MSG_OK;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(TAG_ENCRYPT_DATA, encrypt);
                    msg.setData(bundle);
                }
                communication.sendMessage(msg);
            }
        }.start();
    }

    private void StarDecryptTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] key = Util.hexStringToBytes(userKey);
                byte[] data = Util.hexStringToBytes(userData);
                byte[] decrypt = SM4Utils.decryptData(key, data);

                Message msg = Message.obtain();
                msg.what = TYPE_DECRYPT;
                if (decrypt == null){
                    msg.arg1 = MSG_ERR;
                }else {
                    msg.arg1 = MSG_OK;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(TAG_DECRYPT_DATA, decrypt);
                    msg.setData(bundle);
                }
                communication.sendMessage(msg);
            }
        }.start();
    }

    private boolean checkEncryptActivityData(int btnId){
        userKey = etKey.getText().toString().trim();
        userData = etUserData.getText().toString().trim();

        switch (btnId)
        {
            case R.id.bt_encrypt:
            case R.id.bt_decrypt:
                if(TextUtils.isEmpty(userKey) || userKey.length() != SM4_KEY_STR_LEN)
                {
                    Toast.makeText(context, "请输入16字节的密钥！", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(userData))
                {
                    Toast.makeText(context, "请输入数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userData.length() & 0x0000000F) != 0)
                {
                    Toast.makeText(context, "请输入16字节倍数的数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    //后台处理线程与前台界面的通讯
    static class ExchangeData extends Handler{
        WeakReference<Sm4Controller> view;
        public ExchangeData(Sm4Controller context){
            view = new WeakReference<Sm4Controller>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            Sm4Controller context = view.get();
            switch (msg.what){
                case TYPE_ENCRYPT:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"数据加密失败！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    byte[] encryptData = msg.getData().getByteArray(TAG_ENCRYPT_DATA);
                    if (encryptData != null)
                        context.etResult.setText(Util.getHexString(encryptData));
                    break;
                case TYPE_DECRYPT:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"数据解密失败！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    byte[] decryptData = msg.getData().getByteArray(TAG_DECRYPT_DATA);
                    if (decryptData != null)
                        context.etResult.setText(Util.getHexString(decryptData));
                    break;
            }
        }
    }
}
