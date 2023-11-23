package com.example.masathai.DBUtils;
import java.sql.Connection;
import java.sql.DriverManager;



public class DbConnection {
    public static Connection dbLink;

    public static Connection getConnection(){
        String dbName = "civikquiz";
        String dbUser = "root";
        String dbPassword = "root";
        String url = "jdbc:mysql://localhost/" + dbName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbLink = DriverManager.getConnection(url,dbUser,dbPassword);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return dbLink;

    }


}
