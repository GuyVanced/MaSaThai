package com.example.masathai.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import com.example.masathai.DBUtils.DbConnection;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signUpLink;
    @FXML
    private Label loginMessage;

    private String email, password;

    @FXML
    public void onLoginButtonClicked(){
        email = emailField.getText();
        password = passwordField.getText();
        checkLogin();


    }

    public void checkLogin(){
        DbConnection dbConnection = new DbConnection();
        Connection connection = dbConnection.getConnection();

        String checkLogin= "SELECT COUNT(0) FROM Candidates WHERE Email = '" +  email  +"' AND Password = '" + password + "'";

        try {
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(checkLogin);

            while(queryResult.next()){
                if (queryResult.getInt(1) == 1){
                    loginMessage.setText("Login Successful");

                }
                else{
                    loginMessage.setText("Invalid Email or Password");
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }




    }

}
