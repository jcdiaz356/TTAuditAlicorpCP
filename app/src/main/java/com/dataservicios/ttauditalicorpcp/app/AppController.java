package com.dataservicios.ttauditalicorpcp.app;


import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.dataservicios.ttauditalicorpcp.db.DatabaseManager;
import com.dataservicios.ttauditalicorpcp.services.UpdateService;

//import net.gotev.uploadservice.BuildConfig;
//import net.gotev.uploadservice.UploadService;


public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
	private boolean serviceRunningFlag;

	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		DatabaseManager.init(this);
		Log.d(TAG, "onCreated");

//		INICIO DE SERVICIO SUBIDA DE FOTOS
		startService(new Intent(this, UpdateService.class));

//		startService(new Intent(this, MonitoGPSServices.class));
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public void setServiceRunningFlag(boolean serviceRunningFlag) {
		this.serviceRunningFlag = serviceRunningFlag;
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminated");
		stopService(new Intent(this, UpdateService.class));
//		stopService(new Intent(this, MonitoGPSServices.class));
	}
}