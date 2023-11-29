package com.example.masathai.Controllers;

import com.example.masathai.DBUtils.DbConnection;
import com.example.masathai.Candidate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class QuizController {

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
    @FXML
    private Scene currentScene;



    public int currentQuestionNumber = 1;
    private Map<Integer, String> selectedChoicesMap = new HashMap<>();
    private String flagPath = null;
    private String correctChoice;
    private Timeline timer;
    private int totalQuestions = 22;
    private int points = 0;
    private boolean correctAnswerPicked = false;
    boolean deductedPoint = false;

    private Candidate candidate;
    private int c_id;
    private String fullName;
    private String nationality;
    private String gender;
    private Timeline timeline;
    private String email;


    private int minutes = 5;
    private int seconds = 0;


    public void initialize(int c_id,String email, String fullName, String nationality, String gender) {
        this.c_id = c_id;
        this.email = email;
        this.fullName = fullName;
        this.nationality = nationality;
        this.gender = gender;
        currentScene = nameLabel.getScene();
        setLabels(fullName,nationality,gender);
        loadQuestion(currentQuestionNumber);
        startCountdownTimer();

    }

    private void startCountdownTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateTimerLabel();
                if (minutes == 0 && seconds == 0) {
                    timeline.stop();
                    changeToResultScene();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        String formattedTime = String.format("%d:%02d", minutes, seconds);
        timerLabel.setText(formattedTime);

        if (seconds == 0) {
            if (minutes > 0) {
                minutes--;
                seconds = 59;
            }
        } else {
            seconds--;
        }
    }



    private void setLabels( String fullName, String nationality, String gender){
        nameLabel.setText("Name: " +fullName);
        genderLabel.setText("Gender: " + gender);

        switch (nationality) {

            case "Malaysia":
                flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\malaysian_flag.png";
                break;
            case "Singapore":
                flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\singapore_flag.png";
                break;
            case "Thailand":
                flagPath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\img\\thailand_flag.png";
                break;
        }

        Image flagImage = new Image(flagPath);
        flagImageView.setImage(flagImage);
    }

    private void handleButtonFunction(int questionNumber){
        if (questionNumber == 22){
            nextButton.setDisable(true);
        } else if (questionNumber == 1) {
            previousButton.setDisable(true);
        }
        else{
            nextButton.setDisable(false);
            previousButton.setDisable(false);
        }
    }




    private void loadQuestion(int questionNumber) {
        if(questionNumber == 20){
            nextButton.setText("Proceed to Image Questions");
        }
        if(questionNumber == 21){
            changeToImageChoiceScene();

        }
        else {
            qNoLabel.setText("Question " + questionNumber + " of " + totalQuestions);
            handleButtonFunction(questionNumber);
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


                            String formattedQuestion = String.format("%d. %s", questionNumber, questionText);
                            questionLabel.setText(formattedQuestion);
                            choice1Button.setText("A. " + choice1);
                            choice2Button.setText("B. " + choice2);
                            choice3Button.setText("C. " + choice3);
                            choice4Button.setText("D. " + choice4);


                            correctChoice = resultSet.getString("CorrectChoice");


                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleChoiceSelection(ActionEvent event) {


        Button selectedChoiceButton = (Button) event.getSource();
        String selectedChoice = selectedChoiceButton.getText();


        resetChoiceButtonBackgrounds();
        highlightChoiceButton(selectedChoiceButton);

        evaluateAnswer(selectedChoice);
    }


    private void highlightChoiceButton(Button selectedChoiceButton) {

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


        boolean alreadyAnswered = selectedChoicesMap.containsKey(currentQuestionNumber);


        boolean alreadyReceivedPoint = alreadyAnswered && selectedChoicesMap.get(currentQuestionNumber).endsWith(correctChoice);


        if (alreadyReceivedPoint) {

            if (!isCorrect) {
                points--;
                deductedPoint = true;

            }
        }


        if (isCorrect && !alreadyReceivedPoint) {

            points++;
        } else if (isCorrect && deductedPoint) {
            points++;
            deductedPoint = false;

        }

        selectedChoicesMap.put(currentQuestionNumber, selectedChoice);


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

    public void changeToImageChoiceScene(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/imgChoiceScene.fxml"));
            Parent resultParent = loader.load();


            ImgChoiceController imgChoiceController = loader.getController();
            imgChoiceController.initialize(c_id,email,fullName,gender,nationality,flagPath,points,selectedChoicesMap);

            Stage currentStage = (Stage) nameLabel.getScene().getWindow();


            Scene imgChoiceScene = new Scene(resultParent);
            currentStage.setScene(imgChoiceScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    @FXML
    private void onSubmitButtonClicked(ActionEvent event){
        changeToResultScene();

    }


    @FXML
    private void changeToResultScene() {
        try {
            String fileName = "C_" + String.valueOf(c_id) + "result.txt";

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/candidateResult.fxml"));
            Parent resultParent = loader.load();

            // Access the controller and pass data to the initialize method
            ResultController resultController = loader.getController();
            resultController.initialize(c_id,email, fileName, selectedChoicesMap, points);

            // Get the current stage from any node in the current scene
            Stage currentStage = (Stage) nameLabel.getScene().getWindow();

            // Set the new scene on the stage
            Scene resultScene = new Scene(resultParent);
            currentStage.setScene(resultScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

