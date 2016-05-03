package com.chicken.invasion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.sun.tools.javadoc.Start;

import java.util.concurrent.Callable;

/**
 * Created by Albin on 2016-05-02.
 */
public class GameButton extends InputListener{
    private Sprite sprite;
    private Callable<Void> func;

    public GameButton(Callable<Void> func, Texture t){
        this.sprite = new Sprite(t);
        this.func = func;
    }

    public void draw(Batch batch){
        this.sprite.draw(batch);
    }

    public void setX(float x){
        this.sprite.setX(x);
    }

    public void setY(float y){
        this.sprite.setY(y);
    }

    public void setSize(int w, int h){
        this.sprite.setSize(w, h);
    }

    public float getX(){
        return this.sprite.getX();
    }

    public float getY(){
        return this.sprite.getY();
    }

    @Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        if ((x >= this.getX() || x <= this.getX()+100) && (y >= this.getY() || y <= this.getY()+100)){
            touchFunc();
        }
    }

    public void touchFunc(){
        try {
            this.func.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}