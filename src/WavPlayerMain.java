import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;

import javax.media.Controller;
import javax.media.Manager;
import javax.media.Player;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.file.Paths;



public class WavPlayerMain extends Application{

    MP3Controller controller = new MP3Controller();
    static FileSelector selector = new FileSelector();
    MP3Converter mp3ToWavConverter = new MP3Converter();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage musicStage) {

        //SET THE STAGE
        musicStage.setTitle(".WavPlayer Music Player");
        musicStage.initStyle(StageStyle.TRANSPARENT);

        //CONTAINERS FOR ENTIRE FRONT PANEL LAYOUT
        VBox songTimer = new VBox();
        VBox buttonInterFace = new VBox();
        HBox cornerButtons = new HBox();
        HBox timerBox = new HBox();
        VBox container = new VBox();
        BorderPane frontPanel = new BorderPane();
        Label startTime = new Label();
        Label endTime = new Label();


        //******* PLAY/PAUSE BUTTON *******//
        Button playPauseButton = new Button();
        Image playImage = new Image(getClass().getResourceAsStream("play.png"));
        ImageView playImageView = new ImageView(playImage);
        playImageView.setFitHeight(50.0);
        playImageView.setFitWidth(50.0);
        playPauseButton.setGraphic(playImageView);

        //SPACE TO CONTAIN THE PLAY.PNG / PAUSE.PNG IMAGES FOR BUTTON
        Circle playCircle = new Circle();
        playCircle.setRadius(50);
        playCircle.maxHeight(40);
        playCircle.maxWidth(40);

        playPauseButton.setMaxHeight(40);
        playPauseButton.setMaxWidth(40);
        playPauseButton.setShape(playCircle);
        playPauseButton.setStyle("-fx-background-color: #0a0a0a");
        playPauseButton.setTextFill(Color.WHITE);

        //PLAY/PAUSE BUTTON LISTENER
        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent playClicked) {
                if (controller.mp3Player.getSongPlayer() != null) {//IF THERE IS A LOADED SONG, PLAYER WILL NOT BE NULL.
                    if ((controller.mp3Player.getSongPlayer().getState() != Controller.Started)) {
                        controller.play();
                        Image pauseView = new Image(getClass().getResourceAsStream("pause.png"));
                        ImageView pauseImageView = new ImageView(pauseView);
                        pauseImageView.setFitHeight(50.0);
                        pauseImageView.setFitWidth(50.0);
                        playPauseButton.setGraphic(pauseImageView);
                    }
                    else if (controller.mp3Player.getSongPlayer().getState() == Controller.Started) {//IF PLAYER IS IN STARTED STATE, RE-CLICK OF PLAY BUTTON WILL PAUSE OPERATION.
                        controller.pause();

                        Image resumeView = new Image(getClass().getResourceAsStream("play.png"));
                        ImageView resumeImageView = new ImageView(resumeView);
                        resumeImageView.setFitHeight(50.0);
                        resumeImageView.setFitWidth(50.0);
                        playPauseButton.setGraphic(resumeImageView);
                    }
                } else {//IF SONG IS NOT PICKED, POST ALERT THAT USER MUST PICK A SONG FIRST.
                    Alert noSongWarning = new Alert(Alert.AlertType.WARNING);
                    noSongWarning.setTitle("I Need a Song");
                    noSongWarning.setContentText("Please select a song.");
                    noSongWarning.showAndWait();
                }
            }
        });



        //*******FILE BUTTON*******//
        Button fileButton = new Button();
        fileButton.setText("Select Song");
        fileButton.setStyle("-fx-background-color: linear-gradient(#4d4d4e,#0a0a0a)");
        fileButton.setTextFill(Color.WHITE);

        //FILE BUTTON LISTENER
        fileButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent fileAccess) {
                if ((controller.mp3Player.getSongPlayer() != null) && (controller.mp3Player.getSongPlayer().getState() == Player.Started)) {
                    controller.pause();

                    Image resumeView = new Image(getClass().getResourceAsStream("play.png"));
                    ImageView resumeImageView = new ImageView(resumeView);
                    resumeImageView.setFitHeight(50.0);
                    resumeImageView.setFitWidth(50.0);
                    playPauseButton.setGraphic(resumeImageView);
                }
                selector.importFinished = false;
                while (!selector.importFinished) {
                    selector.findFile();
                }
                if (selector.songPath != null) {
                    try {
                        if (selector.songPath.endsWith("wav")) {
                            controller.mp3Player.setSongFile(new File(selector.songPath));
                        }

                        if (selector.songPath.endsWith("mp3")) {
                            mp3ToWavConverter.convertToWav(selector.inputFiles[0]);
                            selector.songPath = mp3ToWavConverter.convertedWav.getAbsolutePath();
                            controller.mp3Player.setSongFile(new File(selector.songPath));
                        }


                        controller.mp3Player.setSongData(AudioSystem.getAudioFileFormat(controller.mp3Player.getSongFile()));


                        controller.mp3Player.setSongFrames(controller.mp3Player.getSongData().getFrameLength());
                        controller.mp3Player.setFrameSpeed(controller.mp3Player.getSongData().getFormat().getFrameRate());

                        controller.mp3Player.setSongPath(Paths.get(selector.songPath));
                        controller.mp3Player.setSongURL(controller.mp3Player.getSongPath().toUri().toURL());

                        if (controller.mp3Player.getSongPlayer() != null) {
                            controller.mp3Player.getSongPlayer().close();
                            controller.mp3Player.resetPlayer();
                            controller.mp3Player.getTimer().setText(String.format("0:00/%d:%02d", controller.mp3Player.getSongTime() / 60, controller.mp3Player.getSongTime() - (controller.mp3Player.getSongTime() / 60) * 60));
                            controller.mp3Player.getTimeSlider().setValue(0);
                        }

                        controller.mp3Player.setSongPlayer(Manager.createRealizedPlayer(controller.mp3Player.getSongURL()));

                        controller.mp3Player.setSongTime(((int) (controller.mp3Player.getSongFrames() / controller.mp3Player.getFrameSpeed())));

                        controller.mp3Player.getTimeSlider().setMax((int) controller.mp3Player.getSongTime());
                        endTime.setText(String.format("%d:%02d", controller.mp3Player.getSongTime() / 60, controller.mp3Player.getSongTime() - (controller.mp3Player.getSongTime() / 60) * 60));

                        selector.importFinished = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });



        //Formats the box that holds the play button and file access button. They will be put in a horizontal order.
        buttonInterFace.setAlignment(Pos.BASELINE_CENTER);
        buttonInterFace.setPadding(new Insets(0,20,0,20));
        buttonInterFace.setSpacing(30);
        buttonInterFace.getChildren().addAll(playPauseButton,fileButton);


        //*******MINIMIZE BUTTON*******//
        Button minimizeButton = new Button();
        minimizeButton.setText("-");
        minimizeButton.setMinHeight(25);
        minimizeButton.setMinWidth(50);
        minimizeButton.setStyle("-fx-background-color: #4d4d4e");
        minimizeButton.setTextFill(Color.WHITE);

        //MINIMIZE BUTTON LISTENER
        minimizeButton.setOnAction(new EventHandler<ActionEvent>() {
           public void handle(ActionEvent minimizeWindow) {
               musicStage.setIconified(true);
           }
        });


        //*******FULL SCREEN BUTTON*******//
        Button fullScreenButton = new Button();
        fullScreenButton.setText("▭");
        fullScreenButton.setMinHeight(25);
        fullScreenButton.setMinWidth(50);
        fullScreenButton.setTextFill(Color.WHITE);
        fullScreenButton.setStyle("-fx-background-color: #4d4d4e");

        //FULL SCREEN BUTTON LISTENER//
        fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent fullScreen) {
                if (!musicStage.isMaximized()) {//Case: Screen isn't in maximized state yet.
                    musicStage.setMaximized(true);
                    controller.mp3Player.getTimeSlider().setMinSize(1200,50);
                    buttonInterFace.setPadding(new Insets(490,0,0,0));
                    timerBox.setPadding(new Insets(10,0,10,0));
                } else {//Case: Screen is already maximized.
                    musicStage.setMaximized(false);
                    controller.mp3Player.getTimeSlider().setMinSize(500,50);
                    buttonInterFace.setPadding(new Insets(0,0,0,0));
                    timerBox.setPadding(new Insets(0,0,0,0));
                }
            }
        });


        //*******EXIT BUTTON*******//
        Button exitButton = new Button();
        exitButton.setText("x");
        exitButton.setMinHeight(20);
        exitButton.setMinWidth(50);
        exitButton.setStyle("-fx-background-color: #ff0000");
        exitButton.setTextFill(Color.WHITE);

        //EXIT BUTTON LISTENER//
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent exitClicked) {
                musicStage.hide();//Close musicStage view.
                if (controller.mp3Player.getSongPlayer() != null) {//Assuming song was selected, so there is a realized player present.
                    controller.mp3Player.getSongPlayer().stop();//Closes the realized player.
                }
                System.exit(0);//Clean exit.
            }
        });


        //This VBox songTimer contains the timerBox with the slider and labels of startTime and endTime as well as the timer box that holds the song time.
        songTimer.setAlignment(Pos.BASELINE_CENTER);
        songTimer.setPadding(new Insets(5,20,5,20));
        songTimer.setSpacing(15);

        startTime.setText("0:00");
        startTime.setTextFill(Color.WHITE);

        endTime.setText(String.format("%d:%02d",controller.mp3Player.getSongTime()/60,controller.mp3Player.getSongTime()-(controller.mp3Player.getSongTime()/60)*60));
        endTime.setTextFill(Color.WHITE);

        //This timer box will hold the current song time progression.
        TextArea timer = controller.mp3Player.getTimer();
        timer.setMinHeight(24);
        timer.setMaxWidth(70);

        timerBox.setAlignment(Pos.BASELINE_CENTER);
        timerBox.getChildren().addAll(startTime,controller.mp3Player.getTimeSlider(),endTime);

        //Formats panel holding the custom minimize, fullscreen, and close buttons on the top right.
        cornerButtons.setAlignment(Pos.TOP_RIGHT);
        cornerButtons.setMaxHeight(30);
        cornerButtons.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.NONE,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        cornerButtons.setStyle("-fx-background-color: #4d4d4e");
        cornerButtons.getChildren().addAll(minimizeButton,fullScreenButton,exitButton);

        //Add the slider and labels along with the song timer text bos to the songTimer VBox. They will be put in vertical order.
        songTimer.getChildren().addAll(timer,timerBox);
        songTimer.setPadding(new Insets(0,20,0,20));

        controller.mp3Player.getTimer().setText(String.format("0:00/%d:%02d",controller.mp3Player.getSongTime()/60,controller.mp3Player.getSongTime()-(controller.mp3Player.getSongTime()/60)*60));


        Label playerTitle = new Label(".WavPlayer");
        playerTitle.setMinHeight(50);
        playerTitle.setMinWidth(150);
        playerTitle.setAlignment(Pos.BASELINE_RIGHT);
        playerTitle.setFont(Font.font("Broadway",40));

        HBox titleHolder = new HBox();
        titleHolder.setAlignment(Pos.BASELINE_CENTER);
        titleHolder.getChildren().add(playerTitle);

        //Container holds both the songTimer box and buttonInterface box. They will be ordered vertically.
        container.getChildren().addAll(titleHolder,buttonInterFace, songTimer);

        //This BorderPane is what is seen on the application. The top section holds the corner buttons and the center holds the content play and access buttons as well as the timer and slider.
        frontPanel.setTop(cornerButtons);
        frontPanel.setCenter(container);
        frontPanel.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID, CornerRadii.EMPTY,BorderWidths.DEFAULT)));
        frontPanel.setStyle("-fx-background-color: linear-gradient(#afafaf,#000000)");

        Scene mainScene = new Scene(frontPanel,720,350);


        musicStage.setScene(mainScene);
        musicStage.show();


    }

}
