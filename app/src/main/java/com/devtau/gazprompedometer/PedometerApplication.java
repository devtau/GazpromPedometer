package com.devtau.gazprompedometer;

import android.app.Application;
import com.squareup.otto.Bus;

public class PedometerApplication extends Application {

	private static PedometerApplication sApp;
	private Bus mEventBus;

	public PedometerApplication() {
		sApp = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mEventBus = new Bus("main");
	}

	public Bus getEventBus() {
		return mEventBus;
	}

	public static PedometerApplication get() {
		return sApp;
	}
}
