package com.example.motionauth.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.motionauth.R;

import java.io.File;


/**
 * 認証するユーザ名を入力させる
 *
 * @author Kensuke Kousaka
 */
public class AuthNameInput extends Activity {
    private static final String TAG = AuthNameInput.class.getSimpleName();

    // ユーザが入力した文字列（名前）を格納する
    public static String name;

    private Context current;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "--- onCreate ---");

        // タイトルバーの非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_auth_name_input);
        current = this;

        name = "";

        nameInput();
    }


    /**
     * ユーザ名を入力させる
     */
    private void nameInput () {
        Log.v(TAG, "--- nameInput ---");
        final EditText nameInput = (EditText) findViewById(R.id.nameInputEditText);

        nameInput.addTextChangedListener(new TextWatcher() {
            // 変更前
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }


            // 変更直前
            public void onTextChanged (CharSequence s, int start, int before, int count) {

            }


            // 変更後
            public void afterTextChanged (Editable s) {
                if (nameInput.getText() != null) {
                    name = nameInput.getText().toString();
                }
            }
        });

        // ソフトウェアキーボートのEnterキーを押した際に，ソフトウェアキーボードを閉じるようにする
        nameInput.setOnKeyListener(new OnKeyListener() {
            public boolean onKey (View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.i(TAG, "Push Enter Key");
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        // OKボタンを押した時に，次のアクティビティに移動
        final Button ok = (Button) findViewById(R.id.okButton);

        ok.setOnClickListener(new OnClickListener() {
            public void onClick (View v) {
                Log.i(TAG, "Click OK Button");

                // 指定したユーザが存在するかどうかを確認する
                if (checkFileExists()) {
                    Log.i(TAG, "User is Existed");
                    moveActivity("com.example.motionauth", "com.example.motionauth.Authentication.AuthMotion", true);
                }
                else {
                    Log.i(TAG, "User is not Existed");
                    Toast.makeText(current, "ユーザが登録されていません", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * 入力したユーザが以前に登録したことのあるユーザかどうかを確認 データがないのに認証はできない
     *
     * @return 登録したことがあるユーザであればtrue，登録したことがなければfalse
     */
    private boolean checkFileExists () {
        Log.v(TAG, "--- checkFileExists ---");
        String folderPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MotionAuth" + File.separator + "MotionAuth" + File.separator + name;
        File file = new File(folderPath);

        return file.exists();
    }


    /**
     * アクティビティを移動する
     *
     * @param pkgName 移動先のパッケージ名
     * @param actName 移動先のアクティビティ名
     * @param flg     戻るキーを押した際にこのアクティビティを表示させるかどうか
     */
    private void moveActivity (String pkgName, String actName, boolean flg) {
        Log.v(TAG, "--- moveActivity ---");
        Intent intent = new Intent();

        intent.setClassName(pkgName, actName);

        if (flg) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivityForResult(intent, 0);
    }
}
