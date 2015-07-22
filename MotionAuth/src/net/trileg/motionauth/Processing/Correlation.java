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
		float[][] sample_accel = new float[3][3];
		float[][] sample_gyro = new float[3][3];

		// iは回数
		for (int i = 0; i < 3; i++) {
			// jはXYZ
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < distance[i][j].length; k++) {
					sample_accel[i][j] += distance[i][j][k];
					sample_gyro[i][j] += angle[i][j][k];
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				sample_accel[i][j] /= distance[i][j].length;
				sample_gyro[i][j] /= angle[i][j].length;
			}
		}

		// Calculate of Average B
		float ave_accel[] = new float[3];
		float ave_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < ave_distance[i].length; j++) {
				ave_accel[i] += ave_distance[i][j];
				ave_gyro[i] += ave_angle[i][j];
			}
		}

		for (int i = 0; i < 3; i++) {
			ave_accel[i] /= ave_distance[i].length;
			ave_gyro[i] /= ave_angle[i].length;
		}

		// Calculate of Sxx
		float Sxx_accel[][] = new float[3][3];
		float Sxx_gyro[][] = new float[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < distance[i][j].length; k++) {
					Sxx_accel[i][j] += Math.pow((distance[i][j][k] - sample_accel[i][j]), 2);
					Sxx_gyro[i][j] += Math.pow((angle[i][j][k] - sample_gyro[i][j]), 2);
				}
			}
		}

		// Calculate of Syy
		float Syy_accel[] = new float[3];
		float Syy_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < ave_distance[i].length; j++) {
				Syy_accel[i] += Math.pow((ave_distance[i][j] - ave_accel[i]), 2);
				Syy_gyro[i] += Math.pow((ave_angle[i][j] - ave_gyro[i]), 2);
			}
		}

		// Calculate of Sxy
		float[][] Sxy_accel = new float[3][3];
		float[][] Sxy_gyro = new float[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < distance[i][j].length; k++) {
					Sxy_accel[i][j] += (distance[i][j][k] - sample_accel[i][j]) * (ave_distance[j][k] - ave_accel[j]);
					Sxy_gyro[i][j] += (angle[i][j][k] - sample_gyro[i][j]) * (ave_angle[j][k] - ave_gyro[j]);
				}
			}
		}

		// Calculate of R
		double[][] R_accel = new double[3][3];
		double[][] R_gyro = new double[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				R_accel[i][j] = Sxy_accel[i][j] / Math.sqrt(Sxx_accel[i][j] * Syy_accel[j]);
				R_gyro[i][j] = Sxy_gyro[i][j] / Math.sqrt(Sxx_gyro[i][j] * Syy_gyro[j]);
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
		float[] sample_accel = new float[3];
		float[] sample_gyro = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				sample_accel[i] += distance[i][j];
				sample_gyro[i] += angle[i][j];
			}
		}

		for (int i = 0; i < 3; i++) {
			sample_accel[i] /= distance[i].length;
			sample_gyro[i] /= angle[i].length;
		}
		//endregion

		//region Calculate of Average B
		float ave_accel[] = new float[3];
		float ave_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < ave_distance[i].length; j++) {
				ave_accel[i] += ave_distance[i][j];
				ave_gyro[i] += ave_angle[i][j];
			}
		}

		for (int i = 0; i < 3; i++) {
			ave_accel[i] /= ave_distance[i].length;
			ave_gyro[i] /= ave_angle[i].length;
		}
		//endregion

		//region Calculate of Sxx
		float Sxx_accel[] = new float[3];
		float Sxx_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				Sxx_accel[i] += Math.pow((distance[i][j] - sample_accel[i]), 2);
				Sxx_gyro[i] += Math.pow((angle[i][j] - sample_gyro[i]), 2);
			}
		}
		//endregion

		//region Calculate of Syy
		float Syy_accel[] = new float[3];
		float Syy_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < ave_distance[i].length; j++) {
				Syy_accel[i] += Math.pow((ave_distance[i][j] - ave_accel[i]), 2);
				Syy_gyro[i] += Math.pow((ave_angle[i][j] - ave_gyro[i]), 2);
			}
		}
		//endregion

		//region Calculate of Sxy
		float Sxy_accel[] = new float[3];
		float Sxy_gyro[] = new float[3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				Sxy_accel[i] += (distance[i][j] - sample_accel[i]) * (ave_distance[i][j] - ave_accel[i]);
				Sxy_gyro[i] += (angle[i][j] - sample_gyro[i]) * (ave_angle[i][j] - ave_gyro[i]);
			}
		}
		//endregion

		//region Calculate of R
		double R_accel[] = new double[3];
		double R_gyro[] = new double[3];

		for (int i = 0; i < 3; i++) {
			R_accel[i] = Sxy_accel[i] / Math.sqrt(Sxx_accel[i] * Syy_accel[i]);
			LogUtil.log(Log.DEBUG, "R_accel" + i + ": " + R_accel[i]);
			R_gyro[i] = Sxy_gyro[i] / Math.sqrt(Sxx_gyro[i] * Syy_gyro[i]);
			LogUtil.log(Log.DEBUG, "R_gyro" + i + ": " + R_gyro[i]);
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
