package net.trileg.motionauth.Processing;

import static java.lang.Math.*;

public class RotateVector {
  /**
   * 与えられた軸・データ長の距離及び角度配列を，combineメソッドを利用して回転させる
   * @param distance 軸・データ長の距離配列
   * @param angle 軸・データ長の角度配列
   * @return 軸・データ長の距離を角度を用いて回転させたベクトル配列
   */
  public double[][] rotate (double[][] distance, double[][] angle) {
    double[][] rotated = new double[distance.length][distance[0].length];
    double[] combined;

    double angleX = 0.0, angleY = 0.0, angleZ = 0.0;

    for (int item = 0; item < angle[0].length; item++) {
      angleX += angle[0][item];
      angleY += angle[1][item];
      angleZ += angle[2][item];

      combined = combine(distance[0][item], distance[1][item], distance[2][item],
          toRadians(-angleX), toRadians(-angleY), toRadians(-angleZ));
      rotated[0][item] = combined[0];
      rotated[1][item] = combined[1];
      rotated[2][item] = combined[2];
    }

    return rotated;
  }


  /**
   * 与えられた距離データを回転させる
   * 回転は反時計まわりが基本のようなので，ピッチ等の値を正負逆転させている
   * 参考：http://usi3.com/Position_estimation_by_using_acceleration_sensor.html
   * @param distanceX 回転させる距離データ（x）
   * @param distanceY 回転させる距離データ（y）
   * @param distanceZ 回転させる距離データ（z）
   * @param pitch ピッチ
   * @param roll ロール
   * @param yaw ヨー
   * @return 回転させた距離データ
   */
  private double[] combine(double distanceX, double distanceY, double distanceZ,
                           double pitch, double roll, double yaw) {
    double sinPitch = round(sin(pitch));
    double cosPitch = round(cos(pitch));
    double sinRoll = round(sin(roll));
    double cosRoll = round(cos(roll));
    double sinYaw = round(sin(yaw));
    double cosYaw = round(cos(yaw));

    double vx = distanceX, vy = distanceY, vz = distanceZ;
    double tmpX, tmpY, tmpZ;

    // X軸周りの回転
    tmpY = vy;
    tmpZ = vz;
    vy = tmpY * cosPitch - tmpZ * sinPitch;
    vz = tmpY * sinPitch + tmpZ * cosPitch;

    // Y軸周りの回転
    tmpX = vx;
    tmpZ = vz;
    vx = tmpX * cosRoll + tmpZ * sinRoll;
    vz = -tmpX * sinRoll + tmpZ * cosRoll;

    // Z軸周りの回転
    tmpX = vx;
    tmpY = vy;
    vx = tmpX * cosYaw - tmpY * sinYaw;
    vy = tmpX * sinYaw + tmpY * cosYaw;

    return new double[]{vx, vy, vz};
  }
}
