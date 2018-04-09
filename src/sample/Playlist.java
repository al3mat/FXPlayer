    package sample;

    import java.io.File;
    import java.util.*;

    import javafx.scene.control.Alert;
    import javafx.scene.media.Media;
    import javafx.scene.media.MediaPlayer;

    class Playlist
    {
        private List<MediaPlayer> pl = new Vector<>();
        List<String> names = new Vector<>();

        void addSong(String name)
        {
            names.add(name);
            pl.add(new MediaPlayer(new Media(new File(name).toURI().toString())));
        }


        void removeSong(int pos, int playing)
        {
            if (pos == playing)
            {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("invalid selection");
                error.setContentText("selezionato brano in riproduzione, impossibile rimuoverlo dalla playlist");
                error.showAndWait();
            }
            else
            {
                pl.remove(pos);
                names.remove(pos);
            }
        }




        MediaPlayer currentSong(int i)
        {
            return pl.get(i);
        }

        void removeAll()
        {
            names.clear();
            pl.clear();
        }

        int nSongs()
        {
            return pl.size();
        }
    }