package com.doncapo.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Havard on 23.04.2017.
 */
public class Raindrop implements Pool.Poolable {
    private Rectangle raindropStat;
    public boolean alive;

    public Raindrop(){
        this.raindropStat = new Rectangle();
        raindropStat.width = 64;
        raindropStat.height = 64;
    }

    public void init(float x, float y){
        raindropStat.x = x;
        raindropStat.y = y;
        alive = true;
    }

    @Override
    public void reset() {
        raindropStat.x = 0;
        raindropStat.y = 0;
        alive = false;
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
