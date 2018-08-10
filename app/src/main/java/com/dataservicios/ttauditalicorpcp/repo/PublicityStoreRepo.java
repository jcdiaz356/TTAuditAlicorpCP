package com.dataservicios.ttauditalicorpcp.repo;

import android.content.Context;

import com.dataservicios.ttauditalicorpcp.db.DatabaseHelper;
import com.dataservicios.ttauditalicorpcp.db.DatabaseManager;
import com.dataservicios.ttauditalicorpcp.model.PublicityStore;

import java.sql.SQLException;
import java.util.List;

public class PublicityStoreRepo implements Crud {
    private DatabaseHelper helper;

    public PublicityStoreRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        PublicityStore object = (PublicityStore) item;
        try {
            index = helper.getPublicityStoreDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        PublicityStore object = (PublicityStore) item;

        try {
            helper.getPublicityStoreDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        PublicityStore object = (PublicityStore) item;

        try {
            helper.getPublicityStoreDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<PublicityStore> items = null;
        int counter = 0;
        try {
            items = helper.getPublicityStoreDao().queryForAll();

            for (PublicityStore object : items) {
                // do something with object
                helper.getPublicityStoreDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        PublicityStore wishList = null;
        try {
            wishList = helper.getPublicityStoreDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<PublicityStore> items = null;

        try {
            items = helper.getPublicityStoreDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }

    @Override
    public Object findFirstReg() {

        Object wishList = null;
        try {
            wishList = helper.getPublicityStoreDao().queryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    @Override
    public long countReg() {
        long count = 0;
        try {
            count = helper.getPublicityStoreDao().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Busca una lista de PublicityStore por su company_id
     * @param company_id
     * @return Retorna lista de PublicityStores
     */
    public List<PublicityStore> findByCompanyId(int company_id) {

        List<PublicityStore> wishList = null;
        try {
            wishList = helper.getPublicityStoreDao().queryBuilder().where().eq("company_id",company_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    /**
     * Busca una lista de PublicityStore por su type
     * @param type tipo pe producto: Propio o competencia
     * @return Retorna lista de PublicityStores
     */
    public List<PublicityStore> findByTypeCompetity(int type) {

        List<PublicityStore> wishList = null;
        try {
            wishList = helper.getPublicityStoreDao().queryBuilder().where().eq("type",type).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

    public List<PublicityStore> findByCategoryIdAndTypeBodega(int category_id, String type) {

        List<PublicityStore> wishList = null;
        try {
            wishList = helper.getPublicityStoreDao().queryBuilder().where().eq("category_product_id",category_id).and().eq("type",type).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


}