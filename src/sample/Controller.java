package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Random;


public class Controller {

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
	public ToggleButton shuffleButton = new ToggleButton();
	public AnchorPane paneControls = new AnchorPane();

	UI grafica = new UI();


	//aggiungo bottoni per gestire la playlist
	public Button addSong = new Button(); 
	public Button removeSong = new Button();
	int position = 0, oldPosition = -1;
	boolean playlistOn = false;
	Random rand = new Random();

	//Inizializziamo la sorgente audio
	private String path = new String("src/sample/Hero.mp3");
	private Media source = new Media(new File(path).toURI().toString());;
	private MediaPlayer player = new MediaPlayer(source);

	//Inizializziamo i parametri per i Tag
	boolean gotSongTime = true, shuffleOn = false;												//cambiare quando si avanza di una canzone nella playlist 
	int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;
	Loop loop = new Loop();
	boolean loop_state = false, end = false;
	Playlist pl = new Playlist();
	int totalS_on_moving;
	List<String> added;
	int pos;


	FileSelection fs = new FileSelection();
	//																sistemare il loop in base alla variabile che indica se � in riproduzione una playlist

	public void initialize(){
		grafica.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);
	}


	public void addSongToPlaylist(ActionEvent e)
	{
		fs.start(new Stage());
		added = fs.filename;

		if(added != null)
		{
			//			path = added.get(position);
			for(pos = 0; pos < added.size(); pos++)
			{
				pl.addSong(added.get(pos));
				System.out.println("ricevuto il percorso:" +added.get(pos));
			}
		}//controllare filepah per riproduzione del file corrente
	}



	public void removeSongFromPlaylist(ActionEvent e)
	{
		if(pl.nSongs() > 0)
			pl.removeSong(0, 0);//cambiare 0,0 con start, end
		else
			System.out.println("impossibile rimuovere canzoni: non ne sono presenti in playlist");
	}//serve il numero della canzone->da integrare nella parte grafica, quando si seleziona


	public void setBackwardButton(ActionEvent e)
	{
		if(player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
			this.stop();
		}

		if(shuffleOn)
		{
			this.randomGenerator();
		}
		else
		{
			//			player = pl.previousSong(position);
			if(position > 0)
			{
				position--;
				player = pl.currentSong(position);
			}
			else
			{
				if(position == 0)
					player = pl.currentSong(position);
			}

		}

		this.playSong();

		System.out.println("Backwarding in the playlist");
	}

	public void setForwardButton(ActionEvent e)
	{
		if(player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
			this.stop();

		if(shuffleOn)
		{
			this.randomGenerator();
		}
		else
		{
			if(position < pl.nSongs())
			{
				position++;
				player = pl.currentSong(position);
				//			player = pl.nextSong(position);
			}
			else
			{
				if(position == pl.nSongs())
					player = pl.currentSong(position);
			}
		}

		this.playSong();

		System.out.println("Forwarding in the playlist");
	}

	public void setShuffleButton(ActionEvent e)
	{
		shuffleOn = shuffleButton.isSelected();

		if (shuffleButton.isSelected()){
			grafica.setShuffleOnIcon(shuffleButton);
		} else {
			grafica.setShuffleOffIcon(shuffleButton);
		}
		System.out.println("Shuffle " + shuffleOn);
	}


	public void setRepeatButton(ActionEvent e)
	{
		if(!shuffleOn)
		{	loop_state = !loop_state;
			this.playlistOn = loop.loopCall(loop_state, player);
		}
	}



	public void playSong()
	{
		if (player.getStatus().equals(MediaPlayer.Status.PLAYING))
		{
			player.pause();
			grafica.setPlayIcon(playButton);
		}
		else 
		{
			if(player.getStatus().equals(MediaPlayer.Status.PAUSED))
			{
				player.play();
				grafica.setPauseIcon(playButton);
			}
			else
			{				
				if(playlistOn) {
                    path = pl.names.get(position);
                    player = pl.currentSong(position);
                    System.out.println("nel caso di playlist " + path);
                }
				source = new Media(new File(path).toURI().toString());                          //sistemare l'assegnamento
				getTrackInfo();

				player.play();
				grafica.setPauseIcon(playButton);
				timeSlider.setMax(player.getTotalDuration().toSeconds());
				setVolume();
				setTrackTime();
				end = false;	
			}
		}
	}


	public void setPlayButton(ActionEvent event) 
	{
	    System.out.println(pl.nSongs());

		if(!player.getStatus().equals(MediaPlayer.Status.PLAYING) && !player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
			if(pl.nSongs() == 0)
			{
				System.out.println("No songs in playlist, opened file " + path);
			}
			else
			{
				playlistOn = true;

				if(shuffleOn)
					this.randomGenerator();

			}//sistemare: cambiare if in base alla selezione: brano singolo o playlist
		}						//controllare la correttezza di shuffle																					//controllare la correttezza di shuffle
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
			grafica.setPlayIcon(playButton);
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

		//Settiamo i tag sulla UI
		trackImage.setImage((Image)source.getMetadata().get("image"));
		artistLabel.setText("Artista: " + source.getMetadata().get("artist"));
		titleLabel.setText("Titolo: " + source.getMetadata().get("title"));
		albumLabel.setText("Album: " + source.getMetadata().get("album"));
		genreLabel.setText("Genere: " + source.getMetadata().get("genre"));
		yearLabel.setText("Anno: " + source.getMetadata().get("year"));
	}

	public void setVolume()
	{
		volumeSlider.valueProperty().addListener(observable -> {
			if (volumeSlider.isValueChanging()){
				player.setVolume(volumeSlider.getValue()/100);
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
					if(playlistOn && position != pl.nSongs()-1 )
					{
						System.out.println("Chiamata a stop non in ultima canzone");
						this.stop();

						if(!shuffleOn)
							position++;
						else
						{
							this.randomGenerator();
							System.out.println("Generato il numero random " + position);
						}

						player = pl.currentSong(position);
						playSong();
					}
					else
					{
						if(loop_state && playlistOn && (position == pl.nSongs()-1))
						{
							if(!shuffleOn)
								position = 0;
							else
							{
								this.randomGenerator();
								System.out.println("Ultimo brano in playlist: generato il numero random " + position);
							}

							this.stop();
							player = pl.currentSong(position);
							this.playSong();
						}				
						else
						{
							System.out.println("Ultimo caso di stop");
							this.stop();

							if(!shuffleOn)
								position = 0;
							else
							{
								if(!shuffleOn)
								{
									this.stop();	
								}
								else
								{	
									this.randomGenerator();
									System.out.println("Ultimo brano in pl: generato il numero random"+position);
									player = pl.currentSong(position);
									this.playSong();
								}
							}

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

		if(!loop_state && !shuffleOn)
		{
			end = true;
			player.stop();
			System.out.println("end");

			if(!gotSongTime)
				gotSongTime = !gotSongTime;
		}
		else
		{
			if((loop_state && this.playlistOn)||shuffleOn)
			{
				player.stop();
				loop_state = !loop_state;									//la funzione di loop si interrompe quando � premuto il tasto stop


				if(!gotSongTime)
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

	void randomGenerator()
	{
		this.oldPosition = position;

		do {
			position = rand.nextInt(pl.nSongs());
		}while(position > pl.nSongs()-1 || position == oldPosition);
	}
}
