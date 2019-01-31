package com.dataservicios.ttauditalicorpcp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.app.AppController;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.repo.MediaRepo;
import com.dataservicios.ttauditalicorpcp.util.AuditUtil;
import com.dataservicios.ttauditalicorpcp.util.BitmapLoader;
import com.dataservicios.ttauditalicorpcp.util.Connectivity;
import com.dataservicios.ttauditalicorpcp.util.GlobalConstant;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.Response;

public class UpdateService extends Service {

    private final String LOG_TAG = UpdateService.class.getSimpleName();
    private final Integer contador = 0;
    private Context context = this;
    static final int DELAY =  60000; //1 minutos de espera
//    static final int DELAY = 9000; // segundo de espera
    private boolean runFlag = false;
    private Updater updater;
    private AppController application;
    private MediaRepo mediaRepo;
    private Media media;
    private ArrayList<Media> medias;
    private AuditUtil auditUtil;
    private File file;

    public UpdateService() {
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (AppController) getApplication();
        // updater = new Updater();
        mediaRepo = new MediaRepo(this);
        media = new Media();
        auditUtil = new AuditUtil(context);
                Log.d(LOG_TAG, "onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        runFlag = false;
        application.setServiceRunningFlag(false);
//        updater.interrupt();
//        updater = null;
                Log.d(LOG_TAG, "onDestroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!runFlag){
            runFlag = true;
            application.setServiceRunningFlag(true);
//            updater.start();
        }

                Log.d(LOG_TAG, "onStarted");
        return START_STICKY;
    }

    private class Updater extends Thread {
        public Updater(){
            super("UpdaterService-UpdaterThread");
        }

        @Override
        public void run() {

            UpdateService updaterService = UpdateService.this;
            while (updaterService.runFlag) {
                Log.d(LOG_TAG, "UpdaterThread running");
                try{
                    if(Connectivity.isConnected(context)) {
                        if (Connectivity.isConnectedFast(context)) {
                                    Log.i(LOG_TAG," Conexión rápida" );
                            if (mediaRepo.countReg() >0 ) {
                                media.setId(0);
                                medias = (ArrayList<Media>) mediaRepo.findAll();
                                for (Media m: medias){
                                    file = null;
                                    file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + m.getFile());
                                    if(file.exists()){
                                        media = m;
                                        break;
                                    }
                                }
//
                                if (media.getId() != 0){
                                    // NOTA eliminar  de "auditUtil.uploadMedia"
                                    // la eliminación de archivos
                                    //  file.delete() para controlar la eliminación en base de datos
                                    //boolean response = auditUtil.uploadMedia(media,1);
                                    boolean response = auditUtil.sendUploadPhotoServer(media);
                                    if (response) {
                                        file = null;
                                        file = new File(BitmapLoader.getAlbumDirTemp(context).getAbsolutePath() + "/" + media.getFile());
                                        if(file.exists()){
                                            file.delete();
                                            mediaRepo.delete(media);
                                        }
                                        Log.i(LOG_TAG," Se envió correctamente los datos al servidor, se eliminó el registro en la base de datos, y se eliminó  el archivo " );
                                    } else {
                                        Log.i(LOG_TAG," El servidor responde falso, no se pudo enviar el archivo al servidor " );
                                    }
                                }
                            } else{
                                        Log.i(LOG_TAG, "No se encontró registros media para el envío");
                            }

                        }else {
                                    Log.i(LOG_TAG," COnexión a internet Lenta" );
                        }
                    } else {
                                Log.i(LOG_TAG,"No hay conexión a internet" );
                    }
                    Thread.sleep(DELAY);
                }catch(InterruptedException e){
                    updaterService.runFlag = false;
                    application.setServiceRunningFlag(true);
                }
            }
        }
    }
}


