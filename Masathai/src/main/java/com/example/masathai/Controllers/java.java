//package application;
//
//import java.io.IOException;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.control.Label;
//import javafx.util.Duration;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class QuizController {
//
//    private Stage stage;
//    private Scene scene;
//
//    @FXML
//    private Button questionNumber;
//
//    @FXML
//    private Button quizQuestion;
//
//    @FXML
//    private Button op1;
//
//    @FXML
//    private Button op2;
//
//    @FXML
//    private Button op3;
//
//    @FXML
//    private Button op4;
//
//    @FXML
//    private Button prevQuestion;
//
//    @FXML
//    private Button nextQuestion;
//
//    @FXML
//    private Button timerLabel;
//
//    private int minutes = 5;
//    private int seconds = 0;
//    private Timeline timeline;
//
//    private int currentQuestionIndex = 0;
//    private List<Question> questions;
//    public static int totalRightAnswers = 0;
//
//    @FXML
//    public void initialize() {
//        // Load questions from CSV file
//        loadQuestionsFromCSV("questions.csv");
//
//        // Display the first question
//        showQuestion();
//
//        startCountdownTimer();
//
//    }
//
//
//    private void startCountdownTimer() {
//        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                updateTimerLabel();
//                if (minutes == 0 && seconds == 0) {
//                    timeline.stop();
//                    try {
//                        switchToTestEndScene(event);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }));
//
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//    }
//
//    private void updateTimerLabel() {
//        String formattedTime = String.format("%d:%02d", minutes, seconds);
//        timerLabel.setText(formattedTime);
//
//        if (seconds == 0) {
//            if (minutes > 0) {
//                minutes--;
//                seconds = 59;
//            }
//        } else {
//            seconds--;
//        }
//    }
//
//
//    private void showQuestion() {
//        Question currentQuestion = questions.get(currentQuestionIndex);
//
//        questionNumber.setText("" + currentQuestion.getQuestionNumber());
//        quizQuestion.setText(currentQuestion.getQuestion());
//        op1.setText(currentQuestion.getOption1());
//        op2.setText(currentQuestion.getOption2());
//        op3.setText(currentQuestion.getOption3());
//        op4.setText(currentQuestion.getOption4());
//    }
//
//    @FXML
//    private void nextQuestion(ActionEvent event) {
//        if (currentQuestionIndex < questions.size() - 1) {
//            currentQuestionIndex++;
//            showQuestion();
//
//            // Disable the "Next" button if the current question is the 20th question
//            if (currentQuestionIndex == 19) {
//                nextQuestion.setText("Image Section");
//                nextQuestion.setOnAction(ev -> {
//                    try {
//                        switchToImageQuizScene(event);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                });
//            } else {
//                nextQuestion.setText("Next");
//                nextQuestion.setOnAction(ev -> nextQuestion(event));
//            }
//        }
//    }
//
//    @FXML
//    private void prevQuestion(ActionEvent event) {
//        if (currentQuestionIndex > 0) {
//            currentQuestionIndex--;
//            showQuestion();
//
//            // Enable the "Next" button if moving back from the 20th question
//            if (currentQuestionIndex == 18) {  // Assuming the last question is at index 19
//                nextQuestion.setText("Next");
//                nextQuestion.setOnAction(ev -> nextQuestion(event)); // Passing a dummy event
//            }
//        }
//    }
//
//
//    @FXML
//    private void optionClick(ActionEvent event) throws IOException {
//        Button clickedOption = (Button) event.getSource();
//        Question currentQuestion = questions.get(currentQuestionIndex);
//        String selectedOption = clickedOption.getText();
//
//        if (selectedOption.equals(currentQuestion.getCorrectAnswer())) {
//            totalRightAnswers++;
//        }
//
//        // Log the answers to a CSV file
//        logAnswersToCSV(currentQuestion.getQuestionNumber(), selectedOption ,selectedOption.equals(currentQuestion.getCorrectAnswer()));
//
//        // Move to the next question
//        nextQuestion(event);
//    }
//
//    private void logAnswersToCSV(int questionNumber, String selectedOption, boolean isCorrect) {
//        try (FileWriter writer = new FileWriter("test_answers.csv", true)) {
//            // If it's the first question in a line, add the user's email
//            if (questionNumber == 1) {
//                writer.append(LoginController.dashboardEmail);
//            }
//
//            // Add the information for the current question
//            writer.append("," + questionNumber + "," + selectedOption + "," + isCorrect);
//
//            // If it's the last question in a line, start a new line
//            if (questionNumber == questions.size()) {
//                writer.append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadQuestionsFromCSV(String filePath) {
//        questions = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split(",");
//                int questionNumber = Integer.parseInt(data[0]);
//                String questionText = data[1];
//                String option1 = data[2];
//                String option2 = data[3];
//                String option3 = data[4];
//                String option4 = data[5];
//                String correctAnswer = data[6];
//
//                Question question = new Question(questionNumber, questionText, option1, option2, option3, option4, correctAnswer);
//                questions.add(question);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void switchToDashboardScene(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("/view/DashboardScene.fxml"));
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//
//        String cupertino_dark_css = this.getClass().getResource("cupertino-dark.css").toExternalForm();
//        scene.getStylesheets().add(cupertino_dark_css);
//
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    public void switchToImageQuizScene(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("/view/ImageQuizScene.fxml"));
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//
//        String cupertino_dark_css = this.getClass().getResource("cupertino-dark.css").toExternalForm();
//        scene.getStylesheets().add(cupertino_dark_css);
//
//        stage.setScene(scene);
//        stage.show();
//    }
//
////    public void switchToTestEndScene() throws IOException {
////        Parent root = FXMLLoader.load(getClass().getResource("/view/ImageQuizScene.fxml"));
////        scene = new Scene(root);
////
////        String cupertino_dark_css = this.getClass().getResource("cupertino-dark.css").toExternalForm();
////        scene.getStylesheets().add(cupertino_dark_css);
////
////        stage.setScene(scene);
////        stage.show();
////    }
//
//
//    public void switchToTestEndScene(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("/view/TestEndScene.fxml"));
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//
//        String cupertino_dark_css = this.getClass().getResource("cupertino-dark.css").toExternalForm();
//        scene.getStylesheets().add(cupertino_dark_css);
//
//        stage.setScene(scene);
//        stage.show();
//
//        nextQuestion.setDisable(true);
//        nextQuestion.setOnAction(null);
//    }
//
//}