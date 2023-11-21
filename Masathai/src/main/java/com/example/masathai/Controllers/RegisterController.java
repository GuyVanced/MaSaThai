package main.java.com.example.masathai.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passportField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> nationalityComboBox;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private CheckBox agreeCheckBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerButton.setOnAction(this::registerButtonClicked);
        loginButton.setOnAction(this::switchToLoginPage);
    }

    private void registerButtonClicked(ActionEvent event) {
        String firstName = firstNameField.getText();
        String middleName = middleNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String passport = passportField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";
        String nationality = nationalityComboBox.getValue();
        String gender = genderComboBox.getValue();
        boolean agreeToTerms = agreeCheckBox.isSelected();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || passport.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty() ||
                nationality == null || gender == null || !agreeToTerms) {
            showAlert("Error", "Please fill in all the fields and agree to the terms and conditions");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match");
            return;
        }

        boolean registrationSuccessful = saveToDatabase(firstName, middleName, lastName, email,
                passport, password, dob, nationality, gender, agreeToTerms);

        if (registrationSuccessful) {
            switchToLoginPage(event);
        } else {
            showAlert("Error", "Registration failed. Please try again.");
        }
    }

    private void switchToLoginPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userLogin.fxml"));
            Parent loginParent = loader.load();
            Scene loginScene = new Scene(loginParent);

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(loginScene);
            window.setTitle("Login");

            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to switch to login page");
        }
    }

    private boolean saveToDatabase(String firstName, String middleName, String lastName,
                                   String email, String passport, String password, String dob,
                                   String nationality, String gender, boolean agreeToTerms) {
        String url = "jdbc:mysql://localhost:3306/javafx";
        String username = "root";
        String dbPassword = "sailendra011";

        try (Connection connection = DriverManager.getConnection(url, username, dbPassword)) {
            String sql = "INSERT INTO users (first_name, middle_name, last_name, email, passport, password, dob, nationality, gender, agree_to_terms) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, firstName);
                statement.setString(2, middleName);
                statement.setString(3, lastName);
                statement.setString(4, email);
                statement.setString(5, passport);
                statement.setString(6, password);
                statement.setString(7, dob);
                statement.setString(8, nationality);
                statement.setString(9, gender);
                statement.setBoolean(10, agreeToTerms);

                int rowsAffected = statement.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save registration details to the database: " + e.getMessage());
            return false;
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
