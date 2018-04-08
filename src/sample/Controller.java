package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
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
	public ListView playList;
	public ToggleButton shuffleButton = new ToggleButton();
	public AnchorPane paneControls = new AnchorPane();
    int loopStatus = 0;
    boolean ended = false;

	private UI grafica = new UI();


	//aggiungo bottoni per gestire la playlist
	public Button addSong = new Button();
	public Button removeSong = new Button();
	int position = 0, oldPosition = -1;
	boolean playlistOn = true;
	Random rand = new Random();

	//Inizializziamo la sorgente audio
	private String path = new String("src/sample/Hero.mp3");
	private Media source = new Media(new File(path).toURI().toString());;
	private MediaPlayer player = new MediaPlayer(source);

	//Inizializziamo i parametri per i Tag
	boolean gotSongTime = true, shuffleOn = false;												//cambiare quando si avanza di una canzone nella playlist
	int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;
	boolean end = false;
	Playlist pl = new Playlist();
	int totalS_on_moving;
	List<String> added;
	int pos = 0, dim;
	boolean mooved = false, click = false;
	listTitle lt = new listTitle();
	int clicked;
	boolean wasPlaying = false;
	boolean stopped = false, stopPressed = false;

	List<String> styleList = new ArrayList<>();
	FileSelection fs = new FileSelection();

	public void initialize()
	{
		grafica.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);
	}

	private void reloadUI(UI gui)
    {
		gui.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel);
	}

	public void addSongToPlaylist(ActionEvent e)
	{
		fs.start(new Stage());
		added = fs.filename;

        if(added != null)
		{
			//			path = added.get(position);

			for(pos = 0; pos < added.size()-dim; pos++)
			{
				pl.addSong(added.get(pos));
			}

			dim = pl.nSongs();

			lt.takeTitles(pl.names);
            playList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            playList.setOrientation(Orientation.VERTICAL);
			playList.setItems(lt.st);
		}//controllare filepah per riproduzione del file corrente
	}



	public void removeSongFromPlaylist(ActionEvent e)
	{
		if(pl.nSongs() > 0)
		{
            pl.removeSong(clicked, clicked);//cambiare 0,0 con start, end
            playList.getItems().remove(clicked);
            click = false;
        }
		else
			System.out.println("impossibile rimuovere canzoni: non ne sono presenti in playlist");
	}//serve il numero della canzone->da integrare nella parte grafica, quando si seleziona


	public void setBackwardButton(ActionEvent e)
	{
	    if(e.getSource().equals(backwardButton))
	    {
            if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
            {
                if(player.getStatus().equals(MediaPlayer.Status.PAUSED))
                    wasPlaying = false;
                else
                    wasPlaying = true;

                stopped = true;
                this.stop();
            }

            if (shuffleOn)
            {
                this.randomGenerator();
            }
            else
                {
                if (position > 0 && loopStatus != 1)
                {
                        position--;
                }
            }//nel caso in cui posizione == 0->non cambia

        this.assign();
        }
	}


	public void setForwardButton(ActionEvent e)
    {
        if (e.getSource().equals(forwardButton))
        {
            if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
            {
                if(player.getStatus().equals(MediaPlayer.Status.PAUSED))
                    wasPlaying = false;
                else
                    wasPlaying = true;

                stopped = true;
                this.stop();
            }

            if (shuffleOn)
            {
                this.randomGenerator();
            }
            else
                {
                if (position < pl.nSongs()-1)
                {
                    if (loopStatus != 1)
                    {
                        if(loopStatus == 2 && position == pl.nSongs()-1)
                            position = 0;
                        else
                            position++;
                    }
                }
                else
                    {
                    if (position == pl.nSongs()-1 && loopStatus != 1)
                    {
                        if(loopStatus != 2)
                            wasPlaying = false;

                        position = 0;
                    }
                }
            }

        this.assign();
        }
    }


	public void setShuffleButton(ActionEvent e)
	{
	    System.out.println("shuffle vale "+shuffleOn);

		if (shuffleButton.isSelected() && loopStatus != 1)
		{
			grafica.setShuffleOnIcon(shuffleButton);
			loopStatus = 2;
			grafica.setRepeatAllIcon(repeatButton);
            shuffleOn = shuffleButton.isSelected();
		}
		else
		    {
		        loopStatus = 0;
			    grafica.setShuffleOffIcon(shuffleButton);
			    grafica.setRepeatOffIcon(repeatButton);
			    shuffleOn = false;
		    }
	}


	public void setRepeatButton(ActionEvent e)
	{
		{
		    if(loopStatus == 0)
		    {
		        loopStatus++;
                grafica.setRepeatOneIcon(repeatButton);
            }
            else
                {
                if (loopStatus == 1 && playlistOn)
                {
                    grafica.setRepeatAllIcon(repeatButton);
                    loopStatus++;
                }
                else
                    {
                        if(loopStatus == 2)
                        {
                            grafica.setRepeatOffIcon(repeatButton);

                            System.out.println("quando premo stop loop shuffle vale "+shuffleOn);
                            if(shuffleOn)
                            {
                                grafica.setShuffleOffIcon(shuffleButton);
                                shuffleOn = false;
                            }
                            loopStatus = 0;
                        }
                    }
            }
		}
	}



	public void playSong()
	{
		if (player.getStatus().equals(MediaPlayer.Status.PLAYING) && (!ended && !stopped))
		{
		    System.out.println("primo caso play\t"+elapsedM+"  "+elapsedS+"\t"+player.getStatus());
			player.pause();
			grafica.setPlayIcon(playButton);
		}
		else 
		{
			if(player.getStatus().equals(MediaPlayer.Status.PAUSED) || loopStatus == 1)
			{
			    System.out.println("caso play di loop di singola canzone");

				player.play();
				grafica.setPauseIcon(playButton);
			}
			else {
				if (playlistOn && !mooved) {
					System.out.println("caso play con shuffle posizione " + position + " filename " + pl.names.get(position));
					path = pl.names.get(position);
					player = pl.currentSong(position);
				} else {
					path = pl.names.get(clicked);
					player = pl.currentSong(clicked);
					click = false;
				}
			}

            gotSongTime = true;
            mooved = false;
            System.out.println(path);
            source = new Media(new File(path).toURI().toString());//new Media(new File(path).toURI().toString());                          //sistemare l'assegnamento
            getTrackInfo();
            grafica.setPauseIcon(playButton);
            timeSlider.setMax(player.getTotalDuration().toSeconds());
            setVolume();
            setTrackTime();
            end = false;
            player.play();
		}
		ended = false;
		stopped = false;
	}


	public void setPlayButton(ActionEvent event) 
	{
	    System.out.println(pl.nSongs());

		if(!player.getStatus().equals(MediaPlayer.Status.PLAYING) && !player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
			if(click)
			{
			    playlistOn = false;
			}
			else
			{
			    if(pl.nSongs() > 0)
			    	playlistOn = true;
			    else
			        playlistOn = false;

				if(shuffleOn)
					this.randomGenerator();

			}
		}

        if(playlistOn || click)
		    this.playSong();
	}


	public void setStopButton(ActionEvent event)
    {

		if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
		{
            shuffleOn = false;
            stopped = true;
			this.stop();
            loopStatus = 0;									//la funzione di loop si interrompe quando è premuto il tasto stop
            grafica.setRepeatOffIcon(repeatButton);
            grafica.setPlayIcon(playButton);
		}

		stopPressed = true;
	}

	public void getTrackInfo() 
	{
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
		volumeSlider.valueProperty().addListener(observable ->
        {
			if (volumeSlider.isValueChanging())
			{
				player.setVolume(volumeSlider.getValue()/100);
			}
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
		try
        {
			unzipper.unzip(zipFilePath, destDirectory);
		}
		catch (Exception ex)
        {
			ex.printStackTrace();
		}	//gestione degli errori

		//Controlliamo l'integrità dei file
		for(int i=0; i<styleList.size(); i++)
		{
			check = new File(styleList.get(i));
			if (!check.isFile() || !check.exists()){
				System.out.println("Skin non è completa!");
				return;
			}
		}

		//Copiamo i file della Skin nella cartella del proggetto
		for (int i=0;i<styleList.size();i++)
		{
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
			if((!timeSlider.isValueChanging() && !end)||(!timeSlider.isValueChanging() && mooved))
			{
				timeSlider.setValue(newTime.toSeconds());
				elapsedS = (int)player.getCurrentTime().toSeconds();
				elapsedM = elapsedS / 60;
				elapsedS -= elapsedM * 60;  

				if((elapsedM * 60 + elapsedS) == (totalM * 60 + totalS))
				{
				    ended = true;

					if(playlistOn && position != pl.nSongs()-1 )
					{
						System.out.println("Chiamata a stop non in ultima canzone");
						this.stop();

						if(!shuffleOn && loopStatus != 1)
							position++;
						else
						{
						    if(loopStatus != 1)
							    this.randomGenerator();
						}

						player = pl.currentSong(position);
						playSong();
					}
					else
					{
						if(loopStatus == 2 && playlistOn && (position == pl.nSongs()-1))//se siamo in loop di una playlist e siamo arrivati all'ultima canzone
						{
							if(shuffleOn)
                                this.randomGenerator();
							else
							    position = 0;

							this.stop();
							player = pl.currentSong(position);
							this.playSong();
						}				
						else
						{
						    if(loopStatus == 0)
						        grafica.setPlayIcon(playButton);

							this.stop();

							if(!shuffleOn)
							{
							    if(loopStatus != 1)
                                    position = 0;

                                gotSongTime = true;
                            }
							else
							{
                                if(loopStatus == 0)
									this.randomGenerator();
									this.playSong();
							}

							if(loopStatus == 0)
							{
								playlistOn = false;
							}

                            player = pl.currentSong(position);

							if(loopStatus != 0)
							    playSong();
						}
					}
				}
				else
				{
					player.setStartTime(player.getCurrentTime());
				}
				printTime();
				end = false;
				mooved = false;
			}
		});
	}

	private void stop()
	{
		if(ended || stopped)
        {
            System.out.println("shuffleon "+shuffleOn+"\tstopped "+ stopped +"\tmooved " +mooved);
            if(shuffleOn && stopPressed)
            {
            	System.out.println("shuffle disattivato nello stop");
				grafica.setShuffleOffIcon(shuffleButton);
				stopPressed = false;
			}

            if(loopStatus != 1)
                grafica.setPlayIcon(playButton);
        }

        player.stop();
        elapsedS = 0;
        elapsedM = 0;
        this.printTime();
        timeSlider.setValue(0);
        player.setStartTime(javafx.util.Duration.seconds(0));

        if(!playlistOn)
		    playlistOn = true;

		if(loopStatus == 0)
        {
			end = true;
			System.out.println("end");

			if(!gotSongTime || (loopStatus != 1 && !gotSongTime))
				gotSongTime = !gotSongTime;
		}//se non siamo in un loop
        else
            end = true;

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

	public void isClicked()
    {
       if(playList.getFocusModel().getFocusedIndex() == playList.getSelectionModel().getSelectedIndex())
           clicked = playList.getSelectionModel().getSelectedIndex();

       click = true;
    }


    void assign()
    {
        player = pl.currentSong(position);

        System.out.println("in assign position vale "+position+" stopped "+stopped+" wasplaying "+wasPlaying);

        if(wasPlaying)
            this.playSong();

        mooved = true;
        wasPlaying = false;
        stopped = false;
    }
}