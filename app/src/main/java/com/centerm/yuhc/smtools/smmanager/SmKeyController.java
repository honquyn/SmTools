package com.centerm.yuhc.smtools.smmanager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.yuhc.smtools.R;
import com.security.cipher.sm.SM2Utils;
import com.security.cipher.sm.Sm2KeyPair;
import com.security.cipher.sm.Util;

/**
 * Created by yuhc on 16-2-23.
 *
 */
public class SmKeyController implements View.OnClickListener {
    private static final String TAG = SmKeyController.class.getSimpleName();

    private EditText priKey;
    private EditText pubKey;

    Context context;
    public SmKeyController(Context context){
        this.context = context;
    }

    public View getSmKeyView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.smkey_activity, null);
        Button button = (Button) view.findViewById(R.id.general_key_pair_btn);
        button.setOnClickListener(this);
        button = (Button) view.findViewById(R.id.general_pub_key_btn);
        button.setOnClickListener(this);

        priKey = (EditText) view.findViewById(R.id.et_pri_key);
        pubKey = (EditText) view.findViewById(R.id.et_pub_key_result);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.general_key_pair_btn:
                Sm2KeyPair keyPair = SM2Utils.generateKeyPair();
                priKey.setText(Util.getHexString(keyPair.getPriKey(),true));
                pubKey.setText(Util.getHexString(keyPair.getPubKey(),true));
                break;
            case R.id.general_pub_key_btn:
                String key = priKey.getText().toString().trim();
                if (TextUtils.isEmpty(key) || key.length() != 64){
                    Toast.makeText(context, "请输入32字节的私钥", Toast.LENGTH_SHORT).show();
                    break;
                }
                byte[] publicKey = SM2Utils.generatePubKeyFromPriKey(Util.hexStringToBytes(key));
                pubKey.setText(Util.getHexString(publicKey));
                break;
        }
    }
}
