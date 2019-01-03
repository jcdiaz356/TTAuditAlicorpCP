package com.dataservicios.ttauditalicorpcp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean>
{

    public static final String LOG_TAG = ExportDatabaseFileTask.class.getSimpleName();
    private Context context;
    private final ProgressDialog dialog ;
    public ExportDatabaseFileTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(this.context);
    }

    // can use UI thread here

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage(context.getString(R.string.message_db_exporting) );
        this.dialog.show();
    }

    // automatically done on worker thread (separate from UI thread)
    protected Boolean doInBackground(final String... args) {
//        File dbFile = new File(Environment.getDataDirectory() + "/data/com.dataservicios.ttauditalicorpcp/databases/db_alicorpcp");
        File dbFile = new File(Environment.getDataDirectory() + GlobalConstant.DATABASE_PATH_DIR);
//        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        File exportDir = BitmapLoader.getDataBaseDirBackup(context);

        if (!exportDir.exists())  {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, dbFile.getName());
        try  {
            file.createNewFile();
            this.copyFile(dbFile, file);
            return true;
        }
        catch (IOException e)  {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
    }

    // can use UI thread here

    @Override

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }

        if (success) {
            Toast.makeText(context, R.string.message_db_export_succes , Toast.LENGTH_LONG).show();

            File filePath = BitmapLoader.getDataBaseDirBackup(context);
            try {

                if (filePath.isDirectory()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri myUri = Uri.parse(String.valueOf(filePath));
                    intent.setDataAndType(myUri , "resource/folder");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context,R.string.message_directory_backup_no_found, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(context,R.string.message_app_no_installing, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(GlobalConstant.MARKET_OPEN_APP_ESFILEEXPLORE));
                context.startActivity(intent);
            }finally {

            }
        }
        else{
            Toast.makeText(context, R.string.message_db_export_failed, Toast.LENGTH_SHORT).show();
        }

    }

    void copyFile(File src, File dst) throws IOException {

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try   {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }

        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null)  outChannel.close();
        }
    }
}