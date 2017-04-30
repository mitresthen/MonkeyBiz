package com.doncapo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Havard on 30.04.2017.
 */
public class Theme {
    private String theme;
    private String basePath;

    private final String backgroundPath = "background/background.png";
    public Texture backgroundTexture;

    private final String charactersFolder = "characters";
    public final Array<Texture> characters;

    private final String playerFolder = "player";
    private final String playerTopPath = "playerTop.png";
    public Texture playerTop;
    private final String playerMainPath = "playerMain.png";
    public Texture playerMain;
    private final String playerGrabbingPath = "playerGrabbing.png";
    public Texture playerGrabbing;
    private final String playerTetherPath = "tetherSegment.png";
    public Texture playerTether;

    private final String itemsFolder = "items";
    private final String goodItemsFolder = "goodItems";
    public final Array<Texture> goodItems;


    private final String soundsFolder = "sounds";
    private final String actionFile = "action.wav";
    public Sound actionSound;
    private final String musicFile = "music.mp3";
    public Music loopingMusic;


    public Theme(String basePath){
        this.basePath = basePath;
        goodItems = new Array<Texture>();
        characters = new Array<Texture>();
    }

    public void loadRandomTheme(){
        FileHandle[] themes = Gdx.files.internal(basePath).list();
        int themeNr = MathUtils.random(1, themes.length);
        themeNr--;
        theme = themes[themeNr].path();

        String currentItemsFolder = folderCombine(theme, itemsFolder);
        loadTexturesForFolder(goodItems, folderCombine(currentItemsFolder, goodItemsFolder));

        loadTexturesForFolder(characters, folderCombine(theme, charactersFolder));

        String currentSoundsFolder = folderCombine(theme, soundsFolder);
        actionSound = Gdx.audio.newSound(Gdx.files.internal(folderCombine(currentSoundsFolder, actionFile)));
        loopingMusic = Gdx.audio.newMusic(Gdx.files.internal(folderCombine(currentSoundsFolder, musicFile)));
        backgroundTexture = new Texture(Gdx.files.internal(folderCombine(theme, backgroundPath)));

        String currentPlayerFolder = folderCombine(theme, playerFolder);
        playerTop = new Texture(Gdx.files.internal(folderCombine(currentPlayerFolder, playerTopPath)));
        playerMain = new Texture(Gdx.files.internal(folderCombine(currentPlayerFolder, playerMainPath)));
        playerGrabbing = new Texture(Gdx.files.internal(folderCombine(currentPlayerFolder, playerGrabbingPath)));
        playerTether = new Texture(Gdx.files.internal(folderCombine(currentPlayerFolder, playerTetherPath)));

    }

    private void loadTexturesForFolder(Array<Texture> textureArray, String folderToLoadFrom){
        FileHandle[] textureItemsHandles = Gdx.files.internal(folderToLoadFrom).list();
        for (FileHandle textureFiles : textureItemsHandles ) {
            textureArray.add(new Texture(Gdx.files.internal(textureFiles.path())));
        }
    }

    public void dispose(){
        backgroundTexture.dispose();
        disposeArray(characters);
        disposeArray(goodItems);
        playerGrabbing.dispose();
        playerMain.dispose();
        playerTop.dispose();
        actionSound.dispose();
        loopingMusic.dispose();
    }

    private void disposeArray(Array<Texture> textureArray){
        for (Texture tex: textureArray) {
            tex.dispose();
        }
    }
    private String folderCombine(String first, String second){
        return first + "/" + second;
    }
}
