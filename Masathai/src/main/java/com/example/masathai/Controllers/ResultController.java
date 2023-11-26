package com.example.masathai.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


public class ResultController {
        @FXML
        private Label marksLabel;
        @FXML
        private Label correctAnswersLabel;
        @FXML
        private Label incorrectAnswersLabel;
        @FXML
        Label remarksLabel;
        @FXML
        PieChart pieChart;
        @FXML
        Label correctPercentageLabel;
        @FXML
        Label incorrectPercentageLabel;
        @FXML
        Label resultLabel;
        @FXML
        Button homeButton;

        String email;
        int obtainedMarks;
        int totalQuestions = 20;
        String allresultsFilePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\" + "allResults.txt";

        public void initialize(int c_id,String email,String fileName, Map<Integer, String> selectedChoicesMap, int obtainedMarks){
                this.obtainedMarks = obtainedMarks;
                this.email = email;
                writeResponsesToFile(fileName,selectedChoicesMap,obtainedMarks);
                writeToResultsFile( c_id, obtainedMarks);
                displayAnalysis(obtainedMarks);

        }

        public void writeToResultsFile(int c_id, int obtainedMarks){
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(allresultsFilePath, true))) {
                        // Append the new result to the file
                        writer.write(c_id + "," + obtainedMarks);
                        writer.newLine();
                } catch (IOException e) {

                        e.printStackTrace();
                }
        }



        private void writeResponsesToFile(String filename, Map<Integer, String> selectedChoicesMap, int totalMarks) {

                String filePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\results\\individual\\" + filename;
                try {
                        File file = new File(filePath);
                        if (file.exists()) {
                                file.delete();
                        }
                        file.createNewFile();

                        try (FileWriter writer = new FileWriter(file, false)) {
                                // Write the header
                                writer.write("QuestionNo,SelectedAnswer\n");

                                // Write the data for each question
                                for (Map.Entry<Integer, String> entry : selectedChoicesMap.entrySet()) {
                                        int questionNumber = entry.getKey();
                                        String selectedAnswer = entry.getValue();


                                        writer.write(questionNumber + "," + selectedAnswer + "\n");
                                }
                                System.out.println("Responses written to file: " + filename);

                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }

        }

        public void displayAnalysis(int obtainedMarks, String email){
                this.email = email;
                marksLabel.setText(String.valueOf(obtainedMarks) + "/15");
                int incorrectAnswers = totalQuestions - obtainedMarks;

                correctAnswersLabel.setText("Correct Answers : " + String.valueOf(obtainedMarks));
                incorrectAnswersLabel.setText("Incorrect Answers : " + String.valueOf(incorrectAnswers));

                if(obtainedMarks<10){
                        resultLabel.setText("Fail");
                        remarksLabel.setText("Sorry, you were not qualified for the eligibility of the citizenship. Please try again after 15 days.");
                        resultLabel.setStyle("-fx-text-fill: red;");
                }
                else{
                        resultLabel.setText("Pass");
                        remarksLabel.setText("Congratulations! You are officially eligible for the Masathai citizenship. Please visit the office after 3 days.");
                        resultLabel.setStyle("-fx-text-fill: green;");
                }
                updatePieChart(obtainedMarks, incorrectAnswers);

        }public void displayAnalysis(int obtainedMarks){

                marksLabel.setText(String.valueOf(obtainedMarks) + "/15");
                int incorrectAnswers = totalQuestions - obtainedMarks;

                correctAnswersLabel.setText("Correct Answers : " + String.valueOf(obtainedMarks));
                incorrectAnswersLabel.setText("Incorrect Answers : " + String.valueOf(incorrectAnswers));

                if(obtainedMarks<10){
                        resultLabel.setText("Fail");
                        remarksLabel.setText("Sorry, you were not qualified for the eligibility of the citizenship. Please try again after 15 days.");
                        resultLabel.setStyle("-fx-text-fill: red;");
                }
                else{
                        resultLabel.setText("Pass");
                        remarksLabel.setText("Congratulations! You are officially eligible for the Masathai citizenship. Please visit the office after 3 days.");
                        resultLabel.setStyle("-fx-text-fill: green;");
                }
                updatePieChart(obtainedMarks, incorrectAnswers);

        }

        public void updatePieChart(int correctCount, int incorrectCount) {
                pieChart.getData().clear();
                PieChart.Data correctData = new PieChart.Data("Correct", correctCount);
                PieChart.Data incorrectData = new PieChart.Data("Incorrect", incorrectCount);

                double totalCount = correctCount + incorrectCount;
                pieChart.getData().addAll(correctData, incorrectData);
                double correctPercentage = (correctCount/totalCount) * 100;
                double incorrectPercentage = (incorrectCount/totalCount) * 100;


                // Set the legend visibility to false
                pieChart.setLegendVisible(false);
                correctPercentageLabel.setText(String.format("Correct : %.1f%%", correctPercentage));
                incorrectPercentageLabel.setText(String.format("Incorrect : %.1f%%", incorrectPercentage));

        }

        public void onHomeButtonClick(ActionEvent event){
                try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/masathai/app/dashboard.fxml"));
                        Parent dashboardParent = loader.load();
                        Scene dashboardScene = new Scene(dashboardParent,762,550);
                        DashboardController dashboardController = (DashboardController) loader.getController();
                        dashboardController.initialize(email);

                        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();


                        window.setScene(dashboardScene);
                        window.setTitle("Dashboard");

                        window.show();
                } catch (IOException e) {
                        e.printStackTrace();

                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }


}
