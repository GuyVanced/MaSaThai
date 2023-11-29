package com.example.masathai.Controllers;

import com.example.masathai.Candidate;
import com.example.masathai.DBUtils.DbConnection;
import com.example.masathai.QuizApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DashboardController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel, emailLabel, genderLabel, nationalityLabel, dobLabel;
    @FXML
    private Button seeAllButton;


    private Candidate candidate;
    private String filePath;
    private boolean alreadySubmitted = false;
    String email, fullName, nationality, gender;
    int c_id;
    String resultFilePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\" + "allResults.txt";

    public void initialize(String email) throws SQLException {
        this.email = email;
        candidate = fetchCandidateDetails(this.email);
        this.c_id = candidate.getC_id();

        this.fullName = candidate.getFullName();
        this.nationality = candidate.getNationality();
        this.gender = candidate.getGender();

        checkAlreadySubmitted();
    }

    public void checkAlreadySubmitted() {
        filePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\individual\\" + "C_" + candidate.getC_id() + "result.txt";
        if (fileExists(filePath)) {
            alreadySubmitted = true;
        } else {
            alreadySubmitted = false;
        }

    }

    private boolean fileExists(String filePath) {

        return Files.exists(Paths.get(filePath));
    }

    @FXML
    private void onStartButtonClick(ActionEvent event) {
        if (alreadySubmitted) {
            showErrorAlert("Quiz Already Submitted", "You have already submitted your quiz. Please check your results from the dashboard");
        } else {
            switchToQuizPage();
        }
    }

    @FXML
    private void onSeeResultButtonClick(ActionEvent event) {
        if (alreadySubmitted) {
            retrieveResult();
        } else {
            showErrorAlert("No result found", "Seems like you haven't taken the quiz yet");
        }
    }

    @FXML
    private void onSeeAllClicked() {
        changeToAllResultsScene();
    }


    private void retrieveResult() {
        int obtainedMarks = getObtainedMarks(c_id);
        switchToResultPage(obtainedMarks);
    }

    public int getObtainedMarks(int c_id) {
        Map<Integer, Integer> resultMap = readResultsFromFile();


        if (resultMap.containsKey(c_id)) {
            System.out.println(c_id);
            return resultMap.get(c_id);

        } else {
            System.out.println(c_id);
            showErrorAlert("Error", "Failed to retrieve results");
            System.out.println(c_id);
            return 0;
        }


    }

    // Method to read results from the file and store them in a map
    private Map<Integer, Integer> readResultsFromFile() {
        Map<Integer, Integer> resultMap = new HashMap<>();
        BufferedReader reader = null;

        try {
            // Create a BufferedReader for reading
            reader = new BufferedReader(new FileReader(resultFilePath));

            // First time reading
            readLines(reader, resultMap);

            // The reader is still open here, so you can read again if needed
            // For example, if you want to read again in another method

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the reader in the finally block to ensure it's closed even if an exception occurs
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultMap;
    }


    private void readLines(BufferedReader reader, Map<Integer, Integer> resultMap) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                int c_id = Integer.parseInt(parts[0].trim());
                int obtainedMarks = Integer.parseInt(parts[1].trim());
                resultMap.put(c_id, obtainedMarks);
            }
        }
    }

    private void changeToAllResultsScene() {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/allResults.fxml"));
        Parent resultParent = loader.load();

        // Access the controller and pass data to the initialize method
        AllResultController allResultController = loader.getController();
        allResultController.initialize();

        // Get the current stage from any node in the current scene
        Stage currentStage = (Stage) titleLabel.getScene().getWindow();

        // Set the new scene on the stage
        Scene allResultScene = new Scene(resultParent);
        currentStage.setScene(allResultScene);
    } catch( IOException e)
    {
        e.printStackTrace();
    }

}

    private void switchToQuizPage() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/quizApp.fxml"));
            Parent resultParent = loader.load();

            // Access the controller and pass data to the initialize method
            QuizController quizController = loader.getController();
            quizController.initialize(c_id,email,fullName,nationality,gender);

            // Get the current stage from any node in the current scene
            Stage currentStage = (Stage) titleLabel.getScene().getWindow();

            // Set the new scene on the stage
            Scene quizScene = new Scene(resultParent);
            currentStage.setScene(quizScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToResultPage(int obtainedMarks){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/candidateResult.fxml"));
            Parent resultParent = loader.load();

            // Access the controller and pass data to the initialize method
            ResultController resultController = loader.getController();
            resultController.displayAnalysis(obtainedMarks,email);

            // Get the current stage from any node in the current scene
            Stage currentStage = (Stage) titleLabel.getScene().getWindow();

            // Set the new scene on the stage
            Scene resultScene = new Scene(resultParent);
            currentStage.setScene(resultScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Candidate fetchCandidateDetails(String email) throws SQLException {
        // Perform database query to fetch candidate details based on the email
        // Example query: SELECT * FROM Candidates WHERE Email = ?
        // Execute the query using PreparedStatement
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Candidates WHERE Email = ?")) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int c_id = resultSet.getInt("C_ID");
                    String firstName = resultSet.getString("First_Name");
                    String middleName = resultSet.getString("Middle_Name");
                    String lastName = resultSet.getString("Last_Name");
                    String gender = resultSet.getString("gender");
                    String fullName = concatenateNames(firstName, middleName, lastName);
                    String nationality = resultSet.getString("Nationality");
                    String dob = resultSet.getString("dob");


                    setLabels(fullName, email, nationality, gender, dob);
                    return new Candidate(c_id,fullName, gender, nationality);

                }
            }
        }
        throw new SQLException("Candidate not found with email: " + email);
    }

    private void setLabels(String fullName, String email, String nationality, String gender, String dob){
        nameLabel.setText("Full Name : " + fullName);
        emailLabel.setText("Email : " + email);
        nationalityLabel.setText("Nationality : " + nationality);
        genderLabel.setText("Gender : " + gender);
        dobLabel.setText("Date of Birth : " + dob);
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

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    @FXML
    private void onExitButtonClick(ActionEvent event){
        Platform.exit();
    }
}
