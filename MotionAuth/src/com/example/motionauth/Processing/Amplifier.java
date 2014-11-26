package com.example.motionauth.Processing;

import android.util.Log;

/**
 * データの値を増幅させる
 *
 * @author Kensuke Kousaka
 */
public class Amplifier {
	private static final String TAG = Amplifier.class.getSimpleName();

	private static final double AMPLIFICATION_VALUE = 2;

	private boolean isRangeCheck = false;


	/**
	 * 全試行回数中，一回でもデータの幅が閾値よりも小さければtrueを返す
	 *
	 * @param data チェックするdouble型三次元配列データ
	 * @return 全試行回数中，一回でもデータの幅が閾値よりも小さければtrue，そうでなければfalse
	 */
	public boolean CheckValueRange (double[][][] data, double checkRangeValue) {
		Log.v(TAG, "--- CheckValueRange ---");

		Log.i(TAG, "checkRangeValue = " + checkRangeValue);

		double[][] max = new double[data.length][data[0].length];
		double[][] min = new double[data.length][data[0].length];

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				max[i][j] = 0;
				min[i][j] = 0;
			}
		}

		double range;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				for (int k = 0; k < data[i][j].length; k++) {
					if (data[i][j][k] > max[i][j]) {
						max[i][j] = data[i][j][k];
					}
					else if (data[i][j][k] < min[i][j]) {
						min[i][j] = data[i][j][k];
					}
				}
			}
		}

		for (int i = 0; i < max.length; i++) {
			for (int j = 0; j < max[i].length; j++) {
				range = max[i][j] - min[i][j];
				Log.e(TAG, "range = " + range);
				if (range < checkRangeValue) isRangeCheck = true;
			}
		}

		return isRangeCheck;
	}


	/**
	 * 与えられたデータをAMPLIFICATION_VALUEで指定した値だけ増幅させる
	 *
	 * @param data 増幅させるdouble型三次元配列データ
	 * @return 増幅後のdouble型三次元配列データ
	 */
	public double[][][] Amplify (double[][][] data) {
		Log.v(TAG, "--- Amplify ---");

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				for (int k = 0; k < data[i][j].length; k++) {
					data[i][j][k] *= AMPLIFICATION_VALUE;
				}
			}
		}

		return data;
	}


	/**
	 * 与えられたデータをAMPLIFICATION_VALUEで指定した値だけ増幅させる
	 *
	 * @param data 増幅させるdouble型二次元配列データ
	 * @return 増幅後のdouble型二次元配列データ
	 */
	public double[][] Amplify (double[][] data) {
		Log.v(TAG, "--- Amplify ---");

		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] *= AMPLIFICATION_VALUE;
			}
		}

		return data;
	}
}
