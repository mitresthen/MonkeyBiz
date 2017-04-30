package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Havard on 23.04.2017.
 */
public class GameOverScreen implements Screen {
    final Main game;
    final int finalScore;

    private int screenWidth = 1440;
    private int screenHeight = 2560;

    OrthographicCamera camera;
    public GameOverScreen(final Main game, final int finalScore){
        this.game = game;
        this.finalScore = finalScore;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        game.font.getData().setScale(6, 4);
        game.font.draw(game.batch, "Game Over ", screenWidth/3, screenHeight-300);
        game.font.draw(game.batch, "Final score " + finalScore, screenWidth/3, screenHeight-500);
        game.font.draw(game.batch, "Tap anywhere to \nreturn to main menu!", screenWidth/3 - 50, screenHeight-700);
        game.batch.end();

        if(Gdx.input.justTouched()){
            game.setScreen(new MainMenuScreen(game));
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
