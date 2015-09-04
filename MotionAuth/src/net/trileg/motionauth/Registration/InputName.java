package net.trileg.motionauth.Registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.trileg.motionauth.R;
import net.trileg.motionauth.Utility.LogUtil;


/**
 * Input user name.
 *
 * @author Kensuke Kosaka
 */
public class InputName extends Activity {
  public static String name;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LogUtil.log(Log.INFO);

    // Disable title bar.
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_regist_name_input);

    name = "";

    nameInput();
  }


  /**
   * Input user name.
   */
  private void nameInput() {
    LogUtil.log(Log.INFO);

    final EditText nameInput = (EditText) findViewById(R.id.nameInputEditText);

    nameInput.addTextChangedListener(new TextWatcher() {
      // Before text changed.
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      // Just before text changed.
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      // After text changed.
      public void afterTextChanged(Editable s) {
        if (nameInput.getText() != null) name = nameInput.getText().toString().trim();
      }
    });

    // Close software keyboard if user push ENTER KEY.
    nameInput.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
          InputMethodManager inputMethodManager = (InputMethodManager) InputName.this.getSystemService(Context.INPUT_METHOD_SERVICE);
          inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

          return true;
        }
        return false;
      }
    });

    // Move activity if user push OK button.
    final Button ok = (Button) findViewById(R.id.okButton);

    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        LogUtil.log(Log.DEBUG, "Click ok button");
        // Check user inputted name is correctly.
        if (name.length() == 0) {
          Toast.makeText(InputName.this, "名前が入力されていません", Toast.LENGTH_LONG).show();
        } else {
          InputName.this.moveActivity(getPackageName(), getPackageName() + ".Registration.Registration", true);
        }
      }
    });
  }


  /**
   * Move activity.
   *
   * @param pkgName Destination package name.
   * @param actName Destination activity name.
   * @param flg     Whether to show this activity if user push BACK KEY.
   */
  private void moveActivity(String pkgName, String actName, boolean flg) {
    LogUtil.log(Log.INFO);

    Intent intent = new Intent();
    intent.setClassName(pkgName, actName);
    if (flg) intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

    startActivity(intent);
  }

}
