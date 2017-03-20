package com.devtau.gazprompedometer.Events;

import com.devtau.gazprompedometer.Logger;
import com.devtau.gazprompedometer.PedometerApplication;

public class Events {

	private Events() {
	}

	public static void post(Object event) {
		PedometerApplication.get().getEventBus().post(event);
	}

	public static void register(Object listener) {
		PedometerApplication.get().getEventBus().register(listener);
	}

	public static void unregister(Object listener) {
		try {
			PedometerApplication.get().getEventBus().unregister(listener);
		} catch (Exception e) {
			Logger.e("Failed to unregister " + listener, e);
		}
	}
}
