package com.doncapo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Havard on 29.04.2017.
 */
public class ItemCarrier {
    private Rectangle itemCarrierStat;
    public Texture itemCarrierTexture;
    public com.doncapo.game.Items.Item carriedItem;
    public boolean spentDamage;
    private final float baselineY = 256;
    private final int baselineWidth = 190;
    private final int baselineHeight = 300;
    private final int baselineReduction = 30;
    private float reduction;
    private int movementSpeed;

    public ItemCarrier(Texture itemCarrierTexture, com.doncapo.game.Items.Item carriedItem, int movementSpeed){
        this.itemCarrierStat = new Rectangle();
        this.itemCarrierTexture = itemCarrierTexture;
        this.carriedItem = carriedItem;
        float newHeight = carriedItem.getY()-baselineY;
        float scale = newHeight/baselineHeight;
        float newWidth = (newHeight/baselineHeight)*baselineWidth;
        reduction = baselineReduction*scale;
        itemCarrierStat.setSize(newWidth, newHeight-reduction);
        itemCarrierStat.setSize(newWidth, newHeight);
        float fruitMidX = carriedItem.getX()+(carriedItem.getWidth()/2);
        itemCarrierStat.setX(fruitMidX- newWidth/2);
        itemCarrierStat.setY(baselineY);
        spentDamage = false;
        this.movementSpeed = movementSpeed;
    }

    public void update(float delta){
        itemCarrierStat.x -= movementSpeed * delta;
    }

    public void draw(SpriteBatch batch){
        batch.draw(itemCarrierTexture, getX(), getY(), getWidth(), getHeight() + reduction);
    }
    public float getX(){
        return itemCarrierStat.getX();
    }

    public float getY(){
        return itemCarrierStat.getY();
    }

    public float getWidth(){ return itemCarrierStat.getWidth(); }
    public float getHeight(){ return itemCarrierStat.getHeight(); }

    public boolean overlaps(Rectangle otherRect){
        return itemCarrierStat.overlaps(otherRect);
    }
}
