package com.dataservicios.ttauditalicorpcp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.adapter.PublicityAdapterReciclerView;

import com.dataservicios.ttauditalicorpcp.db.DatabaseManager;
import com.dataservicios.ttauditalicorpcp.model.Audit;
import com.dataservicios.ttauditalicorpcp.model.AuditRoadStore;
import com.dataservicios.ttauditalicorpcp.model.Company;
import com.dataservicios.ttauditalicorpcp.model.Publicity;

import com.dataservicios.ttauditalicorpcp.model.PublicityStore;
import com.dataservicios.ttauditalicorpcp.model.Route;
import com.dataservicios.ttauditalicorpcp.model.Store;
import com.dataservicios.ttauditalicorpcp.repo.AuditRepo;
import com.dataservicios.ttauditalicorpcp.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditalicorpcp.repo.CompanyRepo;

import com.dataservicios.ttauditalicorpcp.repo.PublicityStoreRepo;
import com.dataservicios.ttauditalicorpcp.repo.RouteRepo;
import com.dataservicios.ttauditalicorpcp.repo.StoreRepo;
import com.dataservicios.ttauditalicorpcp.util.AuditUtil;
import com.dataservicios.ttauditalicorpcp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class PublicitiesActivity extends AppCompatActivity {
    private static final String LOG_TAG = PublicitiesActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager                          session;
    private Activity activity =  this;
    private int                                     user_id;
    private int                                     store_id;
    private int                                     audit_id;
    private int                                     category_product_id;
    private TextView tvPublicityHistoryTitle;
    private TextView tvTotal;
    private Button btSave;
    private PublicityStoreRepo                           publicityStoreRepo ;
    private AuditRepo                               auditRepo ;
    private StoreRepo                               storeRepo;
    private CompanyRepo                             companyRepo;
    private RouteRepo                               routeRepo;
    private AuditRoadStoreRepo                      auditRoadStoreRepo ;
    private PublicityAdapterReciclerView            publicityAdapterReciclerView;
    private RecyclerView publicityRecyclerView;

    private Publicity                               publicity ;
    private Audit                                   audit ;
    private Store                                   store;
    private Company                                 company;
    private Route                                   route;
    private AuditRoadStore                          auditRoadStore;
    private ArrayList<PublicityStore> publicityStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicities);

        tvTotal                 = (TextView) findViewById(R.id.tvTotal);
        btSave                  = (Button) findViewById(R.id.btSave);



        DatabaseManager.init(this);

        publicityStoreRepo           = new PublicityStoreRepo(activity);
        auditRepo               = new AuditRepo(activity);
        storeRepo               = new StoreRepo(activity);
        companyRepo             = new CompanyRepo(activity);
        routeRepo               = new RouteRepo(activity);
        auditRoadStoreRepo      = new AuditRoadStoreRepo(activity);

        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        audit_id            = bundle.getInt("audit_id");
        category_product_id = bundle.getInt("category_product_id");


        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        audit               = (Audit) auditRepo.findById(audit_id);
        store               = (Store) storeRepo.findById(store_id);
        route               = (Route) routeRepo.findById(store.getRoute_id());
        company             = (Company) companyRepo.findFirstReg();
        auditRoadStore      = (AuditRoadStore)  auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        showToolbar(audit.getFullname().toString(),false);

        publicityRecyclerView  = (RecyclerView) findViewById(R.id.publicity_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        publicityRecyclerView.setLayoutManager(linearLayoutManager);

//        publicityStores = (ArrayList<PublicityStore>) publicityStoreRepo.findAll();
        publicityStores = (ArrayList<PublicityStore>) publicityStoreRepo.findByCategoryIdAndTypeBodega(category_product_id,store.getType());

        int contador = 0;
       // publicityStores.indexOf(m.getId());
//        if(store.getType().equals("AASS")) {
//            publicityStores.clear();
//            publicityStores.add((Publicity) publicityStoreRepo.findById(585));
//            publicityStores.add((Publicity) publicityStoreRepo.findById(586));
//            publicityStores.add((Publicity) publicityStoreRepo.findById(683));
//        } else {
//                for(int i = 0; i< publicityStores.size(); i++) {
//                    if(publicityStores.get(i).getId() == 585 ) publicityStores.remove(i);
//                }
//                for(int i = 0; i< publicityStores.size(); i++) {
//                    if(publicityStores.get(i).getId() == 586 ) publicityStores.remove(i);
//                }
//                for(int i = 0; i< publicityStores.size(); i++) {
//                    if(publicityStores.get(i).getId() == 683 ) publicityStores.remove(i);
//                }
//
//        }

        publicityAdapterReciclerView =  new PublicityAdapterReciclerView(publicityStores, R.layout.cardview_publicity, activity,store_id,audit_id);

        publicityRecyclerView.setAdapter(publicityAdapterReciclerView);


        int total               = publicityStores.size();
        int publicitiesAudits   = 0;

        for(PublicityStore p: publicityStores){
            if(p.getActive()==1) publicitiesAudits ++;
        }

        tvTotal.setText(String.valueOf(publicitiesAudits) + " de " + String.valueOf(total));

        if(publicityStores.size() == 0) {
            btSave.setVisibility(View.INVISIBLE);
        }


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PublicityStore p:publicityStores ){
                    if(p.getActive()==0){
                        alertDialogBasico(getString(R.string.message_audit_material_pop) + ": \n " + p.getFullname().toString());
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

//                        if(store.getStatus_change() ==1) {
//                            finish();
//                        }
//                        else if(store.getStatus_change() == 0) {
//                            if(store.getType().equals("AASS")) {
//                                new savePoll().execute();
//                            } else {
//                                Poll poll = new Poll();
//                                poll.setPublicity_id(0);
//                                poll.setOrder(28);
//                                PollPublicityActivity.createInstance((Activity) activity, store_id,audit_id,poll);
//                                finish();
//                            }
//                        }
                        new savePoll().execute();
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



    class savePoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando ProductDetail...");
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
//            if(store.getVisit_id()==1){
//                if (!AuditUtil.sendAlertPlanningPop(company.getId(), store_id,route.getId())) return false;
//            }


            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once productDetail deleted
            if (result){

                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
//                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditIdAndVisitId(store_id,audit_id,store.getVisit_id());
                auditRoadStore.setAuditStatus(1);
                auditRoadStoreRepo.update(auditRoadStore);
                finish();
            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }


    public void showToolbar(String title, boolean upButton){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

//        if(publicityStores.size() == 0 ) {
//            super.onBackPressed ();
//        } else {
//            for (Publicity p:publicityStores ){
//
//                if(p.getStatus()==0){
//                    alertDialogBasico(getString(R.string.message_audit_material_pop) + ": \n " + p.getFullname().toString());
//                    return;
//                }
//            }
//            alertDialogBasico(getString(R.string.message_save_audit_material_pop));
//        }

        if(publicityStores.size() == 0 ) {
            super.onBackPressed ();
        } else {
            alertDialogBasico(getString(R.string.message_save_audit_products));
        }

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
