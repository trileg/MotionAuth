package net.trileg.motionauth.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import net.trileg.motionauth.Processing.CipherCrypt;

import java.io.*;
import java.util.ArrayList;


/**
 * Write data to SD Card.
 *
 * @author Kensuke Kosaka
 */
public class ManageData {
  private static final String APP_NAME = "MotionAuth";
  private FileOutputStream fos;
  private OutputStreamWriter osw;
  private BufferedWriter bw;


  /**
   * @param userName   User name.
   * @param dataName Data name.
   * @param sensorName   Sensor name.
   * @param data       Double type 2-array data to write.
   * @return Return true when write data complete, otherwise false.
   */
  public boolean writeDoubleTwoArrayData(String userName, String dataName, String sensorName, double[][] data) {
    LogUtil.log(Log.INFO);

    String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SDCard not mounted");
      return false;
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String FOLDER_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName + File.separator + dataName;

    File file = new File(FOLDER_PATH);

    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.ERROR, "Make directory error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
      return false;
    }

    try {
      String filePath = FOLDER_PATH + File.separator + sensorName + ".dat";
      file = new File(filePath);

      fos = new FileOutputStream(file, false);
      osw = new OutputStreamWriter(fos, "UTF-8");
      bw = new BufferedWriter(osw);

      for (int item = 0; item < data[0].length; item++) {
        bw.write(data[0][item] + " " + data[1][item] + " " + data[2][item] + "\n");
      }

      bw.flush();
      bw.close();
      osw.close();
      fos.close();
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
      return false;
    }
    return true;
  }


  /**
   * @param userName   User name.
   * @param dataName Data name.
   * @param sensorName   Sensor name.
   * @param data       Double type 3-array data to write.
   */
  public void writeDoubleThreeArrayData(String userName, String dataName, String sensorName, double[][][] data) {
    LogUtil.log(Log.INFO);

    String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SDCard not mounted");
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String FOLDER_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName + File.separator + dataName;

    File file = new File(FOLDER_PATH);

    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.ERROR, "Make directory Error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      for (int time = 0; time < Enum.NUM_TIME; time++) {
        String filePath = FOLDER_PATH + File.separator + sensorName + String.valueOf(time) + ".dat";
        file = new File(filePath);

        fos = new FileOutputStream(file, false);
        osw = new OutputStreamWriter(fos, "UTF-8");
        bw = new BufferedWriter(osw);

        for (int item = 0; item < data[time][0].length; item++) {
          bw.write(data[time][0][item] + " " + data[time][1][item] + " " + data[time][2][item] + "\n");
        }

        bw.flush();
        bw.close();
        osw.close();
        fos.close();
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }


  public void writeRData(String userName, String dataName, double[][] R_accel, double[][] R_linearAccel, double[][] R_gyro) {
    LogUtil.log(Log.INFO);

    String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SD card not mounted");
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String DIRECTORY_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName;

    File file = new File(DIRECTORY_PATH);

    try {
      if (!file.exists() && !file.mkdirs()) {
        LogUtil.log(Log.ERROR, "Make directory error");
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      String filePath = DIRECTORY_PATH + File.separator + dataName + ".dat";
      file = new File(filePath);

      fos = new FileOutputStream(file, false);
      osw = new OutputStreamWriter(fos, "UTF-8");
      bw = new BufferedWriter(osw);

      String[] dimension = {"x", "y", "z"};
      for (int time = 0; time < Enum.NUM_TIME; time++) {
        for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
          bw.write(String.valueOf(time + 1) + "_" + "R-accel" + "_" + dimension[axis] + " " + R_accel[time][axis] + "\n");
        }
        for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
          bw.write(String.valueOf(time + 1) + "_" + "R-linearAccel" + "_" + dimension[axis] + " " + R_linearAccel[time][axis] + "\n");
        }
        for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
          bw.write(String.valueOf(time + 1) + "_" + "R-gyro" + "_" + dimension[axis] + " " + R_gyro[time][axis] + "\n");
        }
      }

      bw.flush();
      bw.close();
      osw.close();
      fos.close();
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }


  /**
   * @param userName   User name.
   * @param dataName Data name.
   * @param R_accel    Double type 1-array distance-R data.
   * @param R_linearAccel Double type 1-array linear distance R data.
   * @param R_gyro     Double type 1-array angle-R data.
   */
  public void writeRData(String userName, String dataName, double[] R_accel, double[] R_linearAccel, double[] R_gyro) {
    LogUtil.log(Log.INFO);

    String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SDCard not mounted");
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String FOLDER_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName;

    File file = new File(FOLDER_PATH);

    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.ERROR, "Make directory error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      String filePath = FOLDER_PATH + File.separator + dataName + ".dat";
      file = new File(filePath);

      fos = new FileOutputStream(file, false);
      osw = new OutputStreamWriter(fos, "UTF-8");
      bw = new BufferedWriter(osw);

      String[] dimension = {"x", "y", "z"};
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        bw.write("R_accel" + "_" + dimension[axis] + " " + R_accel[axis] + "\n");
      }
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        bw.write("R_linearAccel" + "_" + dimension[axis] + " " + R_linearAccel[axis] + "\n");
      }
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        bw.write("R_gyro" + "_" + dimension[axis] + " " + R_gyro[axis] + "\n");
      }

      bw.flush();
      bw.close();
      osw.close();
      fos.close();
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }


  public void writeR(String userName, String dataName, double data) {
    LogUtil.log(Log.INFO);

    String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SDCard not mounted");
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String FOLDER_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName;

    File file = new File(FOLDER_PATH);

    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.ERROR, "Make directory error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      String filePath = FOLDER_PATH + File.separator + dataName + ".dat";
      file = new File(filePath);

      fos = new FileOutputStream(file, false);
      osw = new OutputStreamWriter(fos);
      bw = new BufferedWriter(osw);

      bw.write(String.valueOf(data));

      bw.flush();
      bw.close();
      osw.close();
      fos.close();
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }


  /**
   * Save authentication key data which is collected from Registration.
   *
   * @param userName        User name.
   * @param averageDistance Double type 2-array average distance data.
   * @param averageLinearDistance Double type 2-array average linear distance data.
   * @param averageAngle    Double type 2-array average angle data.
   * @param ampValue        Amplifier value.
   * @param context         Caller context.
   */
  public void writeRegisterData(String userName, double[][] averageDistance, double[][] averageLinearDistance,
                                double[][] averageAngle, double ampValue, Context context) {

    LogUtil.log(Log.INFO);

    CipherCrypt mCipherCrypt = new CipherCrypt(context);

    String[][] averageDistanceStr = new String[averageDistance.length][averageDistance[0].length];
    String[][] averageLinearDistanceStr = new String[averageLinearDistance.length][averageLinearDistance[0].length];
    String[][] averageAngleStr = new String[averageAngle.length][averageAngle[0].length];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < averageDistance[axis].length; item++) {
        averageDistanceStr[axis][item] = String.valueOf(averageDistance[axis][item]);
        averageLinearDistanceStr[axis][item] = String.valueOf(averageLinearDistance[axis][item]);
        averageAngleStr[axis][item] = String.valueOf(averageAngle[axis][item]);
      }
    }

    // 暗号化
    String[][] encryptedAverageDistanceStr = mCipherCrypt.encrypt(averageDistanceStr);
    String[][] encryptedAverageLinearDistanceStr = mCipherCrypt.encrypt(averageLinearDistanceStr);
    String[][] encryptedAverageAngleStr = mCipherCrypt.encrypt(averageAngleStr);

    // 配列データを特定文字列を挟んで連結する
    ConvertArrayAndString mConvertArrayAndString = new ConvertArrayAndString();
    String registerDistanceData = mConvertArrayAndString.arrayToString(encryptedAverageDistanceStr);
    String registerLinearDistanceData = mConvertArrayAndString.arrayToString(encryptedAverageLinearDistanceStr);
    String registerAngleData = mConvertArrayAndString.arrayToString(encryptedAverageAngleStr);

    Context mContext = context.getApplicationContext();
    SharedPreferences userPref = mContext.getSharedPreferences("UserList", Context.MODE_PRIVATE);
    SharedPreferences.Editor userPrefEditor = userPref.edit();

    userPrefEditor.putString(userName, "");
    userPrefEditor.apply();

    SharedPreferences preferences = mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    editor.putString(userName + "distance", registerDistanceData);
    editor.putString(userName + "linearDistance", registerLinearDistanceData);
    editor.putString(userName + "angle", registerAngleData);
    editor.putString(userName + "amplify", String.valueOf(ampValue));
    editor.apply();
  }


  /**
   * Read registered data from SharedPreferences
   *
   * @param context  Context use to get Application unique SharedPreferences.
   * @param userName User name.
   * @return Double type 2-array registered data list.
   */
  public ArrayList<double[][]> readRegisteredData(Context context, String userName) {
    LogUtil.log(Log.INFO);
    Context mContext = context.getApplicationContext();

    SharedPreferences preferences = mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);

    String registeredDistanceData = preferences.getString(userName + "distance", "");
    String registeredLinearDistanceData = preferences.getString(userName + "linearDistance", "");
    String registeredAngleData = preferences.getString(userName + "angle", "");

    if ("".equals(registeredDistanceData)) throw new RuntimeException();

    ConvertArrayAndString mConvertArrayAndString = new ConvertArrayAndString();
    CipherCrypt mCipherCrypt = new CipherCrypt(context);

    String[][] decryptedDistance = mCipherCrypt.decrypt(mConvertArrayAndString.stringToArray(registeredDistanceData));
    String[][] decryptedLinearDistance = mCipherCrypt.decrypt(mConvertArrayAndString.stringToArray(registeredLinearDistanceData));
    String[][] decryptedAngle = mCipherCrypt.decrypt(mConvertArrayAndString.stringToArray(registeredAngleData));

    double[][] distance = new double[decryptedDistance.length][decryptedDistance[0].length];
    double[][] linearDistance = new double[decryptedLinearDistance.length][decryptedLinearDistance[0].length];
    double[][] angle = new double[decryptedAngle.length][decryptedAngle[0].length];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < decryptedDistance[axis].length; item++) {
        distance[axis][item] = Double.valueOf(decryptedDistance[axis][item]);
        linearDistance[axis][item] = Double.valueOf(decryptedLinearDistance[axis][item]);
        angle[axis][item] = Double.valueOf(decryptedAngle[axis][item]);
      }
    }

    ArrayList<double[][]> result = new ArrayList<>();
    result.add(distance);
    result.add(linearDistance);
    result.add(angle);

    return result;
  }


  /**
   * @param userName  User name.
   * @param dataName Data name.
   * @param sensorName Sensor name.
   * @param data Float type 3-array list data.
   */
  public void writeFloatData(String userName, String dataName, String sensorName, float[][][] data) {
    LogUtil.log(Log.INFO);

    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SD-Card not mounted");
      return;
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String DIR_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName + File.separator + dataName;

    File file = new File(DIR_PATH);
    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.DEBUG, "Make directory Error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      for (int time = 0; time < Enum.NUM_TIME; time++) {
        String filePath = DIR_PATH + File.separator + sensorName + String.valueOf(time) + ".dat";
        file = new File(filePath);

        fos = new FileOutputStream(file, false);
        osw = new OutputStreamWriter(fos, "UTF-8");
        bw = new BufferedWriter(osw);

        for (int item = 0; item < data[time][0].length; item++) {
          bw.write(data[time][0][item] + " " + data[time][1][item] + " " + data[time][2][item] + "\n");
        }

        bw.flush();
        bw.close();
        osw.close();
        fos.close();
      }
    } catch (IOException e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }


  public void writeFloatData(String userName, String dataName, String sensorName, float[][] data) {
    LogUtil.log(Log.INFO);

    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      LogUtil.log(Log.ERROR, "SD-Card not mounted");
      return;
    }

    String SD_PATH = Environment.getExternalStorageDirectory().getPath();
    String DIR_PATH = SD_PATH + File.separator + APP_NAME + File.separator + userName + File.separator + dataName;

    File file = new File(DIR_PATH);
    try {
      if (!file.exists()) {
        if (!file.mkdirs()) {
          LogUtil.log(Log.DEBUG, "Make directory Error");
        }
      }
    } catch (Exception e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }

    try {
      String filePath = DIR_PATH + File.separator + sensorName + ".dat";
      file = new File(filePath);

      fos = new FileOutputStream(file, false);
      osw = new OutputStreamWriter(fos, "UTF-8");
      bw = new BufferedWriter(osw);

      for (int item = 0; item < data[0].length; item++) {
        bw.write(data[0][item] + " " + data[1][item] + " " + data[2][item] + "\n");
      }

      bw.flush();
      bw.close();
      osw.close();
      fos.close();
    } catch (IOException e) {
      LogUtil.log(Log.ERROR, e.getMessage(), e.getCause());
    }
  }
}
