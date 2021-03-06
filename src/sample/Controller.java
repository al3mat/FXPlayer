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
        public Button addSong = new Button();
        public Button removeSong = new Button();
        public Button removeAllSongsButton = new Button();
        public Label artistLabel = new Label();
        public Label titleLabel = new Label();
        public Label albumLabel = new Label();
        public Label genreLabel = new Label();
        public Label yearLabel = new Label();
        public Label totalTimeLabel = new Label();
        public Label bitRateLabel = new Label();
        public Label elapsedTimeLabel = new Label();
        public ImageView trackImage = new ImageView();
        public Slider timeSlider = new Slider();
        public Slider volumeSlider = new Slider();
        public ListView playList;
        public ToggleButton shuffleButton = new ToggleButton();
        public AnchorPane paneControls = new AnchorPane();
        public AnchorPane panePlaylist = new AnchorPane();
        private Alert error;

        private int loopStatus = 0;
        private boolean ended = false;

        private UI grafica = new UI();


        private int position = 0;
        private boolean playlistOn = true;
        private Random rand = new Random();

        //Inizializziamo la sorgente audio
        private String path = "";
        private MediaPlayer player;

        //Inizializziamo i parametri per i Tag
        private boolean gotSongTime = true, shuffleOn = false;												//cambiare quando si avanza di una canzone nella playlist
        private int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;
        private boolean end = false;
        private Playlist pl = new Playlist();
        private int totalS_on_moving;
        private int dim;
        private boolean mooved = false, click = false;
        private listTitle lt = new listTitle();
        private int clicked;
        private boolean wasPlaying = false;
        private boolean stopped = false, stopPressed = false;

        private List<String> styleList = new ArrayList<>();
        private FileSelection fs = new FileSelection();

        public void initialize()
        {
            grafica.setUI(playButton, stopButton, forwardButton, backwardButton, repeatButton, shuffleButton, trackImage, paneControls, artistLabel, titleLabel, albumLabel, genreLabel, yearLabel, addSong, removeSong, styleButton, removeAllSongsButton, panePlaylist);

            playList.setOnMouseClicked(click -> isClicked());

        }

        public void addSongToPlaylist()
        {
            fs.start(new Stage());
            List<String> added = fs.filename;

            if(added != null)
            {
                int pos;
                for(pos = 0; pos < added.size()-dim; pos++)
                {
                    pl.addSong(added.get(pos));
                }

                dim = pl.nSongs();

                lt.takeTitles(pl.names);
                playList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                playList.setOrientation(Orientation.VERTICAL);
                playList.setItems(lt.st);
            }//controllare filepah per riproduzione del file corrente
        }

        public void setRemoveAllSongsButton(ActionEvent e)
        {
            System.out.println(pl.nSongs());
            if(e.getSource().equals(removeAllSongsButton) && pl.nSongs()>0) {
                if (e.getSource().equals(removeAllSongsButton)) {
                    playList.getItems().clear();
                    pl.removeAll();
                    lt.clearNameList();
                }
            }
            else
            {
                error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("invalid selection");
                error.setContentText("impossibile rimuovere brani dalla playlist: la playlist è vuota");
                error.showAndWait();
            }
        }


        public void removeSongFromPlaylist()
        {
            if(pl.nSongs() > 0)
            {
                if(clicked != position) {
                    pl.removeSong(clicked, position);
                    playList.getItems().remove(clicked);
                }
                else
                {
                    error = new Alert(Alert.AlertType.ERROR);
                    error.setHeaderText("invalid selection");
                    error.setContentText("selezionato brano in riproduzione, impossibile rimuoverlo dalla playlist");
                    error.showAndWait();
                }


                click = false;
            }
            else
            {
                error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile eliminare brani: non ne sono presenti in playlist");
                error.showAndWait();
            }
        }//serve il numero della canzone->da integrare nella parte grafica, quando si seleziona


        public void setBackwardButton(ActionEvent e)
        {
            if(e.getSource().equals(backwardButton) && player != null)
            {
                if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
                {
                    wasPlaying = !player.getStatus().equals(MediaPlayer.Status.PAUSED);

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
            else
            {
                error = new Alert(Alert.AlertType.WARNING);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile tornare al brano precedente: non sono presenti brani in playlist");
                error.showAndWait();

            }
            playList.getSelectionModel().select(position);
        }


        public void setForwardButton(ActionEvent e)
        {

            if (e.getSource().equals(forwardButton) && player != null)
            {
                if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
                {
                    wasPlaying = !player.getStatus().equals(MediaPlayer.Status.PAUSED);

                    stopped = true;
                    this.stop();
                }

                if (shuffleOn)
                {
                    this.randomGenerator();
                    this.assign();
                    gotSongTime = true;
                    //getTrackInfo(position);//modificato
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
            else
            {
                error = new Alert(Alert.AlertType.WARNING);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile avanzare nella playlist: non sono presenti brani in playlist");
                error.showAndWait();

            }
            playList.getSelectionModel().select(position);
        }


        public void setShuffleButton(ActionEvent e)
        {
            if(e.getSource().equals(shuffleButton) && pl.nSongs()>0)
            {
                if (shuffleButton.isSelected() && loopStatus != 1 && playlistOn)
                {
                    grafica.setShuffleOnIcon(shuffleButton);
                    loopStatus = 2;
                    grafica.setRepeatAllIcon(repeatButton);
                    shuffleOn = shuffleButton.isSelected();
                }
                else
                {
                    if (shuffleOn)
                    {
                        loopStatus = 0;
                        grafica.setShuffleOffIcon(shuffleButton);
                        grafica.setRepeatOffIcon(repeatButton);
                        shuffleOn = false;
                    }
                }
            }
            else
            {
                error = new Alert(Alert.AlertType.WARNING);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile impostare la riproduzione casuale: non sono presenti brani in playlist");
                error.showAndWait();

            }
        }


        public void setRepeatButton(ActionEvent e)
        {
            if(e.getSource().equals(repeatButton) && player != null)
            {
                {
                    if (loopStatus == 0) {
                        loopStatus++;
                        grafica.setRepeatOneIcon(repeatButton);
                    }
                    else
                    {
                        if (loopStatus == 1 && playlistOn)
                        {
                            grafica.setRepeatAllIcon(repeatButton);
                            loopStatus++;
                            click = false;
                        }
                        else
                        {
                            if (loopStatus == 1)
                            {
                                loopStatus = 0;
                                grafica.setRepeatOffIcon(repeatButton);
                            }

                            if (loopStatus == 2)
                            {
                                grafica.setRepeatOffIcon(repeatButton);

                                System.out.println("quando premo stop loop shuffle vale " + shuffleOn);
                                if (shuffleOn)
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
            else
            {
                error = new Alert(Alert.AlertType.WARNING);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile impostare il loop sui brani: non ne sono presenti in playlist");
                error.showAndWait();

            }
        }



        private void playSong()
        {
            if (player.getStatus().equals(MediaPlayer.Status.PLAYING) && (!ended && !stopped))
            {
                player.pause();
                grafica.setPlayIcon(playButton);
            }
            else
            {
                if(player.getStatus().equals(MediaPlayer.Status.PAUSED) || loopStatus == 1)
                {
                    player.play();
                    grafica.setPauseIcon(playButton);

                }
                else
                {
                    if (playlistOn && !mooved)
                    {
                        path = pl.names.get(position);
                        player = pl.currentSong(position);
                        getTrackInfo(position);
                        playList.getSelectionModel().select(position);
                    }
                    else
                    {
                        path = pl.names.get(clicked);
                        player = pl.currentSong(clicked);
                        gotSongTime = true;
                        playlistOn = true;
                        getTrackInfo(clicked);
                        playList.getSelectionModel().select(clicked);
                        position = clicked;
                    }
                }
                mooved = false;
                System.out.println(path);
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


        public void setPlayButton()
        {
            if (path.isEmpty())
            {
                if (playList.getItems().isEmpty())
                {
                    error = new Alert(Alert.AlertType.WARNING);
                    error.setHeaderText("no songs in playlist");
                    error.setContentText("impossibile riprodurre brani: non ne sono presenti in playlist");
                    error.showAndWait();

                    return;
                }
                path = pl.names.get(0);
                Media source = new Media(new File(path).toURI().toString());
                player = new MediaPlayer(source);
            }

            if(!player.getStatus().equals(MediaPlayer.Status.PLAYING) || !player.getStatus().equals(MediaPlayer.Status.PAUSED))
            {
                if(click)
                {
                    playlistOn = false;
                }
                else
                {
                    playlistOn = pl.nSongs() > 0;

                    if(shuffleOn)
                        this.randomGenerator();
                }
            }

            if(playlistOn || click)
                this.playSong();
        }


        public void setStopButton()
        {
            if(player != null)
            {
                if (player.getStatus().equals(MediaPlayer.Status.PLAYING) || player.getStatus().equals(MediaPlayer.Status.PAUSED))
                {
                    stopped = true;
                    stopPressed = true;
                    this.stop();
                    loopStatus = 0;                                    //la funzione di loop si interrompe quando è premuto il tasto stop
                    grafica.setRepeatOffIcon(repeatButton);
                    grafica.setPlayIcon(playButton);
                    stopped = false;
                }
            }
            else
            {
                error = new Alert(Alert.AlertType.WARNING);
                error.setHeaderText("no songs in playlist");
                error.setContentText("impossibile eseguire stop sul brano: non ne sono presenti in playlist");
                error.showAndWait();

            }
        }

        private void getTrackInfo(int i)
        {
            if (gotSongTime)
            {
                totalS = (int)player.getTotalDuration().toSeconds();
                totalM = totalS / 60;
                totalS -= totalM * 60;
                gotSongTime=false;
                System.out.println(i+" dura   "+totalM+":"+totalS);

                if (totalS > 9)
                {
                    totalTimeLabel.setText(totalM + ":" + totalS);
                }
                else
                {
                    totalTimeLabel.setText(totalM + ":0" + totalS);
                }

                bitrateCalc(totalM, totalS);
            }

            //Settiamo i tag sulla UI dopo aver fatto i controlli sui dati ricevuti
            if (pl.currentSong(i).getMedia().getMetadata().get("image") == null)
                grafica.setTrackImage(trackImage);
            else
                trackImage.setImage((Image) pl.currentSong(i).getMedia().getMetadata().get("image"));

            if(pl.currentSong(i).getMedia().getMetadata().get("artist") == null)
                artistLabel.setText("Artista: -");
            else
                artistLabel.setText("Artista: " + pl.currentSong(i).getMedia().getMetadata().get("artist"));

            if (pl.currentSong(i).getMedia().getMetadata().get("title") == null)
                titleLabel.setText("Titolo: -");
            else
                titleLabel.setText("Titolo: " + pl.currentSong(i).getMedia().getMetadata().get("title"));

            if (pl.currentSong(i).getMedia().getMetadata().get("title") == null)
                titleLabel.setText("Titolo: -");
            else
                titleLabel.setText("Titolo: " + pl.currentSong(i).getMedia().getMetadata().get("title"));

            if (pl.currentSong(i).getMedia().getMetadata().get("album") == null)
                albumLabel.setText("Album: -");
            else
                albumLabel.setText("Album: " + pl.currentSong(i).getMedia().getMetadata().get("album"));

            if (pl.currentSong(i).getMedia().getMetadata().get("genre") == null)
                genreLabel.setText("Genere: -");
            else
                genreLabel.setText("Genere: " + pl.currentSong(i).getMedia().getMetadata().get("genre"));

            if (pl.currentSong(i).getMedia().getMetadata().get("year") == null)
                yearLabel.setText("Anno: -");
            else
                yearLabel.setText("Anno: " + pl.currentSong(i).getMedia().getMetadata().get("year"));
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
                styleList.add(12, System.getProperty("java.io.tmpdir") + n + "FXStyle/add.png");
                styleList.add(13, System.getProperty("java.io.tmpdir") + n + "FXStyle/delete.png");
                styleList.add(14, System.getProperty("java.io.tmpdir") + n + "FXStyle/delete_all.png");
                styleList.add(15, System.getProperty("java.io.tmpdir") + n + "FXStyle/style.png");
                styleList.add(15, System.getProperty("java.io.tmpdir") + n + "FXStyle/style.cfg");
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
                styleList.set(12, System.getProperty("java.io.tmpdir") + n + "FXStyle/add.png");
                styleList.set(13, System.getProperty("java.io.tmpdir") + n + "FXStyle/delete.png");
                styleList.set(14, System.getProperty("java.io.tmpdir") + n + "FXStyle/delete_all.png");
                styleList.set(15, System.getProperty("java.io.tmpdir") + n + "FXStyle/style.png");
                styleList.set(15, System.getProperty("java.io.tmpdir") + n + "FXStyle/style.cfg");
            }


            //Scelgo il file ZIP
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Scegliere il file zip");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            if (selectedFile==null)
            {
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
            for (String aStyleList1 : styleList) {
                check = new File(aStyleList1);
                if (!check.isFile() || !check.exists()) {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("FXPlayer");
                    alertError.setHeaderText("Skin non è completa!");
                    alertError.setContentText("Controllare il file .zip");
                    alertError.showAndWait();

                    System.out.println("Skin non è completa!");
                    return;
                }
            }

            //Copiamo i file della Skin nella cartella del proggetto
            for (String aStyleList : styleList) {
                check = new File(aStyleList);
                Path file1 = Paths.get(aStyleList);
                Path file2 = Paths.get(System.getProperty("user.dir") + "/src/sample/img/" + check.getName());
                Files.copy(file1, file2, StandardCopyOption.REPLACE_EXISTING);

            }

            //Avviso cambio skin
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("FXPlayer");
            alert.setHeaderText("Skin cambiata con successo!");
            alert.setContentText("Riavviare il programma per i cambiamenti.");
            alert.showAndWait();

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
                            if(loopStatus == 2 &&  position == pl.nSongs()-1)//se siamo in loop di una playlist e siamo arrivati all'ultima canzone//playlistOn && rimosso
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
                                    else
                                    {
                                        if(click)
                                        {
                                            position = clicked;
                                        }
                                    }

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
                if(shuffleOn && stopPressed)
                {
                    grafica.setShuffleOffIcon(shuffleButton);
                    shuffleOn = false;
                    stopPressed = false;
                }

                if(loopStatus != 1)
                    grafica.setPlayIcon(playButton);
            }

            if(loopStatus != 1 && click)//aggiunto
                click = false;

            player.stop();
            elapsedS = 0;
            elapsedM = 0;
            gotSongTime = true;
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
            int oldPosition = position;

            do
            {
                position = rand.nextInt(pl.nSongs());
            }while(position > pl.nSongs()-1 || position == oldPosition);
        }


        private void isClicked()
        {
            if(playList.getItems().size() != 0)
            {
                clicked = playList.getSelectionModel().getSelectedIndex();
                click = true;
            }
        }


        private void assign()
        {
            player = pl.currentSong(position);
            if(wasPlaying)
                this.playSong();

            mooved = true;
            wasPlaying = false;
            stopped = false;
        }

        private void bitrateCalc(int m, int s){
            File f = new File(path);
            int bitrate = (int)(f.length() * 8 / (m * 60 + s) / 1000);
            bitRateLabel.setText(bitrate + "Kbps");
            System.out.println(player.getRate());
        }
    }