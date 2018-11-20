package com.dataservicios.ttauditalicorpcp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.repo.ProductRepo;
import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.adapter.CategoryProductAdapterRecyclerView;
import com.dataservicios.ttauditalicorpcp.db.DatabaseManager;
import com.dataservicios.ttauditalicorpcp.model.Audit;
import com.dataservicios.ttauditalicorpcp.model.AuditRoadStore;
import com.dataservicios.ttauditalicorpcp.model.CategoryProduct;
import com.dataservicios.ttauditalicorpcp.model.Company;
import com.dataservicios.ttauditalicorpcp.model.Poll;
import com.dataservicios.ttauditalicorpcp.model.Route;
import com.dataservicios.ttauditalicorpcp.model.Store;
import com.dataservicios.ttauditalicorpcp.repo.AuditRepo;
import com.dataservicios.ttauditalicorpcp.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditalicorpcp.repo.CategoryProductRepo;
import com.dataservicios.ttauditalicorpcp.repo.CompanyRepo;
import com.dataservicios.ttauditalicorpcp.repo.PollRepo;
import com.dataservicios.ttauditalicorpcp.repo.ProductRepo;
import com.dataservicios.ttauditalicorpcp.repo.RouteRepo;
import com.dataservicios.ttauditalicorpcp.repo.StoreRepo;
import com.dataservicios.ttauditalicorpcp.util.AuditUtil;
import com.dataservicios.ttauditalicorpcp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryProductActivity extends AppCompatActivity {
    private static final String LOG_TAG = CategoryProductActivity.class.getSimpleName();
    private SessionManager session;
    private Activity activity =  this;
    private ProgressDialog pDialog;
    private int                                     user_id;
    private int                                     store_id;
    private int                                     audit_id;
    private TextView tvTotal;
    private Button btSave;
    private ProductRepo productRepo;
    private StoreRepo storeRepo ;
    private RouteRepo routeRepo ;
    private CompanyRepo companyRepo ;
    private CategoryProductRepo categoryProductRepo ;
    private AuditRepo auditRepo ;
    private AuditRoadStoreRepo auditRoadStoreRepo ;
    private PollRepo pollRepo ;
    private CategoryProductAdapterRecyclerView categoryProductAdapterRecyclerView;
    private RecyclerView categoryProductRecyclerView;
    private Audit audit ;
    private CategoryProduct categoryProduct;
    private Company company ;
    private Route route ;
    private Store store ;
    private Poll poll;
    private AuditRoadStore auditRoadStore;
    private ArrayList<CategoryProduct> categoryProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        tvTotal                 = (TextView) findViewById(R.id.tvTotal);
        btSave                  = (Button) findViewById(R.id.btSave);
        DatabaseManager.init(this);

        storeRepo           = new StoreRepo(activity);
        productRepo         = new ProductRepo(activity);
        auditRepo           = new AuditRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        routeRepo           = new RouteRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);
        categoryProductRepo = new CategoryProductRepo(activity);
        pollRepo            = new PollRepo(activity);

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        audit_id = bundle.getInt("audit_id");

        company             = (Company)companyRepo.findFirstReg();
        store               = (Store) storeRepo.findById(store_id);
        route               = (Route) routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)  auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
       // poll                = (Poll) pollRepo.findByCompanyAuditIdAndOrder(auditRoadStore.getList().getCompany_audit_id(),3);

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        audit = (Audit) auditRepo.findById(audit_id);
        showToolbar(audit.getFullname().toString(),false);

        categoryProductRecyclerView  = (RecyclerView) findViewById(R.id.category_product_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryProductRecyclerView.setLayoutManager(linearLayoutManager);

        categoryProducts = (ArrayList<CategoryProduct>) categoryProductRepo.findAll();

        categoryProductAdapterRecyclerView =  new CategoryProductAdapterRecyclerView(categoryProducts, R.layout.cardview_category_product, activity,store_id,audit_id,company.getId());
        categoryProductRecyclerView.setAdapter(categoryProductAdapterRecyclerView);

        int total               = categoryProducts.size();
        int productsAudits   = 0;

        for(CategoryProduct p: categoryProducts){
            if(p.getStatus()==1) productsAudits ++;
        }

        tvTotal.setText(String.valueOf(productsAudits) + " de " + String.valueOf(total));
        if(categoryProducts.size() == 0) {
            btSave.setVisibility(View.INVISIBLE);
        }
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CategoryProduct p:categoryProducts ){
                    if(p.getStatus()==0){
                        alertDialogBasico(getString(R.string.message_audit_categories) + ": \n " + p.getFullname().toString());
                        return;
                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                         new savePoll().execute();


//                        Poll poll = new Poll();
//                        poll.setOrder(9);
//                        PollActivity.createInstance((Activity) activity, store_id,audit_id,poll);



                       // finish();
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


    }

    private ArrayList<CategoryProduct> filter(ArrayList<CategoryProduct> models, String query) {

        query = query.toLowerCase();
        final ArrayList<CategoryProduct> filteredModelList = new ArrayList<>();
        for (CategoryProduct s : models) {
            final String fullName = s.getFullname().toLowerCase().trim();
            if (fullName.contains(query) ) {
                filteredModelList.add(s);
            }

        }
        return filteredModelList;
    }

    public void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //storeAdapterRecyclerView.getFilter().filter(newText.toString());
                final ArrayList<CategoryProduct> filteredMStoreList = filter(categoryProducts, newText);
                //adapter.setFilter(filteredModelList);
                categoryProductAdapterRecyclerView.setFilter(filteredMStoreList);
                return false;
            }
        });
        return true;
    }

    class savePoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!AuditUtil.closeAuditStore(audit_id, store_id, company.getId(), route.getId())) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted
            if (result){
                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
                //AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditIdAndVisitId(store_id,audit_id,store.getVisit_id());
                auditRoadStore.setAuditStatus(1);
                auditRoadStoreRepo.update(auditRoadStore);
                finish();
            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

        if(categoryProducts.size() == 0 ) {
            super.onBackPressed ();
        } else {
            alertDialogBasico(getString(R.string.message_save_audit_products));
        }

        //super.onBackPressed ();

    }


    private void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.show();

    }
}