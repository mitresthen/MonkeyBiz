package com.doncapo.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Havard on 23.04.2017.
 */
public class Item {
    private Rectangle itemRectangle;
    public Texture itemTexture;

    public Item(Texture itemTexture, float x, float y){
        this.itemRectangle = new Rectangle();
        itemRectangle.width = 128;
        itemRectangle.height = 128;
        itemRectangle.x = x;
        itemRectangle.y = y;
        this.itemTexture = itemTexture;
    }

    public void update(int dropFactor, float delta){
        itemRectangle.x -= dropFactor * delta;
    }

    public float getX(){
        return itemRectangle.getX();
    }

    public float getY(){
        return itemRectangle.getY();
    }

    public float getWidth(){ return itemRectangle.getWidth(); }

    public boolean overlaps(Rectangle otherRect){
        return itemRectangle.overlaps(otherRect);
    }
}
