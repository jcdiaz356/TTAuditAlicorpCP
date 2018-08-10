package com.dataservicios.ttauditalicorpcp.model;

import com.j256.ormlite.field.DatabaseField;

public class PublicityStore {

    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String fullname;
    @DatabaseField
    private int publicity_id;
    @DatabaseField
    private String tipo_bodega;
    @DatabaseField
    private String type;
    @DatabaseField
    private int company_id;
    @DatabaseField
    private int category_product_id;
    @DatabaseField
    private String image;
    @DatabaseField
    private String category_name;
    @DatabaseField
    private int active;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getPublicity_id() {
        return publicity_id;
    }

    public void setPublicity_id(int publicity_id) {
        this.publicity_id = publicity_id;
    }

    public String getTipo_bodega() {
        return tipo_bodega;
    }

    public void setTipo_bodega(String tipo_bodega) {
        this.tipo_bodega = tipo_bodega;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getCategory_product_id() {
        return category_product_id;
    }

    public void setCategory_product_id(int category_product_id) {
        this.category_product_id = category_product_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}
