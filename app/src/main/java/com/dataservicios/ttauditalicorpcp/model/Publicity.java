package com.dataservicios.ttauditalicorpcp.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Jaime Eduardo on 06/10/2015.
 */
public class Publicity {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String fullname;
    @DatabaseField
    private String image;
    @DatabaseField
    private int category_id;
    @DatabaseField
    private String category_name;
    @DatabaseField
    private int company_id;
    @DatabaseField
    private int active;
}
