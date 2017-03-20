package com.devtau.gazprompedometer;

public class Contract {

	public interface StepDetectorListener {
		void showSensorUnavailable();
		void updateSteps(int stepsCount);
	}

	public interface StepDetectorInterface {
		void registerListener();
		void unregisterListener();
	}
}
