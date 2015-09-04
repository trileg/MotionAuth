package net.trileg.motionauth.Processing;

import android.util.Log;
import net.trileg.motionauth.Authentication.InputName;
import net.trileg.motionauth.Utility.Enum;
import net.trileg.motionauth.Utility.LogUtil;
import net.trileg.motionauth.Utility.ManageData;


/**
 * Calculate correlation.
 *
 * @author Kensuke Kosaka
 */
public class Correlation {
  private ManageData mManageData = new ManageData();
  private Enum mEnum = new Enum();

  /**
   * Calculate correlation to check whether each motion data is same motion or not.
   *
   * @param distance     Double type 3-array distance data.
   * @param angle        Double type 3-array angle data.
   * @param ave_distance Double type 2-array distance average data.
   * @param ave_angle    Double type 2-array angle average data.
   * @return Result of correlation (Enum.MEASURE value).
   */
  public Enum.MEASURE measureCorrelation(double[][][] distance, double[][][] angle, double[][] ave_distance, double[][] ave_angle) {
    LogUtil.log(Log.INFO);

    // 相関係数の計算

    // Calculate of Average A
    float[][] sample_accel = new float[Enum.NUM_TIME][Enum.NUM_AXIS];
    float[][] sample_gyro = new float[Enum.NUM_TIME][Enum.NUM_AXIS];

    // iは回数
    for (int time = 0; time < Enum.NUM_TIME; time++) {
      // jはXYZ
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        for (int item = 0; item < distance[time][axis].length; item++) {
          sample_accel[time][axis] += distance[time][axis][item];
          sample_gyro[time][axis] += angle[time][axis][item];
        }
      }
    }

    for (int time = 0; time < Enum.NUM_TIME; time++) {
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        sample_accel[time][axis] /= distance[time][axis].length;
        sample_gyro[time][axis] /= angle[time][axis].length;
      }
    }

    // Calculate of Average B
    float ave_accel[] = new float[Enum.NUM_AXIS];
    float ave_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < ave_distance[axis].length; item++) {
        ave_accel[axis] += ave_distance[axis][item];
        ave_gyro[axis] += ave_angle[axis][item];
      }
    }

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      ave_accel[axis] /= ave_distance[axis].length;
      ave_gyro[axis] /= ave_angle[axis].length;
    }

    // Calculate of Sxx
    float Sxx_accel[][] = new float[Enum.NUM_TIME][Enum.NUM_AXIS];
    float Sxx_gyro[][] = new float[Enum.NUM_TIME][Enum.NUM_AXIS];

    for (int time = 0; time < Enum.NUM_TIME; time++) {
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        for (int item = 0; item < distance[time][axis].length; item++) {
          Sxx_accel[time][axis] += Math.pow((distance[time][axis][item] - sample_accel[time][axis]), 2);
          Sxx_gyro[time][axis] += Math.pow((angle[time][axis][item] - sample_gyro[time][axis]), 2);
        }
      }
    }

    // Calculate of Syy
    float Syy_accel[] = new float[Enum.NUM_AXIS];
    float Syy_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < ave_distance[axis].length; item++) {
        Syy_accel[axis] += Math.pow((ave_distance[axis][item] - ave_accel[axis]), 2);
        Syy_gyro[axis] += Math.pow((ave_angle[axis][item] - ave_gyro[axis]), 2);
      }
    }

    // Calculate of Sxy
    float[][] Sxy_accel = new float[Enum.NUM_TIME][Enum.NUM_AXIS];
    float[][] Sxy_gyro = new float[Enum.NUM_TIME][Enum.NUM_AXIS];

    for (int time = 0; time < Enum.NUM_TIME; time++) {
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        for (int item = 0; item < distance[time][axis].length; item++) {
          Sxy_accel[time][axis] += (distance[time][axis][item] - sample_accel[time][axis]) * (ave_distance[axis][item] - ave_accel[axis]);
          Sxy_gyro[time][axis] += (angle[time][axis][item] - sample_gyro[time][axis]) * (ave_angle[axis][item] - ave_gyro[axis]);
        }
      }
    }

    // Calculate of R
    double[][] R_accel = new double[Enum.NUM_TIME][Enum.NUM_AXIS];
    double[][] R_gyro = new double[Enum.NUM_TIME][Enum.NUM_AXIS];

    for (int time = 0; time < Enum.NUM_TIME; time++) {
      for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
        R_accel[time][axis] = Sxy_accel[time][axis] / Math.sqrt(Sxx_accel[time][axis] * Syy_accel[axis]);
        R_gyro[time][axis] = Sxy_gyro[time][axis] / Math.sqrt(Sxx_gyro[time][axis] * Syy_gyro[axis]);
      }
    }

    mManageData.writeRData("RegistLRdata", "R_accel", net.trileg.motionauth.Registration.InputName.name, R_accel);
    mManageData.writeRData("RegistLRdata", "R_gyro", net.trileg.motionauth.Registration.InputName.name, R_gyro);

    for (double[] i : R_accel) {
      for (double j : i) {
        LogUtil.log(Log.DEBUG, "R_accel: " + j);
      }
    }

    for (double[] i : R_gyro) {
      for (double j : i) {
        LogUtil.log(Log.DEBUG, "R_gyro: " + j);
      }
    }

    double R_point = 0.0;
    for (double[] i : R_accel) {
      for (double j : i) {
        R_point += j;
      }
    }
    for (double[] i : R_gyro) {
      for (double j : i) {
        R_point += j;
      }
    }

    R_point = R_point / 18;

    mManageData.writeR("R", net.trileg.motionauth.Registration.InputName.name, R_point);

    // X
    if ((R_accel[0][0] > mEnum.LOOSE && R_accel[1][0] > mEnum.LOOSE) || (R_accel[1][0] > mEnum.LOOSE && R_accel[2][0] > mEnum.LOOSE) || (R_accel[0][0] > mEnum.LOOSE && R_accel[2][0] > mEnum.LOOSE)) {
      // Y
      if ((R_accel[0][1] > mEnum.LOOSE && R_accel[1][1] > mEnum.LOOSE) || (R_accel[1][1] > mEnum.LOOSE && R_accel[2][1] > mEnum.LOOSE) || (R_accel[0][1] > mEnum.LOOSE && R_accel[2][1] > mEnum.LOOSE)) {
        // Z
        if ((R_accel[0][2] > mEnum.LOOSE && R_accel[1][2] > mEnum.LOOSE) || (R_accel[1][2] > mEnum.LOOSE && R_accel[2][2] > mEnum.LOOSE) || (R_accel[0][2] > mEnum.LOOSE && R_accel[2][2] > mEnum.LOOSE)) {
          // X
          if ((R_gyro[0][0] > mEnum.LOOSE && R_gyro[1][0] > mEnum.LOOSE) || (R_gyro[1][0] > mEnum.LOOSE && R_gyro[2][0] > mEnum.LOOSE) || (R_gyro[0][0] > mEnum.LOOSE || R_gyro[2][0] > mEnum.LOOSE)) {
            // Y
            if ((R_gyro[0][1] > mEnum.LOOSE && R_gyro[1][1] > mEnum.LOOSE) || (R_gyro[1][1] > mEnum.LOOSE && R_gyro[2][1] > mEnum.LOOSE) || (R_gyro[0][1] > mEnum.LOOSE || R_gyro[2][1] > mEnum.LOOSE)) {
              // Z
              if ((R_gyro[0][2] > mEnum.LOOSE && R_gyro[1][2] > mEnum.LOOSE) || (R_gyro[1][2] > mEnum.LOOSE && R_gyro[2][2] > mEnum.LOOSE) || (R_gyro[0][2] > mEnum.LOOSE && R_gyro[2][2] > mEnum.LOOSE)) {

                // X
                if ((R_accel[0][0] > mEnum.NORMAL && R_accel[1][0] > mEnum.NORMAL) || (R_accel[1][0] > mEnum.NORMAL && R_accel[2][0] > mEnum.NORMAL) || (R_accel[0][0] > mEnum.NORMAL && R_accel[2][0] > mEnum.NORMAL)) {
                  // Y
                  if ((R_accel[0][1] > mEnum.NORMAL && R_accel[1][1] > mEnum.NORMAL) || (R_accel[1][1] > mEnum.NORMAL && R_accel[2][1] > mEnum.NORMAL) || (R_accel[0][1] > mEnum.NORMAL && R_accel[2][1] > mEnum.NORMAL)) {
                    // Z
                    if ((R_accel[0][2] > mEnum.NORMAL && R_accel[1][2] > mEnum.NORMAL) || (R_accel[1][2] > mEnum.NORMAL && R_accel[2][2] > mEnum.NORMAL) || (R_accel[0][2] > mEnum.NORMAL && R_accel[2][2] > mEnum.NORMAL)) {
                      // X
                      if ((R_gyro[0][0] > mEnum.NORMAL && R_gyro[1][0] > mEnum.NORMAL) || (R_gyro[1][0] > mEnum.NORMAL && R_gyro[2][0] > mEnum.NORMAL) || (R_gyro[0][0] > mEnum.NORMAL && R_gyro[2][0] > mEnum.NORMAL)) {
                        // Y
                        if ((R_gyro[0][1] > mEnum.NORMAL && R_gyro[1][1] > mEnum.NORMAL) || (R_gyro[1][1] > mEnum.NORMAL && R_gyro[2][1] > mEnum.NORMAL) || (R_gyro[0][1] > mEnum.NORMAL && R_gyro[2][1] > mEnum.NORMAL)) {
                          // Z
                          if ((R_gyro[0][2] > mEnum.NORMAL && R_gyro[1][2] > mEnum.NORMAL) || (R_gyro[1][2] > mEnum.NORMAL && R_gyro[2][2] > mEnum.NORMAL) || (R_gyro[0][2] > mEnum.NORMAL && R_gyro[2][2] > mEnum.NORMAL)) {

                            // X
                            if ((R_accel[0][0] > mEnum.STRICT && R_accel[1][0] > mEnum.STRICT) || (R_accel[1][0] > mEnum.STRICT && R_accel[2][0] > mEnum.STRICT) || (R_accel[0][0] > mEnum.STRICT && R_accel[2][0] > mEnum.STRICT)) {
                              // Y
                              if ((R_accel[0][1] > mEnum.STRICT && R_accel[1][1] > mEnum.STRICT) || (R_accel[1][1] > mEnum.STRICT && R_accel[2][1] > mEnum.STRICT) || (R_accel[0][1] > mEnum.STRICT && R_accel[2][1] > mEnum.STRICT)) {
                                // Z
                                if ((R_accel[0][2] > mEnum.STRICT && R_accel[1][2] > mEnum.STRICT) || (R_accel[1][2] > mEnum.STRICT && R_accel[2][2] > mEnum.STRICT) || (R_accel[0][2] > mEnum.STRICT && R_accel[2][2] > mEnum.STRICT)) {
                                  // X
                                  if ((R_gyro[0][0] > mEnum.STRICT && R_gyro[1][0] > mEnum.STRICT) || (R_gyro[1][0] > mEnum.STRICT && R_gyro[2][0] > mEnum.STRICT) || (R_gyro[0][0] > mEnum.STRICT && R_gyro[2][0] > mEnum.STRICT)) {
                                    // Y
                                    if ((R_gyro[0][1] > mEnum.STRICT && R_gyro[1][1] > mEnum.STRICT) || (R_gyro[1][1] > mEnum.STRICT && R_gyro[2][1] > mEnum.STRICT) || (R_gyro[0][1] > mEnum.STRICT && R_gyro[2][1] > mEnum.STRICT)) {
                                      // Z
                                      if ((R_gyro[0][2] > mEnum.STRICT && R_gyro[1][2] > mEnum.STRICT) || (R_gyro[1][2] > mEnum.STRICT && R_gyro[2][2] > mEnum.STRICT) || (R_gyro[0][2] > mEnum.STRICT && R_gyro[2][2] > mEnum.STRICT)) {
                                        return Enum.MEASURE.PERFECT;
                                      }
                                      // NORMALより大きくSTRICT以下
                                      else {
                                        return Enum.MEASURE.CORRECT;
                                      }
                                    } else {
                                      return Enum.MEASURE.CORRECT;
                                    }

                                  } else {
                                    return Enum.MEASURE.CORRECT;
                                  }

                                } else {
                                  return Enum.MEASURE.CORRECT;
                                }
                              } else {
                                return Enum.MEASURE.CORRECT;
                              }
                            } else {
                              return Enum.MEASURE.CORRECT;
                            }

                          }
                          // LOOSEより大きくNORMAL以下
                          else {
                            return Enum.MEASURE.INCORRECT;
                          }
                        } else {
                          return Enum.MEASURE.INCORRECT;
                        }

                      } else {
                        return Enum.MEASURE.INCORRECT;
                      }

                    } else {
                      return Enum.MEASURE.INCORRECT;
                    }
                  } else {
                    return Enum.MEASURE.INCORRECT;
                  }
                } else {
                  return Enum.MEASURE.INCORRECT;
                }
              }
              // LOOSE以下
              else {
                return Enum.MEASURE.BAD;
              }
            } else {
              return Enum.MEASURE.BAD;
            }
          } else {
            return Enum.MEASURE.BAD;
          }
        } else {
          return Enum.MEASURE.BAD;
        }
      } else {
        return Enum.MEASURE.BAD;
      }
    } else {
      return Enum.MEASURE.BAD;
    }
  }


  /**
   * Calculate correlation to check whether each motion data is same motion or not.
   *
   * @param distance     Double type 2-array distance data.
   * @param angle        Double type 2-array angle data.
   * @param ave_distance Double type 2-array distance average data.
   * @param ave_angle    Double type 2-array angle average data.
   * @return Result of correlation (Enum.MEASURE value).
   */
  public Enum.MEASURE measureCorrelation(double[][] distance, double[][] angle, double[][] ave_distance, double[][] ave_angle) {
    LogUtil.log(Log.INFO);

    LogUtil.log(Log.DEBUG, "distancesample: " + String.valueOf(distance[0][0]));
    LogUtil.log(Log.DEBUG, "anglesample: " + String.valueOf(angle[0][0]));
    LogUtil.log(Log.DEBUG, "avedistancesample: " + String.valueOf(ave_distance[0][0]));
    LogUtil.log(Log.DEBUG, "aveanglesample: " + String.valueOf(ave_angle[0][0]));

    //region Calculate of Average A
    float[] sample_accel = new float[Enum.NUM_AXIS];
    float[] sample_gyro = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < distance[axis].length; item++) {
        sample_accel[axis] += distance[axis][item];
        sample_gyro[axis] += angle[axis][item];
      }
    }

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      sample_accel[axis] /= distance[axis].length;
      sample_gyro[axis] /= angle[axis].length;
    }
    //endregion

    //region Calculate of Average B
    float ave_accel[] = new float[Enum.NUM_AXIS];
    float ave_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < ave_distance[axis].length; item++) {
        ave_accel[axis] += ave_distance[axis][item];
        ave_gyro[axis] += ave_angle[axis][item];
      }
    }

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      ave_accel[axis] /= ave_distance[axis].length;
      ave_gyro[axis] /= ave_angle[axis].length;
    }
    //endregion

    //region Calculate of Sxx
    float Sxx_accel[] = new float[Enum.NUM_AXIS];
    float Sxx_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < distance[axis].length; item++) {
        Sxx_accel[axis] += Math.pow((distance[axis][item] - sample_accel[axis]), 2);
        Sxx_gyro[axis] += Math.pow((angle[axis][item] - sample_gyro[axis]), 2);
      }
    }
    //endregion

    //region Calculate of Syy
    float Syy_accel[] = new float[Enum.NUM_AXIS];
    float Syy_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < ave_distance[axis].length; item++) {
        Syy_accel[axis] += Math.pow((ave_distance[axis][item] - ave_accel[axis]), 2);
        Syy_gyro[axis] += Math.pow((ave_angle[axis][item] - ave_gyro[axis]), 2);
      }
    }
    //endregion

    //region Calculate of Sxy
    float Sxy_accel[] = new float[Enum.NUM_AXIS];
    float Sxy_gyro[] = new float[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      for (int item = 0; item < distance[axis].length; item++) {
        Sxy_accel[axis] += (distance[axis][item] - sample_accel[axis]) * (ave_distance[axis][item] - ave_accel[axis]);
        Sxy_gyro[axis] += (angle[axis][item] - sample_gyro[axis]) * (ave_angle[axis][item] - ave_gyro[axis]);
      }
    }
    //endregion

    //region Calculate of R
    double R_accel[] = new double[Enum.NUM_AXIS];
    double R_gyro[] = new double[Enum.NUM_AXIS];

    for (int axis = 0; axis < Enum.NUM_AXIS; axis++) {
      R_accel[axis] = Sxy_accel[axis] / Math.sqrt(Sxx_accel[axis] * Syy_accel[axis]);
      LogUtil.log(Log.DEBUG, "R_accel" + axis + ": " + R_accel[axis]);
      R_gyro[axis] = Sxy_gyro[axis] / Math.sqrt(Sxx_gyro[axis] * Syy_gyro[axis]);
      LogUtil.log(Log.DEBUG, "R_gyro" + axis + ": " + R_gyro[axis]);
    }
    //endregion

    mManageData.writeRData("AuthRData", InputName.userName, R_accel, R_gyro);

    //region 相関の判定
    //相関係数が一定以上あるなら認証成功
    if (R_accel[0] > 0.5) {
      if (R_accel[1] > 0.5) {
        if (R_accel[2] > 0.5) {
          if (R_gyro[0] > 0.5) {
            if (R_gyro[1] > 0.5) {
              if (R_gyro[2] > 0.5) {
                return Enum.MEASURE.CORRECT;
              } else {
                return Enum.MEASURE.INCORRECT;
              }
            } else {
              return Enum.MEASURE.INCORRECT;
            }
          } else {
            return Enum.MEASURE.INCORRECT;
          }
        } else {
          return Enum.MEASURE.INCORRECT;
        }
      } else {
        return Enum.MEASURE.INCORRECT;
      }
    } else {
      return Enum.MEASURE.INCORRECT;
    }
    //endregion
  }
}
