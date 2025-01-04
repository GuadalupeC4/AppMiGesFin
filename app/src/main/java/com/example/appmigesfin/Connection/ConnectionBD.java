package com.example.appmigesfin.Connection;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionBD {

    private String ip = "192.168.0.5";
    private String usuario = "sa";
    private String password = "042027";
    private String basedatos = "MIGESFIN";

    public Connection connect(){
        Connection connection = null;
        String connectionURL = null;

        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + this.ip + "/" + this.basedatos +";user=" + this.usuario + ";password" + this.password + ";";
            connection = DriverManager.getConnection(connectionURL);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("Error de conexion SQL: ", e.getMessage());
        }
        return connection;
    }
}
