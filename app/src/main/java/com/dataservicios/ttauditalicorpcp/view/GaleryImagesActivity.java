package com.dataservicios.ttauditalicorpcp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.BuildConfig;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.adapter.ImageAdapter;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.repo.MediaRepo;
import com.dataservicios.ttauditalicorpcp.util.BitmapLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static com.dataservicios.ttauditalicorpcp.util.BitmapLoader.copyFile;

public class GaleryImagesActivity extends AppCompatActivity {
    private static final String LOG_TAG = GaleryImagesActivity.class.getSimpleName();
    private Activity activity =  this;
    private ImageAdapter        imageAdapter;
    private GridView imagegrid;
    private FabSpeedDial        fabMenuCamera;
    private ArrayList<String> filesSdcard = new ArrayList<String>();
    private File[]              listFile;
    private Media               media;


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
        Intent intent = new Intent(context, GaleryImagesActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_images);


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



        fabMenuCamera = (FabSpeedDial) findViewById(R.id.fabMenuCamera);
        imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter(activity,getFromFileSdcard());
        imagegrid.setAdapter(imageAdapter);

        fabMenuCamera.setMenuListener(new SimpleMenuListenerAdapter(){
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //TODO: Start some activity
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.action_camera:


                        String short_name_file = String.valueOf(media.getStore_id());

                        Bundle bundle  =  new Bundle();
                        Intent intent = new Intent(activity, CameraActivity.class);

                        bundle.putString("short_name_file",short_name_file);
                        intent.putExtras(bundle);

                        startActivity(intent);
                        break;
                    case R.id.action_upload_photo:
//                        Toast.makeText(activity, menuItem.getTitle().toString() , Toast.LENGTH_SHORT).show();
                        uploadDiles();
                        break;
                    case R.id.action_delete_photo:
                        deleteFileSdCard();
                        break;
                    case R.id.action_sharing_photo:
//                        Toast.makeText(activity, menuItem.getTitle().toString() , Toast.LENGTH_SHORT).show();
                        sharingImageFromSdCard();
                        break;
                }
                return false;
            }
        });


    }

    /**
     * Función para compartir archivos de imágen}
     */
    private void sharingImageFromSdCard() {

        File file = BitmapLoader.getAlbumDir(activity);
        ArrayList<String> names_file = new ArrayList<String>();

        if (file.isDirectory()) {
            int contador = 0;
            names_file.clear();
            if (listFile.length > 0) {

                int count = imagegrid.getAdapter().getCount();
                ArrayList<Uri> uris = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    RelativeLayout itemLayout = (RelativeLayout) imagegrid.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.itemCheckBox);
                    if (checkbox.isChecked()) {
                        contador++;
                       // listFile[i].delete();
                        //final File photoFile = new File(getFilesDir(), "foo.jpg");
                        File file_ = new File(String.valueOf(listFile[i]));
//                        uris.add(Uri.fromFile(file_));
                        Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider",file_);
//                        Uri contentUri = FileProvider.getUriForFile(activity, "com.dataservicios.ttauditalicorpcp.fileProvider", file_);
                        uris.add(contentUri);

                    }
                }

                if (contador > 0) {
//                    -----------COMPARTIENDO MULTIPLES ARCHIVOS DE IMAGEN

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);

                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                    intent.setType("image/*"); /* This example is sharing jpeg images. */

                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(intent);

                    Toast.makeText(activity, "Seleccionó " + String.valueOf(contador) + " imágenes", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.message_selection_image, Toast.LENGTH_LONG).show();
                    return;
                }

            } else {

                Toast toast;
                toast = Toast.makeText(activity, R.string.message_image_no_found, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }


        }
    }

    /**
     * Eliminado archivos de la Memoria
     */
    private void deleteFileSdCard() {

        File file = BitmapLoader.getAlbumDir(activity);
        //final File[]  listFileDelete = null;
        final ArrayList<String> names_file = new ArrayList<String>();

        if (file.isDirectory()) {
            int contador = 0;
//            names_file.clear();
            if (listFile.length > 0) {
                int count = imagegrid.getAdapter().getCount();
                for (int i = 0; i < count; i++) {
                    // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    RelativeLayout itemLayout = (RelativeLayout) imagegrid.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.itemCheckBox);
                    if (checkbox.isChecked()) {
                        contador++;
                        // listFile[i].delete();
                        names_file.add( listFile[i].getName()) ;

                    }
                }

                if (contador > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.message_delete_file);
                    builder.setMessage(R.string.message_delete_file_information);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (int i = 0; i < names_file.size(); i++) {
                                       // names_file[i].delete();
                                        File file_delete = new File(BitmapLoader.getAlbumDir(activity) + "/" + names_file.get(i)) ;
                                        file_delete.delete();
                                    }
                                    onRestart();
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

                    Toast.makeText(activity, "Seleccionó " + String.valueOf(contador) + " imágenes", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.message_selection_image, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast toast;
                toast = Toast.makeText(activity, R.string.message_image_no_found, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
    }

    /**
     * Obtener Archivos desde Tarjeta de memoria
     * @return
     */
    private ArrayList<String> getFromFileSdcard()
    {
        //File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images + getAlbumName());
        File file= BitmapLoader.getAlbumDir(activity);
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            if (listFile != null){
                for (int i = 0; i < listFile.length; i++)
                {
//                    if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", media.getStore_id()) ))
//                    {
                        filesSdcard.add(listFile[i].getAbsolutePath());
//                    }
                }
            }
        }
        return filesSdcard ;
    }

    /**
     * Función para subir imágenes
     */
    private void uploadDiles() {
        File file= BitmapLoader.getAlbumDir(activity);
        ArrayList<String> names_file = new ArrayList<String>();
        if (file.isDirectory()) {
            int position =0;
            int contador = 0;
            int holder_counter=0;
            names_file.clear();
            if (listFile.length>0){
                //for (int i = 0; i < listFile.length; i++){
                int total = imageAdapter.getCount();
                int count = imagegrid.getAdapter().getCount();
                for (int i = 0; i < count; i++) {
                    // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    RelativeLayout itemLayout = (RelativeLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.itemCheckBox);
                    if(checkbox.isChecked())
                    {
                        contador ++;
                        // Log.d("Item "+String.valueOf(i), checkbox.getTag().toString());
                        //Toast.makeText(activity,checkbox.getTag().toString() ,Toast.LENGTH_LONG).show();
                        if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", media.getStore_id()) )) {
                            String name = listFile[i].getName();
                            names_file.add(name);
                            //  holder_counter++;
                            try {
                                copyFile(BitmapLoader.getAlbumDir(activity) + "/" + listFile[i].getName(), BitmapLoader.getAlbumDirTemp(activity).getAbsolutePath() + "/" + listFile[i].getName());
                                copyFile(BitmapLoader.getAlbumDir(activity) + "/" + listFile[i].getName(), BitmapLoader.getAlbumDirBackup(activity) + "/" + listFile[i].getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        listFile[i].delete();
                    }
                }

                if(contador > 0){

                    Toast.makeText(activity, "Seleccionó " + String.valueOf(contador) + " imágenes", Toast.LENGTH_LONG).show();
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

        MediaRepo mediaRepo = new MediaRepo(activity);
        for (int i = 0; i < names_file.size(); i++) {
            String foto = names_file.get(i);
            //String pathFile =getAlbumDirTemp().getAbsolutePath() + "/" + foto ;
            String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
            media.setCreated_at(created_at);
            media.setFile(foto);
            mediaRepo.create(media);
        }

        ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
        Log.d(LOG_TAG,medias.toString());
        finish();

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        //overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

}
