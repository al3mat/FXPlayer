    package sample;
    import java.io.File;
    import java.util.List;
    import java.util.Vector;

    import javafx.scene.control.Alert;
    import javafx.scene.control.Alert.AlertType;
    import javafx.stage.FileChooser;
    import javafx.stage.FileChooser.ExtensionFilter;
    import javafx.stage.Stage;

    class FileSelection
    {
        List<String> filename = new Vector<>();
        private ExtensionFilter ef = new ExtensionFilter("AudioFiles", "*.mp3", "*.wav");


        void start(Stage stage)
        {
            FileChooser fc = new FileChooser();
            fc.setTitle("file chooser");
            fc.getExtensionFilters().add(ef);

            List<File> selected = fc.showOpenMultipleDialog(stage);

            if(selected != null)
            {
                int pos;
                for (pos = 0; pos < selected.size(); pos++)
                {
                    filename.add(pos, selected.get(pos).toString());
                    fileController(pos);
                }
            }
        }


        private void fileController(int i)
        {
            if(!filename.get(i).contains(".mp3") && !filename.get(i).contains(".wav"))
            {
                filename = null;
                Alert error = new Alert(AlertType.ERROR);
                error.setHeaderText("invalid selection");
                error.setContentText("selezionata tipologia di file invalida, selezionare un file con estensione:\n.mp3\n.wav\n.aiff");
                error.showAndWait();
                System.out.println("errore: inserito file non audio");
            }
        }
    }