package com.devtau.gazprompedometer;

import android.app.ActivityManager;
import android.content.Context;
import java.util.Locale;

public class Util {

	private static final String LOG_TAG = "Util";

	public static String formatFloat(float delta) {
		return String.format(Locale.getDefault(), "%.1f", delta);
	}

	public static void logRunningServices(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Logger.d(LOG_TAG, "Running services list:");
		for (ActivityManager.RunningServiceInfo rsi: manager.getRunningServices(Integer.MAX_VALUE)) {
			Logger.d(LOG_TAG, "process " + rsi.process + " with component " + rsi.service.getClassName());
		}
	}

	public static boolean isServiceRunning(Context context, Class serviceClass) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
