package com.doncapo.game.Items;

import com.badlogic.gdx.graphics.Texture;
import com.doncapo.game.PlayerCharacter;
import com.doncapo.game.Theme;

/**
 * Created by Havard on 05.05.2017.
 */
public class GoodItem extends Item {
    public GoodItem(Texture itemTexture, float x, float y, int movementSpeed) {
        super(itemTexture, x, y, movementSpeed);
    }

    @Override
    public void effectGather(PlayerCharacter playerCharacter, Theme theme) {
        playerCharacter.gainScore();
        theme.actionSound.play();
    }

    @Override
    public void effectOffScreen(PlayerCharacter playerCharacter, Theme theme) {
        playerCharacter.looseLife();
        theme.lostLifeSound.play();
    }
}
