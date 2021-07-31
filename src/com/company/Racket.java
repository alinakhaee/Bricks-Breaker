package com.company;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Racket {
    Rectangle racket = new Rectangle();
    int mark = 1, speed=20;
    public Racket(int x, int y, int width, int height, Color paint){
        racket.setX(x);
        racket.setY(y);
        racket.setWidth(width);
        racket.setHeight(height);
        racket.setFill(paint);
        racket.setStroke(Color.BLACK);
    }
    public int getSpeed(){ return  speed;}
    public int getMark(){ return mark;}
    public void setMark(int mark){ this.mark = mark;}
    public void setX(int x){ racket.setX(x); }
    public int getX(){ return (int)racket.getX(); }
    public int getY(){ return (int)racket.getY();}
    public void addToLayout(Pane pane){ pane.getChildren().add(racket); }
    public int getWidth(){ return (int)racket.getWidth(); }
    public void setWidth(int width){ racket.setWidth(width);}
    public Rectangle getInRectangleForm(){ return racket;}
    public double getDegree(Ball ball){
        int degree=0;
        for(int i=-5 ; i<=racket.getWidth()+5 ; i++)
            if(i+racket.getX() == ball.getCenterX())
                degree = i/mark + 30;
        return degree*Math.PI/180;
    }
}
