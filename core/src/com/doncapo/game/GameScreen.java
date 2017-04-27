package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Havard on 17.04.2017.
 */
public class GameScreen implements Screen {
    final Main game;

    private Texture bananasImage;
    private Texture appleImage;
    private Texture pineappleImage;

    private Texture bedouinManImage;
    private Sprite bedouinManSprite;

    private Texture mainMonkeyImage;
    private Texture mainMonkeyGrabbingImage;
    private Texture monkeyTailBranchImage;

    private Texture backgroundImage;
    private Sound dropSound;
    private Music rainMusic;

    private OrthographicCamera camera;
    private Sprite mainMonkey;
    private Sprite branchTailSprite;
    private Sprite backgroundSprite;
    private final Array<Fruit> activeRaindrops;

    private long lastDropTime;
    private int dropsGathered;
    private int lives;
    private int dropFactor;

    private int screenWidth = 1440;
    private int screenHeight = 2560;


    public GameScreen(final Main game) {
        this.game = game;

        bananasImage = new Texture(Gdx.files.internal("fruits/bananas.png"));
        appleImage = new Texture(Gdx.files.internal("fruits/apple.png"));
        pineappleImage = new Texture(Gdx.files.internal("fruits/pineapple.png"));

        mainMonkeyImage = new Texture(Gdx.files.internal("mainMonkey.png"));
        mainMonkeyGrabbingImage = new Texture(Gdx.files.internal("mainMonkeyGrabbing.png"));
        monkeyTailBranchImage = new Texture(Gdx.files.internal("monkeyTailBranch.png"));

        prepareBedouinMan();
        prepareBackground();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);


        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        game.batch = new SpriteBatch();
        createMonkeyTail();
        createMonkey();

        activeRaindrops = new Array<Fruit>();

        lives = 3;
        dropFactor = 300;
        lastDropTime = TimeUtils.nanoTime();
        lastLostTime = TimeUtils.nanoTime();
    }

    private void prepareBedouinMan(){
        bedouinManImage = new Texture(Gdx.files.internal("bedouinMan.png"));
        bedouinManSprite = new Sprite(bedouinManImage);
        resetBedouinMan();
    }

    private void resetBedouinMan(){
        bedouinManSprite.setSize(190, 300);
        bedouinManSprite.setPosition(screenWidth, 256);
    }

    private void prepareBackground(){
        backgroundImage = new Texture(Gdx.files.internal("desertBackground.png"));
        backgroundImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundSprite = new Sprite(backgroundImage);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setSize(screenWidth, screenHeight);
    }

    private void createMonkey(){
        mainMonkey = new Sprite(mainMonkeyImage);
        mainMonkey.setSize(256, 256);
        mainMonkey.setX((screenWidth/2)-(mainMonkey.getWidth()/2));
        mainMonkey.setY(bucketRestPosition());
    }

    private void createMonkeyTail(){
        branchTailSprite = new Sprite(monkeyTailBranchImage);
        branchTailSprite.setSize(256, 64);
        branchTailSprite.setX((screenWidth/2)-(monkeyTailBranchImage.getWidth()/2));
        branchTailSprite.setY(screenHeight-256);
    }

    private Fruit getRandomFruit(float x, float y){
        int rnd = MathUtils.random(0, 2);
        switch (rnd) {
            case 0:
                return new Fruit(appleImage, x, y);
            case 1:
                return new Fruit(bananasImage, x, y);
            case 2:
                return new Fruit(pineappleImage, x, y);
            default:
                return new Fruit(appleImage, x, y);
        }
    }

    private void spawnFruit(){
        resetBedouinMan();
        float yValue = MathUtils.random(256, (screenHeight/2));
        Fruit fruit = getRandomFruit(screenWidth, yValue);
        bedouinManSprite.setScale(yValue/bedouinManSprite.getHeight());
        activeRaindrops.add(fruit);
        lastDropTime = TimeUtils.nanoTime();
    }

    private float bucketRestPosition(){
        return screenHeight-(2* mainMonkey.getHeight());
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    private void renderBackground(){
        backgroundSprite.draw(game.batch);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        renderBackground();
        renderText();

        branchTailSprite.draw(game.batch);
        mainMonkey.draw(game.batch);
        for(Fruit fruit : activeRaindrops){
            game.batch.draw(fruit.fruitTexture, fruit.getX(), fruit.getY());
        }
        bedouinManSprite.draw(game.batch);
        game.batch.end();

        updateBucket();

        if(TimeUtils.nanoTime() - lastDropTime > (5000000000l)) spawnFruit();
        calculateChanges();
    }

    private void calculateChanges(){
        bedouinManSprite.translateX(-dropFactor*Gdx.graphics.getDeltaTime());
        Fruit tempDrop;
        int len = activeRaindrops.size;
        for(int i = len; --i >= 0;){
            tempDrop = activeRaindrops.get(i);
            tempDrop.update(dropFactor, Gdx.graphics.getDeltaTime());

            if(bedouinManSprite.getBoundingRectangle().overlaps(mainMonkey.getBoundingRectangle())){
                looseLife();
            }
            if(tempDrop.getX() + tempDrop.getWidth() < 0){
                looseLife();
                activeRaindrops.removeIndex(i);
            }
            else if(tempDrop.overlaps(mainMonkey.getBoundingRectangle())){
                gatherDrop(tempDrop);
                activeRaindrops.removeIndex(i);
            }
        }
    }

    private void renderText(){
        game.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        game.font.getData().setScale(4, 3);
        game.font.draw(game.batch, "Score: " + dropsGathered, 0, screenHeight);
        String remainingLives = "Lives: " + lives;
        game.font.draw(game.batch, remainingLives, 3*(screenWidth/4), screenHeight);

    }

    private float goalY = 0;
    private void updateBucket(){
        if(goalY < 10){
            float yMovement = (screenHeight/2)*Gdx.graphics.getDeltaTime();
            mainMonkey.translateY(yMovement);
        }
        if(Gdx.input.isTouched() && mainMonkey.getY() >= bucketRestPosition()){
            mainMonkey.setTexture(mainMonkeyImage);
            goalY += Gdx.graphics.getDeltaTime()*(screenHeight/2);
        }
        else{
            float yMovement = goalY /15;
            mainMonkey.translateY(-yMovement);
            goalY -= yMovement;
        }
        if(goalY > bucketRestPosition())
            goalY = bucketRestPosition();
        if(mainMonkey.getY() < 0) mainMonkey.setY(0);
        if(mainMonkey.getY() > bucketRestPosition()) mainMonkey.setY(bucketRestPosition());
    }


    private long lastLostTime;
    private void looseLife(){
        if(TimeUtils.nanoTime() - lastLostTime < (2000000000l)){
            return;
        }
        lastLostTime =TimeUtils.nanoTime();
        lives--;
        if(lives == 0){
            endGame();
        }
    }

    private void gatherDrop(Fruit gatheredDrop){
        dropsGathered++;
        mainMonkey.setTexture(mainMonkeyGrabbingImage);
        dropSound.play();
    }

    private void endGame(){
        rainMusic.stop();
        game.setScreen(new GameOverScreen(game, dropsGathered));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        rainMusic.stop();
        game.setScreen(new PauseScreen(game, this));
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        bananasImage.dispose();
        mainMonkeyImage.dispose();
        bananasImage.dispose();
        backgroundImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
