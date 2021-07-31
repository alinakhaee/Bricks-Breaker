package com.company;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Main extends Application {

    Media gameMusic = new Media(new File("gameSong.mp3").toURI().toString());
    Media menuMusic = new Media(new File("menuSong.mp3").toURI().toString());
    Media endingMusic = new Media(new File("endSong.mp3").toURI().toString());
    MediaPlayer gameMP = new MediaPlayer(gameMusic), menuMP = new MediaPlayer(menuMusic), endingMP = new MediaPlayer(endingMusic);
    MediaView mv = new MediaView();
    ImageView playImageView = new ImageView(), logoImageView = new ImageView();
    Scene gameScene, menuScene, endingScene, settingScene, aboutScene;
    Random r = new Random();
    Racket racket;
    Brick[][] bricks = new Brick[4][10];
    Ball ball;
    Text timer = new Text("00"), scoreText = new Text();
    Pane root = new Pane();
    String difficultyString = "Medium";
    Color ballcolor=Color.SADDLEBROWN, racketColor=Color.GOLD, brickColor=Color.GOLD;
    boolean firstclick=true, onGift=false;
    int signX=1, signY=-1, bricksBroken=0, totalScore=0, shom, bricksToWin=40, countdown=0;
    double degree=Math.PI/2;
    Gift gift = new Gift(0);
    AnimationTimer ballMovement ;
    Instant start, finish, giftStart, lastIntersected;
    TranslateTransition ttt = new TranslateTransition(javafx.util.Duration.seconds(0.5));
    ScaleTransition scc = new ScaleTransition(javafx.util.Duration.seconds(1));
    ScaleTransition scc1 = new ScaleTransition(javafx.util.Duration.seconds(1));
    FillTransition ftt = new FillTransition(javafx.util.Duration.seconds(1)), fttt = new FillTransition(javafx.util.Duration.seconds(0.5));

    public void start(Stage stage) {

        //starting scene
        Pane startRoot = new Pane();
        startRoot.setStyle("-fx-background-color: white");
        Rectangle rect = new Rectangle(10, 10, Color.GOLD); rect.setX(200); rect.setY(-10);
        FillTransition tempft = new FillTransition(javafx.util.Duration.seconds(1), rect); tempft.setFromValue(Color.GOLD); tempft.setToValue(Color.GOLD);
        ScaleTransition st = new ScaleTransition(javafx.util.Duration.seconds(4), rect); st.setToY(101);
        ScaleTransition st1 = new ScaleTransition(javafx.util.Duration.seconds(4), rect); st1.setToX(43);
        FillTransition ft = new FillTransition(javafx.util.Duration.seconds(3), rect); ft.setFromValue(Color.GOLD); ft.setToValue(Color.BLACK);
        ImageView view = new ImageView(new Image(new File("aboutButton.png").toURI().toString())); view.setFitHeight(30); view.setFitWidth(30); view.setX(195); view.setY(-100);
        ImageView view1 = new ImageView(new Image(new File("settingButton.png").toURI().toString())); view1.setFitHeight(40); view1.setFitWidth(40); view1.setX(190); view1.setY(-100);
        ImageView view2 = new ImageView(new Image(new File("playLogo.png").toURI().toString())); view2.setFitHeight(150); view2.setFitWidth(150); view2.setX(135); view2.setY(-200);
        TranslateTransition tt = new TranslateTransition(javafx.util.Duration.seconds(2), view); tt.setToX(0); tt.setToY(492);
        TranslateTransition tt1 = new TranslateTransition(javafx.util.Duration.seconds(2), view1); tt1.setToX(0); tt1.setToY(425);
        TranslateTransition tt2 = new TranslateTransition(javafx.util.Duration.seconds(2), view2); tt2.setToX(0); tt2.setToY(335);
        SequentialTransition sq = new SequentialTransition(tempft, st, st1, ft, tt, tt1, tt2);
        sq.setOnFinished(event -> stage.setScene(menuScene));
        startRoot.getChildren().addAll(rect, view, view1, view2);
        sq.play();
        Scene startScene = new Scene(startRoot, 410, 490);
        startScene.setOnMouseClicked(event -> {
            sq.stop();
            stage.setScene(menuScene);
        });

        // ending scene
        Pane endingRoot = new Pane();
        endingScene = new Scene(endingRoot, 420, 500);

        // game scene
        gameScene = new Scene(root, 420, 500);
        racket = new Racket(210-60, 490, 120, 5, racketColor);
        ball = new Ball(ballcolor, racket, 6);
        timer.setFont(new Font("Chiller", 35)); timer.setX(145); timer.setY(25);
        ttt.setToX(0); ttt.setToY(500); scc.setToY(100); scc1.setToX(85); ftt.setToValue(Color.BLACK); scc1.setOnFinished(event -> gameMP.stop());
        fttt.setFromValue(Color.BLACK); fttt.setCycleCount(8); fttt.setAutoReverse(true); ftt.setOnFinished(event -> endingMP.play());
        fttt.setOnFinished(event -> {
            Instant temp=Instant.now();
            while (Duration.between(temp,Instant.now()).toMillis()<1000){}
        });
        SequentialTransition sqq = new SequentialTransition(ttt, scc, scc1, ftt, fttt);
        sqq.setOnFinished(event -> {
            stage.setScene(endingScene);
            endingMP.setCycleCount(-1);
        });
        ballMovement = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ball.setCenter(ball.getCenterX()+signX*ball.getSpeed()*Math.cos(degree), ball.getCenterY()+signY*ball.getSpeed()*Math.sin(degree));
                if(onGift && Duration.between(giftStart, Instant.now()).getSeconds()>=countdown){
                    timer.setText("" + (gift.time-countdown));
                    countdown++;
                }
                if(onGift && Duration.between(giftStart, Instant.now()).getSeconds() > gift.time ){
                    countdown=0;
                    root.getChildren().remove(timer);
                    gift.endGift(ball, racket, shom, root);
                    onGift = false;
                }
                for(int i=0 ; i<4 ; i++)
                    for(int j=0 ; j<10 ; j++)
                        if(ball.isIntersected(bricks[i][j].getInRectangleForm())) {
                            if(Math.abs(ball.getCenterY()-5-(bricks[i][j].getY()+40))<=7) {
                                signY = 1;
                                ball.setCenter(ball.getCenterX(), ball.getCenterY()+ball.getSpeed());
                            }
                            else if(Math.abs(ball.getCenterX()+5-bricks[i][j].getX())<=7) {
                                signX = -1;
                                ball.setCenter(ball.getCenterX()-ball.getSpeed(), ball.getCenterY());
                            }
                            else if(Math.abs(ball.getCenterX()-5-(bricks[i][j].getX()+40))<=7) {
                                signX = 1;
                                ball.setCenter(ball.getCenterX()+ball.getSpeed(), ball.getCenterY());
                            }
                            else if(Math.abs(ball.getCenterY()+5 - bricks[i][j].getY())<=7) {
                                signY = -1;
                                ball.setCenter(ball.getCenterX(), ball.getCenterY()-ball.getSpeed());
                            }
                            if(Duration.between(lastIntersected, Instant.now()).toMillis() >= 30){
                                bricks[i][j].number--;
                                totalScore += ball.getMark();
                                scoreText.setText("Score : " + totalScore);
                            }
                            if(bricks[i][j].number != 0) bricks[i][j].setNewNumber(root);
                            if(bricks[i][j].number==0) {
                                bricks[i][j].removeFromLayout(root);
                                bricksBroken++;
                                if(bricks[i][j].hasGift() && onGift==false) {
                                    gift = new Gift(r.nextInt(3));
                                    shom = gift.shomare;
                                    gift.applyGift(ball, racket, root, brickColor);
                                    onGift = true;
                                    timer.setFill(brickColor);
                                    giftStart = Instant.now();
                                    root.getChildren().add(timer);
                                }
                                if(bricksBroken == bricksToWin){
                                    Rectangle rectangle = new Rectangle(200, -10, 10, 10);
                                    rectangle.setFill(brickColor);
                                    root.getChildren().add(rectangle);
                                    ttt.setNode(rectangle); scc.setNode(rectangle); scc1.setNode(rectangle); ftt.setShape(rectangle);
                                    ftt.setFromValue(brickColor); fttt.setToValue(brickColor); fttt.setShape(rectangle);
                                    finish = Instant.now();
                                    makeEndingScene(endingRoot, (int)Duration.between(start,finish).getSeconds()/60, (int)Duration.between(start,finish).getSeconds()%60, totalScore, bricksBroken, stage );
                                    sqq.play();
                                    stop();
                                }
                                lastIntersected = Instant.now();
                            }
                        }
                if(ball.isIntersected(racket.getInRectangleForm())) {
                    signY = -1;
                    degree = racket.getDegree(ball);
                    if(degree<Math.PI/2) signX=-1;
                    else if(degree>Math.PI/2){
                        signX=1;
                        degree = Math.PI - degree;
                    }
                }
                if(ball.getCenterX()+5 >= root.getWidth()) signX = -1;
                if(ball.getCenterX()-5 <= 0) {
                    signX = 1;
                    if(degree*180/Math.PI > 75)
                        degree = 70*Math.PI/180;
                }
                if(ball.getCenterY()-5 <= 0) signY = 1;
                if(ball.getCenterY()+5>=500) {
                    finish = Instant.now();
                    Rectangle rectangle = new Rectangle(200, -10, 10, 10);
                    rectangle.setFill(brickColor);
                    root.getChildren().add(rectangle);
                    ftt.setFromValue(brickColor); ttt.setNode(rectangle); scc.setNode(rectangle); scc1.setNode(rectangle); ftt.setShape(rectangle);
                    fttt.setToValue(brickColor); fttt.setShape(rectangle);
                    sqq.play();
                    makeEndingScene(endingRoot, (int)Duration.between(start,finish).getSeconds()/60, (int)Duration.between(start,finish).getSeconds()%60, totalScore, bricksBroken, stage );
                    stop();
                }
                gameScene.setOnKeyPressed(ke ->{
                    if(ke.getCode() == KeyCode.RIGHT && racket.getX()+racket.getWidth()<gameScene.getWidth() )
                        racket.setX(racket.getX()+racket.getSpeed());
                    if(ke.getCode() == KeyCode.LEFT && racket.getX()>0)
                        racket.setX(racket.getX()-racket.getSpeed());
                });
            }
        };

        // about scene
        Pane aboutroot = new Pane();
        aboutroot.setStyle("-fx-background-color: black");
        aboutScene = new Scene(aboutroot, 420, 500);
        Label label = new Label("There are 40 bricks in the scene that you have to break all of them to win");
        label.setLayoutY(80); label.setLayoutX(17); label.setTextFill(Color.GOLD);
        Label label1 = new Label("Each brick is filled with a number inside it");
        label1.setLayoutY(107); label1.setLayoutX(101); label1.setTextFill(Color.GOLD);
        Label label2 = new Label("Which show the number of times you have to hit the brick to remove it");
        label2.setLayoutY(124); label2.setLayoutX(23); label2.setTextFill(Color.GOLD);
        Label label3 = new Label("Maximum time of hitting is 4 and minimum is 1");
        label3.setLayoutY(141); label3.setLayoutX(84); label3.setTextFill(Color.GOLD);
        Label label4 = new Label("White bricks with gold lines are bricks with gift");
        label4.setLayoutY(172); label4.setLayoutX(88); label4.setTextFill(Color.GOLD);
        Label label5 = new Label("You can not have two gifts at the same time");
        label5.setLayoutY(189); label5.setLayoutX(96); label5.setTextFill(Color.GOLD);
        Label label6 = new Label("Gifts are random and there are only 3 kinds of gift");
        label6.setLayoutY(206); label6.setLayoutX(78); label6.setTextFill(Color.GOLD);
        Label label7 = new Label("1.Double Score(20s)     2.Slower Ball(10s)     3.Longer Racket(10s)");
        label7.setLayoutY(223); label7.setLayoutX(39); label7.setTextFill(Color.GOLD);
        Label label8 = new Label("You can select difficulty and color of objects in Settings part");
        label8.setLayoutY(260); label8.setLayoutX(52); label8.setTextFill(Color.GOLD);
        Label label9 = new Label("Difficulty has been divided in three levels");
        label9.setLayoutY(277); label9.setLayoutX(101); label9.setTextFill(Color.GOLD);
        Label label10 = new Label("Game difficulty depends on ball speed");
        label10.setLayoutY(294); label10.setLayoutX(108); label10.setTextFill(Color.GOLD);
        Label label11 = new Label("Developed by : Ali Nakhaee Sharif");
        label11.setLayoutY(421); label11.setLayoutX(120); label11.setTextFill(Color.GOLD);
        Image logoInAbout = new Image(new File("logo.jpg").toURI().toString());
        ImageView aboutView = new ImageView(logoInAbout);
        aboutView.setY(25); aboutView.setX(110); aboutView.setFitHeight(25); aboutView.setFitWidth(200);
        ImageView back = new ImageView(new Image(new File("backButton.png").toURI().toString()));
        back.setLayoutX(190); back.setLayoutY(446); back.setFitHeight(40); back.setFitWidth(40);
        ImageView back2 = new ImageView(new Image(new File("backButton2.jpg").toURI().toString()));
        back2.setLayoutX(135); back2.setLayoutY(446); back2.setFitHeight(40); back2.setFitWidth(150);
        back2.setOnMouseClicked(event -> stage.setScene(menuScene));
        back.setOnMouseEntered(event -> {aboutroot.getChildren().remove(back); aboutroot.getChildren().add(back2);});
        back2.setOnMouseExited(event -> {aboutroot.getChildren().remove(back2); aboutroot.getChildren().add(back);});
        aboutroot.getChildren().addAll(label,label1,label2,label3,label4,label5,label6,label7,label8,label9,label10,label11,back, aboutView);

        //setting scene
        Pane settingroot = new Pane();
        settingScene = new Scene(settingroot, 420, 500);

        //menu scene
        Pane menuRoot = new Pane();
        menuScene = new Scene(menuRoot, 420, 500);
        menuRoot.setStyle("-fx-background-color: black");
        Image playLogo = new Image(new File("playLogo.png").toURI().toString());
        menuMP.play();
        playImageView.setImage(playLogo);
        playImageView.setX(135); playImageView.setY(135); playImageView.setFitHeight(150); playImageView.setFitWidth(150);
        playImageView.setOnMouseClicked(event -> {
            menuMP.stop();
            makeGameScene(root, ballcolor, racketColor, brickColor, stage);
            lastIntersected = Instant.now();
            stage.setScene(gameScene);
        });
        playImageView.setOnMouseEntered(event -> {
            playImageView.setX(123); playImageView.setY(122); playImageView.setFitHeight(175); playImageView.setFitWidth(175);
        });
        playImageView.setOnMouseExited(event -> {
            playImageView.setX(135); playImageView.setY(135); playImageView.setFitWidth(150); playImageView.setFitHeight(150);
        });
        logoImageView.setImage(new Image(new File("logo.jpg").toURI().toString()));
        logoImageView.setX(110); logoImageView.setY(60); logoImageView.setFitWidth(200); logoImageView.setFitHeight(30);
        ImageView settingButton = new ImageView(new Image(new File("settingButton.png").toURI().toString()));
        ImageView settingButton2 = new ImageView(new Image(new File("settingButton2.png").toURI().toString()));
        settingButton.setLayoutX(190); settingButton.setLayoutY(325); settingButton.setFitHeight(40); settingButton.setFitWidth(40);
        settingButton2.setLayoutX(160); settingButton2.setLayoutY(325); settingButton2.setFitHeight(40); settingButton2.setFitWidth(100);
        settingButton2.setOnMouseClicked(event -> {
            makeSettingScene(settingroot, ballcolor, racketColor, brickColor, stage);
            stage.setScene(settingScene);
        });
        settingButton.setOnMouseEntered(event -> {menuRoot.getChildren().remove(settingButton); menuRoot.getChildren().add(settingButton2);});
        settingButton2.setOnMouseExited(event -> {menuRoot.getChildren().remove(settingButton2); menuRoot.getChildren().add(settingButton);});
        ImageView aboutButton = new ImageView(new Image(new File("aboutButton.png").toURI().toString()));
        ImageView aboutButton2 = new ImageView(new Image(new File("aboutButton2.png").toURI().toString()));
        aboutButton.setFitWidth(30); aboutButton.setFitHeight(30); aboutButton.setX(195); aboutButton.setY(392);
        aboutButton2.setFitWidth(77); aboutButton2.setFitHeight(30); aboutButton2.setX(172); aboutButton2.setY(392);
        aboutButton.setOnMouseEntered(event -> {menuRoot.getChildren().remove(aboutButton); menuRoot.getChildren().add(aboutButton2);});
        aboutButton2.setOnMouseExited(event -> {menuRoot.getChildren().remove(aboutButton2); menuRoot.getChildren().add(aboutButton);});
        aboutButton2.setOnMouseClicked(event -> stage.setScene(aboutScene));
        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.seconds(1), logoImageView);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0); fadeOut.setCycleCount(Animation.INDEFINITE); fadeOut.setAutoReverse(true); fadeOut.play();
        menuRoot.getChildren().addAll(logoImageView, playImageView, settingButton, aboutButton);

        stage.setResizable(false);
        stage.setTitle("Bricks Breaker.exe");
        stage.getIcons().add(new Image(new File("icon.png").toURI().toString()));
        stage.setScene(startScene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }

    public void makeGameScene(Pane pane, Color ballc, Color racketc, Color brickc, Stage stage){
        pane.getChildren().clear();
        pane.setStyle("-fx-background-color: black");
        racket = new Racket(  210-60, 485, 120, 5, racketc);
        racket.addToLayout(pane);
        scoreText.setText("Score : 00");
        scoreText.setFont(new Font("Haettenschweiler", 32)); scoreText.setFill(brickc); scoreText.setX(260); scoreText.setY(25);
        pane.getChildren().addAll(mv, scoreText);
        pane.setMaxHeight(500);
        pane.setMaxWidth(420);
        gameScene.setRoot(pane);
        for(int i=0 ; i<4 ; i++)
            for(int j=0 ; j<10 ; j++){
                bricks[i][j] = new Brick(j*40+10, i*40+30, brickc);
                bricks[i][j].addToLayout(pane);
            }
        ball = new Ball(ballc, racket, ball.getSpeed());
        ball.addToLayout(pane);
        Button startButton = new Button("Start The Game");
        startButton.setLayoutX(151); startButton.setLayoutY(259); startButton.setPrefWidth(123); startButton.setPrefHeight(25);
        startButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );"
        );
        startButton.setOnMouseEntered(event -> startButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" + "-fx-background-radius: 10;"));
        startButton.setOnMouseExited(event -> startButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );"));
        Button remakeButton = new Button("Remake");
        remakeButton.setLayoutX(164); remakeButton.setLayoutY(302); remakeButton.setPrefWidth(97); remakeButton.setPrefHeight(25);
        remakeButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );" +
                "-fx-font-weight: bold"
        );
        remakeButton.setOnMouseEntered(event -> remakeButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" + "-fx-background-radius: 10;" + "-fx-font-weight: bold"));
        remakeButton.setOnMouseExited(event -> remakeButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );" +
                "-fx-font-weight: bold"));
        Button backButton = new Button("Back to Menu");
        backButton.setLayoutX(175); backButton.setLayoutY(343); backButton.setPrefWidth(76); backButton.setPrefHeight(25);
        backButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );" +
                "-fx-font-size: 10px"
        );
        backButton.setOnMouseEntered(event -> backButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" + "-fx-background-radius: 10;" + "-fx-font-size: 10px"));
        backButton.setOnMouseExited(event -> backButton.setStyle("-fx-background-color: " + getWeb(brickc) + ";" +
                "-fx-background-radius: 10;" +
                "-fx-effect:  dropshadow( gaussian, " + getWeb(brickc.darker()) + " , 0,0,0,7 );" +
                "-fx-font-size: 10px"));
        Button pauseButton = new Button(); pauseButton.setLayoutX(396); pauseButton.setLayoutY(4);
        pauseButton.setShape(new Circle(10)); pauseButton.setMaxSize(20, 20); pauseButton.setMinSize(20, 20);
        pauseButton.setStyle("-fx-background-color: "+ getWeb(brickc));
        pauseButton.setOnMouseEntered(event -> pauseButton.setStyle("-fx-background-color: " + getWeb(brickColor.darker().darker())));
        pauseButton.setOnMouseExited(event -> pauseButton.setStyle("-fx-background-color: " + getWeb(brickColor)));
        Button playButton = new Button(); playButton.setLayoutX(396); playButton.setLayoutY(4);
        playButton.setShape(new Circle(10)); playButton.setMaxSize(20, 20); playButton.setMinSize(20, 20);
        playButton.setStyle("-fx-background-color: "+ getWeb(brickc));
        playButton.setOnMouseEntered(event -> playButton.setStyle("-fx-background-color: " + getWeb(brickc.darker().darker())));
        playButton.setOnMouseExited(event -> playButton.setStyle("-fx-background-color: " + getWeb(brickc)));
        ImageView pause = new ImageView(new Image(new File("pause.png").toURI().toString())); pause.setFitWidth(20); pause.setFitHeight(20);
        ImageView play = new ImageView(new Image(new File("play.png").toURI().toString())); play.setFitWidth(8); play.setFitHeight(8);
        pauseButton.setDisable(true); pauseButton.setGraphic(pause); playButton.setGraphic(play);
        startButton.setOnMouseClicked(event -> {
                start = Instant.now();
                gameMP.setCycleCount(-1);
                gameMP.play();
                ballMovement.start();
                pauseButton.setDisable(false);
                pane.getChildren().removeAll(startButton, remakeButton, backButton);
            }
        );
        pauseButton.setOnMouseClicked(event -> {
            pane.getChildren().remove(pauseButton);
            pane.getChildren().add(playButton);
            ballMovement.stop();
            gameMP.pause();
        });
        playButton.setOnMouseClicked(event -> {
            gameMP.play();
            pane.getChildren().remove(playButton);
            pane.getChildren().add(pauseButton);
            ballMovement.start();
        });
        startButton.setTextFill(Color.BLACK); remakeButton.setTextFill(Color.BLACK); backButton.setTextFill(Color.BLACK);
        remakeButton.setOnMouseClicked(event -> makeGameScene(pane, ballc, racketc, brickc, stage));
        backButton.setOnMouseClicked(event -> {gameMP.stop(); menuMP.play(); stage.setScene(menuScene);});
        pane.getChildren().addAll(startButton, remakeButton, backButton, pauseButton);
        racket.setMark(1);
        ftt.setFromValue(brickc);
        timer.setFill(ballc);
        onGift=false; firstclick=true; totalScore=0; bricksBroken=0; signX=0; signY=0; countdown=0;
    }

    public void makeSettingScene(Pane pane, Color ballc, Color racketc, Color brickc, Stage stage){
        pane.getChildren().clear();
        pane.setStyle("-fx-background-color: black");
        pane.setMaxWidth(420);
        pane.setMaxHeight(500);
        settingScene.setRoot(pane);
        ChoiceBox<String> difficultyBox = new ChoiceBox<>();
        difficultyBox.getItems().addAll("Easy","Medium","Hard");
        difficultyBox.setValue(difficultyString);
        difficultyBox.getStylesheets().add("file:/D:/JAVA/break/choiceBox.css");
        Label difficultyText = new Label("Difficulty");
        difficultyText.setTextFill(Color.GOLD); difficultyText.setLayoutX(188); difficultyText.setLayoutY(133);
        difficultyBox.setLayoutX(160); difficultyBox.setLayoutY(155); difficultyBox.setPrefWidth(100);
        difficultyBox.setStyle("-fx-background-color: gold;");
        difficultyBox.setOnMouseEntered(event -> difficultyBox.setStyle("-fx-background-color: #5aff55"));
        difficultyBox.setOnMouseExited(event -> difficultyBox.setStyle("-fx-background-color: gold"));
        Label ballColorText = new Label("Ball Color");
        ballColorText.setTextFill(Color.GOLD); ballColorText.setLayoutX(45); ballColorText.setLayoutY(229);
        ColorPicker colorPickerBall = new ColorPicker(ballc);
        colorPickerBall.setLayoutX(14); colorPickerBall.setLayoutY(250); colorPickerBall.setStyle("-fx-background-color: gold"); colorPickerBall.setMaxWidth(100);
        Label racketColorText = new Label("Racket Color");
        racketColorText.setTextFill(Color.GOLD); racketColorText.setLayoutX(179); racketColorText.setLayoutY(229);
        ColorPicker colorPickerRacket = new ColorPicker(racketc);
        colorPickerRacket.setLayoutX(159); colorPickerRacket.setLayoutY(250); colorPickerRacket.setStyle("-fx-background-color: gold"); colorPickerRacket.setMaxWidth(100);
        Label brickColorText = new Label("Brick Color");
        brickColorText.setTextFill(Color.GOLD); brickColorText.setLayoutX(320); brickColorText.setLayoutY(229);
        ColorPicker colorPickerBrick = new ColorPicker(brickc);
        colorPickerBrick.setLayoutX(300); colorPickerBrick.setLayoutY(250); colorPickerBrick.setStyle("-fx-background-color: gold"); colorPickerBrick.setMaxWidth(100);
        pane.getChildren().addAll(difficultyBox, colorPickerBall, colorPickerBrick, colorPickerRacket, difficultyText, ballColorText, racketColorText, brickColorText);
        Circle exampleBall = new Circle(74,394,5, ballc);
        Rectangle exampleRacket = new Rectangle(14, 435, 120, 5);
        exampleRacket.setFill(racketc);
        Rectangle exampleBrick = new Rectangle(54, 317, 40, 40);
        exampleBrick.setFill(brickc);
        colorPickerBall.setOnAction(event -> exampleBall.setFill(colorPickerBall.getValue()));
        colorPickerBall.setOnMouseEntered(event -> colorPickerBall.setStyle("-fx-background-color: #5aff55"));
        colorPickerBall.setOnMouseExited(event -> colorPickerBall.setStyle("-fx-background-color: gold"));
        colorPickerBrick.setOnAction(event -> exampleBrick.setFill(colorPickerBrick.getValue()));
        colorPickerBrick.setOnMouseEntered(event -> colorPickerBrick.setStyle("-fx-background-color: #5aff55"));
        colorPickerBrick.setOnMouseExited(event -> colorPickerBrick.setStyle("-fx-background-color: gold"));
        colorPickerRacket.setOnAction(event -> exampleRacket.setFill(colorPickerRacket.getValue()));
        colorPickerRacket.setOnMouseEntered(event -> colorPickerRacket.setStyle("-fx-background-color: #5aff55"));
        colorPickerRacket.setOnMouseExited(event -> colorPickerRacket.setStyle("-fx-background-color: gold"));
        Label exampleLabel = new Label("Example of Gameplay");
        exampleLabel.setTextFill(Color.GOLD); exampleLabel.setLayoutX(17); exampleLabel.setLayoutY(458);
        TextField textField = new TextField(""+bricksToWin);
        textField.setLayoutX(185); textField.setLayoutY(325); textField.setStyle("-fx-background-color: gold"); textField.setMaxWidth(50);
        ImageView submitButton = new ImageView(new Image(new File("submitButton.png").toURI().toString()));
        submitButton.setLayoutX(367); submitButton.setLayoutY(407); submitButton.setFitWidth(30); submitButton.setFitHeight(30);
        ImageView submitButton2 = new ImageView(new Image(new File("submitButton2.jpg").toURI().toString()));
        submitButton2.setLayoutX(317); submitButton2.setLayoutY(407); submitButton2.setFitWidth(100); submitButton2.setFitHeight(30);
        Label bricksTowinLabel = new Label("Bricks To Win");
        bricksTowinLabel.setLayoutX(175); bricksTowinLabel.setLayoutY(305); bricksTowinLabel.setTextFill(Color.GOLD);
        submitButton2.setOnMouseClicked(event -> {
            Text submitText = new Text(357, 394, "Submited");
            submitText.setFill(Color.GOLD);
            pane.getChildren().add(submitText);
            ballcolor = colorPickerBall.getValue();
            racketColor = colorPickerRacket.getValue();
            brickColor = colorPickerBrick.getValue();
            difficultyString = difficultyBox.getValue();
            if(difficultyString.equals("Medium")) ball.setSpeed(6);
            else if(difficultyString.equals("Easy")) ball.setSpeed(5);
            else if(difficultyString.equals("Hard"))ball.setSpeed(8);
            if(textField.getText().isEmpty()){
                textField.setStyle("-fx-background-color: red");
                textField.setText(""+bricksToWin);
            }
            else if(Integer.parseInt(textField.getText()) <= 40 && Integer.parseInt(textField.getText()) > 0) {
                bricksToWin = Integer.parseInt(textField.getText());
                textField.setStyle("-fx-background-color: gold");
            }
            else {
                textField.setStyle("-fx-background-color: red");
                textField.setText(""+bricksToWin);
            }
        });
        submitButton.setOnMouseEntered(event -> {pane.getChildren().remove(submitButton); pane.getChildren().add(submitButton2);});
        submitButton2.setOnMouseExited(event -> {pane.getChildren().remove(submitButton2); pane.getChildren().add(submitButton);});
        ImageView back = new ImageView(new Image(new File("backButton.png").toURI().toString()));
        back.setLayoutX(362); back.setLayoutY(446); back.setFitHeight(40); back.setFitWidth(40);
        ImageView back2 = new ImageView(new Image(new File("backButton2.jpg").toURI().toString()));
        back2.setLayoutX(267); back2.setLayoutY(446); back2.setFitHeight(40); back2.setFitWidth(150);
        back2.setOnMouseClicked(event -> stage.setScene(menuScene));
        back.setOnMouseEntered(event -> {pane.getChildren().remove(back); pane.getChildren().add(back2);});
        back2.setOnMouseExited(event -> {pane.getChildren().remove(back2); pane.getChildren().add(back);});
        Image logoInSettings = new Image(new File("logo.jpg").toURI().toString());
        ImageView settingsView = new ImageView(logoInSettings);
        settingsView.setFitWidth(200); settingsView.setFitHeight(25); settingsView.setX(109); settingsView.setY(14);
        Label line1 = new Label("After you choose difficulty and color");
        line1.setTextFill(Color.GOLD); line1.setLayoutY(66); line1.setLayoutX(114);
        Label line2 = new Label("Don't forget to click submit");
        line2.setTextFill(Color.GOLD); line2.setLayoutX(137); line2.setLayoutY(83);
        pane.getChildren().addAll(exampleBall, exampleBrick, exampleRacket, exampleLabel, submitButton, back, settingsView, line1, line2, textField, bricksTowinLabel);
    }

    public void makeEndingScene(Pane pane, int minute, int seconds, int score, int bricksb, Stage stage){
        pane.getChildren().clear();
        pane.setStyle("-fx-background-color: black");
        Image youWin = new Image(new File("youWin.png").toURI().toString());
        Image youLose = new Image(new File("youLose.png").toURI().toString());
        ImageView youWinView = new ImageView(youWin); youWinView.setY(85); youWinView.setX(74); youWinView.setFitHeight(252); youWinView.setFitWidth(272);
        ImageView youLoseView = new ImageView(youLose); youLoseView.setFitWidth(180); youLoseView.setFitHeight(150); youLoseView.setX(120); youLoseView.setY(100);
        Label endingLogoView = new Label("Bricks Breaker"); endingLogoView.setFont(new Font("Broadway", 30));
        endingLogoView.setLayoutX(85); endingLogoView.setLayoutY(29); endingLogoView.setTextFill(brickColor);
        if(bricksBroken!=bricksToWin)
            pane.getChildren().addAll(youLoseView, endingLogoView);
        else pane.getChildren().addAll(youWinView, endingLogoView);
        Label text = new Label("Time : " + minute + " Minute(s) & " + seconds + " second(s)");
        Label text1 = new Label("Total Score : " + score);
        Label text2 = new Label("Bricks Broken : " + bricksb);
        text.setTextFill(brickColor); text.setLayoutY(296); text.setLayoutX(121);
        text1.setTextFill(brickColor); text1.setLayoutY(319); text1.setLayoutX(169);
        text2.setTextFill(brickColor); text2.setLayoutY(344); text2.setLayoutX(163);
        Button goback = new Button(); goback.setShape(new Circle(24.5)); goback.setMaxSize(49, 49); goback.setMinSize(49,49);
        ImageView back = new ImageView(new Image(new File("back.png").toURI().toString())); back.setFitHeight(40); back.setFitWidth(40);
        goback.setGraphic(back); goback.setLayoutX(184); goback.setLayoutY(423); goback.setStyle("-fx-background-color: " + getWeb(brickColor));
        goback.setOnMouseClicked(event -> {
            endingMP.stop();
            menuMP.play();
            stage.setScene(menuScene);
        });
        goback.setOnMouseEntered(event -> goback.setStyle("-fx-background-color: " + getWeb(brickColor.darker().darker())));
        goback.setOnMouseExited(event -> goback.setStyle("-fx-background-color: " + getWeb(brickColor)));
        ImageView restart = new ImageView(new Image(new File("restart.png").toURI().toString())); restart.setFitWidth(25); restart.setFitHeight(25);
        Button restart2 = new Button(); restart2.setGraphic(restart); restart2.setStyle("-fx-background-color: " + getWeb(brickColor)); restart2.setLayoutX(188);
        restart2.setShape(new Circle(20)); restart2.setMinSize(40, 40); restart2.setMinSize(40,40); restart2.setLayoutY(377);
        restart2.setOnMouseClicked(event -> {
            endingMP.stop();
            makeGameScene(root, ballcolor, racketColor, brickColor, stage);
            stage.setScene(gameScene);
        });
        restart2.setOnMouseEntered(event -> restart2.setStyle("-fx-background-color: " + getWeb(brickColor.darker().darker())));
        restart2.setOnMouseExited(event -> restart2.setStyle("-fx-background-color: " + getWeb(brickColor)));
        pane.getChildren().addAll(text, text1, text2, goback, restart2);
    }

    public String getWeb(Color paint){
        String red = Integer.toHexString((int)(paint.getRed()*255));
        String green = Integer.toHexString((int)(paint.getGreen()*255));
        String blue = Integer.toHexString((int)(paint.getBlue()*255));
        if (red.equals("0")) red = "00";
        if (green.equals("0")) green = "00";
        if (blue.equals("0")) blue = "00";
        return "#" + red + green + blue;
    }
}