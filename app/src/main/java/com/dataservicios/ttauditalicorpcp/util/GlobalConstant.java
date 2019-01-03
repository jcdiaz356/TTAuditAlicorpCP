package com.dataservicios.ttauditalicorpcp.util;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {


    public static String dominio = "http://ttaudit.com";
//    public static String dominio = "http://7d8836d7.ngrok.io";
    public static final String URL_USER_IMAGES = dominio + "/media/users/";
    public static final String URL_PUBLICITY_IMAGES = dominio + "/media/images/alicorp/publicities/";
  //  public static String directory_images = "/Pictures/" ;
 //   public static final String UPLOAD_URL = "http://ab02935a.ngrok.io/upload.php";

//  Prefijos para las imagenes
    public static final String JPEG_FILE_PREFIX = "_cp_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

//  Directorios para  almacenamiento
    public static final String ALBUN_NAME = "cp_Photo";
    public static final String ALBUN_NAME_TEMP = "cp_Photo_Temp";
    public static final String ALBUN_NAME_BACKUP = "cp_Photo_Backup";
    public static final String DATA_BASE_DIR_NAME_BACKUP = "cp_db_Backup";

//   Variable  apps externos
    public static final String MARKET_OPEN_APP_ESFILEEXPLORE = "market://details?id=com.estrongs.android.pop&hl=es";
    public static final String DATABASE_PATH_DIR ="/data/com.dataservicios.ttauditalicorpcp/databases/db_alicorpcp";
}

