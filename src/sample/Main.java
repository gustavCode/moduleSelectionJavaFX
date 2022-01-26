package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("moduleSelectionView.fxml"));
        primaryStage.setTitle("Final Year Module Selection Tool");
        primaryStage.setScene(new Scene(root, 650, 580));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
