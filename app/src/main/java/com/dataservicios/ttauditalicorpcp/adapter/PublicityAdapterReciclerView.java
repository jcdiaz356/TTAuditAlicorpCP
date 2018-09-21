package com.dataservicios.ttauditalicorpcp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.model.Poll;
import com.dataservicios.ttauditalicorpcp.model.PublicityStore;
import com.dataservicios.ttauditalicorpcp.model.Store;
import com.dataservicios.ttauditalicorpcp.repo.StoreRepo;
import com.dataservicios.ttauditalicorpcp.R;

import com.dataservicios.ttauditalicorpcp.view.PollPublicityActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jcdia on 3/06/2017.
 */

public class PublicityAdapterReciclerView extends RecyclerView.Adapter<PublicityAdapterReciclerView.PublicityViewHolder> {
    private ArrayList<PublicityStore> publicityStores;
    private int                         resource;
    private Activity activity;
    private int                         store_id;
    private int                         audit_id;

    public PublicityAdapterReciclerView(ArrayList<PublicityStore> publicityStores, int resource, Activity activity, int store_id, int audit_id) {
        this.publicityStores    = publicityStores;
        this.resource       = resource;
        this.activity       = activity;
        this.store_id       = store_id;
        this.audit_id       = audit_id;

    }

    @Override
    public PublicityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new PublicityViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(PublicityViewHolder holder, int position) {
        final PublicityStore publicityStore = publicityStores.get(position);


        holder.tvFullName.setText(publicityStore.getFullname());
        //holder.tvDescription.setText(publicityStore.getDescription());
        Picasso.with(activity)
                .load(publicityStore.getImage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.thumbs_ttaudit)
                .into(holder.imgPhoto);
        if(publicityStore.getActive() == 0){
            holder.imgStatus.setVisibility(View.INVISIBLE);
            holder.btAudit.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.VISIBLE);
            holder.btAudit.setVisibility(View.INVISIBLE);
        }
        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StoreRepo storeRepo = new StoreRepo(activity);
                Store store = (Store) storeRepo.findById(store_id);
//                if(store.getStatus_change() == 0) {
//
//
//
//
//                    Poll poll = new Poll();
//                    poll.setPublicity_id(publicityStore.getId());
//                    poll.setOrder(23);
//                    PollPublicityActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                } else  if( store.getStatus_change() == 1) {
                    Poll poll = new Poll();
                    poll.setPublicity_store_id(publicityStore.getId());
                    poll.setCategory_product_id(publicityStore.getCategory_product_id());
                    if(publicityStore.getCategory_product_id() == 53) {
                        poll.setOrder(7);
                    } else if (publicityStore.getCategory_product_id() == 54) {
                        poll.setOrder(4);
                    }


                    PollPublicityActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                }


                Toast.makeText(activity,String.valueOf(publicityStore.getId()) + " - " + String.valueOf(publicityStore.getPublicity_id()) + " - " + String.valueOf(publicityStore.getCategory_product_id()),Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return publicityStores.size();
    }

    public class PublicityViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFullName;
        //private TextView    tvDescription;
        private Button btAudit;
        private ImageView imgPhoto;
        private ImageView imgStatus;

        public PublicityViewHolder(View itemView) {
            super(itemView);
            tvFullName      = (TextView) itemView.findViewById(R.id.tvFullName);
           // tvDescription   = (TextView) itemView.findViewById(R.id.tvDescription);
            btAudit         = (Button)  itemView.findViewById(R.id.btAudit);
            imgPhoto        = (ImageView)  itemView.findViewById(R.id.imgPhoto);
            imgStatus       = (ImageView)  itemView.findViewById(R.id.imgStatus);
        }
    }
}
