package com.chicken.invasion.oldstuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kristoffer on 2016-05-02.
 */
public class Wave implements Runnable{
    private ArrayList<EnemyObject> enemies;
    private ArrayList<EnemyObject> potentialEnemies;
    private Thread thread;
    private int level;
    private int difficulty;
    private int nbrOfSent = 0;
    private BitmapFont waveFont;
    private float fontX, fontY;

    private static long startWaveTime;

    public Wave(int level,int difficulty){
        this.level = level;
        this.difficulty = difficulty;

        enemies = new ArrayList<EnemyObject>();

        potentialEnemies = new ArrayList<EnemyObject>();
        //potentialEnemies.add(new EnemyObject());
        potentialEnemies.add(potentialEnemies.size() ,new EnemyObject("ekak-spritesheet.png", 1, true));
        potentialEnemies.add(potentialEnemies.size() ,new EnemyObject("army-spritesheet.png", 2, true));
        potentialEnemies.add(potentialEnemies.size() ,new EnemyObject("tramp-spritesheet.png", 4, true));
        potentialEnemies.add(potentialEnemies.size() ,new EnemyObject("robo-sheet.png", 10, false));

        initNewWaveFont();

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        while(difficulty != 0){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Model.getInstance().getState() == Model.State.RUNNING) {
                spawn();
            }
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private EnemyObject getRandomEnemy(){
        Random rand = new Random();

        int tmp = 0;

        if (potentialEnemies.size() > 1){
            tmp = rand.nextInt(potentialEnemies.size());
        }

        return new EnemyObject(potentialEnemies.get(tmp));
    }

    private void spawn(){
        while (true){
            EnemyObject e = getRandomEnemy();
            if (e.getHealth() <= this.difficulty){
                spawnEnemy(e);
                difficulty -= e.getHealth();
                break;
            }
        }
    }

    private void spawnEnemy(EnemyObject e){
        enemies.add(enemies.size(), e);
        nbrOfSent++;
    }

    public ArrayList<EnemyObject> getEnemies() {
        return enemies;
    }

    public void displayWaveFont() { startWaveTime = System.currentTimeMillis(); }

    public boolean isDisplayWaveFont() { return (startWaveTime + 2000)>System.currentTimeMillis();}

    public int getLevel() { return level; }

    public String getWaveString(){ return "Wave " + getLevel(); }

    public BitmapFont getWaveFont(){ return waveFont; }

    public float getFontX() {
        return fontX;
    }

    public float getFontY() {
        return fontY;
    }

    public int getDifficulty(){
        return difficulty;
    }

    private void initNewWaveFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ChunkfiveEx.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;

        waveFont = generator.generateFont(parameter);
        waveFont.getData().setScale(0.02f);
        waveFont.setColor(Color.WHITE);
        waveFont.setUseIntegerPositions(false);


        fontX = Gdx.graphics.getWidth() / 200 - 0.5f;
        fontY = Gdx.graphics.getHeight() / 100 - 7;

        generator.dispose();
    }

    public void dispose(){
        waveFont.dispose();
        for (EnemyObject e : enemies){
            e.dispose();
        }
    }
}
