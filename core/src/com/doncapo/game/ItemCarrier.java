package com.doncapo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Havard on 29.04.2017.
 */
public class ItemCarrier {
    private Rectangle itemCarrierStat;
    public Texture itemCarrierTexture;
    public Item carriedItem;
    public boolean spentDamage;
    private final float baselineY = 256;
    private final int baselineWidth = 190;
    private final int baselineHeight = 300;
    private float reduction = 0;

    public ItemCarrier(Texture itemCarrierTexture, Item carriedItem){
        this.itemCarrierStat = new Rectangle();
        this.itemCarrierTexture = itemCarrierTexture;
        this.carriedItem = carriedItem;
        float newHeight = carriedItem.getY()-baselineY;
        float newWidth = (newHeight/baselineHeight)*baselineWidth;
        reduction = newHeight/8;
        itemCarrierStat.setSize(newWidth, newHeight-reduction);
        float fruitMidX = carriedItem.getX()+(carriedItem.getWidth()/2);
        itemCarrierStat.setX(fruitMidX- newWidth/2);
        itemCarrierStat.setY(baselineY);
        spentDamage = false;
    }

    public float getReduction(){
        return reduction;
    }
    public void update(int dropFactor, float delta){
        itemCarrierStat.x -= dropFactor * delta;
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
