package com.dataservicios.ttauditalicorpcp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.AlbumStorageDirFactory;
import com.dataservicios.ttauditalicorpcp.BaseAlbumDirFactory;
import com.dataservicios.ttauditalicorpcp.FroyoAlbumDirFactory;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.adapter.ImageAdapter;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.repo.CompanyRepo;
import com.dataservicios.ttauditalicorpcp.repo.MediaRepo;
import com.dataservicios.ttauditalicorpcp.util.BitmapLoader;
import com.dataservicios.ttauditalicorpcp.util.GlobalConstant;
import com.dataservicios.ttauditalicorpcp.util.MemoryUsage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.zelory.compressor.Compressor;

import static com.dataservicios.ttauditalicorpcp.util.BitmapLoader.copyFile;


/**
 * Created by user on 06/02/2015.
 */
public class AndroidCustomGalleryActivity extends AppCompatActivity {
    public static final String LOG_TAG = AndroidCustomGalleryActivity.class.getSimpleName();
    private static final int TAKE_PICTURE = 1;

    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory  mAlbumStorageDirFactory = null;
    private ImageAdapter            imageAdapter;
    private ArrayList<String> fPath = new ArrayList<String>();// list of file paths
    private File[] listFileAll;
    private File[] listFileFiltered;
    private ArrayList<File>    listFileSelected  = new  ArrayList<File>();
    //private ArrayList<String> names_file = new ArrayList<String>();
    private Activity activity = (Activity) this;;
    private Media                   media;
    private MediaRepo               mediaRepo;
    private CompanyRepo             companyRepo;
    private Button btDeletePhoto;


    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param media pregunta que se mostrará segun el oreden
     */
    public static void createInstance(Activity activity, Media media) {
        Intent intent = getLaunchIntent(activity, media);
        activity.startActivity(intent);
    }
    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param media
     * @return retorna un Intent listo para usar
     */
    private static Intent getLaunchIntent(Context context, Media media) {
        Intent intent = new Intent(context, AndroidCustomGalleryActivity.class);
        intent.putExtra("store_id"              ,media.getStore_id()           );
        intent.putExtra("poll_id"               ,media.getPoll_id()            );
        intent.putExtra("company_id"            ,media.getCompany_id()         );
        intent.putExtra("publicities_id"        ,media.getPublicity_id()       );
        intent.putExtra("product_id"            ,media.getProduct_id()         );
        intent.putExtra("category_product_id"   ,media.getCategory_product_id());
        intent.putExtra("monto"                 ,media.getMonto()              );
        intent.putExtra("razon_social"          ,media.getRazonSocial()        );
        intent.putExtra("tipo"                  ,media.getType()               );
        return intent;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        showToolbar(getString(R.string.title_activity_upload_photo),false);

        Bundle bundle = getIntent().getExtras();
        media= new Media();
        media.setStore_id(bundle.getInt("store_id"));
        media.setPoll_id(bundle.getInt("poll_id"));
        media.setCompany_id(bundle.getInt("company_id"));
        media.setPublicity_id(bundle.getInt("publicities_id"));
        media.setProduct_id(bundle.getInt("product_id"));
        media.setCategory_product_id(bundle.getInt("category_product_id"));
        media.setMonto(bundle.getString("monto"));
        media.setRazonSocial(bundle.getString("razon_social"));
        media.setType(bundle.getInt("tipo"));

        mediaRepo = new MediaRepo(activity);

        getFromSdcard();

        final GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter(activity,fPath);
        imagegrid.setAdapter(imageAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        Button btn_photo = (Button)findViewById(R.id.take_photo);
        Button btn_upload = (Button)findViewById(R.id.save_images);
        btDeletePhoto = (Button) findViewById(R.id.btDeletePhoto);
        // Register the onClick listener with the implementation above
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // create intent with ACTION_IMAGE_CAPTURE action
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName =   String.format("%06d", Integer.parseInt(String.valueOf(media.getStore_id())))+ "_" + media.getCompany_id() + GlobalConstant.JPEG_FILE_PREFIX + timeStamp;
                File albumF = BitmapLoader.getAlbumDir(activity);
                // to save picture remove comment
                File file = new File(albumF,imageFileName+GlobalConstant.JPEG_FILE_SUFFIX);
                Uri photoPath = Uri.fromFile(file);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                 mCurrentPhotoPath = BitmapLoader.getAlbumDir(activity).getAbsolutePath();
//                // start camera activity
//                startActivityForResult(intent, TAKE_PICTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(activity, "com.dataservicios.ttauditalicorpcp.fileProvider", file);
                    //intent.setDataAndType(contentUri, type);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                }
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                File file= BitmapLoader.getAlbumDir(activity);
                if (file.isDirectory()) {
                    int contador = 0;
                    //names_file.clear();
                    if (listFileAll.length>0){

                        int count = imagegrid.getAdapter().getCount();
                        for (int i = 0; i < count; i++) {
                           // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                            RelativeLayout itemLayout = (RelativeLayout)imagegrid.getChildAt(i); // Find by under LinearLayout

                            CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.itemCheckBox);
                            if(checkbox.isChecked())
                            {
                                String name = listFileAll[i].getName();
                                //listFileSelected.add(listFileAll[i]);
                                contador ++;
                                    try {

                                        copyFile(BitmapLoader.getAlbumDir(activity) + "/" + listFileAll[i].getName(), BitmapLoader.getAlbumDirBackup(activity).getAbsolutePath() + "/" + listFileAll[i].getName());
                                       // compresFileDestinationtion(BitmapLoader.getAlbumDirBackup(activity).getAbsolutePath(), listFileAll[i]);
                                        compresFileDestinationtion(BitmapLoader.getAlbumDirTemp(activity).getAbsolutePath(), listFileAll[i]);

                                        String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                                        media.setCreated_at(created_at);
                                        media.setFile(name);
                                        mediaRepo.create(media);
                                        listFileAll[i].delete();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        alertDialogBasico("No se pudo enviar la imagen intentelo nuevamente");
                                        return;
                                    }

                            }
                        }

//                        -----------PCOPIA Los ARCHIVOS AÑADIDOS con la selección del cheked-----------
//                        ------------------------------------------------------------------------------
//                        for ( File filesa: listFileSelected){
//
//                            try {
//                                compresFileDestinationtion(BitmapLoader.getAlbumDirBackup(activity).getAbsolutePath(),filesa);
//                                compresFileDestinationtion(BitmapLoader.getAlbumDirTemp(activity).getAbsolutePath(),filesa);
//                                String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
//                                media.setCreated_at(created_at);
//                                media.setFile(filesa.getName());
//                                mediaRepo.create(media);
//                                filesa.delete();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                alertDialogBasico("No se pudo enviar la imagen intentelo nuevamente");
//                                return;
//                            }
//
//                        }

                        Log.i(LOG_TAG, listFileSelected.toString());

                        if(contador > 0){

                            Toast.makeText(activity, "Seleccionó " + String.valueOf(contador) + " imágenes", Toast.LENGTH_LONG).show();
                            finish();
                        } else{
                            Toast.makeText(activity, R.string.message_selection_image, Toast.LENGTH_LONG).show();
                            return;
                        }


                        //return;

                    } else {

                        Toast toast;
                        toast = Toast.makeText(activity, R.string.message_image_no_found , Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                }

//                ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
//                Log.d(LOG_TAG,medias.toString());
//                finish();

            }
        });



        btDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int countFile = imagegrid.getAdapter().getCount();
                int count = 0 ;
                for (int i = 0; i < countFile; i++) {
                    // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    RelativeLayout itemLayout = (RelativeLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.itemCheckBox);
                    if(checkbox.isChecked())
                    {
                        count ++;
                    }
                }

                if(count == 0){
                    Toast.makeText(activity, R.string.message_selection_image, Toast.LENGTH_LONG).show();
                    return;
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_delete_file);
                builder.setMessage(R.string.message_delete_file_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < countFile; i++) {
                            // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                            RelativeLayout itemLayout = (RelativeLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                            CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.itemCheckBox);
                            if(checkbox.isChecked())
                            {
                                String name = listFileAll[i].getName();
                                //listFileSelected.add(listFileAll[i]);
                                listFileAll[i].delete();
                            }
                            checkbox.setChecked(false);
                        }
                        getFromSdcard();
                        // imageAdapter.
                        imageAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                builder.setCancelable(false);
            }
        });

        // Pasando  megas el tamaño de la memoria libre
        long size = (long) (MemoryUsage.getAvailableInternalMemorySize()/ Math.pow(1024,2));
        //if(size < 1024) {
        if(size < 100) {
            alertDialogBasico(getString(R.string.message_title_avalible_internal_memory),getString(R.string.message_avalible_internal_memory));
        }

    }


    //Enviar a AgenteDetailActivity


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");

        // getting values from selected ListItem
        String aid = idPDV;
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked

                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }



    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            AndroidCustomGalleryActivity.createInstance((Activity) activity, this.media);
            finish();
        }

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public void getFromSdcard()
    {

        //File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images + getAlbumName());
        fPath.clear();
        File file= BitmapLoader.getAlbumDir(activity);
        if (file.isDirectory())
        {
            listFileAll = file.listFiles();
            if (listFileAll != null){
                for (int i = 0; i < listFileAll.length; i++)
                {
//                    if (  listFileAll[i].getName().substring(0,6).equals(String.format("%06d", media.getStore_id()) ))
//                    {
                        fPath.add(listFileAll[i].getAbsolutePath());

//                    }

                }
            }
        }
    }




    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        //overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    public void alertDialogBasico(String title, String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        // on pressing cancel button
        builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(R.string.text_configure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                startActivity(intent);
            }
        });



        builder.show();

    }



    public void compresFileDestinationtion(String destinationDirectory, File file ) throws IOException {

        new Compressor(activity)
                .setMaxWidth(500)
                .setMaxHeight(352)
                .setQuality(60)

                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(destinationDirectory )
                .compressToFile(file);
    }


    public void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.show();

    }

}
