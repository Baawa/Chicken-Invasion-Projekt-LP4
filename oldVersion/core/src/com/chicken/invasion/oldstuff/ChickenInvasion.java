package com.chicken.invasion.oldstuff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.chicken.invasion.Helpers.GameButton;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.Iterator;

public class ChickenInvasion extends ApplicationAdapter implements GestureDetector.GestureListener{

    //Interface for various callbacks to the android launcher
    public interface GameCallback {
        void onStartActivityStore();
        void onStartActivityHighScore();
        void onStartActivityInputName(int points);
        void saveScore(int score);
        int getChickenLegs();
        ThrowableObject getTO();
        Background getEquippedBackground();
        void getTOUpgrade();
    }

    // Local variable to hold the callback implementation
    private GameCallback gameCallback;

    // Setter for the callback
    public void setMyGameCallback(GameCallback callback) {
        gameCallback = callback;
    }
    //END GameCallback------------------------------

    //Score callback interface ---------
    public interface isHighScoreCallback{
        boolean isHighscore(int points);
    }
    // Local variable to hold the callback implementation
    private isHighScoreCallback isHighScoreCallback;
    // Setter for the callback
    public void setMyIsHighScoreCallback(isHighScoreCallback callback) { isHighScoreCallback = callback; }
    //END isHighScoreCallback-------------------------------

	private Model model;
	private SpriteBatch batch;
	private Sprite backgroundimg;
    private World world;
	private PlayerObject playerObject;
    private Wave wave;
    private Rectangle bottom;
    private BitmapFont fontScore, fontWings;
    private Texture chickenLeg;
    private ThrowableHolder throwableHolder;
    private BackgroundHolder backgroundHolder;
    private Background bGround;
    private Texture backgroundtexture;

	private GameButton startBtn;
	private GameButton pauseBtn;
    private GameButton storeBtn;
    private GameButton highscoreBtn;
    private GameButton backBtn;
    private GameButton restartBtn;
    private GameButton muteBtn;
    private GameButton unmuteBtn;
    private GameButton settingsBtn;

	private Camera camera;
    private Music bgMusic;

    private Boolean showSettings = false;
    private Boolean muteMusic = false;

    @Override
	public void create () {

		model = Model.getInstance();
        playerObject = new PlayerObject();
		this.world = new World(new Vector2(0, 0), true);
        throwableHolder = new ThrowableHolder(this, world);
        backgroundHolder = new BackgroundHolder();

        /*if (playerObject.getCurrentTO() == null){
            ThrowableObject tmp = new ThrowableObject("Beachball", "beachball200x200", this.world, playerObject);
            tmp.setDamage(1);
            tmp.setSpeed(1.5);
            tmp.setRotationSpeed(0.5f);
            playerObject.setEquippedTO(tmp);
        }*/

		batch = new SpriteBatch();

        //Camera
        camera = new OrthographicCamera(Gdx.graphics.getWidth()/100,Gdx.graphics.getHeight()/100);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

        //Background
        setBackground(gameCallback.getEquippedBackground());
        drawBackground();

        wave = new Wave(1,model.getDifficulty());

        bottom = new Rectangle(0f,0f,25f,0.1f);

        initFonts();
        initButtons();

		Gdx.input.setInputProcessor(new GestureDetector(this));
        Gdx.gl.glClearColor(1, 1, 1, 1);

        playerObject.addChickenWings(gameCallback.getChickenLegs());

        gameCallback.getTOUpgrade();

        // MUSIKAAA!!!
        if (muteMusic == false) {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic/ChickenInvasion-BackgroundMusic.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.5f);
            bgMusic.play();
        }
        if(playerObject.getCurrentTO() == null){
           ThrowableObject tempTO2 =  gameCallback.getTO();
            playerObject.removeTO();
            playerObject.setEquippedTO(tempTO2);
        }
	}


    @Override
    public void resume() {
        super.resume();
        setBackground(gameCallback.getEquippedBackground());
        drawBackground();
    }


	@Override
	public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        switch (model.getState()) {
            case RUNNING:

                if (model.shouldSpawnObject()) {
                    spawnThrowable();
                    model.setSpawnObject(false);
                }

                batch.begin();

                //highScoreInput.draw(batch,255);
                drawRunningGame(batch);

                batch.end();

                world.step(1 / 60f, 6, 2);
                break;

            case PAUSED:
                batch.begin();

                drawPausedGame(batch);

                batch.end();
                break;

            case STOPPED:
                batch.begin();

                drawStartScreen(batch);

                batch.end();
                break;

            case GAMEOVER:
                batch.begin();

                drawGameOverScreen(batch);

                batch.end();

                break;

            default:
                System.out.print("Nu blev det galet");
                break;
        }
	}

    private void drawFonts() {
        //draw score
        fontScore.draw(batch,
                String.valueOf(playerObject.getScore()),
                (float) Gdx.graphics.getWidth() / 200 - 0.5f,
                (float) Gdx.graphics.getHeight() / 100 - 2,
                0.1f,
                Align.center,
                false);

        //Show new wave?
        if (wave.isDisplayWaveFont()){
            wave.getWaveFont().draw(batch,
                    wave.getWaveString(),
                    wave.getFontX(),
                    wave.getFontY(),
                    0f,
                    Align.center,
                    false);
        }

    }

    public void drawAlways(){
        backgroundimg.draw(batch);

        batch.draw(chickenLeg, 0.1f, Gdx.graphics.getHeight() / 100 - 1f, 1, 1);

        //Draw chicken wings
        fontWings.draw(batch, String.valueOf(playerObject.getChickenWings()), 1.5f, Gdx.graphics.getHeight()/100 - 0.2f);
    }

	public void startGame(){
		if (model.getState() == Model.State.PAUSED || model.getState() == Model.State.STOPPED || model.getState() == Model.State.GAMEOVER){

			if (model.getState() == Model.State.GAMEOVER || model.getState() == Model.State.STOPPED ){
                drawBackground();
                model.restartWaves();
                wave = new Wave(1,model.getDifficulty());
                playerObject.resetScore();
            }

            spawnThrowable();
			model.startGame();
		}
	}

    public void drawBackground(){
        backgroundtexture = new Texture(bGround.getImageURL() + ".png");
        backgroundimg = new Sprite(backgroundtexture);
        backgroundimg.setPosition(0, 0);
        backgroundimg.setSize(Gdx.graphics.getWidth() / 100, Gdx.graphics.getHeight() / 100);
    }

    public void createIntent(String className) {
        if (gameCallback != null) {

            // initiate which ever callback method you need.
            if (className.equals("Store")) {
                gameCallback.onStartActivityStore();
            } else if (className.equals("HighScore")) {
                gameCallback.onStartActivityHighScore();
            } else if (className.equals("InputName")) {
                gameCallback.onStartActivityInputName(playerObject.getScore());
            }
        }
    }

    public void goBack(){
        model.stopGame();
    }

    public void restartGame(){
        model.restartWaves();
        wave = new Wave(1,model.getDifficulty());
        playerObject.resetScore();
        startGame();
    }

	public void pauseGame(){
		model.pauseGame();
	}

    public void mute(){
        if (muteMusic == true){
            muteMusic = false;
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic/ChickenInvasion-BackgroundMusic.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.5f);
            bgMusic.play();
            return;
        } else {
            muteMusic = true;
            bgMusic.stop();
            return;
        }
    }

    public void showSettingsView(){
        showSettings = !showSettings;
    }

    private void drawRunningGame(SpriteBatch batch){
        drawAlways();
        drawEnemies();
        checkCollision();
        playerObject.draw(batch);
        drawFonts();

        pauseBtn.setX(Gdx.graphics.getWidth() / 100 - 2);
        pauseBtn.setY(Gdx.graphics.getHeight() / 100 - 1.1f);
        pauseBtn.draw(batch);
    }

    private void drawGameOverScreen(SpriteBatch batch){
        drawAlways();
        drawEnemies();
        playerObject.drawOnly(batch);


        //Game Over specifics
        Sprite gameOver = new Sprite(new Texture("gameover.png"));
        gameOver.setSize(400 / 50, 237f / 50);
        gameOver.setX(Gdx.graphics.getWidth() / 200 - 4);
        gameOver.setY(Gdx.graphics.getWidth() / 200 + 4f);
        gameOver.draw(batch);

        restartBtn.setX((Gdx.graphics.getWidth() / 200) + 0.1f + (restartBtn.getWidth() / 2));
        restartBtn.setY(Gdx.graphics.getHeight() / 200 - 3.0f);
        restartBtn.draw(batch);

        backBtn.setX((Gdx.graphics.getWidth() / 200) - 0.1f - backBtn.getWidth() - (backBtn.getWidth() / 2));
        backBtn.setY(Gdx.graphics.getHeight() / 200 - 3.0f);
        backBtn.draw(batch);

    }

    private void drawStartScreen(SpriteBatch batch){

        drawAlways();

        Sprite startBanner = new Sprite(new Texture("startBanner.png"));
        startBanner.setSize(350 / 50, 250f / 50);
        startBanner.setX(Gdx.graphics.getWidth() / 200 - startBanner.getWidth() / 2);
        startBanner.setY(Gdx.graphics.getWidth() / 200 + 4f);
        startBanner.draw(batch);

        startBtn.setX(Gdx.graphics.getWidth() / 200 - startBtn.getWidth() / 2);
        startBtn.setY(startBtn.getHeight() / 2 + 0.1f);
        startBtn.draw(batch);

        storeBtn.setX(Gdx.graphics.getWidth() / 200 - startBtn.getWidth() / 2 - 0.5f - storeBtn.getWidth());
        storeBtn.setY(startBtn.getY() - 0.1f);
        storeBtn.draw(batch);

        highscoreBtn.setX(Gdx.graphics.getWidth() / 200 + startBtn.getWidth() / 2 + 0.5f);
        highscoreBtn.setY(startBtn.getY() - 0.1f);
        highscoreBtn.draw(batch);

        settingsBtn.setX(Gdx.graphics.getWidth() / 100 - 0.1f - settingsBtn.getWidth());
        settingsBtn.setY(Gdx.graphics.getHeight() / 100 - 0.1f - settingsBtn.getHeight());
        settingsBtn.draw(batch);

        if (showSettings){
            Sprite settingsView = new Sprite(new Texture("settings.png"));
            settingsView.setSize(350 / 50, 500 / 50);
            settingsView.setX(Gdx.graphics.getWidth() / 200 - settingsView.getWidth() / 2);
            settingsView.setY(Gdx.graphics.getWidth() / 200);
            settingsView.draw(batch);

            if (muteMusic == false){
                muteBtn.setX(Gdx.graphics.getWidth() / 200 - muteBtn.getWidth() / 2);
                muteBtn.setY(Gdx.graphics.getHeight() / 200 - 2.0f);
                muteBtn.draw(batch);
            } else{
                unmuteBtn.setX(Gdx.graphics.getWidth() / 200 - unmuteBtn.getWidth() / 2);
                unmuteBtn.setY(Gdx.graphics.getHeight() / 200 - 2.0f);
                unmuteBtn.draw(batch);
            }
        }
    }

    private void drawPausedGame(SpriteBatch batch){
        drawAlways();
        drawEnemies();
        playerObject.drawOnly(batch);
        drawFonts();

        //Paused game specifics

        Sprite pauseBanner = new Sprite(new Texture("pausedGame.png"));
        pauseBanner.setSize(400 / 50, 240f / 50);
        pauseBanner.setX(Gdx.graphics.getWidth() / 200 - pauseBanner.getWidth() / 2);
        pauseBanner.setY(Gdx.graphics.getWidth() / 200 + 4f);
        pauseBanner.draw(batch);

        startBtn.setX(Gdx.graphics.getWidth() / 200 - startBtn.getWidth() / 2);
        startBtn.setY(Gdx.graphics.getHeight() / 200 - 2.0f);
        startBtn.draw(batch);

        restartBtn.setX((Gdx.graphics.getWidth() / 200) + 0.1f + (restartBtn.getWidth() / 2));
        restartBtn.setY(Gdx.graphics.getHeight() / 200 - 2.0f - 1.0f - startBtn.getHeight());
        restartBtn.draw(batch);

        backBtn.setX((Gdx.graphics.getWidth() / 200) - 0.1f - backBtn.getWidth() - (backBtn.getWidth() / 2));
        backBtn.setY(Gdx.graphics.getHeight() / 200 - 2.0f - 1.0f - startBtn.getHeight());
        backBtn.draw(batch);

    }

    private void initButtons(){
        startBtn = new com.chicken.invasion.Helpers.GameButton(new Callable<Void>() {
            public Void call() throws Exception {
                startGame();
                return null;
            }
        }, new Texture("play220x220.png"));
        startBtn.setSize(220 / 100, 220 / 100);

        backBtn = new com.chicken.invasion.Helpers.GameButton(new Callable<Void>() {
            public Void call() throws Exception {
                goBack();
                return null;
            }
        }, new Texture("back220x220.png"));
        backBtn.setSize(220 / 100, 220 / 100);

        restartBtn = new com.chicken.invasion.Helpers.GameButton(new Callable<Void>() {
            public Void call() throws Exception {
                restartGame();
                return null;
            }
        }, new Texture("restart220x220.png"));
        restartBtn.setSize(220 / 100, 220 / 100);

        highscoreBtn = new com.chicken.invasion.Helpers.GameButton(new Callable<Void>() {
            public Void call() throws Exception {
                createIntent("HighScore");
                return null;
            }
        }, new Texture("highscore200x200.png"));
        highscoreBtn.setSize(200 / 100, 200 / 100);

        storeBtn = new com.chicken.invasion.Helpers.GameButton(new Callable<Void>() {
            public Void call() throws Exception {
                createIntent("Store");
                return null;
            }
        }, new Texture("store200x200.png"));
        storeBtn.setSize(200 / 100, 200 / 100);

        pauseBtn = new com.chicken.invasion.Helpers.GameButton((new Callable<Void>() {
            public Void call() throws Exception {
                pauseGame();
                return null;
            }
        }), new Texture("pause200x200.png"));
        pauseBtn.setSize(200 / 200, 200 / 200);

        muteBtn = new com.chicken.invasion.Helpers.GameButton((new Callable<Void>() {
            public Void call() throws Exception {
                mute();
                return null;
            }
        }), new Texture("mute220x220.png"));
        muteBtn.setSize(220 / 100, 220 / 100);

        unmuteBtn = new com.chicken.invasion.Helpers.GameButton((new Callable<Void>() {
            public Void call() throws Exception {
                mute();
                return null;
            }
        }), new Texture("unmute220x220.png"));
        unmuteBtn.setSize(220 / 100, 220 / 100);

        settingsBtn = new com.chicken.invasion.Helpers.GameButton((new Callable<Void>() {
            public Void call() throws Exception {
                showSettingsView();
                return null;
            }
        }), new Texture("cogwheel.png"));
        settingsBtn.setSize(1, 1);
    }

    private void initFonts(){
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ChunkfiveEx.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 100;

        //score
        fontScore = fontGenerator.generateFont(parameter);
        fontScore.getData().setScale(0.03f);
        fontScore.setUseIntegerPositions(false);
        fontScore.setColor(Color.WHITE);

        //chicken wings
        parameter.size = 60;
        fontWings = fontGenerator.generateFont(parameter);
        fontWings.getData().setScale(0.015f);
        fontWings.setUseIntegerPositions(false);
        fontWings.setColor(Color.WHITE);

        //Chicken leg icon
        chickenLeg = new Texture("chickenleg-skevsomfan.png");
    }

	private void spawnThrowable(){

		playerObject.addThrowables(model.getNumberOfThrowables());
	}

    private void checkCollision(){
        for (Iterator<EnemyObject> iterEnemies = wave.getEnemies().iterator(); iterEnemies.hasNext(); ) {
            EnemyObject e = iterEnemies.next();
            if (playerObject.getThrowables().size() != 0) {
                if (playerObject.getThrowables().get(0).getCollideRect().overlaps(e.getCollideRect()) && playerObject.getCurrentTO().isThrown()) {
                    playerObject.getCurrentTO().onCollison();

                    if (playerObject.getCurrentTO().getDamage()>= e.getHealth()){
                        iterEnemies.remove();
                        playerObject.incScore((int)e.getTotalHealth());

                    }
                    else{
                        //push and hurt enemy
                        e.incY();
                        e.decHealth(playerObject.getCurrentTO().getDamage());
                    }
                    // MUSIKAAA!!!
                    Music music = Gdx.audio.newMusic(Gdx.files.internal("gamemusic/ChickenSound.mp3"));
                    music.setLooping(false);
                    music.setVolume(1.0f);
                    music.play();

                    if (wave.getEnemies().size() == 0 && wave.getDifficulty() == 0) {
                        model.nextWave();
                        wave.dispose();
                        wave = new Wave(wave.getLevel() + 1, model.getDifficulty());
                        playerObject.addThrowables(model.getNumberOfThrowables());

                        wave.displayWaveFont();
                        System.out.println("Level: " + wave.getLevel());
                    }
                    break;
                }
            }
            //Check if playerObject lost
            if (e.getCollideRect().overlaps(bottom)) {
                model.gameOver();
                gameCallback.saveScore(playerObject.getScore());
                if (isHighScoreCallback.isHighscore(playerObject.getScore())){
                    createIntent("InputName");
                }
                break;
            }
        }
    }

    private void drawEnemies(){
        ArrayList<EnemyObject> temp = new ArrayList<EnemyObject>(wave.getEnemies().size());
        for (EnemyObject e: wave.getEnemies()){
            temp.add(e);
        }
        // Generate an iterator. Start just after the last element.
        ListIterator<EnemyObject> li = temp.listIterator(temp.size());

        // Iterate in reverse.
        while(li.hasPrevious()) {
            EnemyObject e = li.previous();
                //update if not paused
                if (model.getState() == Model.State.RUNNING) {
                    e.update(Gdx.graphics.getDeltaTime());
                }
                //draw
                batch.draw(e.getTextureRegion(), e.getX(), e.getY(), e.getWidth(), e.getHeight());
            }
    }

    @Override
    public void dispose(){
        super.dispose();
        bgMusic.dispose();
        batch.dispose();
        fontScore.dispose();
        fontWings.dispose();
        chickenLeg.dispose();
        world.dispose();
        wave.dispose();
        playerObject.dispose();
        backBtn.dispose();
        highscoreBtn.dispose();
        pauseBtn.dispose();
        restartBtn.dispose();
        startBtn.dispose();
        storeBtn.dispose();
    }

    public PlayerObject getPlayerObject(){
        return playerObject;
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    public ThrowableHolder getThrowableHolder(){
        return throwableHolder;
    }

    public void setBackground(Background b) {
        bGround = b;
    }

    public Background getEquippedBGround(){
        return bGround;
    }

    public BackgroundHolder getBackgroundHolder() {
        return backgroundHolder;
    }

    // GESTURES -------------------------------------------------------------------------

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Vector3 coords=new Vector3(x,y,0);
		Vector3 coords2= camera.unproject(coords);


        if (model.getState() == Model.State.RUNNING){
            this.pauseBtn.clicked(coords2.x,coords2.y);
        }

        if (model.getState() == Model.State.PAUSED){
            this.startBtn.clicked(coords2.x,coords2.y);
            this.restartBtn.clicked(coords2.x, coords2.y);
            this.backBtn.clicked(coords2.x, coords2.y);
        }

        if (model.getState() == Model.State.STOPPED){
            this.startBtn.clicked(coords2.x,coords2.y);
            this.storeBtn.clicked(coords2.x,coords2.y);
            this.highscoreBtn.clicked(coords2.x, coords2.y);
            this.settingsBtn.clicked(coords2.x, coords2.y);
            if (showSettings){
                if (muteMusic){
                    this.unmuteBtn.clicked(coords2.x, coords2.y);
                } else{
                    this.muteBtn.clicked(coords2.x, coords2.y);
                }
            }
        }

        if (model.getState() == Model.State.GAMEOVER){
            this.restartBtn.clicked(coords2.x, coords2.y);
            this.backBtn.clicked(coords2.x, coords2.y);
        }

		return true;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

        if (model.getState() == Model.State.RUNNING && playerObject.getThrowables().size() != 0) {
            if ((x >= playerObject.getCurrentTO().getX() || x <= playerObject.getCurrentTO().getX() + 100) && (y >= playerObject.getCurrentTO().getY() || y <= playerObject.getCurrentTO().getY() + 100) && -deltaY > 0) {
                float throwX = deltaX;
                float throwY = -deltaY;
                float totalLength = (float) (Math.sqrt(Math.pow(throwX, 2) + Math.pow(throwY, 2)));
                if (throwY > 0) {
                    playerObject.getCurrentTO().throwToPoint(throwX / totalLength, throwY / totalLength);
                }

            }
        }
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
}
