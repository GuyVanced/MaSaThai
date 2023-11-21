module com.example.masathai {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
            
    opens com.example.masathai to javafx.fxml;
    exports com.example.masathai;
    exports com.example.masathai.Controllers;
    opens com.example.masathai.Controllers to javafx.fxml;
}