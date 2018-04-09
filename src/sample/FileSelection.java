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
    List<File> selected;
    FileChooser fc;
    List<String> filename = new ArrayList<String>();
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

        selected = fc.showOpenMultipleDialog(stage);

        if(selected != null)
        {
            for (pos = 0; pos < selected.size(); pos++)
            {
                filename.add(pos, selected.get(pos).toString());
                fileController(pos);
            }
        }
    }


    boolean fileController(int i)
    {
        if(!filename.get(i).contains(".mp3") && !filename.get(i).contains(".wav"))
        {
            filename = null;
            error = new Alert(AlertType.ERROR);
            error.setHeaderText("invalid selection");
            error.setContentText("selezionata tipologia di file invalida, selezionare un file con estensione:\n.mp3\n.wav\n.aiff");
            error.showAndWait();
            System.out.println("errore: inserito file non audio");
            return false;
        }
        return true;
    }
}