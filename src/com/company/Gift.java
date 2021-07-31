package com.company;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Gift {
    int shomare, time;
    Label label = new Label();
    public Gift(int shomare){
        this.shomare = shomare;
        if(this.shomare==0) time=20;
        if(this.shomare==1) time=10;
        if(this.shomare==2) time=10;
    }
    public void applyGift(Ball ball , Racket racket, Pane pane, Color paint){
        if(shomare==0) {
            ball.setMark(2);
            label = new Label("DOUBLE SCORE");
        }
        if(shomare==1) {
            ball.setSpeed(ball.getSpeed()-2) ;
            label = new Label("SLOWER BALL");
        }
        if(shomare==2) {
            racket.setWidth(racket.getWidth() * 2);
            racket.setMark(racket.getMark()*2);
            label = new Label("BIGGER RACKET");
        }
        label.setTextFill(paint);
        label.setFont(new Font("Chiller", 22));
        pane.getChildren().add(label);
    }
    public void endGift(Ball ball, Racket racket, int shomare, Pane pane){
        if(shomare==0)
            ball.setMark(1);
        if(shomare==1)
            ball.setSpeed(ball.getSpeed()+2);
        if(shomare==2) {
            racket.setWidth(racket.getWidth() / 2);
            racket.setMark(racket.getMark()/2);
        }
        pane.getChildren().removeAll(label);
    }
}
