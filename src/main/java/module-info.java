module com.fileparser {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                            
    opens com.fileparser to javafx.fxml;
    exports com.fileparser;
}