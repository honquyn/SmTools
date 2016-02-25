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
import com.security.cipher.sm.Util;

import java.lang.ref.WeakReference;


/**
 * Created by yuhc on 16-2-23.
 *
 */
public class Sm2Controller implements View.OnClickListener {

    public static final int PRI_KEY_STR_LEN = 64;   //私钥长度为32字节
    public static final int PUB_KEY_STR_LEN = 128;  //公钥长度为64字节
    public static final int SIGN_DATA_STR_LEN = 128;   //签名数据长度64字节，与公钥长度一致

    public static final int TYPE_SIGN = 1;
    public static final int TYPE_VERIFY = 2;
    public static final int TYPE_ENCRYPT = 3;
    public static final int TYPE_DECRYPT = 4;

    public static final int MSG_OK = 1;
    public static final int MSG_ERR = 2;

    public static final String TAG_SIGN_DATA = "result_sign";
    public static final String TAG_ENCRYPT_DATA = "result_encrypt";
    public static final String TAG_DECRYPT_DATA = "result_decrypt";

    private static final String TAG = Sm2Controller.class.getSimpleName();
    private EditText etUserData;
    private EditText etSignedData;
    private EditText etPubKey;
    private EditText etPriKey;
    private EditText etEncryptData;

    private EditText etUserDataSign;
    private EditText etPubKeySign;
    private EditText etPriKeySign;

    private String priKey;
    private String pubKey;
    private String userData;
    private String signedData;

    private String priKeySign;
    private String pubKeySign;
    private String userDataSign;

    private Handler communication;

    ProgressDialog dialog;

    Context context;

    public Sm2Controller(Context context){
        this.context = context;
    }

    public View getSm2View(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sm2_activity, null);
        Button button = (Button) view.findViewById(R.id.bt_sign_sm2);
        button.setOnClickListener(this);

        button = (Button) view.findViewById(R.id.bt_verify_sm2);
        button.setOnClickListener(this);

        button = (Button) view.findViewById(R.id.bt_encrypt_sm2);
        button.setOnClickListener(this);

        button = (Button) view.findViewById(R.id.bt_decrypt_sm2);
        button.setOnClickListener(this);


        return view;
    }

    public View getSm2EncryptView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sm2_encrypt_activity, null);
        Button button = (Button) view.findViewById(R.id.bt_encrypt_sm2_single);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.bt_decrypt_sm2_single);
        button.setOnClickListener(this);

        etUserData = (EditText) view.findViewById(R.id.et_input_plain_sm2);
        etPriKey = (EditText) view.findViewById(R.id.et_prikey_sm2);
        etPubKey = (EditText) view.findViewById(R.id.et_pubkey_sm2);
        etEncryptData = (EditText) view.findViewById(R.id.et_result);

        initCommunication();
        return view;
    }

    public View getSm2SignView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sm2_sign_activity, null);
        Button button = (Button) view.findViewById(R.id.bt_sign_single);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.bt_verify_single);
        button.setOnClickListener(this);

        etUserDataSign = (EditText) view.findViewById(R.id.et_input_plain_sm2);
        etSignedData = (EditText) view.findViewById(R.id.et_signed_sm2);
        etPriKeySign = (EditText) view.findViewById(R.id.et_prikey_sm2);
        etPubKeySign = (EditText) view.findViewById(R.id.et_pubkey_sm2);

        initCommunication();
        return view;
    }

    private void initCommunication() {
        communication = new ExchangeData(this);
        if(dialog == null){
            dialog = new ProgressDialog(context);
            dialog.setTitle("SMTools");
            dialog.setMessage("处理中，请稍候...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_sign_sm2:
                Log.i(TAG, "Sign button clicked!");
                break;
            case R.id.bt_verify_sm2:
                Log.i(TAG, "Verify button clicked!");
                break;
            case R.id.bt_encrypt:
                Log.i(TAG, "Encrypt button clicked!");
                break;
            case R.id.bt_decrypt:
                Log.i(TAG, "Decrypt button clicked!");
                break;
            case R.id.bt_sign_single:
                if (checkSignActivityData(R.id.bt_sign_single)){
                    if (dialog != null) {
                        dialog.setTitle("正在签名");
                        dialog.show();
                    }
                    StarSignTask();
                }
                break;
            case R.id.bt_verify_single:
                if (checkSignActivityData(R.id.bt_verify_single)){
                    if (dialog != null) {
                        dialog.setTitle("正在验证签名");
                        dialog.show();
                    }
                    StarVerifyTask();
                }
                break;
            case R.id.bt_encrypt_sm2_single:
                if (checkEncryptActivityData(R.id.bt_encrypt_sm2_single)){
                    if (dialog != null) {
                        dialog.setTitle("正在加密");
                        dialog.show();
                    }
                    StarEncryptTask();
                }
                break;
            case R.id.bt_decrypt_sm2_single:
                if (checkEncryptActivityData(R.id.bt_decrypt_sm2_single)){
                    if (dialog != null) {
                        dialog.setTitle("正在解密");
                        dialog.show();
                    }
                    StarDecryptTask();
                }
                break;
        }
    }

    //签名操作
    private void StarSignTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] key = Util.hexStringToBytes(priKeySign);
                byte[] data = Util.hexStringToBytes(userDataSign);
                byte[] sign = SM2Utils.sign(key, data);

                Message msg = Message.obtain();
                msg.what = TYPE_SIGN;
                if (sign == null){
                    msg.arg1 = MSG_ERR;
                }else {
                    msg.arg1 = MSG_OK;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(TAG_SIGN_DATA, sign);
                    msg.setData(bundle);
                }
                communication.sendMessage(msg);
            }
        }.start();
    }

    //验签操作
    private void StarVerifyTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] key = Util.hexStringToBytes(pubKeySign);
                byte[] data = Util.hexStringToBytes(userDataSign);
                byte[] sign = Util.hexStringToBytes(signedData);

                boolean result = SM2Utils.verifySign(key, data, sign);
                Message msg = Message.obtain();
                msg.what = TYPE_VERIFY;
                msg.arg1 = result?MSG_OK:MSG_ERR;
                communication.sendMessage(msg);
            }
        }.start();
    }

    //加密操作
    private void StarEncryptTask(){

        new Thread(){
            @Override
            public void run() {
                byte[] key = Util.hexStringToBytes(pubKey);
                byte[] data = Util.hexStringToBytes(userData);

                byte[] result = SM2Utils.encrypt(key, data);
                Message msg = Message.obtain();

                msg.what = TYPE_ENCRYPT;
                if (result == null){
                    msg.arg1 = MSG_ERR;
                }else {
                    msg.arg1 = MSG_OK;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(TAG_ENCRYPT_DATA, result);
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
                byte[] key = Util.hexStringToBytes(priKey);
                byte[] data = Util.hexStringToBytes(userData);
                byte[] result = SM2Utils.decrypt(key, data);
                Message msg = Message.obtain();
                msg.what = TYPE_DECRYPT;
                if (result == null){
                    msg.arg1 = MSG_ERR;
                }else {
                    msg.arg1 = MSG_OK;
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(TAG_DECRYPT_DATA, result);
                    msg.setData(bundle);
                }
                communication.sendMessage(msg);
            }
        }.start();
    }

    //检查编辑框的输入数据
    private boolean checkSignActivityData(int btnId){
        priKeySign = etPriKeySign.getText().toString().trim();
        pubKeySign = etPubKeySign.getText().toString().trim();
        userDataSign = etUserDataSign.getText().toString().trim();
        signedData = etSignedData.getText().toString().trim();

        switch (btnId)
        {
            case R.id.bt_sign_single:
                if(TextUtils.isEmpty(priKeySign) || priKeySign.length() != PRI_KEY_STR_LEN)
                {
                    Toast.makeText(context, "请输入32字节的私钥！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(userDataSign))
                {
                    Toast.makeText(context, "请输入待签名的数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userDataSign.length() & 0x00000001) == 1)
                {
                    Toast.makeText(context, "请输入偶数个字符！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case R.id.bt_verify_single:
                if(TextUtils.isEmpty(pubKeySign) || pubKeySign.length() != PUB_KEY_STR_LEN)
                {
                    Toast.makeText(context, "请输入64字节的公钥！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(userDataSign))
                {
                    Toast.makeText(context, "请输入待验证的数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userDataSign.length() & 0x00000001) == 1)
                {
                    Toast.makeText(context, "请输入偶数个待验证的字符！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(TextUtils.isEmpty(signedData) || signedData.length() != SIGN_DATA_STR_LEN)
                {
                    Toast.makeText(context, "请输入64字节的签名！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    //检查编辑框的输入数据
    private boolean checkEncryptActivityData(int btnId){
        priKey = etPriKey.getText().toString().trim();
        pubKey = etPubKey.getText().toString().trim();
        userData = etUserData.getText().toString().trim();

        switch (btnId)
        {
            case R.id.bt_decrypt_sm2_single:
                if(TextUtils.isEmpty(priKey) || priKey.length() != PRI_KEY_STR_LEN)
                {
                    Toast.makeText(context, "请输入32字节的私钥！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(userData))
                {
                    Toast.makeText(context, "请输入密文数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userData.length() & 0x00000001) == 1)
                {
                    Toast.makeText(context, "密文数据请输入偶数个字符！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case R.id.bt_encrypt_sm2_single:
                if(TextUtils.isEmpty(pubKey) || pubKey.length() != PUB_KEY_STR_LEN)
                {
                    Toast.makeText(context, "请输入64字节的公钥！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(userData))
                {
                    Toast.makeText(context, "请输入待加密的数据！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if((userData.length() & 0x00000001) == 1)
                {
                    Toast.makeText(context, "明文数据请输入偶数个待验证的字符！",Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
        }
        return true;
    }

    //后台处理线程与前台界面的通讯
    static class ExchangeData extends Handler{
        WeakReference<Sm2Controller> view;
        public ExchangeData(Sm2Controller context){
            view = new WeakReference<Sm2Controller>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            Sm2Controller context = view.get();
            switch (msg.what){
                case TYPE_SIGN:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"签名失败！",Toast.LENGTH_SHORT).show();
                    }else {
                        byte[] signData = msg.getData().getByteArray(TAG_SIGN_DATA);
                        if (signData != null) {
                            context.etSignedData.setText(Util.getHexString(signData));
                        }
                    }
                    break;
                case TYPE_VERIFY:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"签名验证失败！",Toast.LENGTH_SHORT).show();
                        context.etSignedData.setText("");
                    }
                    else
                        Toast.makeText(context.context,"签名验证成功！",Toast.LENGTH_SHORT).show();

                    break;
                case TYPE_ENCRYPT:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"数据加密失败！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    byte[] encryptData = msg.getData().getByteArray(TAG_ENCRYPT_DATA);
                    if (encryptData != null)
                        context.etEncryptData.setText(Util.getHexString(encryptData));
                    break;
                case TYPE_DECRYPT:
                    if (msg.arg1 == MSG_ERR){
                        Toast.makeText(context.context,"数据解密失败！",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    byte[] decryptData = msg.getData().getByteArray(TAG_DECRYPT_DATA);
                    if (decryptData != null)
                        context.etEncryptData.setText(Util.getHexString(decryptData));
                    break;
            }
            if (context.dialog != null) {
                context.dialog.dismiss();
            }
        }
    }
}
