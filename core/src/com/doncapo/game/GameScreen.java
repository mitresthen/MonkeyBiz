package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
    private PlayerCharacter playerCharacter;

    private Theme theme;

    private OrthographicCamera camera;
    private Sprite backgroundSprite;
    private final Array<Item> activeItems;
    private final Array<ItemCarrier> activeCarriers;

    private long lastDropTime;
    private int dropsGathered;
    private int dropFactor;

    private int screenWidth = 1440;
    private int screenHeight = 2560;


    public GameScreen(final Main game) {
        this.game = game;

        theme = new Theme("themes");
        theme.loadRandomTheme();

        playerCharacter = new PlayerCharacter(theme.playerTop, theme.playerMain, theme.playerGrabbing, theme.playerTether, screenWidth, screenHeight);

        prepareBackground();

        theme.loopingMusic.setLooping(true);


        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        game.batch = new SpriteBatch();

        activeItems = new Array<Item>();
        activeCarriers = new Array<ItemCarrier>();

        dropFactor = 300;
        lastDropTime = TimeUtils.nanoTime();
    }


    private void prepareBackground(){
        backgroundSprite = new Sprite(theme.backgroundTexture);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setSize(screenWidth, screenHeight);
    }



    private Item getRandomItem(float x, float y){
        int rnd = MathUtils.random(0, theme.goodItems.size-1);
        return new Item(theme.goodItems.get(rnd), x, y);
    }

    private ItemCarrier getRandomItemCarrier(Item item){
        int rnd = MathUtils.random(0, theme.characters.size-1);
        return new ItemCarrier(theme.characters.get(rnd), item);
    }

    private void spawnItem(){
        dropFactor++;
        float yValue = MathUtils.random(512, ((2*screenHeight)/3));
        Item item = getRandomItem(screenWidth, yValue);
        ItemCarrier carrier = getRandomItemCarrier(item);
        activeCarriers.add(carrier);
        activeItems.add(item);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {
        theme.loopingMusic.play();
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
        playerCharacter.drawBranch(game.batch);
        playerCharacter.drawPlayer(game.batch);

        for(Item item : activeItems){
            game.batch.draw(item.itemTexture, item.getX(), item.getY());
        }
        for(ItemCarrier carriers : activeCarriers){
            game.batch.draw(carriers.itemCarrierTexture, carriers.getX(), carriers.getY(), carriers.getWidth(), carriers.getHeight()+ carriers.getReduction());
        }
        game.batch.end();

        updateBucket(delta);

        if(TimeUtils.nanoTime() - lastDropTime > (5000000000l)) spawnItem();
        calculateChanges(delta);
    }

    private void calculateChanges(float delta){
        calculateItemChanges(delta);
        calculateCarrierChanges(delta);
    }
    private void calculateItemChanges(float delta){
        Item tempDrop;
        int len = activeItems.size;
        for(int i = len; --i >= 0;){
            tempDrop = activeItems.get(i);
            tempDrop.update(dropFactor, delta);

            if(tempDrop.getX() + tempDrop.getWidth() < 0){
                looseLife();
                activeItems.removeIndex(i);
            }
            else if(tempDrop.overlaps(playerCharacter.playerRectangle)){
                gatherItem(tempDrop);
                activeItems.removeIndex(i);
            }
        }
    }

    private void calculateCarrierChanges(float delta){
        ItemCarrier tempItemCarrier;
        int lenCarriersList = activeCarriers.size;
        for(int i = lenCarriersList; --i >= 0;){
            tempItemCarrier = activeCarriers.get(i);
            tempItemCarrier.update(dropFactor, delta);

            if(tempItemCarrier.overlaps(playerCharacter.playerRectangle)){
                if(!tempItemCarrier.spentDamage){
                    tempItemCarrier.spentDamage = true;
                    looseLife();
                }
            }
            if(tempItemCarrier.getX() + tempItemCarrier.getWidth() < 0){
                activeCarriers.removeIndex(i);
            }
        }
    }


    private void renderText(){
        game.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        game.font.getData().setScale(4, 3);
        game.font.draw(game.batch, "Score: " + dropsGathered, 0, screenHeight);
        String remainingLives = "Lives: " + playerCharacter.getLives();
        game.font.draw(game.batch, remainingLives, 3*(screenWidth/4), screenHeight);

    }

    private float goalY = 0;
    private void updateBucket(float delta){
        if(playerCharacter.playerRectangle.getY() >= playerCharacter.bucketRestPosition){
            playerCharacter.releaseItem();
        }
        if(goalY < 10){
            float yMovement = (screenHeight/2)*delta;
            playerCharacter.translateY(yMovement);
        }
        if(Gdx.input.isTouched() && playerCharacter.playerRectangle.getY() >= playerCharacter.bucketRestPosition){
            goalY += delta*(screenHeight/2);
        }
        else{
            float yMovement = goalY /15;
            playerCharacter.translateY(-yMovement);
            goalY -= yMovement;
        }
        if(goalY > playerCharacter.bucketRestPosition)
            goalY = playerCharacter.bucketRestPosition;
        if(playerCharacter.playerRectangle.getY() < 0) playerCharacter.playerRectangle.setY(0);
        if(playerCharacter.playerRectangle.getY() > playerCharacter.bucketRestPosition) playerCharacter.playerRectangle.setY(playerCharacter.bucketRestPosition);
    }


    private void looseLife(){
        playerCharacter.looseLife();
        if(playerCharacter.getLives() == 0){
            endGame();
        }
    }

    private void gatherItem(Item gatheredItem){
        dropsGathered++;
        playerCharacter.grabItem(gatheredItem);
        theme.actionSound.play();
    }

    private void endGame(){
        theme.loopingMusic.stop();
        game.setScreen(new GameOverScreen(game, dropsGathered));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        theme.loopingMusic.stop();
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
        theme.dispose();
    }

}
