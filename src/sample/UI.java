    package sample;

    import javafx.scene.control.Label;
    import javafx.scene.control.ToggleButton;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.control.Button;
    import javafx.scene.layout.*;
    import javafx.scene.text.Font;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.util.List;

    class UI {

        //Images
        private Image pauseImg = new Image(getClass().getResourceAsStream("/sample/img/pause.png"));
        private Image playImg = new Image(getClass().getResourceAsStream("/sample/img/play.png"));
        private Image stopImg = new Image(getClass().getResourceAsStream("/sample/img/stop.png"));
        private Image backwardImg = new Image(getClass().getResourceAsStream("/sample/img/previous.png"));
        private Image forwardImg = new Image(getClass().getResourceAsStream("/sample/img/next.png"));
        private Image shuffleOffImg = new Image(getClass().getResourceAsStream("/sample/img/shuffle_off.png"));
        private Image shuffleOnImg = new Image(getClass().getResourceAsStream("/sample/img/shuffle_on.png"));
        private Image repeatOffImg = new Image(getClass().getResourceAsStream("/sample/img/repeat_off.png"));
        private Image repeatOneImg = new Image(getClass().getResourceAsStream("/sample/img/repeat_1.png"));
        private Image repeatAllImg = new Image(getClass().getResourceAsStream("/sample/img/repeat_all.png"));
        private Image albumImage = new Image(getClass().getResourceAsStream("/sample/img/defaultcover.jpg"));
        private Image addImage = new Image(getClass().getResourceAsStream("/sample/img/add.png"));
        private Image deleteImage = new Image(getClass().getResourceAsStream("/sample/img/delete.png"));
        private Image deleteAllImage = new Image(getClass().getResourceAsStream("/sample/img/delete_all.png"));
        private Image style = new Image(getClass().getResourceAsStream("/sample/img/style.png"));

        //Player Background
        private Image image = new Image(getClass().getResourceAsStream("/sample/img/background.jpg"));
        private BackgroundSize backgroundSize = new BackgroundSize(400, 200, true, true, true, false);
        private BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        private Background background = new Background(backgroundImage);

        //ImageViews
        private ImageView pauseView = new ImageView(pauseImg);
        private ImageView playView = new ImageView(playImg);
        private ImageView stopView = new ImageView(stopImg);
        private ImageView backwardView = new ImageView(backwardImg);
        private ImageView forwardView = new ImageView(forwardImg);
        private ImageView shuffleOffView = new ImageView(shuffleOffImg);
        private ImageView shuffleOnView = new ImageView(shuffleOnImg);
        private ImageView repeatOffView = new ImageView(repeatOffImg);
        private ImageView repeatOneView = new ImageView(repeatOneImg);
        private ImageView repeatAllView = new ImageView(repeatAllImg);
        private ImageView addImageView = new ImageView(addImage);
        private ImageView deleteImageView = new ImageView(deleteImage);
        private ImageView deleteAllView = new ImageView(deleteAllImage);
        private ImageView styleView = new ImageView(style);

        private List<String> config;

        {
            try {
                config = Files.readAllLines(new File(System.getProperty("user.dir") + "/src/sample/img/style.cfg").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String fontType = config.get(0);
        private Integer fontSize = Integer.valueOf(config.get(1));
        private String playlistColor = config.get(2);

        void setPlayIcon(Button playButton){
            playButton.setGraphic(playView);
        }

        void setPauseIcon(Button pauseButton){
            pauseButton.setGraphic(pauseView);
        }

        void setRepeatOffIcon(Button shuffleButton){
            shuffleButton.setGraphic(repeatOffView);
        }

        void setRepeatOneIcon(Button shuffleButton){
            shuffleButton.setGraphic(repeatOneView);
        }

        void setRepeatAllIcon(Button shuffleButton){
            shuffleButton.setGraphic(repeatAllView);
        }

        void setShuffleOffIcon(ToggleButton shuffleButton){
            shuffleButton.setGraphic(shuffleOffView);
        }

        void setShuffleOnIcon(ToggleButton shuffleButton){
            shuffleButton.setGraphic(shuffleOnView);
        }

        void setTrackImage(ImageView trackImage){
            trackImage.setImage(albumImage);
        }

        void setUI(Button playButton, Button stopButton, Button forwardButton, Button backwardButton, Button repeatButton, ToggleButton shuffleButton, ImageView trackImage, AnchorPane paneControls, Label artistLabel, Label titleLabel, Label albumLabel, Label genreLabel, Label yearLabel, Button addSongButton, Button removeSongButton, Button styleButton, Button removeAllSongsButton, AnchorPane panePlaylist){

            playButton.setGraphic(playView);
            stopButton.setGraphic(stopView);
            backwardButton.setGraphic(backwardView);
            forwardButton.setGraphic(forwardView);
            shuffleButton.setGraphic(shuffleOffView);
            repeatButton.setGraphic(repeatOffView);
            trackImage.setImage(albumImage);
            paneControls.setBackground(background);
            panePlaylist.setStyle("-fx-background-color: #" + playlistColor + ";");
            addSongButton.setGraphic(addImageView);
            removeSongButton.setGraphic(deleteImageView);
            styleButton.setGraphic(styleView);
            removeAllSongsButton.setGraphic(deleteAllView);


            //Setting font
            artistLabel.setFont(new Font(fontType, fontSize));
            titleLabel.setFont(new Font(fontType, fontSize));
            albumLabel.setFont(new Font(fontType, fontSize));
            genreLabel.setFont(new Font(fontType, fontSize));
            yearLabel.setFont(new Font(fontType, fontSize));
        }
    }