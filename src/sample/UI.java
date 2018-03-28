package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

public class UI {


    Image pauseimg = new Image(getClass().getResourceAsStream("/sample/img/pause.png"));

    public void setPauseIcon(Button pauseButton){

        pauseButton.setGraphic(new ImageView(pauseimg));
    }

}