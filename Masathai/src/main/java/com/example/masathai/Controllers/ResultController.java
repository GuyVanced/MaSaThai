package com.example.masathai.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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




        public void initialize(String fileName, Map<Integer, String> selectedChoicesMap, int obtainedMarks,int totalQuestions){
                writeResponsesToFile(fileName,selectedChoicesMap,obtainedMarks);
                displayAnalysis(obtainedMarks, totalQuestions);
        }

        private void writeResponsesToFile(String filename, Map<Integer, String> selectedChoicesMap, int totalMarks) {

                String filePath = "E:\\Academics\\IIMS\\5th Sem\\Advanced Programming\\Assignment\\Masathai Project\\code\\Masathai\\src\\main\\resources\\com\\example\\masathai\\data\\" + filename;
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

                                        // Write QuestionNo and SelectedAnswer to the file
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

        private void displayAnalysis(int obtainedMarks, int totalQuestions){
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
}
