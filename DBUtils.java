package com.example.complete;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String Username) {
        Parent root = null;

        if (Username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(Username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try{
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            }catch (IOException e){
                e.printStackTrace();


        }
    }
        Stage stage = (Stage)  ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        }

    public static void signUpUser(ActionEvent event, String Username, String Password, String Email, String Gender){
        Connection connection = null;
        PreparedStatement  psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mysql//localhost:3306/javafx", "root", "dollar#10");
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, Username);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()){
                System.out.println("User not found in database.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect");
                alert.show();
            }else {
                while (resultSet.next()){
                    String retrievedPassword = resultSet.getString("Password");
                    if (retrievedPassword.equals(Password)){
                        changeScene(event, "loggedIn.fxml", "Welcome!", "retrievedChannel");

                    }else {
                        System.out.println("Passwords did not match");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The provided credentials are incorrect");
                        alert.show();
                    }
                }
            }

    } catch (SQLException e){
            e.printStackTrace();
        }finally {
            if(resultSet !=null){
                try{
                    resultSet.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null){
                try{
                    preparedStatement.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try{
                    connection.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        }

}
