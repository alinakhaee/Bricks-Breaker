package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Random;

public class Brick {
    Rectangle brick = new Rectangle();
    Random r = new Random();
    int number = r.nextInt(4)+1, temp=r.nextInt(35);
    boolean gift = temp%17==0 ;
    Text text2 = new Text();
    Image giftImage = new Image(new File("brickGift.png").toURI().toString());
    ImagePattern giftPattern = new ImagePattern(giftImage, -30, -10, 40, 40, false);
    ImageView giftView = new ImageView(giftImage);
    public Brick(int x, int y, Color paint){
        brick.setX(x);
        brick.setY(y);
        brick.setFill(paint);
        brick.setStroke(Color.BLACK);
        brick.setStrokeWidth(2);
        //if(gift) brick.setFill(giftPattern);
        brick.setWidth(40);
        brick.setHeight(40);
        Text text = new Text(brick.getX()+17, brick.getY()+24, Integer.toString(number));
        text2 = text;
    }
    public Rectangle getInRectangleForm(){ return brick;}
    public boolean hasGift(){ return gift;}
    public void setX(int x){ brick.setX(x);}
    public void setY(int y){ brick.setY(y);}
    public int getX(){ return (int)brick.getX();}
    public int getY(){ return (int)brick.getY();}
    public void addToLayout(Pane pane){
        pane.getChildren().add(brick);
        if(hasGift()){
            giftView.setFitWidth(40); giftView.setFitHeight(40); giftView.setX(brick.getX()); giftView.setY(brick.getY());
            pane.getChildren().add(giftView);
        }
        pane.getChildren().add(text2);
    }
    public void removeFromLayout(Pane pane){
        pane.getChildren().removeAll(brick, text2, giftView);
        brick.setX(0);
        brick.setY(0);
        brick.setWidth(0);
        brick.setHeight(0);
    }
    public void setNewNumber(Pane pane){
        pane.getChildren().remove(text2);
        text2.setText(Integer.toString(number));
        pane.getChildren().add(text2);

    }
}
