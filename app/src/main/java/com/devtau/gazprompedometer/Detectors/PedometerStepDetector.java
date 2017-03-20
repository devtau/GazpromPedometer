package com.devtau.gazprompedometer.Detectors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.devtau.gazprompedometer.Contract;
/**
 * Более простой класс, использующий новый аппаратный шагомер, представленный в последних смартфонах.
 * Апи для него доступен с kitkat (19+) и это укладывается в ТЗ, однако произвольную чувствительность настроить он нам не позволяет.
 */
public class PedometerStepDetector implements SensorEventListener, Contract.StepDetectorInterface {

	private static final String LOG_TAG = "PedometerStepDetector";
	private Contract.StepDetectorListener mView;
	private SensorManager mSensorManager;
	private Sensor mPedometer;
	private int initialStepsCountFromReboot;


	public PedometerStepDetector(Context context, Contract.StepDetectorListener view) {
		this.mView = view;

		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mPedometer = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (mPedometer == null) {
			mView.showSensorUnavailable();
		}
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		int currentSteps = (int) sensorEvent.values[0];
		if (initialStepsCountFromReboot == 0) {
			initialStepsCountFromReboot = currentSteps;
		}
		mView.updateSteps(currentSteps - initialStepsCountFromReboot);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		/*NOP*/
	}

	@Override
	public void registerListener() {
		mSensorManager.registerListener(this, mPedometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void unregisterListener() {
		mSensorManager.unregisterListener(this);
	}
}
