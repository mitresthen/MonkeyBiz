package com.doncapo.game.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.doncapo.game.Theme;

/**
 * Created by Havard on 05.05.2017.
 */
public class ItemFactory {

    private static final int goodItemSpawnRate = 80; // x = x*10% chance of spawn
    private static final int bonusItemSpawnRate = 5 + goodItemSpawnRate;
    private static final int badItemSpawnRate = 15 + bonusItemSpawnRate;

    public static Item createRandomItem(Theme theme, float x, float y, int movementSpeed){
        int random = MathUtils.random(0, 101);
        if(random < goodItemSpawnRate){
            return new GoodItem(selectRandomTexture(theme.goodItems), x, y, movementSpeed);
        }
        if(random >=goodItemSpawnRate && random < bonusItemSpawnRate){
            return new BonusItem(selectRandomTexture(theme.bonusItems), x, y, movementSpeed);
        }else{
            return new BadItem(selectRandomTexture(theme.badItems), x, y, movementSpeed);
        }
    }

    private static Texture selectRandomTexture(Array<Texture> textures){
        int size = textures.size;
        return textures.get(MathUtils.random(0, size-1));
    }
}
