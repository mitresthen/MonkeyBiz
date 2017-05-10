package com.doncapo.game.Items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.doncapo.game.PlayerCharacter;
import com.doncapo.game.Theme;

/**
 * Created by Havard on 23.04.2017.
 */
public abstract class Item {

    private Rectangle itemRectangle;
    public Texture itemTexture;
    private int movementSpeed;

    public Item(Texture itemTexture, float x, float y, int movementSpeed){
        this.itemRectangle = new Rectangle();
        itemRectangle.width = 128;
        itemRectangle.height = 128;
        itemRectangle.x = x;
        itemRectangle.y = y;
        this.itemTexture = itemTexture;
        this.movementSpeed = movementSpeed;
    }

    public void update(float delta){
        itemRectangle.x -= movementSpeed * delta;
    }

    public abstract void effectGather(PlayerCharacter playerCharacter, Theme theme);
    public abstract void effectOffScreen(PlayerCharacter playerCharacter, Theme theme);

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
