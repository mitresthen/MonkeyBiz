package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.doncapo.game.Items.ItemFactory;
import com.doncapo.game.Items.Item;
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

    private int screenWidth = 1440;
    private int screenHeight = 2560;

    private long spawnInterval;


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

        spawnInterval = 5000000000l;
        lastDropTime = TimeUtils.nanoTime() - spawnInterval/2;
    }


    private void prepareBackground(){
        backgroundSprite = new Sprite(theme.backgroundTexture);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setSize(screenWidth, screenHeight);
    }

    private Item getRandomItem(float x, float y, int movementSpeed){
        return ItemFactory.createRandomItem(theme, x, y, movementSpeed);
    }

    private ItemCarrier getRandomItemCarrier(com.doncapo.game.Items.Item item, int movementSpeed){
        int rnd = MathUtils.random(0, theme.characters.size-1);
        return new ItemCarrier(theme.characters.get(rnd), item, movementSpeed);
    }

    private void spawnItem(){
        float yValue = MathUtils.random(512, (2*screenHeight)/3);
        spawnInterval = MathUtils.random(3000000000l, 6000000000l);
        int movementSpeed = MathUtils.random(300, 380);
        Item item = getRandomItem(screenWidth, yValue, movementSpeed);
        ItemCarrier carrier = getRandomItemCarrier(item, movementSpeed);
        activeCarriers.add(carrier);
        activeItems.add(item);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {
        theme.gameOverSound.stop();
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
        drawLives();
        playerCharacter.drawBranch(game.batch);
        playerCharacter.drawPlayer(game.batch);

        for(ItemCarrier carriers : activeCarriers){
            carriers.draw(game.batch);
        }

        for(com.doncapo.game.Items.Item item : activeItems){
            game.batch.draw(item.itemTexture, item.getX(), item.getY());
        }
        game.batch.end();

        playerCharacter.update(delta);

        if(TimeUtils.nanoTime() - lastDropTime > (spawnInterval)) spawnItem();
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
            tempDrop.update(delta);

            if(tempDrop.getX() + tempDrop.getWidth() < 0){
                tempDrop.effectOffScreen(playerCharacter, theme);
                activeItems.removeIndex(i);
            }
            else if(tempDrop.overlaps(playerCharacter.playerRectangle)){
                gatherItem(tempDrop);
                activeItems.removeIndex(i);
            }

            if(!stillAlive()){
                endGame();
            }
        }
    }

    private void calculateCarrierChanges(float delta){
        ItemCarrier tempItemCarrier;
        int lenCarriersList = activeCarriers.size;
        for(int i = lenCarriersList; --i >= 0;){
            tempItemCarrier = activeCarriers.get(i);
            tempItemCarrier.update(delta);

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

    private void drawLives(){
        int lives = playerCharacter.getLives();
        final int size = 64;
        for(int i = 1; i<= lives; i++){
            game.batch.draw(theme.lifeTexture, screenWidth-(size*i), screenHeight-size, size, size);
        }
    }

    private void renderText(){
        game.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        game.font.getData().setScale(4, 3);
        game.font.draw(game.batch, "Score: " + playerCharacter.getScore(), 0, screenHeight);
        //String remainingLives = "Lives: " + playerCharacter.getLives();
        //game.font.draw(game.batch, remainingLives, 3*(screenWidth/4), screenHeight);

    }

    private void looseLife(){
        playerCharacter.looseLife();
        if(!stillAlive()){
            endGame();
        }else{
            theme.lostLifeSound.play();
        }
    }

    private boolean stillAlive(){
        if(playerCharacter.getLives() < 0) {
            return false;
        }
        else{
            return true;
        }
    }

    private void gatherItem(Item gatheredItem){
        gatheredItem.effectGather(playerCharacter, theme);
        playerCharacter.grabItem(gatheredItem);
    }

    private void endGame(){
        theme.loopingMusic.stop();
        theme.gameOverSound.play();
        game.setScreen(new GameOverScreen(game, playerCharacter.getScore()));
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
