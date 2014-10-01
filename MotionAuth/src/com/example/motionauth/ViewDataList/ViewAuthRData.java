package com.example.motionauth.ViewDataList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.motionauth.R;

import java.io.*;
import java.util.ArrayList;

/**
 * 認証試験モードにおいて出た相関係数の結果を表示する
 *
 * @author Kensuke Kousaka
 */
public class ViewAuthRData extends Activity {
    private static final String TAG = ViewAuthRData.class.getSimpleName();

    String item = null;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "--- onCreate ---");

        setContentView(R.layout.activity_view_auth_rdata);

        viewAuthRData();
    }


    private void viewAuthRData () {
        Log.v(TAG, "--- viewAuthRData ---");

        Intent intent = getIntent();
        item = intent.getStringExtra("item");

        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ArrayList<String> dataList = readData();

        for (String i : dataList) {
            adapter.add(i);
        }

        lv.setAdapter(adapter);
    }


    private ArrayList<String> readData () {
        Log.v(TAG, "--- readData ---");
        ArrayList<String> dataList = new ArrayList<String>();

        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "MotionAuth" + File.separator + "AuthRData" + File.separator + item;

        File file = new File(filePath);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String s;

            while ((s = br.readLine()) != null) {
                dataList.add(s);
            }

            br.close();
            isr.close();
            fis.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return dataList;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return dataList;
        }
        catch (IOException e) {
            e.printStackTrace();
            return dataList;
        }
        return dataList;
    }
}