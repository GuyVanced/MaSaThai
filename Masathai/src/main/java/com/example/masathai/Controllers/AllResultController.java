package com.example.masathai.Controllers;

import com.example.masathai.Candidate;
import com.example.masathai.DBUtils.DbConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllResultController {
    @FXML
    private ComboBox<String> dropDownMenu;
    @FXML
    private Label fullNameLabel, genderLabel, nationalityLabel, dobLabel, marksLabel, resultLabel;

    // File path for the text file
    private static final String FILE_PATH = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\allResults.txt";

    @FXML
    public void initialize() {
        // Initialize menu choices when the controller is initialized
        initializeMenuChoices();
    }

    private void initializeMenuChoices() {
        List<String> menuChoices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Split the line by a delimiter (assuming a comma in this case)
                String[] columns = line.split(",");

                // Check if the line has at least one column
                if (columns.length > 0) {
                    // Extract the c_id from the first column
                    String c_id = columns[0].trim();

                    // Add "C_" + c_id to the menu choices
                    menuChoices.add("C_" + c_id);
                }
            }

            // Set the menu choices
            dropDownMenu.getItems().addAll(menuChoices);

        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
        }
    }

    @FXML
    private void onSeeResultButtonClicked(ActionEvent event) throws SQLException {

        String selectedOption = dropDownMenu.getSelectionModel().getSelectedItem();



        if (selectedOption != null) {

            String c_id = selectedOption.substring(2);

            int marks = getMarksFromDataSource(c_id);
            // Find details based on c_id
            Candidate candidate = findDetailsById(c_id);

            if (candidate != null) {
                // Set details to corresponding labels
                String dobFormatted = candidate.getDob().toString(); // Adjust formatting as needed
                dobLabel.setText("DOB : " + dobFormatted);
                fullNameLabel.setText("Full Name : " + candidate.getFullName());
                genderLabel.setText("Gender : " + candidate.getGender());

                marksLabel.setText("Marks : " + String.valueOf(marks));
                if(marks>=15){
                    resultLabel.setText("Result : Pass");
                }
                else{
                    resultLabel.setText("Result : Fail");
                }

            } else {
                System.out.println("Details not found for c_id: " + c_id);

            }
        } else {

            System.out.println("Please select an option before clicking the button.");
        }
    }

    // Assume UserDetails is a class representing user details
    private Candidate findDetailsById(String c_id) throws SQLException {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Candidates WHERE C_ID = ?")) {
            preparedStatement.setString(1, c_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    String firstName = resultSet.getString("First_Name");
                    String middleName = resultSet.getString("Middle_Name");
                    String lastName = resultSet.getString("Last_Name");
                    String gender = resultSet.getString("gender");
                    String fullName = concatenateNames(firstName, middleName, lastName);
                    String nationality = resultSet.getString("Nationality");
                    Date dob = resultSet.getDate("dob");

                    return new Candidate(fullName, gender, nationality, dob);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new SQLException("Candidate not found with email: " + c_id);
    }

    private String concatenateNames(String firstName, String middleName, String lastName) {
        // Implement your logic for concatenation (you can adjust this based on your requirements)
        StringBuilder fullNameBuilder = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            fullNameBuilder.append(firstName).append(" ");
        }
        if (middleName != null && !middleName.isEmpty()) {
            fullNameBuilder.append(middleName).append(" ");
        }
        if (lastName != null && !lastName.isEmpty()) {
            fullNameBuilder.append(lastName);
        }
        return fullNameBuilder.toString().trim();  // Trim to remove trailing spaces
    }


    private int getMarksFromDataSource(String c_id) {
        // File path for the text file
        String filePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\allResults.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Split the line by a delimiter (assuming a comma in this case)
                String[] columns = line.split(",");

                // Check if the line has at least two columns
                if (columns.length >= 2) {
                    // Extract the c_id from the first column
                    String currentCId = columns[0].trim();

                    // Check if the current line's c_id matches the target c_id
                    if (currentCId.equals(c_id)) {
                        // Parse and return the marks from the second column
                        return Integer.parseInt(columns[1].trim());
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            // Handle IOException or NumberFormatException as needed
        }

        // Return a default value or handle the case when marks are not found
        return -1;
    }

    @FXML
    private void onExitButtonClicked(ActionEvent event){
        Platform.exit();
    }
}
