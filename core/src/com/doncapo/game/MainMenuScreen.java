package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Havard on 17.04.2017.
 */
public class MainMenuScreen implements Screen {
    final Main game;
    private Texture backgroundImage;
    private Sprite backgroundSprite;

    private int screenWidth = 1440;
    private int screenHeight = 2560;

    OrthographicCamera camera;
    public MainMenuScreen(final Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        prepareBackground();
    }

    @Override
    public void show() {

    }
    private void prepareBackground(){
        backgroundImage = new Texture(Gdx.files.internal("themes/desertTheme/background/background.png"));
        backgroundImage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundSprite = new Sprite(backgroundImage);
        backgroundSprite.setOrigin(0,0);
        backgroundSprite.setSize(screenWidth, screenHeight);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        backgroundSprite.draw(game.batch);
        game.font.getData().setScale(5, 5);
        game.font.draw(game.batch, "Welcome", screenWidth/3, 2*screenHeight/3);
        game.font.draw(game.batch, "Press, hold and release \nto launch monkey", screenWidth/3, 2*screenHeight/3 - 200);
        game.font.draw(game.batch, "Tap anywhere to begin", screenWidth/3, 2*screenHeight/3 - 400);
        game.batch.end();

        if(Gdx.input.justTouched()){
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
