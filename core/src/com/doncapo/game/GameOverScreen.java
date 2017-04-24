package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by Havard on 23.04.2017.
 */
public class GameOverScreen implements Screen {
    final Main game;
    final int finalScore;

    OrthographicCamera camera;
    public GameOverScreen(final Main game, final int finalScore){
        this.game = game;
        this.finalScore = finalScore;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
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
        game.font.draw(game.batch, "Game Over ", 100, 300);
        game.font.draw(game.batch, "Final score " + finalScore, 100, 200);
        game.font.draw(game.batch, "Tap anywhere to return to main menu!", 100, 100);
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
