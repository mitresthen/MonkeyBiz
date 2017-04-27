package com.doncapo.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Havard on 23.04.2017.
 */
public class Fruit {
    private Rectangle raindropStat;
    public Texture fruitTexture;

    public Fruit(Texture fruitTexture, float x, float y){
        this.raindropStat = new Rectangle();
        raindropStat.width = 128;
        raindropStat.height = 128;
        raindropStat.x = x;
        raindropStat.y = y;
        this.fruitTexture = fruitTexture;
    }

    public void update(int dropFactor, float delta){
        raindropStat.x -= dropFactor * delta;
    }

    public float getX(){
        return raindropStat.getX();
    }

    public float getY(){
        return raindropStat.getY();
    }

    public float getWidth(){ return raindropStat.getWidth(); }

    public boolean overlaps(Rectangle otherRect){
        return raindropStat.overlaps(otherRect);
    }
}
