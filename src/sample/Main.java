package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FXPlayer");
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.setMinHeight(350);
        primaryStage.setMinWidth(400);
        primaryStage.setMaxWidth(400);
        primaryStage.show();

        //Finestra Equilizzatore
        StackPane secondaryLayout = new StackPane();
        Scene secondScene = new Scene(secondaryLayout, 300, 200);
        Stage newWindow = new Stage();
        newWindow.setTitle("Equilizzatore");
        newWindow.setScene(secondScene);
        newWindow.setX(primaryStage.getX());
        newWindow.setX(primaryStage.getY());
        //newWindow.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
//push test git