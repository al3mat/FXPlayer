package sample;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FileSelection
{
    List<File> selected = new ArrayList<File>();
    FileChooser fc;

    /* Da controllare se servono
    Button addPl = new Button("add_to_playlist");
    Button reproduct = new Button("reproduct");
    Button cancel = new Button("cancel");
    */

    List<String> filename = new ArrayList<>();
    Alert error;
    boolean set = false, addPlaylist = false;
    ExtensionFilter ef = new ExtensionFilter("AudioFiles", "*.mp3", "*.wav");
    int pos;


    void start(Stage stage)
    {
        fc = new FileChooser();
        fc.setTitle("file chooser");
        fc.getExtensionFilters().add(ef);

        addPlaylist = false;

        //		while(selected.equals(null) || !this.fileController())
        selected = fc.showOpenMultipleDialog(stage);

        for(pos = 0; pos < selected.size(); pos++)
        {
            if(fileController())
                filename.set(pos, selected.get(pos).toString());
        }//modificato
    }


    boolean fileController()
    {
        if(!filename.contains(".mp3") && !filename.contains(".wav") && filename.equals(null))//controllare con getfilechooser
        {
            filename = null;
            error = new Alert(AlertType.ERROR);
            error.setHeaderText("invalid selection");
            error.setContentText("selezionata tipologia di file invalida, selezionare un file con estensione mp3 o wav");
            System.out.println("errore: inserito file non audio");
            return false;
        }
        return true;
    }
}