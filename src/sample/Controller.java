package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import java.io.*;
import com.mpatric.mp3agic.*;


public class Controller {

	// InputStream input = getClass().getResourceAsStream("C:\\Users\\LeX\\Desktop\\FXPlayer\\pause.png");

	// Image image = new Image(input);
	//ImageView imageView = new ImageView(image);

	// Parametri UI
	public Button playButton = new Button();
	public Button stopButton = new Button();
	public Button forwardButton = new Button();
	public Button backwardButton = new Button();
	public Button repeatButton = new Button();
	public Label artistLabel = new Label();
	public Label titleLabel = new Label();
	public Label albumLabel = new Label();
	public Label genreLabel = new Label();
	public Label yearLabel = new Label();
	public Label totalTimeLabel = new Label();
	public Label sampleRateLabel = new Label();
	public Label bitRateLabel = new Label();
	public Label elapsedTimeLabel = new Label();
	public ImageView trackImage = new ImageView();
	public Slider timeSlider = new Slider();
	public Slider volumeSlider = new Slider();
	public ListView playList = new ListView();
	//public Button shuffleButton = new Button();

	UI grafica = new UI();
	byte[] imageData;


	//aggiungo bottoni per gestire la playlist
	public Button addSong = new Button(); 
	public Button removeSong = new Button();
	int position = 0;
	boolean playlistOn = false;

	//Inizializziamo la sorgente audio
	private String path = new String("src/sample/Hero.mp3");
	private Media source = new Media(new File(path).toURI().toString());
	private MediaPlayer player = new MediaPlayer(source);
	boolean initialized = false;

	//Inizializziamo i parametri per i Tag
	private Mp3File mp3file;
	private ID3v2 id3v2Tag;

	Boolean gotSongTime = true;												//cambiare quando si avanza di una canzone nella playlist 
	int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;

	Loop loop = new Loop();
	boolean loop_state = false, end = false, pl_initialized = false;
	Playlist pl = new Playlist();
	int totalS_on_moving;



	public void addSongToPlaylist(ActionEvent e)
	{
		//aggiungere sistema grafico per aggiungere canzoni alla playlist
	}

	
	public void removeSongFromPlaylist(ActionEvent e)
	{
		if(pl.nSongs() != 0)
			pl.removeSong(0, 0);
		else
			System.out.println("impossibile rimuovere canzoni: non ne sono presenti in playlist");
	}//serve il numero della canzone->da integrare nella parte grafica, quando si seleziona


	void setBackwardButton(ActionEvent e)
	{
		if(player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
			this.stop();
		player = pl.nextSong(position);
		this.playSong();

		System.out.println("backwarding in the playlist");
	}

	void setForwardButton(ActionEvent e)
	{
		if(player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
			this.stop();
		player = pl.previousSong(position);
		this.playSong();

		System.out.println("forwarding in the playlist");
	}

	//collegamento pulsante-funzione di loop
	public void setRepeatButton(ActionEvent e)
	{
		loop_state = !loop_state;

		if(loop_state)
		{
			if(loop.songs == 1)
				loop.mp = player;
			else
			{
				this.playlistOn = true;
			}//controllare che si possa fare e non salti canzoni a causa dell'incremento anche più in basso


			//			loop.songs= 1;

			if(loop.songs == 1)
				loop.loop_selection();
		}
		else
			loop.stop_loop(player);
	}



	public void playSong()
	{		
		if (player.getStatus().equals(MediaPlayer.Status.PLAYING))
		{
			player.pause();
		} 
		else 
		{
			if(player.getStatus().equals(MediaPlayer.Status.PAUSED))
			{
				player.play();
			}
			else
			{				
				if(playlistOn)
				{
					path = pl.names.get(position);
					
					System.out.println("riproduzione\t"+path+"\talla posizione "+position);

					player = pl.currentSong(position);
				}

				getTrackInfo();

				player.play();
				timeSlider.setMax(player.getTotalDuration().toSeconds());
				setVolume();
				setTrackTime();
				grafica.setPauseIcon(playButton);
				end = false;	
			}
		}
	}


	public void setPlayButton(ActionEvent event) 
	{
		if(!player.getStatus().equals(MediaPlayer.Status.PLAYING) && !player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
			if(pl.nSongs() == 0)
			{
				System.out.println("no songs in playlist, opened file\t" + path);
			}
			else
			{
				playlistOn = true;
			}//sistemare: cambiare if in base alla selezione: brano singolo o playlist
		}
		this.playSong();
	}

	public void setStopButton(ActionEvent event) {

		if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
			player.stop();
			player.setStartTime(javafx.util.Duration.seconds(0));
			timeSlider.setValue(0);
			gotSongTime = true;
			elapsedS = 0;
			elapsedM = 0;
		}
	}

	public void getTrackInfo() 
	{

		//Calcoliamo la durata della canzone (timeM : timeS)
		//DA RIFARE CON LA PLAYLIST
		if (gotSongTime)
		{
			totalS = (int)player.getTotalDuration().toSeconds();

			totalM = totalS / 60;
			totalS -= totalM * 60;
			gotSongTime=false;

			if (totalS > 9) 
			{
				totalTimeLabel.setText(totalM + ":" + totalS);
			} 
			else 
			{
				totalTimeLabel.setText(totalM + ":0" + totalS);
			}
		}

		//Apriamo il file audio e leggiamo i tag
		try 
		{
			mp3file = new Mp3File(path);
		} 
		catch (IOException ie)
		{
			ie.printStackTrace();
		}
		catch (UnsupportedTagException ut)
		{
			ut.printStackTrace();
		}
		catch (InvalidDataException id)
		{
			id.printStackTrace();
		}

		id3v2Tag = mp3file.getId3v2Tag();

		//Estraiamo la cover dalla traccia
		imageData = id3v2Tag.getAlbumImage();

		if (imageData != null) 
		{
			RandomAccessFile file;
			try
			{
				file = new RandomAccessFile("src/sample/data/albumcover.jpg", "rw");
				file.write(imageData);
				file.close();
				FileInputStream imgSrc = new FileInputStream("src/sample/data/albumcover.jpg");
				Image img = new Image(imgSrc);
				trackImage.setImage(img);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		//Settiamo i tag sulla UI
		artistLabel.setText("Artista: " + id3v2Tag.getArtist());
		titleLabel.setText("Titolo: " + id3v2Tag.getTitle());
		albumLabel.setText("Album: " + id3v2Tag.getAlbum());
		genreLabel.setText("Genere: " + id3v2Tag.getGenreDescription());
		yearLabel.setText("Anno: " + id3v2Tag.getYear());
	}

	public void setVolume()
	{
		volumeSlider.valueProperty().addListener(new InvalidationListener() 
		{
			@Override
			public void invalidated(Observable observable)
			{
				if (volumeSlider.isValueChanging()){
					player.setVolume(volumeSlider.getValue()/100);
				}
			}
		});
	}

	public void setBalance()
	{
		volumeSlider.setOnDragExited((Event) -> 
		{
			//Da fare dopo la UI
		});
	}


	public void setTrackTime()
	{
		timeSlider.setOnMouseReleased((MouseEvent) -> 
		{
			this.totalS_on_moving = this.totalM * 60 + this.totalS;
			player.stop();
			player.setStartTime(javafx.util.Duration.seconds(timeSlider.getValue()));
			player.play();
			totalM = this.totalS_on_moving / 60;
			this.totalS_on_moving -= totalM * 60;
			totalS = this.totalS_on_moving;
		});//cerco di conservare il valore iniziale della durata del pezzo

		player.currentTimeProperty().addListener((obs, oldTime, newTime) -> 
		{
			if(!timeSlider.isValueChanging() && !end) 
			{
				timeSlider.setValue(newTime.toSeconds());
				elapsedS = (int)player.getCurrentTime().toSeconds();
				elapsedM = elapsedS / 60;
				elapsedS -= elapsedM * 60;  

				if((elapsedM * 60 + elapsedS) == (totalM * 60 + totalS))
				{	
					if(playlistOn && position != pl.nSongs()-1 )// && !loop_state)
					{
						position++;
						this.stop();
						player = pl.currentSong(position);

						//						if(!initialized)
						//							initialized = !initialized;

						playSong();

						System.out.println(position + " position\t total " + pl.nSongs() + " state "+ playlistOn);
					}
					else
					{
						if(loop_state && playlistOn && (position == pl.nSongs()-1))
						{
							position = 0;
							this.stop();
							player = pl.currentSong(position);
							this.playSong();
						}				
						else
						{
							this.stop();

							if(!loop_state)
							{
								playlistOn = false;							
							}							
						}

					}
				}
				else
				{
					player.setStartTime(player.getCurrentTime());
				}
				printTime();
			}
		});
	}

	void stop()
	{
		elapsedS = 0;
		elapsedM = 0;
		timeSlider.setValue(0);
		player.setStartTime(javafx.util.Duration.seconds(0));

		if(!loop_state)
		{
			end = true;
			player.stop();
			System.out.println("end");
			gotSongTime = !gotSongTime;
		}
		else
		{
			if(loop_state && this.playlistOn)
			{
				player.stop();
				gotSongTime = !gotSongTime;	
			}

		}
	}

	void printTime()
	{
		if (elapsedS > 9)
		{
			elapsedTimeLabel.setText(elapsedM + ":" + elapsedS);
		}
		else 
		{
			elapsedTimeLabel.setText(elapsedM + ":0" + elapsedS);
		}
	}
}