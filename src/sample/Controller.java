package sample;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Controller {

	// Parametri UI
	public Button playButton = new Button();
	public Button stopButton = new Button();
	public Button forwardButton = new Button();
	public Button backwardButton = new Button();
	public Button repeatButton = new Button();
	public Button styleButton = new Button();
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

	private UI grafica = new UI();


	//aggiungo bottoni per gestire la playlist
	public Button addSong = new Button(); 
	public Button removeSong = new Button();
	private int position = 0, oldPosition = -1;
	private boolean playlistOn = false;
	private Random rand = new Random();

	//Inizializziamo la sorgente audio
	private String path = new String("src/sample/Hero.mp3");
	private Media source = new Media(new File(path).toURI().toString());
	private MediaPlayer player = new MediaPlayer(source);

	//Inizializziamo i parametri per i Tag
	private boolean gotSongTime = true, shuffleOn = false;												//cambiare quando si avanza di una canzone nella playlist
	private int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;
	private Loop loop = new Loop();
	private boolean loop_state = false, end = false;
	private Playlist pl = new Playlist();
	private int totalS_on_moving;
	List<String> added = new ArrayList<String>();
	private int pos;

	List<String> styleList = new ArrayList<>();


	private FileSelection fs = new FileSelection();
	//																sistemare il loop in base alla variabile che indica se � in riproduzione una playlist

	public void initialize(){
		grafica.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);
	}

	private void reloadUI(UI gui){
		gui.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);
	}


	public void addSongToPlaylist()
	{
		fs.start(new Stage());
		added = fs.filename;

		if(added != null)
		{
			//			path = added.get(position);
			for(pos = 0; pos < added.size(); pos++)
				pl.addSong(added.get(position));
		}//controllare filepah per riproduzione del file corrente

		System.out.println("ricevuto il percorso:" +added);
	}



	public void removeSongFromPlaylist()
	{
		if(pl.nSongs() > 0)
			pl.removeSong(0, 0);//cambiare 0,0 con start, end
		else
			System.out.println("impossibile rimuovere canzoni: non ne sono presenti in playlist");
	}//serve il numero della canzone->da integrare nella parte grafica, quando si seleziona


	public void setBackwardButton()
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

	public void setForwardButton()
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

	public void setShuffleButton()
	{
		shuffleOn = shuffleButton.isSelected();

		if (shuffleButton.isSelected()){
			grafica.setShuffleOnIcon(shuffleButton);
		} else {
			grafica.setShuffleOffIcon(shuffleButton);
		}
		System.out.println("Shuffle " + shuffleOn);
	}


	public void setRepeatButton()
	{
		if(!shuffleOn)
		{	loop_state = !loop_state;
			this.playlistOn = loop.loopCall(loop_state, player);
		}
	}



	void playSong()
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
				if(playlistOn)
				{
					path = pl.names.get(position);
					player = pl.currentSong(position);
				}

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


	public void setPlayButton()
	{
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


	public void setStopButton() {

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

	private void setVolume()
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

	public void setStyleButton() throws IOException{

		File check;
		Random rand = new Random();
		int n = rand.nextInt(65536) + 1;

		//Lista dei file da controllare
		if (styleList.isEmpty()) {
			styleList.add(0, System.getProperty("java.io.tmpdir") + n + "FXStyle/background.jpg");
			styleList.add(1, System.getProperty("java.io.tmpdir") + n + "FXStyle/defaultcover.jpg");
			styleList.add(2, System.getProperty("java.io.tmpdir") + n + "FXStyle/next.png");
			styleList.add(3, System.getProperty("java.io.tmpdir") + n + "FXStyle/pause.png");
			styleList.add(4, System.getProperty("java.io.tmpdir") + n + "FXStyle/play.png");
			styleList.add(5, System.getProperty("java.io.tmpdir") + n + "FXStyle/previous.png");
			styleList.add(6, System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_1.png");
			styleList.add(7,System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_all.png");
			styleList.add(8, System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_off.png");
			styleList.add(9, System.getProperty("java.io.tmpdir") + n + "FXStyle/shuffle_off.png");
			styleList.add(10, System.getProperty("java.io.tmpdir") + n + "FXStyle/shuffle_on.png");
			styleList.add(11, System.getProperty("java.io.tmpdir") + n + "FXStyle/stop.png");
		} else {
			styleList.set(0, System.getProperty("java.io.tmpdir") + n + "FXStyle/background.jpg");
			styleList.set(1, System.getProperty("java.io.tmpdir") + n + "FXStyle/defaultcover.jpg");
			styleList.set(2, System.getProperty("java.io.tmpdir") + n + "FXStyle/next.png");
			styleList.set(3, System.getProperty("java.io.tmpdir") + n + "FXStyle/pause.png");
			styleList.set(4, System.getProperty("java.io.tmpdir") + n + "FXStyle/play.png");
			styleList.set(5, System.getProperty("java.io.tmpdir") + n + "FXStyle/previous.png");
			styleList.set(6, System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_1.png");
			styleList.set(7, System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_all.png");
			styleList.set(8, System.getProperty("java.io.tmpdir") + n + "FXStyle/repeat_off.png");
			styleList.set(9, System.getProperty("java.io.tmpdir") + n + "FXStyle/shuffle_off.png");
			styleList.set(10, System.getProperty("java.io.tmpdir") + n + "FXStyle/shuffle_on.png");
			styleList.set(11, System.getProperty("java.io.tmpdir") + n + "FXStyle/stop.png");
		}


		//Scelgo il file ZIP
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Scegliere il file zip");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
		File selectedFile = fileChooser.showOpenDialog(new Stage());

		if (selectedFile==null){
			return;
		}



		//Estraggo il file zip in un direttorio temporaneo
		String zipFilePath = selectedFile.toString();
		String destDirectory = System.getProperty("java.io.tmpdir") + n + "FXStyle";
		UnzipUtility unzipper = new UnzipUtility();
		try {
			unzipper.unzip(zipFilePath, destDirectory);
		} catch (Exception ex) {
			//gestione degli errori
			ex.printStackTrace();
		}

		//Controlliamo l'integrità dei file
		for(int i=0; i<styleList.size(); i++){
			check = new File(styleList.get(i));
			if (!check.isFile() || !check.exists()){
				System.out.println("Skin non è completa!");
				return;
			}
		}

		//Copiamo i file della Skin nella cartella del proggetto
		for (int i=0;i<styleList.size();i++){
			check = new File(styleList.get(i));
			Path file1 = Paths.get(styleList.get(i));
			Path file2 = Paths.get(System.getProperty("user.dir") + "/src/sample/img/" + check.getName());
			Files.copy(file1, file2, StandardCopyOption.REPLACE_EXISTING);

		}

		//Ricarichiamo la skin
		UI gui = new UI();
		gui.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);

		System.out.println("Skin cambiata!");


	}


	private void setTrackTime()
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

	private void stop()
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
				loop_state = !loop_state;									//la funzione di loop si interrompe quando è premuto il tasto stop


				if(!gotSongTime)
					gotSongTime = !gotSongTime;
			}
		}
	}


	private void printTime()
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

	private void randomGenerator()
	{
		this.oldPosition = position;

		do {
			position = rand.nextInt(pl.nSongs());
		}while(position > pl.nSongs()-1 || position == oldPosition);
	}
}