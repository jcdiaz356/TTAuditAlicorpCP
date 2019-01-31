package com.dataservicios.ttauditalicorpcp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataservicios.ttauditalicorpcp.model.CategoryProduct;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.model.CategoryProduct;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.view.AndroidCustomGalleryActivity;
import com.dataservicios.ttauditalicorpcp.view.CategoryProductActivity;
import com.dataservicios.ttauditalicorpcp.view.ProductCompetityActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryProductAdapterRecyclerView extends RecyclerView.Adapter<CategoryProductAdapterRecyclerView.CategoryProductViewHolder> {
    private ArrayList<CategoryProduct>  categoryProducts;
    private int                         resource;
    private Activity                    activity;
    private int                         store_id;
    private int                         audit_id;
    private int                         poll_id;
    private int                         company_id;

    public CategoryProductAdapterRecyclerView(ArrayList<CategoryProduct> categoryProducts, int resource, Activity activity, int store_id, int audit_id,  int company_id) {
        this.categoryProducts = categoryProducts;
        this.resource       = resource;
        this.activity       = activity;
        this.store_id       = store_id;
        this.audit_id       = audit_id;
       // this.poll_id       = poll_id;
        this.company_id       = company_id;

    }

    @Override
    public CategoryProductAdapterRecyclerView.CategoryProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new CategoryProductAdapterRecyclerView.CategoryProductViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(CategoryProductViewHolder holder, int position) {
        final CategoryProduct categoryProduct = categoryProducts.get(position);

        holder.tvFullName.setText(categoryProduct.getFullname());

//        Picasso.get()
//                .load(categoryProduct.getImagen())
//                .placeholder(R.drawable.loading_image)
//                .error(R.drawable.thumbs_ttaudit)
//                .into(holder.imgPhoto);

        if(categoryProduct.getStatus() == 0){
            holder.imgStatus.setVisibility(View.INVISIBLE);
           // holder.btAudit.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatus.setVisibility(View.VISIBLE);
           // holder.btAudit.setVisibility(View.INVISIBLE);
        }
        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Media media = new Media();
//                media.setStore_id(store_id);
//                media.setPoll_id(poll_id);
//                media.setCompany_id(company_id);
//                media.setCategory_product_id(categoryProduct.getId());
//                media.setType(1);
//                AndroidCustomGalleryActivity.createInstance((Activity) activity, media);


                Bundle bundle = new Bundle();
                bundle.putInt("store_id",store_id);
                bundle.putInt("audit_id",audit_id);
                bundle.putInt("category_product_id",categoryProduct.getId());

                Intent intent = new Intent(activity, ProductCompetityActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
               // finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryProducts.size();
    }

    public class CategoryProductViewHolder  extends RecyclerView.ViewHolder{

        private TextView tvFullName;

        private ImageView imgPhoto;
        private ImageView imgStatus;
        private Button btAudit;

        public CategoryProductViewHolder(View itemView) {
            super(itemView);
            tvFullName      = (TextView) itemView.findViewById(R.id.tvFullName);
            imgPhoto        = (ImageView)  itemView.findViewById(R.id.imgPhoto);
            btAudit         = (Button)  itemView.findViewById(R.id.btAudit);
            imgStatus       = (ImageView)  itemView.findViewById(R.id.imgStatus);
        }
    }

    public void setFilter(ArrayList<CategoryProduct> categoryProducts1){
        this.categoryProducts = new ArrayList<>();
        this.categoryProducts.addAll(categoryProducts1);
        notifyDataSetChanged();
    }


}
