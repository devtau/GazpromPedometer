package com.devtau.gazprompedometer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.devtau.gazprompedometer.Detectors.AccelerometerStepDetector;
import com.devtau.gazprompedometer.Events.Events;
import com.devtau.gazprompedometer.Events.NewStepEvent;
import com.devtau.gazprompedometer.Events.PedometerUnavailableEvent;

public class StepService extends Service implements Contract.StepDetectorListener {

	private static final String LOG_TAG = "StepService";
	private Contract.StepDetectorInterface stepDetector;

	public StepService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Logger.d(LOG_TAG, "onBind()");
		return null;
	}

	//onStartCommand выполняется только в том случае, если служба запускается методом startService()
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(LOG_TAG, "onStartCommand()");
		stepDetector = new AccelerometerStepDetector(this, this);
		stepDetector.registerListener();

		//пересоздаст сервис в случае неявного завершения его системой (не методами stopService() или stopSelf())
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stepDetector.unregisterListener();
		Logger.d(LOG_TAG, "onDestroy()");
		Logger.d(LOG_TAG, "is " + getClass().getSimpleName() + " running: " + String.valueOf(Util.isServiceRunning(this, getClass())));
	}

	@Override
	public void showSensorUnavailable() {
		Events.post(new PedometerUnavailableEvent());
	}

	@Override
	public void updateSteps(int stepsCount) {
		Events.post(new NewStepEvent(stepsCount));
	}
}
