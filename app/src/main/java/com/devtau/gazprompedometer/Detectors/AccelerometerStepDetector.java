package com.devtau.gazprompedometer.Detectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.devtau.gazprompedometer.Contract;
import com.devtau.gazprompedometer.Logger;
/**
 * Класс, работающий с датчиком акселлерометра, доступным даже на gingerbread-девайсах (10+).
 * Реализация требует существенно более сложной работы с показаниями датчика.
 */
public class AccelerometerStepDetector implements SensorEventListener, Contract.StepDetectorInterface {

	private static final String LOG_TAG = "AccelerometerStepDetector";
	private static final float ACCELERATION_SENSITIVITY = 3f;// м/с2
	private static final float EXTREMES_ACCEPTED_DIFF = 2f;// м/с2
	private Contract.StepDetectorListener mView;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private int stepsCount;
	private double mLastAverageAccel;
	private Direction mLastDirection;
	private double[] mNegativeExtremes = new double[5];


	public AccelerometerStepDetector(Context context, Contract.StepDetectorListener view) {
		this.mView = view;

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		//используя TYPE_LINEAR_ACCELERATION можно получать данные, очищенные от 9.8
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (mAccelerometer == null) {
			mView.showSensorUnavailable();
		}
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		double averageCurrAccel = calculateCurrAccel(sensorEvent);

		Direction direction = (averageCurrAccel > mLastAverageAccel) ? Direction.UP : Direction.DOWN;
		if (averageCurrAccel - SensorManager.STANDARD_GRAVITY > ACCELERATION_SENSITIVITY) {
			boolean directionChanged = direction != mLastDirection;
			Logger.d(LOG_TAG, "averageCurrAccel=" + averageCurrAccel + ", mLastAverageAccel=" + mLastAverageAccel
					+ ", direction=" + direction + ", mLastDirection=" + mLastDirection);
			if (directionChanged) {
				boolean isAlmostAsLargeAsBefore = processHistoryList(direction, averageCurrAccel);
				if (isAlmostAsLargeAsBefore) {
					mView.updateSteps(++stepsCount);
				}
			}
		}
		mLastDirection = direction;
		mLastAverageAccel = averageCurrAccel;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		/*NOP*/
	}

	@Override
	public void registerListener() {
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}


	private double calculateCurrAccel(SensorEvent sensorEvent) {
		//используя TYPE_ACCELEROMETER результирующее значение всегда будет колебаться около 9.8
		float currXAccel = sensorEvent.values[0];
		float currYAccel = sensorEvent.values[1];
		float currZAccel = sensorEvent.values[2];

		//радиус-вектор является диагональю прямоугольного параллелипипеда
		return Math.sqrt(Math.pow(currXAccel, 2) + Math.pow(currYAccel, 2) + Math.pow(currZAccel, 2));
	}

	private boolean processHistoryList(Direction direction, double averageCurrAccel) {
		boolean isAlmostAsLargeAsBefore = false;
		if (!direction.isUp()) return false;
		for (double prevExtreme: mNegativeExtremes) {
			double diff = Math.abs(averageCurrAccel - prevExtreme);
			isAlmostAsLargeAsBefore = diff < EXTREMES_ACCEPTED_DIFF;
			if (isAlmostAsLargeAsBefore) break;
		}
		mNegativeExtremes[0] = mNegativeExtremes[1];
		mNegativeExtremes[1] = mNegativeExtremes[2];
		mNegativeExtremes[2] = mNegativeExtremes[3];
		mNegativeExtremes[3] = mNegativeExtremes[4];
		mNegativeExtremes[4] = averageCurrAccel;
		return isAlmostAsLargeAsBefore;
	}


	private enum Direction {
		UP, DOWN;

		public boolean isUp() {
			return this == UP;
		}
	}
}
