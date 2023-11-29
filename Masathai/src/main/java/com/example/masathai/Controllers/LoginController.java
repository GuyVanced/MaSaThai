package com.example.masathai.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.util.EventObject;

import com.example.masathai.DBUtils.DbConnection;
import javafx.stage.Stage;

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

    public String getEmail(){
        return email;
    }
    @FXML
    public void onLoginButtonClicked(ActionEvent event){
        email = emailField.getText();
        password = passwordField.getText();
        if(checkLogin()){
            switchToDashboard(event);
        };

    }

    public boolean checkLogin(){
        DbConnection dbConnection = new DbConnection();
        Connection connection = dbConnection.getConnection();

        String checkLogin= "SELECT COUNT(0) FROM Candidates WHERE  BINARY Email = '" +  email  +"' AND  BINARY Password = '" + password + "'";

        try {
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(checkLogin);

            while(queryResult.next()){
                if (queryResult.getInt(1) == 1){
                    loginMessage.setText("Login Successful");
                    return true;

                }
                else{
                    loginMessage.setText("Invalid Email or Password");

                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    return false;
    }
    @FXML
    private void switchToSignupPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/register.fxml"));
            Parent registerParent = loader.load();
            Scene registerScene = new Scene(registerParent,920, 670);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();


            window.setScene(registerScene);
            window.setTitle("Register");

            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch to signup page");
        }
    }

    @FXML
    private void switchToDashboard(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/dashboard.fxml"));
        Parent dashboardParent = loader.load();
        DashboardController dashboardController = (DashboardController) loader.getController();
        dashboardController.initialize(email);
        Scene dashboardScene = new Scene(dashboardParent,770, 560);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();


        window.setScene(dashboardScene);
        window.setTitle("Dashboard");

        window.show();


        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch to dashboard page");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
