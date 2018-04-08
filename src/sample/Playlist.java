package sample;

import java.io.File;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Playlist 
{
	List<MediaPlayer> pl = new ArrayList<>();
	List<String> names = new ArrayList<>();
	Alert error;

	void addSong(String name)
	{	
		names.add(name);
		pl.add(new MediaPlayer(new Media(new File(name).toURI().toString())));
	}

	
	void removeSong(int start, int end, int playing)
	{
	    if(start == playing || end == playing || (playing <= end && playing >= start))
        {
            error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("invalid selection");
            error.setContentText("selezionato brano in riproduzione, impossibile rimuoverlo dalla playlist");
            error.showAndWait();
        }
        else
            {
            if (start == end)
            {
                pl.remove(start);
                names.remove(start);
            } else {
                if (start >= 0 && end > start)
                {
                    if (start == 0 && end == this.nSongs())
                    {
                        pl.removeAll(pl);
                        names.removeAll(names);
                    }
                    else
                        {
                        for (; start < end; start++)
                        {
                            names.remove(start);
                            pl.remove(start);
                        }
                    }
                }
            }
        }
	}//fissare il massimo di end a nsongs

	
	MediaPlayer currentSong(int i)
	{
		if(i < 0)
			System.out.println("errore: inserito un numero negativo");

		return pl.get(i);
	}//controllare, ma non credo sia cosм facile->sistemare il problema dell'errore

	
	int nSongs()
	{
		return pl.size();
	}
}