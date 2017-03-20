package com.devtau.gazprompedometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.devtau.gazprompedometer.Events.Events;
import com.devtau.gazprompedometer.Events.NewStepEvent;
import com.devtau.gazprompedometer.Events.PedometerUnavailableEvent;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	private static final String APP_PREFERENCES = "PedometerApplicationSettings";
	private static final String APP_PREF_WAKE_LOCK = "APP_PREF_WAKE_LOCK";
	private ViewGroup stepsLayout;
	private TextView tvStepsCount, tvPedometerUnavailable;
	private PowerManager.WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initControls();
		initWakeLock();
		startService(new Intent(this, StepService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
		Events.register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Events.unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		boolean preferredWakeLockOn = preferences.getBoolean(APP_PREF_WAKE_LOCK, true);
		menu.getItem(0).setChecked(preferredWakeLockOn);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (wakeLock == null) return super.onOptionsItemSelected(item);
		SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		boolean preferredWakeLockOn = preferences.getBoolean(APP_PREF_WAKE_LOCK, true);
		switch (item.getItemId()) {
			case R.id.acquire_wake_lock:
				if (preferredWakeLockOn) {
					wakeLock.acquire();
				} else if (wakeLock.isHeld()) {
					wakeLock.release();
				}
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(APP_PREF_WAKE_LOCK, !preferredWakeLockOn);
				editor.apply();
				item.setChecked(!preferredWakeLockOn);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	@Subscribe
	public void on(PedometerUnavailableEvent event) {
		if (stepsLayout != null && tvPedometerUnavailable != null) {
			stepsLayout.setVisibility(View.GONE);
			tvPedometerUnavailable.setVisibility(View.VISIBLE);
		}
	}

	@Subscribe
	public void on(NewStepEvent event) {
		if (event.stepsCount != 0 && tvStepsCount != null) {
			tvStepsCount.setText(String.valueOf(event.stepsCount));
		}
	}

	private void initControls() {
		stepsLayout = (ViewGroup) findViewById(R.id.steps_layout);
		tvStepsCount = (TextView) findViewById(R.id.steps_count);
		tvPedometerUnavailable = (TextView) findViewById(R.id.pedometer_sensor_unavailable);
	}

	private void initWakeLock() {
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
	}
}
