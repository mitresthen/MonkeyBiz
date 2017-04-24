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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Havard on 17.04.2017.
 */
public class GameScreen implements Screen {
    final Main game;

    private Texture dropImage;
    private Texture explosionImage;
    private Texture bucketImage;
    private Texture backgroundImage;
    private Sound dropSound;
    private Music rainMusic;

    private OrthographicCamera camera;
    private Sprite bucket;
    private Sprite backgroundSprite;
    private Vector3 touchPos;
    private final Array<Raindrop> activeRaindrops;
    private final Pool<Raindrop> raindropPool;

    private long lastDropTime;
    private int dropsGathered;
    private int lives;
    private int dropFactor;

    private float red = 0.0f;
    private float green = 0.05f;
    private float blue = 0.2f;

    private int screenWidth = 2560;
    private int screenHeight = 1440;


    public GameScreen(final Main game) {
        this.game = game;

        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        explosionImage = new Texture(Gdx.files.internal("explosion.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        prepareBackground();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        game.batch = new SpriteBatch();
        createBucket();
        touchPos = new Vector3();

        activeRaindrops = new Array<Raindrop>();
        raindropPool = new Pool<Raindrop>() {
            @Override
            protected Raindrop newObject() {
                return new Raindrop();
            }
        };

        lives = 3;
        dropFactor = 200;
        lastDropTime = TimeUtils.nanoTime();
    }

    private void prepareBackground(){
        backgroundImage = new Texture(Gdx.files.internal("chaosBackground.png"));
        backgroundImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundSprite = new Sprite(backgroundImage);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setSize(screenWidth, screenHeight);
    }

    private void createBucket(){
        bucket = new Sprite(bucketImage);
        bucket.setSize(64, 64);
        bucket.setX((screenWidth/2)-(bucket.getWidth()/2));
        bucket.setY(bucketRestPosition());
    }

    private void spawnRaindrop(){
        Raindrop raindrop = raindropPool.obtain();
        raindrop.init(screenWidth, MathUtils.random(raindrop.getWidth()*4, (screenHeight/2)-raindrop.getWidth()));
        activeRaindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private float bucketRestPosition(){
        return screenHeight-(2*bucket.getHeight());
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
        Gdx.gl.glClearColor(red, green, blue, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        renderBackground();
        game.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        game.font.getData().setScale(2, 2);
        game.font.draw(game.batch, "Score: " + dropsGathered, 0, screenHeight);
        String remainingLives = "Lives remaining: " + lives;
        game.font.draw(game.batch, remainingLives, screenWidth-screenWidth/8, screenHeight);
        bucket.draw(game.batch);
        for(Raindrop raindrop: activeRaindrops){
            if(raindrop.alive)
                game.batch.draw(dropImage, raindrop.getX(), raindrop.getY());
            else
                game.batch.draw(explosionImage, raindrop.getX(), raindrop.getY());
        }

        game.batch.end();

        updateBucket();

        if(TimeUtils.nanoTime() - lastDropTime > (5000000000l)) spawnRaindrop();

        Raindrop tempDrop;
        int len = activeRaindrops.size;
        for(int i = len; --i >= 0;){
            tempDrop = activeRaindrops.get(i);
            tempDrop.update(dropFactor, Gdx.graphics.getDeltaTime());

            if(tempDrop.alive == false){
                activeRaindrops.removeIndex(i);
                raindropPool.free(tempDrop);
            }

            if(tempDrop.getX() + tempDrop.getWidth() < 0){
                looseLife();
                tempDrop.alive = false;
            }
            else if(tempDrop.overlaps(bucket.getBoundingRectangle())){
                gatherDrop(tempDrop);
                tempDrop.alive = false;
            }
        }
    }

    private float goalY = 0;
    private void updateBucket(){
        if(goalY < 10){
            float yMovement = (screenHeight/2)*Gdx.graphics.getDeltaTime();
            bucket.translateY(yMovement);
        }
        if(Gdx.input.isTouched() && bucket.getY() >= bucketRestPosition()){
            goalY += Gdx.graphics.getDeltaTime()*(screenHeight/2);
        }
        else{
            float yMovement = goalY /15;
            bucket.translateY(-yMovement);
            goalY -= yMovement;
        }
        if(goalY > bucketRestPosition())
            goalY = bucketRestPosition();
        if(bucket.getY() < 0) bucket.setY(0);
        if(bucket.getY() > bucketRestPosition()) bucket.setY(bucketRestPosition());
    }

    private void looseLife(){
        lives--;
        if(lives == 0){
            endGame();
        }
    }

    private void gatherDrop(Raindrop gatheredDrop){
        dropsGathered++; // ((int)(screenHeight-gatheredDrop.getY())/100) + 5;
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
        dropImage.dispose();
        bucketImage.dispose();
        dropImage.dispose();
        backgroundImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
