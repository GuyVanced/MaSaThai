package com.example.masathai.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImgChoiceController {

    @FXML
    private Label questionLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private ImageView flagImageView;
    @FXML
    private RadioButton choiceA, choiceB, choiceC, choiceD;
    @FXML
    private Button previousButton, nextButton, submitButton;
    @FXML
    private Label timerLabel;

    private boolean correctAnswerPicked = false;
    boolean deductedPoint = false;
    private String correctChoice = "D.";
    private  int currentQuestionNumber = 21;
    private int points;
    private Map<Integer, String> selectedChoicesMap = new HashMap<>();
    private int c_id;
    private String email;
    private String fullName, gender, flagPath;
    private ToggleGroup toggleGroup;
    private String nationality;




    public void initialize(int c_id, String email, String fullName, String gender,String nationality, String flagPath, int points, Map<Integer, String> selectedChoiceMap){
        this.c_id  = c_id;
        this.email = email;
        this.points = points;
        this.selectedChoicesMap = selectedChoiceMap;
        this.fullName = fullName;
        this.gender  = gender;
        this.flagPath = flagPath;
        this.nationality = nationality;
        setLabels();
        initializeButtons();
        previousButton.setDisable(true);


    }
    @FXML
    private void initializeButtons(){

            toggleGroup = new ToggleGroup();
            choiceA.setToggleGroup(toggleGroup);
            choiceB.setToggleGroup(toggleGroup);
            choiceC.setToggleGroup(toggleGroup);
            choiceD.setToggleGroup(toggleGroup);

    }

    private void setLabels(){
        nameLabel.setText(fullName);
        genderLabel.setText(gender);
        Image flagImage = new Image(flagPath);
        flagImageView.setImage(flagImage);

    }


    @FXML
    private void handleChoice(ActionEvent event){
        RadioButton selectedChoiceButton = (RadioButton) event.getSource();
        String selectedChoice = selectedChoiceButton.getText();

        evaluateAnswer(selectedChoice);
    }



    private void evaluateAnswer(String selectedChoice){
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

    @FXML
    private void onSubmitButtonClicked(ActionEvent event){
        changeToResultScene();
    }

    @FXML
    private void onNextButtonClicked(ActionEvent event){
        changetoImageBasedScene();
    }

    @FXML
    private void onPreviousButtonClicked(ActionEvent event){


    }



    @FXML
    private void changetoImageBasedScene(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/imgBasedScene.fxml"));
            Parent resultParent = loader.load();


            ImgBasedController imgBasedController = loader.getController();
            imgBasedController.initialize(c_id,email,fullName,gender,nationality,flagPath,points,selectedChoicesMap);

            Stage currentStage = (Stage) nameLabel.getScene().getWindow();


            Scene imgBasedScene = new Scene(resultParent);
            currentStage.setScene(imgBasedScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
