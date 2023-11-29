package com.example.masathai.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllResultController {
    @FXML
    private ComboBox<String> dropDownMenu;

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
    private void onSeeResultButtonClicked(){

    }
}
