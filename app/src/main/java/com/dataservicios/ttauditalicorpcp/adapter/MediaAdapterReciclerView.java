package com.dataservicios.ttauditalicorpcp.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.dataservicios.ttauditalicorpcp.BuildConfig;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.repo.CompanyRepo;
import com.dataservicios.ttauditalicorpcp.repo.MediaRepo;
import com.dataservicios.ttauditalicorpcp.repo.PollRepo;
import com.dataservicios.ttauditalicorpcp.repo.ProductRepo;
import com.dataservicios.ttauditalicorpcp.repo.PublicityStoreRepo;
import com.dataservicios.ttauditalicorpcp.util.BitmapLoader;
import com.dataservicios.ttauditalicorpcp.util.GlobalConstant;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;

import okhttp3.Response;

/**
 * Created by jcdia on 1/06/2017.
 */

public class MediaAdapterReciclerView extends RecyclerView.Adapter<MediaAdapterReciclerView.MediaViewHolder> {
    public static final String LOG_TAG = MediaAdapterReciclerView.class.getSimpleName();
    private ArrayList<Media> medias;
    private int                 resource;
    private Activity activity;
//    private PollRepo pollRepo;
//    private ProductRepo productRepo;
//    private PublicityStoreRepo publicityStoreRepo;
//    private CompanyRepo companyRepo;
    private MediaRepo mediaRepo;

    private ProgressDialog pDialog;

    public MediaAdapterReciclerView(ArrayList<Media> medias, int resource, Activity activity) {
        this.medias     = medias;
        this.resource   = resource;
        this.activity   = activity;

//        pollRepo =  new PollRepo(activity);
//        productRepo = new ProductRepo(activity);
//        publicityStoreRepo = new PublicityStoreRepo(activity);
        mediaRepo = new MediaRepo(activity);

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getString(R.string.text_upload_photo_progress));
        pDialog.setIndeterminate(true);
        pDialog.setMax(100);

        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);

        AndroidNetworking.initialize(activity);

    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        view.setOnClickListener(new  View.OnClickListener(){
            public void onClick(View v)
            {
                //action
                //Toast.makeText(activity,"dfgdfg",Toast.LENGTH_SHORT).show();
            }
        });
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, final int position) {

        final Media media = medias.get(position);


        holder.tvId.setText(Html.fromHtml("<b>ID Store: </b>" + String.valueOf(media.getStore_id()) ));
        holder.tvFile.setText(Html.fromHtml("<b>Archivo: </b>") + media.getFile());
        holder.tvPoll.setText(Html.fromHtml("<b>Pool ID: </b>") + String.valueOf(media.getPoll_id()));
        holder.tvCategory.setText(Html.fromHtml("<b>Category ID: </b>") + String.valueOf(media.getCategory_product_id()));
        holder.tvPublicity.setText(Html.fromHtml("<b>Publicity ID: </b>") + String.valueOf(media.getPublicity_id()));
        holder.tvProduct.setText(Html.fromHtml("<b>Product ID: </b>") + String.valueOf(media.getProduct_id()));
        holder.tvCompany.setText(Html.fromHtml("<b>Company ID: </b>") + String.valueOf(media.getCompany_id()));
        holder.tvLog.setText( String.valueOf(media.getLog()));


        String pathFile = BitmapLoader.getAlbumDirTemp(activity).getAbsolutePath() + "/" + media.getFile() ;
        final File imgFile = new File(pathFile);

        if (!imgFile.exists()) {
            holder.btShared.setVisibility(View.GONE);
            holder.btUpload.setVisibility(View.GONE);
        } else {
            holder.btShared.setVisibility(View.VISIBLE);
            holder.btUpload.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load(imgFile)
                .resize(60, 70)
                .centerCrop()
                .error(R.drawable.avataruser)
                .into(holder.imgPhoto);
//        Picasso.with(activity)
//                .load(imgFile)
//                .error(R.drawable.avataruser)
//                .into(holder.imgPhoto);

        holder.btShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> uris = new ArrayList<>();
                Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider",imgFile);
                uris.add(contentUri);

                String shareString = Html.fromHtml("Store ID:" +  String.valueOf(media.getStore_id()) +"<br>" +
                        "<b>Poll Id: </b>" +  String.valueOf(media.getPoll_id())   + "<br>" +
                        "<b>Publicit Id: </b>" +  String.valueOf(media.getPublicity_id())   + "<br>" +
                        "<b>Category Id: </b>" +  String.valueOf(media.getCategory_product_id())   + "<br>" +
                        "<b>Product Id: </b>" +  String.valueOf(media.getProduct_id())   + "<br>" +
                        "<b>Company Id: </b>" +  String.valueOf(media.getCompany_id())   + "<br>" +
                        "<b>Photo: </b>" +   String.valueOf(media.getFile())  + "").toString();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);


                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                intent.putExtra(Intent.EXTRA_TEXT, shareString);
                intent.setType("image/*"); /* This example is sharing jpeg images. */

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                activity.startActivity(intent);

            }
        });

        holder.btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFile();

            }


            //SUBIDA DE PHOTOS
            private void uploadFile() {

                HashMap<String, String> params = new HashMap<>();

               // params.put("fotoUp"               ,  imgFile                                                            );
                params.put("archivo"              ,  String.valueOf(imgFile.getName())                );
                params.put("store_id"             ,  String.valueOf(media.getStore_id())              );
                params.put("product_id"           ,  String.valueOf(media.getProduct_id())            );
                params.put("poll_id"              ,  String.valueOf(media.getPoll_id())               );
                params.put("publicities_id"       ,  String.valueOf(media.getPublicity_id())          );
                params.put("category_product_id"  ,  String.valueOf(media.getCategory_product_id())   );
                params.put("company_id"           ,  String.valueOf(media.getCompany_id())            );
                params.put("tipo"                 ,  String.valueOf(media.getType())                  );
                params.put("monto"                ,  String.valueOf(media.getMonto())                 );
                params.put("razon_social"         ,  String.valueOf(media.getRazonSocial())           );
                params.put("horaSistema"          ,  String.valueOf(media.getCreated_at())            );


                pDialog.show();
                AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                        .addMultipartFile("fotoUp",imgFile)
                        .addMultipartParameter(params)
                        .setTag("uploadTest")
                        .setPriority(Priority.HIGH)
                        //.setExecutor(Executors.newSingleThreadExecutor()) // setting an executor to get response or completion on that executor thread
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                // do anything with progress

                                int value = (int) (( bytesUploaded / totalBytes)*100); // (bytesUploaded / totalBytes)*100 + " %");
                                pDialog.setProgress(value);
                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // below code will be executed in the executor provided
                                // do anything with response
                                Log.d(LOG_TAG,response.toString());
                                pDialog.dismiss();

                                int success = 0;
                                try {
                                    success = response.getInt("success");
                                    if (success == 1) {
                                        mediaRepo.delete(media);
                                        imgFile.delete();
                                        //Removiendo elemento un vez eliminado
                                        medias.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,medias.size());
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(activity,activity.getString(R.string.message_error_upload_file),Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                                String err = error.getMessage().toString();
                                Toast.makeText(activity,activity.getString(R.string.message_error_upload_file) + err,Toast.LENGTH_SHORT).show();
//                                Toast.makeText(activity, err,Toast.LENGTH_SHORT);
                                pDialog.dismiss();
                            }
                            @Override
                            protected void finalize() throws Throwable {
//                                notifyItemRangeChanged(position,medias.size());
//                                notifyItemRemoved(position);
                              //  notifyDataSetChanged();
//                                super.finalize();
                            }
                        });
                }

                //ENV*IO DE FOTO AYNCRONO
                // PARA PROBAR
                private void uploadFileSynchronous(){


                    HashMap<String, String> params = new HashMap<>();

                    // params.put("fotoUp"               ,  imgFile                                                            );
                    params.put("archivo"              ,  String.valueOf(imgFile.getName())                   );
                    params.put("store_id"             ,  String.valueOf(media.getStore_id())              );
                    params.put("product_id"           ,  String.valueOf(media.getProduct_id())            );
                    params.put("poll_id"              ,  String.valueOf(media.getPoll_id())               );
                    params.put("publicities_id"       ,  String.valueOf(media.getPublicity_id())          );
                    params.put("category_product_id"  ,  String.valueOf(media.getCategory_product_id())   );
                    params.put("company_id"           ,  String.valueOf(media.getCompany_id())            );
                    params.put("tipo"                 ,  String.valueOf(media.getType())                  );
                    params.put("monto"                ,  String.valueOf(media.getMonto())                 );
                    params.put("razon_social"         ,  String.valueOf(media.getRazonSocial())           );
                    params.put("horaSistema"          ,  String.valueOf(media.getCreated_at())            );

                    ANRequest request = AndroidNetworking.upload(GlobalConstant.dominio + "/insertImagesMayorista")
                            .addMultipartFile("fotoUp",imgFile)
                            .addMultipartParameter(params)
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    // do anything with progress
                                }
                            });

                    ANResponse<JSONObject> response = request.executeForJSONObject();

                    if (response.isSuccess()) {
                        JSONObject jsonObject = response.getResult();
                        Log.d(LOG_TAG, "response : " + jsonObject.toString());
                        Response okHttpResponse = response.getOkHttpResponse();
                        Log.d(LOG_TAG, "headers : " + okHttpResponse.headers().toString());
                    } else {
                        ANError error = response.getError();
                        // Handle Error
                    }
                }
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvFile;
        private TextView tvPoll;
        private TextView tvCategory;
        private TextView tvPublicity;
        private TextView tvProduct;
        private TextView tvCompany;
        private TextView tvLog;

        private ImageView imgPhoto;
        private Button btShared;
        private Button btUpload;
        public MediaViewHolder(View itemView) {
            super(itemView);
            tvId            = (TextView)    itemView.findViewById(R.id.tvId);
            tvFile          = (TextView)    itemView.findViewById(R.id.tvFile);
            tvPoll          = (TextView)    itemView.findViewById(R.id.tvPoll);
            tvCategory      = (TextView)    itemView.findViewById(R.id.tvCategory);
            tvPublicity     = (TextView)    itemView.findViewById(R.id.tvPublicity);
            tvProduct       = (TextView)    itemView.findViewById(R.id.tvProduct);
            tvCompany       = (TextView)    itemView.findViewById(R.id.tvCompany);
            tvLog           = (TextView)    itemView.findViewById(R.id.tvLog);
            imgPhoto        = (ImageView)   itemView.findViewById(R.id.imgPhoto);
            btShared        = (Button)  itemView.findViewById(R.id.btShared);
            btUpload        = (Button)  itemView.findViewById(R.id.btUpload);
        }
    }
}
