package sample;

import java.io.File;
import java.util.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Playlist 
{
	List<MediaPlayer> pl = new ArrayList<MediaPlayer>();
	List<String> names = new ArrayList<String>();

	void addSong(String name)
	{	
		names.add(name);
		pl.add(new MediaPlayer(new Media(new File(name).toURI().toString())));
	}

	
	void removeSong(int start, int end)
	{
		if(start == end)
		{
			pl.remove(start);
			names.remove(start);
		}
		else
		{
			if(start >= 0 && end > start)
			{
				if(start == 0 && end == this.nSongs())
				{
					pl.removeAll(pl);
					names.removeAll(names);
				}
				else
				{
					for(; start<end; start++)
					{
						names.remove(start);
						pl.remove(start);
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
	}//controllare, ma non credo sia così facile->sistemare il problema dell'errore

	
	MediaPlayer previousSong(int i)
	{
		if(i > 0)
			return pl.get(i-1);
		else
			return pl.get(0);
	}

	MediaPlayer nextSong(int i)
	{
		if(i < this.nSongs())
			return pl.get(i+1);
		else
			return pl.get(i);
	}

	
	int nSongs()
	{
		return pl.size();
	}
}