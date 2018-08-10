package com.dataservicios.ttauditalicorpcp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorpcp.R;
import com.dataservicios.ttauditalicorpcp.db.DatabaseManager;
import com.dataservicios.ttauditalicorpcp.model.AuditRoadStore;
import com.dataservicios.ttauditalicorpcp.model.Company;
import com.dataservicios.ttauditalicorpcp.model.Media;
import com.dataservicios.ttauditalicorpcp.model.Poll;
import com.dataservicios.ttauditalicorpcp.model.PollDetail;
import com.dataservicios.ttauditalicorpcp.model.PollOption;
import com.dataservicios.ttauditalicorpcp.model.Publicity;
import com.dataservicios.ttauditalicorpcp.model.PublicityStore;
import com.dataservicios.ttauditalicorpcp.model.Route;
import com.dataservicios.ttauditalicorpcp.model.Store;
import com.dataservicios.ttauditalicorpcp.repo.AuditRoadStoreRepo;
import com.dataservicios.ttauditalicorpcp.repo.CompanyRepo;
import com.dataservicios.ttauditalicorpcp.repo.PollOptionRepo;
import com.dataservicios.ttauditalicorpcp.repo.PollRepo;
import com.dataservicios.ttauditalicorpcp.repo.PublicityStoreRepo;
import com.dataservicios.ttauditalicorpcp.repo.RouteRepo;
import com.dataservicios.ttauditalicorpcp.repo.StoreRepo;
import com.dataservicios.ttauditalicorpcp.util.AuditUtil;
import com.dataservicios.ttauditalicorpcp.util.GPSTracker;
import com.dataservicios.ttauditalicorpcp.util.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class PollPublicityActivity extends AppCompatActivity {
    private static final String LOG_TAG = StoreAuditActivity.class.getSimpleName();
    private SessionManager          session;
    private Activity                activity =  this;
    private ProgressDialog          pDialog;
    private TextView                tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict,tvAuditoria,tvPoll,tvPublicity,tvType ;
    private EditText                etComent;
    private EditText                etCommentOption;
    private Button                  btSaveGeo;
    private Button                  btSave;
    private CheckBox[]              checkBoxArray;
    private RadioButton[]           radioButtonArray;
    private RadioGroup              radioGroup;
    private Switch                  swYesNo;
    private ImageButton             btPhoto;
    private LinearLayout            lyComment;
    private LinearLayout            lyOptions;
    private LinearLayout            lyOptionComment;
    private LinearLayout            lyPublicity;
    private int                     user_id;
    private int                     store_id;
    private int                     audit_id;
    private int                     company_id;
    private int                     orderPoll;
    private int                     category_product_id;
    private int                     publicity_id;
    private int                     publicity_store_id;
    private int                     product_id;
    private Route                   route ;
    private Store                   store ;
    private Poll                    poll;
    private PollOption              pollOption;
    private PollDetail              pollDetail;
    private Publicity               publicity;
    private PublicityStore          publicityStore;
    private RouteRepo               routeRepo ;
    private AuditRoadStoreRepo      auditRoadStoreRepo ;
    private StoreRepo               storeRepo ;
    private CompanyRepo             companyRepo ;
    private PollRepo                pollRepo ;
    private AuditRoadStore          auditRoadStore;
    private PollOptionRepo          pollOptionRepo;
//    private PublicityRepo           publicityRepo;
    private PublicityStoreRepo      publicityStoreRepo;
    private GPSTracker              gpsTracker;
    private ArrayList<PollOption>   pollOptions;
    private int                     isYesNo;
    private String                  comment;
    private String                  selectedOptions;
    private String                  commentOptions;


    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param company_id
     * @param audit_id
     * @param poll Objeti tipo poll
     */
    public static void createInstance(Activity activity, int company_id, int audit_id, Poll poll) {
        Intent intent = getLaunchIntent(activity, company_id,audit_id,poll);
        activity.startActivity(intent);
    }
    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param store_id
     * @param audit_id
     * @return retorna un Intent listo para usar
     */
    private static Intent getLaunchIntent(Context context, int store_id, int audit_id, Poll poll) {
        Intent intent = new Intent(context, PollPublicityActivity.class);
        intent.putExtra("store_id"              , store_id);
        intent.putExtra("audit_id"              , audit_id);
        intent.putExtra("orderPoll"             , poll.getOrder());
        intent.putExtra("category_product_id"   , poll.getCategory_product_id());
        intent.putExtra("publicity_id"          , poll.getPublicity_id());
        intent.putExtra("publicity_store_id"    , poll.getPublicity_store_id());
        intent.putExtra("product_id"            , poll.getProduct_id());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_publicity);


        tvStoreFullName     = (TextView)    findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView)    findViewById(R.id.tvStoreId) ;
        tvAddress           = (TextView)    findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView)    findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView)    findViewById(R.id.tvDistrict) ;
        tvAuditoria         = (TextView)    findViewById(R.id.tvAuditoria) ;
        tvPoll              = (TextView)    findViewById(R.id.tvPoll) ;
        tvPublicity         = (TextView)    findViewById(R.id.tvPublicity) ;
        tvType              = (TextView)    findViewById(R.id.tvType) ;
        btSaveGeo           = (Button)      findViewById(R.id.btSaveGeo);
        btSave              = (Button)      findViewById(R.id.btSave);
        btPhoto             = (ImageButton) findViewById(R.id.btPhoto);
        swYesNo             = (Switch)      findViewById(R.id.swYesNo);
        lyComment           = (LinearLayout)findViewById(R.id.lyComment);
        lyOptions           = (LinearLayout)findViewById(R.id.lyOptions);
        lyOptionComment     = (LinearLayout)findViewById(R.id.lyOptionComment);
        lyPublicity         = (LinearLayout) findViewById(R.id.lyPublicity);
        DatabaseManager.init(this);
        gpsTracker = new GPSTracker(activity);
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }

        Bundle bundle = getIntent().getExtras();
        store_id            = bundle.getInt("store_id");
        audit_id            = bundle.getInt("audit_id");
        orderPoll           = bundle.getInt("orderPoll");
        category_product_id = bundle.getInt("category_product_id");
        publicity_id        = bundle.getInt("publicity_id");
        publicity_store_id  = bundle.getInt("publicity_store_id");
        product_id          = bundle.getInt("product_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        routeRepo           = new RouteRepo(activity);
        storeRepo           = new StoreRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);
        pollRepo            = new PollRepo(activity);
        pollOptionRepo      = new PollOptionRepo((activity));
//        publicityRepo       = new PublicityRepo(activity);
        publicityStoreRepo  = new PublicityStoreRepo(activity);

        etCommentOption     = new EditText(activity);
        etComent            = new EditText(activity);

        final ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        store               = (Store)           storeRepo.findById(store_id);
        route               = (Route)           routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)  auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        poll                = (Poll)            pollRepo.findByCompanyAuditIdAndOrder(auditRoadStore.getList().getCompany_audit_id(),orderPoll);
//        publicity           = (Publicity)       publicityRepo.findById(publicity_id);
        publicityStore      = (PublicityStore)  publicityStoreRepo.findById(publicity_store_id);


        poll.setCategory_product_id(category_product_id);
        poll.setProduct_id(product_id);
        poll.setPublicity_id(publicity_id);
        pollOptions = (ArrayList<PollOption>) pollOptionRepo.findByPollId(poll.getId());

        //showToolbar(publicity.getFullname(),false);

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        tvAuditoria.setText(auditRoadStore.getList().getFullname().toString());
        tvPoll.setText(poll.getQuestion().toString());
        tvType.setText(String.valueOf(store.getType()) + " (" + store.getCadenRuc() + ")");
        tvPublicity.setText(publicityStore.getFullname().toString());

        showToolbar(auditRoadStore.getList().getFullname().toString(),true);

//        if(publicity != null) {
//            tvPublicity.setText(publicity.getFullname().toString() + " ("+ publicity.getId() + ") ");
//        } else{
//            lyPublicity.removeAllViews();
//        }

        establishigPropertyPool(orderPoll);

        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        commentOptions = "";
                        comment = "";
                        if(swYesNo.isChecked())isYesNo=1; else isYesNo =0;
                        selectedOptions="";
                        int counterSelected =0;
                        if(radioButtonArray != null) {

                            for(RadioButton r:radioButtonArray ) {
                                if(r.isChecked()){
                                    selectedOptions=r.getTag().toString();
                                    counterSelected ++;
                                }
                            }
                            commentOptions = etCommentOption.getText().toString() ;
                            if(counterSelected==0){
                                Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if(checkBoxArray != null) {
                            for(CheckBox r:checkBoxArray ) {
                                if(r.isChecked()){
                                    selectedOptions= r.getTag().toString()+"|" + selectedOptions;
                                    counterSelected ++;
                                }
                            }
                            for (PollOption m: pollOptions) {
                                if (m.getComment()==1) {
                                    commentOptions = etCommentOption.getText().toString() + "|" + commentOptions;
                                } else {
                                    commentOptions =  "|" + commentOptions;
                                }
                            }
                            if(counterSelected==0){
                                Toast.makeText(activity, R.string.message_select_options, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        comment = etComent.getText().toString();
                       // commentOptions = etCommentOption.getText().toString();

                        new savePoll().execute();
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

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    private void takePhoto() {

        Media media = new Media();
        media.setStore_id(store_id);
        media.setPoll_id(poll.getId());
        media.setCompany_id(company_id);
        media.setPublicity_id(publicity_id);
//        media.setVisit_id(store.getVisit_id());
        media.setType(1);
        AndroidCustomGalleryActivity.createInstance((Activity) activity, media);
    }


    /**
     * Guarda la pregunta
     */
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

            boolean result = logicProcess(orderPoll);

            return result;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                resulProcess(orderPoll);
            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    /**
     * Guarda la pregunta segun el orden en casos
     * @param orderPoll
     * @return
     */
    private boolean logicProcess(int orderPoll) {

        pollDetail = new PollDetail();
        pollDetail.setPoll_id(poll.getId());
        pollDetail.setStore_id(store_id);
        pollDetail.setSino(poll.getSino());
        pollDetail.setOptions(poll.getOptions());
        pollDetail.setLimits(0);
        pollDetail.setMedia(poll.getMedia());
        pollDetail.setComment(0);
        pollDetail.setResult(isYesNo);
        pollDetail.setLimite("0");
        pollDetail.setComentario(comment);
        pollDetail.setAuditor(user_id);
        pollDetail.setProduct_id(poll.getProduct_id());
        pollDetail.setCategory_product_id(poll.getCategory_product_id());
        pollDetail.setPublicity_id(poll.getPublicity_id());
        pollDetail.setCompany_id(company_id);
        pollDetail.setCommentOptions(poll.getComment());
        pollDetail.setSelectdOptions(selectedOptions);
//        pollDetail.setVisit_id(store.getVisit_id());
        pollDetail.setSelectedOtionsComment(commentOptions);
        pollDetail.setPriority(0);

        if(poll.getCategory_product_id() == 54) {

            switch (orderPoll) {
                case 23:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if(isYesNo==1) {
                        if(poll.getProduct_id()==0) {
//                            if (!AuditUtil.insertHistoryPublicityStore(publicity_id,company_id,store_id,store.getVisit_id())) return false;
                        }
                    }

                    break;
                case 28:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
//                if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;
                    //if (!AuditUtil.sendAlertPlanningPop(company_id, store_id,route.getId())) return false;
                    break;

                case 212:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;

                    break;
                default:
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    break;
            }
        }


        return true;
    }

    /**
     * Resultado despues de aplicar logicProcess
     * @param orderPoll
     */
    private void resulProcess (int orderPoll) {

        switch (orderPoll) {
            case 23:
//                Exception para Vitamina ASS ----------------------

                    if(isYesNo==1) {
                        if(publicity_id==564){
                            poll.setOrder(24);
                            PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                        } else if (publicity_id==565){
                            if(store.getCadenRuc().equals("MIFARMA")) {
                                poll.setOrder(26);
                                PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                            } else {
                                poll.setOrder(25);
                                PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                            }
                        }
                        else if(publicity_id==586 && store.getType().equals("AASS")) {
                            poll.setOrder(214);
                            PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);

                        }                        else {
                            poll.setOrder(26);
                            PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                        }

                    } else if(isYesNo==0){
//                        publicity.setStatus(1);
//                        publicityRepo.update(publicity);

                    }

//                    if(publicity_id==683) {
//                        if (poll.getProduct_id() == 0) {
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("store_id", Integer.valueOf(store_id));
//                            bundle.putInt("audit_id", Integer.valueOf(audit_id));
//                            bundle.putInt("publicity_id", Integer.valueOf(publicity_id));
//                            Intent intent = new Intent(activity, ProductAASSActivity.class);
//                            intent.putExtras(bundle);
//                            activity.startActivity(intent);
//                        } else {
//                            ProductPublicity productPublicity;
//                            ProductPublicityRepo productPublicityRepo = new ProductPublicityRepo(activity);
//                            productPublicity = (ProductPublicity) productPublicityRepo.findById(poll.getProduct_id());
//                            productPublicity.setStatus(1);
//                            productPublicityRepo.update(productPublicity);
//                        }
//                    }


            finish();
                break;
            case 24:
                if(store.getCadenRuc().equals("MIFARMA") || store.getCadenRuc().equals("UNIVERSAL_CENTRAL") || store.getCadenRuc().equals("UNIVERSAL_SURCO")  && isYesNo==1){
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("store_id", Integer.valueOf(store_id));
//                    bundle.putInt("audit_id", Integer.valueOf(audit_id));
//                    bundle.putInt("publicity_id", Integer.valueOf(publicity_id));
//                    Intent intent = new Intent(activity,StockProductPopActivity.class);
//                    intent.putExtras(bundle);
//                    activity.startActivity(intent);
//                    finish();
                }else{
                    poll.setOrder(26);
                    PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                    finish();
                }

                break;
            case 25:
//                if(publicity_id==561) {
//                    publicity.setStatus(1);
//                    publicityRepo.update(publicity);
//                    finish();
//                }
//                else {
//                    poll.setOrder(26);
//                    PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
//                    finish();
//                }

                break;
            case 214:
                poll.setOrder(26);
                PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                finish();
                break;
            case 26:
                poll.setOrder(27);
                PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
                finish();
                break;
            case 27:
//                if(publicity_id==561) {
//                    poll.setOrder(25);
//                    PollPublicityActivity.createInstance(activity, store_id,audit_id,poll);
//                    finish();
//                }
//                else {
//                    publicity.setStatus(1);
//                    publicityRepo.update(publicity);
//                    finish();
//                }
//
//                break;

            case 212:
//                publicity.setStatus(1);
//                publicityRepo.update(publicity);
//                finish();
//                break;
            case 28:

                //AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
//                AuditRoadStore auditRoadStore = (AuditRoadStore) auditRoadStoreRepo.findByStoreIdAndAuditIdAndVisitId(store_id,audit_id,store.getVisit_id());
//                auditRoadStore.setAuditStatus(1);
//                auditRoadStoreRepo.update(auditRoadStore);
                Bundle bundle = new Bundle();
                bundle.putInt("store_id", Integer.valueOf(store_id));
                bundle.putInt("audit_id", Integer.valueOf(audit_id));
//                Intent intent = new Intent(activity,PublicitiesActivity.class);
//                Intent intent = new Intent(activity,CompetityActivity.class);
//                intent.putExtras(bundle);
//                activity.startActivity(intent);
                finish();
                break;
        }

    }

    /**
     * Estableciendo propiedades de un Poll (Media, comment, options)
     */
    private void establishigPropertyPool(int orderPoll) {


        if(poll.getMedia() == 1)  btPhoto.setVisibility(View.VISIBLE); else btPhoto.setVisibility(View.INVISIBLE);
        if(poll.getSino() == 1)  swYesNo.setVisibility(View.VISIBLE); else swYesNo.setVisibility(View.INVISIBLE);
        if(poll.getComment() == 1) showCommentControl(true); else showCommentControl(false);

//        ------Exception Vitaminas AASS - products
        if(poll.getProduct_id()!=0) {
            btPhoto.setVisibility(View.GONE);
        }
//        ------- end -----------------------
        switch (orderPoll) {
            case 26:
                showPollOptionsControl(true);
                swYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) showPollOptionsControl(false); else showPollOptionsControl(true);
                    }
                });
                break;
            case 27:
                showPollOptionsControl(true);
                swYesNo.setTextOn("Bien");
                swYesNo.setTextOff("Mal");
                swYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) showPollOptionsControl(false); else showPollOptionsControl(true);
                    }
                });
                break;
            case 28:
                showPollOptionsControl(false);
                swYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) showPollOptionsControl(true); else showPollOptionsControl(false);
                    }
                });
                break;

            case 212:
                showPollOptionsControl(false);
                swYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) showPollOptionsControl(true); else showPollOptionsControl(false);
                    }
                });
                break;

        }
    }


    /**
     * Muestra control de comentario para el Poll principal
     * @param visibility
     */
    private void showCommentControl(boolean visibility) {

        if(visibility){
            //lyComment.setVisibility(View.VISIBLE);
            lyComment.removeAllViews();
            etComent.setHint(activity.getString(R.string.text_comment));
            lyComment.addView(etComent);
        } else {
            lyComment.removeAllViews();
        }
    }


    /**
     * Muestra las opciones de un Poll siempre y cuando tenga option
     * @param visibility
     */
    private void showPollOptionsControl(boolean visibility) {
//        ArrayList<PollOption>  pollOptions;
//        pollOptions = (ArrayList<PollOption>) pollOptionRepo.findByPollId(poll.getId());


        if(radioGroup != null ){
            radioGroup.clearCheck();
        }
        if (visibility){
            lyOptions.removeAllViews();
            lyOptionComment.removeAllViews();
            if(poll.getOptions()== 1) {
                if (poll.getOption_type() == 0) {
                    radioGroup = new RadioGroup(activity);
                    radioGroup.setOrientation(LinearLayout.VERTICAL);
                    radioButtonArray = new RadioButton[pollOptions.size()];
                    int counter =0;
                    for (PollOption po: pollOptions){
                        radioButtonArray[counter] = new RadioButton(activity);
                        radioButtonArray[counter].setText(po.getOptions());
                        radioButtonArray[counter].setTag(po.getCodigo());
                        if(po.getComment()==1) {
                            radioButtonArray[counter].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lyOptionComment.removeAllViews();
                                    etCommentOption.setHint(activity.getString(R.string.text_comment));
                                    lyOptionComment.addView(etCommentOption);
                                }
                            });
                        } else if(po.getComment()==0){
                            radioButtonArray[counter].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lyOptionComment.removeAllViews();
                                }
                            });
                        }
                        radioGroup.addView(radioButtonArray[counter]);
                        counter ++;
                    }
                    lyOptions.addView(radioGroup);
                } else if (poll.getOption_type() == 1) {
                    checkBoxArray = new CheckBox[pollOptions.size()];
                    int  counter =0;
                    for ( final PollOption po: pollOptions){
                        checkBoxArray[counter] = new CheckBox(activity);
                        checkBoxArray[counter].setText(po.getOptions());
                        checkBoxArray[counter].setTag(po.getCodigo());
                        if(po.getComment()==1) {

                            checkBoxArray[counter].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (buttonView.isChecked()) {
                                        lyOptionComment.removeAllViews();
                                        etCommentOption.setHint(activity.getString(R.string.text_comment));
                                        etCommentOption.setTag(po.getCodigo().toString());
                                        lyOptionComment.addView(etCommentOption);
                                    }
                                    else
                                    {
                                        //not checked
                                        lyOptionComment.removeAllViews();
                                    }
                                }
                            });

                        }
//                        else if(po.getComment()==0){
//                            checkBoxArray[counter].setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    lyOptionComment.removeAllViews();
//                                }
//                            });
//                        }
                        lyOptions.addView(checkBoxArray[counter]);
                        counter ++;
                    }
                    //lyOptions.addView(radioGroup);
                }
            }
        } else {

            lyOptions.removeAllViews();
            lyOptionComment.removeAllViews();
            radioButtonArray = null;
            checkBoxArray = null;
        }

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
        alertDialogBasico(getString(R.string.message_audit_init) );
        //super.onBackPressed();
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
