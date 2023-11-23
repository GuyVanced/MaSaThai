package com.example.masathai.Controllers;

import com.example.masathai.DBUtils.DbConnection;
import com.example.masathai.Candidate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class QuizController   {

    @FXML
    private Label nameLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private Button previousButton, nextButton;
    @FXML
    private Label genderLabel;
    @FXML
    private Button choice1Button, choice2Button, choice3Button, choice4Button;
    @FXML
    private Label timerLabel;
    @FXML
    private ImageView flagImageView;
    @FXML
    private Label qNoLabel;

    private int currentQuestionNumber = 1;
    private Map<Integer, String> selectedChoicesMap = new HashMap<>();
    private String flagPath = null;
    private String correctChoice;
    private Timeline timer;
    private int totalQuestions = 15;
    private int points =0;
    private boolean correctAnswerPicked = false;
    boolean deductedPoint = false;






    public void initialize(String email) {
        try (Connection connection = DbConnection.getConnection()) {
            // Fetch candidate details from the database
            Candidate candidate = fetchCandidateDetails(email);

            // Display candidate details
            nameLabel.setText("Name: " + candidate.getFullName());
            genderLabel.setText("Gender: " + candidate.getGender());

            switch(candidate.getNationality()){

                case "Malaysia":
                    flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\malayisan_flag.png";
                    break;
                case "Singapore" :
                    flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\singapore_flag.png";
                    break;
                case "Thailand":
                    flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\thailand_flag.png";
                    break;
            }

            Image flagImage = new Image(flagPath);
            flagImageView.setImage(flagImage);
            // Load and display the first question
            loadQuestion(currentQuestionNumber);
        } catch (SQLException e) {
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
                    String firstName = resultSet.getString("First_Name");
                    String middleName = resultSet.getString("Middle_Name");
                    String lastName = resultSet.getString("Last_Name");
                    String gender = resultSet.getString("gender");
                    String fullName = concatenateNames(firstName, middleName, lastName);
                    String nationality = resultSet.getString("Nationality");

                    // Create and return a Candidate object with fetched details
                    return new Candidate(fullName, gender, nationality);

                }
            }
        }
        throw new SQLException("Candidate not found with email: " + email);
    }

    private void loadQuestion(int questionNumber) {
        qNoLabel.setText("Question " + questionNumber + " of " + totalQuestions);
        try (Connection connection = DbConnection.getConnection()) {
            // Fetch question details from the database
            String query = "SELECT * FROM questions WHERE QuestionNo = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, questionNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Update JavaFX components with the question details
                        String questionText = resultSet.getString("QuestionText");
                        String choice1 = resultSet.getString("Choice1");
                        String choice2 = resultSet.getString("Choice2");
                        String choice3 = resultSet.getString("Choice3");
                        String choice4 = resultSet.getString("Choice4");

                        // For simplicity, let's assume choices are labeled A, B, C, D
                        String formattedQuestion = String.format("%d. %s", questionNumber, questionText);
                        questionLabel.setText(formattedQuestion);
                        choice1Button.setText("A. " + choice1);
                        choice2Button.setText("B. " + choice2);
                        choice3Button.setText("C. " + choice3);
                        choice4Button.setText("D. " + choice4);

                        // Store the correct choice for later evaluation
                        correctChoice = resultSet.getString("CorrectChoice");



                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChoiceSelection(ActionEvent event) {
        // Handle user's choice selection, evaluate correctness, and update points
        // This method is assumed to be linked to the action of a button representing a choice

        Button selectedChoiceButton = (Button) event.getSource();
        String selectedChoice = selectedChoiceButton.getText();  // Extracting the choice label (A, B, C, D)


        resetChoiceButtonBackgrounds();
        highlightChoiceButton(selectedChoiceButton);
        // Evaluate correctness and update points
        evaluateAnswer(selectedChoice);
    }


    private void highlightChoiceButton(Button selectedChoiceButton) {
        // Highlight the selected choice with a green background
        selectedChoiceButton.setStyle("-fx-background-color: lightgreen;");
    }

    private void resetChoiceButtonBackgrounds() {

        choice1Button.setStyle("");
        choice2Button.setStyle("");
        choice3Button.setStyle("");
        choice4Button.setStyle("");
    }
    private void evaluateAnswer(String selectedChoice) {
        boolean isCorrect = selectedChoice.trim().endsWith(correctChoice);

        // Check if the candidate has already answered this question
        boolean alreadyAnswered = selectedChoicesMap.containsKey(currentQuestionNumber);

        // Check if the candidate has already received a point for this question
        boolean alreadyReceivedPoint = alreadyAnswered && selectedChoicesMap.get(currentQuestionNumber).endsWith(correctChoice);

        // If the candidate is changing the choice for a previously answered question
        if (alreadyReceivedPoint) {
            // Deduct 1 point only if changing from a correct answer to an incorrect one
            if (!isCorrect) {
                points--;
                deductedPoint = true;

            }
        }

        // If the answer is correct and the question has not been answered before
        if (isCorrect &&  !alreadyReceivedPoint) {
            // Award 1 point and update the total points count
            points++;
        } else if (isCorrect && deductedPoint) {
            points++;
            deductedPoint = false;

        }

        // Update the selected choices map with the current choice
        selectedChoicesMap.put(currentQuestionNumber, selectedChoice);

        // Set the correctAnswerPicked flag to true if the answer is correct
        correctAnswerPicked = isCorrect;

        System.out.println("Current Points: " + points);
        System.out.println("Selected Options " + selectedChoicesMap);

    }




    public void nextQuestion(ActionEvent event) {

        if (currentQuestionNumber < totalQuestions) {
            currentQuestionNumber++;
            loadQuestion(currentQuestionNumber);
        }
    }

    public void previousQuestion(ActionEvent event) {
        if (currentQuestionNumber > 1) {
            currentQuestionNumber--;
            loadQuestion(currentQuestionNumber);
        }
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




}
