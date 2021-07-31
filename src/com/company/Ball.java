package com.company;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Ball {
    Circle ball = new Circle();
    int speed, mark=1 ;
    public Ball(Color paint, Racket racket, int speed){
        ball.setFill(paint);
        ball.setRadius(5);
        ball.setCenterX(racket.getX()+racket.getWidth()/2);
        ball.setCenterY(racket.getY()-ball.getRadius());
        this.speed = speed;
    }
    public void setCenter(double x, double y){
        ball.setCenterY(y);
        ball.setCenterX(x);
    }
    public void setMark(int mark){ this.mark = mark ;}
    public int getMark(){ return mark;}
    public void setSpeed(int speed){ this.speed = speed;}
    public int getSpeed() { return speed;}
    public int getRadius(){ return (int)ball.getRadius(); }
    public int getCenterX(){ return (int)ball.getCenterX();}
    public int getCenterY(){ return (int)ball.getCenterY();}
    public void addToLayout(Pane pane){pane.getChildren().add(ball);}
    public boolean isIntersected(Rectangle rectangle){
        return ((Path)Shape.intersect(ball, rectangle)).getElements().size()>0 ;
    }
}
