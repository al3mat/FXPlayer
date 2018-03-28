package sample;

import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Playlist 
{
	List<MediaPlayer> pl = new ArrayList<>();

	void addSong(String name)
	{
		pl.add(new MediaPlayer(new Media(name)));
	}
	
	void removeSong(int index)
	{
		pl.remove(index);
	}

	MediaPlayer currentSong(int i)
	{
		if(i < 0)
			System.out.println("errore: inserito un numero negativo");

		return pl.get(i);
	}//controllare, ma non credo sia così facile
	
	int nSongs()
	{
		return pl.size();
	}
	
}//colegare con i pulsanti: se è premuto add o remuve->chiama il metodo
