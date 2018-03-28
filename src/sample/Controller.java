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
    public Button shuffleButton = new Button();
    public Button repeatButton = new Button();
    public Label artistLabel = new Label();
    public Label titleLabel = new Label();
    public Label albumLabel = new Label();
    public Label genreLabel = new Label();
    public Label yearLabel = new Label();
    public Label totalTimeLabel = new Label();
    public Label sampleRateLabel = new Label();
    public Label bitrateLabel = new Label();
    public Label elapsedTimeLabel = new Label();
    public ImageView trackImage = new ImageView();
    public Slider timeSlider = new Slider();
    public Slider volumeSlider = new Slider();
    public ListView playList = new ListView();

    UI grafica = new UI();


    //Inizializziamo la sorgente audio
    private String path = new String("src/sample/Hero.mp3");
    private Media source = new Media(new File(path).toURI().toString());
    private MediaPlayer player = new MediaPlayer(source);

    //Inizializziamo i parametri per i Tag
    private Mp3File mp3file;
    private ID3v2 id3v2Tag;

    Boolean gotSongTime = true;
    int totalS = 0, totalM = 0, elapsedS = 0, elapsedM = 0;


    public void setPlayButton(ActionEvent event) {
        if (player.getStatus() == MediaPlayer.Status.PLAYING){
            player.pause();
        } else {
            getTrackInfo();
            player.play();
            timeSlider.setMax(player.getTotalDuration().toSeconds());
            setVolume();
            setTrackTime();
            grafica.setPauseIcon(playButton);
        }
    }

    public void setStopButton(ActionEvent event) {
        if (player.getStatus() == MediaPlayer.Status.PLAYING || player.getStatus() == MediaPlayer.Status.PAUSED){
            player.stop();
            player.setStartTime(javafx.util.Duration.seconds(0));
            timeSlider.setValue(0);
            gotSongTime = true;
            elapsedS = 0;
            elapsedM = 0;
        }
    }

    public void getTrackInfo() {

        //Calcoliamo la durata della canzone (timeM : timeS)
        //DA RIFARE CON LA PLAYLIST
        if (gotSongTime){
            totalS = (int)player.getTotalDuration().toSeconds();
            totalM = totalS / 60;
            totalS -= totalM * 60;
            gotSongTime=false;

            if (totalS > 9) {
                totalTimeLabel.setText(totalM + ":" + totalS);
            } else {
                totalTimeLabel.setText(totalM + ":0" + totalS);
            }
        }

        //Apriamo il file audio e leggiamo i tag
        try {
            mp3file = new Mp3File(path);
        } catch (IOException ie){
            ie.printStackTrace();
        } catch (UnsupportedTagException ut){
            ut.printStackTrace();
        } catch (InvalidDataException id){
            id.printStackTrace();
        }
        id3v2Tag = mp3file.getId3v2Tag();

        //Estraiamo la cover dalla traccia
        byte[] imageData = id3v2Tag.getAlbumImage();
        if (imageData != null) {
            RandomAccessFile file;
            try {
                file = new RandomAccessFile("src/sample/data/albumcover.jpg", "rw");
                file.write(imageData);
                file.close();
                FileInputStream imgSrc = new FileInputStream("src/sample/data/albumcover.jpg");
                Image img = new Image(imgSrc);
                trackImage.setImage(img);
            } catch (IOException e){
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

    public void setVolume(){
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (volumeSlider.isValueChanging()){
                    player.setVolume(volumeSlider.getValue()/100);
                }
            }
        });
    }

    public void setBalance(){
        volumeSlider.setOnDragExited((Event) -> {
            //Da fare dopo la UI
        });
    }

    public void setTrackTime(){
        timeSlider.setOnMouseReleased((MouseEvent) -> {
            player.stop();
            player.setStartTime(javafx.util.Duration.seconds(timeSlider.getValue()));
            System.out.println(player.getTotalDuration().toSeconds() + " " + javafx.util.Duration.seconds(timeSlider.getValue()));
            player.play();
        });

       player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if(!timeSlider.isValueChanging()){
                timeSlider.setValue(newTime.toSeconds());
                elapsedS = (int)player.getCurrentTime().toSeconds();
                elapsedM = elapsedS / 60;
                elapsedS -= elapsedM * 60;

                if (elapsedS > 9) {
                    elapsedTimeLabel.setText(elapsedM + ":" + elapsedS);
                } else {
                    elapsedTimeLabel.setText(elapsedM + ":0" + elapsedS);
                }
            }
        });
    }

}