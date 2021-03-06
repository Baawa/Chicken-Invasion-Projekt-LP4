package com.chicken.invasion.oldstuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/**
 * Created by Kristoffer on 2016-05-02.
 */
public class EnemyObject implements Cloneable{
    private float x,y, width, height;
    private double health;
    private double totalHealth;
    private float speed;
    private Rectangle collideRect;
    private boolean push;

    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 2;

    private Animation walkAnimation;
    private Texture walkSheet;
    private TextureRegion currentFrame;

    float stateTime;

    public EnemyObject(EnemyObject e){
        this.walkSheet = e.getWalkSheet();
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth()/FRAME_COLS, walkSheet.getHeight()/FRAME_ROWS);
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation(0.09f, walkFrames);
        stateTime = 0f;

        Random rand = new Random();
        x = (rand.nextFloat()*(Gdx.graphics.getWidth()/120) + 0.5f);
        if (x>12){ x-=1; }
        y = Gdx.graphics.getHeight() / 190;

        width = 2.0f;
        height = 2.0f;

        collideRect = new Rectangle(x,y,width,height);

        health = e.getHealth();
        totalHealth = e.getHealth();
        this.push = e.getPush();
    }

    public EnemyObject(String image, int health, boolean canBePushed){
        walkSheet = new Texture(image);
        TextureRegion[][] tmp = TextureRegion.split(this.walkSheet, this.walkSheet.getWidth()/FRAME_COLS, this.walkSheet.getHeight()/FRAME_ROWS);
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation(0.09f, walkFrames);
        stateTime = 0f;

        Random rand = new Random();
        x = (rand.nextFloat()*(Gdx.graphics.getWidth()/120) + 0.5f);
        if (x>12){ x-=1; }
        y = Gdx.graphics.getHeight() / 190;

        width = 2.0f;
        height = 2.0f;

        collideRect = new Rectangle(x,y,width,height);

        this.health = health;
        this.totalHealth = health;

        this.push = canBePushed;
    }

    public EnemyObject(){
        Random rand = new Random();
        //x = (rand.nextFloat()*(Gdx.graphics.getWidth()/120) + 0.5f);
        //if (x>12){ x-=1; }
        //y = Gdx.graphics.getHeight() / 190;

        width = 2.0f;
        height = 2.0f;

        //collideRect = new Rectangle(x,y,width,height);

        health = 1;
        totalHealth = 1;
        push = true;
    }

    public void update(float dt){
        stateTime += dt;
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        x += -dt/7;
        y += -dt/1.2f;

        width += dt/3f;
        height += dt/3f;

        collideRect.set(x,y,width,height);
    }

    public Rectangle getCollideRect() { return collideRect; }

    public TextureRegion getTextureRegion() { return currentFrame; }

    public float getX() {
        return x;
    }

    public void incY(){
        if (this.push){
            this.y ++;
        }
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public double getHealth(){
        return this.health;
    }

    public double getTotalHealth(){
        return this.totalHealth;
    }

    public void decHealth(double damage){
        this.health -= damage;
    }

    public void setPush(boolean push){
        this.push = push;
    }

    public boolean getPush(){
        return this.push;
    }

    @Override
    public EnemyObject clone(){
        EnemyObject clone = new EnemyObject();
        clone.collideRect = this.collideRect;
        clone.width = this.width;
        clone.currentFrame = this.currentFrame;
        clone.health = this.health;
        clone.height = this.height;
        clone.speed = this.speed;
        clone.walkAnimation = this.walkAnimation;
        clone.walkSheet = this.walkSheet;
        clone.x = this.x;
        clone.y  =this.y;
        clone.setPush(this.push);

        return clone;
    }

    public void dispose() {
        walkSheet.dispose();
    }

    public Texture getWalkSheet(){
        return this.walkSheet;
    }
}
