package com.doncapo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Havard on 29.04.2017.
 */
public class PlayerCharacter {
    private final int starterLives = 3;
    public final float bucketRestPosition;
    private int lives;

    private boolean grabbing;

    private Texture grabbedItemTexture;

    private float screenWidth;
    private float screenHeight;

    private final float standardWidth = 256;
    private final float standardHeight = 256;
    public Rectangle playerRectangle;
    private Texture defaultTexture;
    private Texture grabbingTexture;

    private Texture branchTexture;
    private Rectangle branchRectangle;
    private final float branchWidth = 256;
    private final float branchHeight = 64;

    private Texture tetherTexture;
    private Rectangle tetherRectangle;
    private final float tetherWidth = 256;
    private final float tetherHeight = 16;

    public PlayerCharacter(Texture branchTexture, Texture defaultTexture, Texture grabbingTexture, Texture tetherTexture, int screenWidth, int screenHeight){
        bucketRestPosition = screenHeight-(2*standardHeight);
        this.lives = starterLives;
        this.branchTexture = branchTexture;
        this.defaultTexture = defaultTexture;
        this.tetherTexture = tetherTexture;
        this.grabbingTexture = grabbingTexture;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        createMonkey();
        createMonkeyTail();
        createTether();
    }

    private void createMonkey(){
        playerRectangle = new Rectangle();
        playerRectangle.setSize(standardWidth, standardHeight);
        playerRectangle.setX((screenWidth/2)-(playerRectangle.getWidth()/2));
        playerRectangle.setY(bucketRestPosition);
    }

    private void createMonkeyTail(){
        branchRectangle = new Rectangle();
        branchRectangle.setSize(branchWidth, branchHeight);
        branchRectangle.setX((screenWidth/2)-(branchTexture.getWidth()/2));
        branchRectangle.setY(screenHeight-branchWidth);
    }

    private void createTether(){
        tetherRectangle = new Rectangle();
        tetherRectangle.setSize(tetherWidth, tetherHeight);
    }

    public void drawBranch(SpriteBatch batch){
        drawGeneral(batch, branchTexture, branchRectangle);
    }

    public void drawPlayer(SpriteBatch batch){
        if(grabbing){
            drawGeneral(batch, grabbingTexture, playerRectangle);
            batch.draw(grabbedItemTexture, (screenWidth/2)-(grabbedItemTexture.getWidth()/2), playerRectangle.getY()-(grabbedItemTexture.getHeight()/2));
        }
        else {
            drawGeneral(batch, defaultTexture, playerRectangle);
        }
        drawTether(batch);

    }

    private void drawTether(SpriteBatch batch){
        float segments = (int)(((branchRectangle.getY() - (playerRectangle.getY() + playerRectangle.getHeight()))/tetherRectangle.getHeight()));
        int segmentsDiscrete = MathUtils.ceil(segments);
        segmentsDiscrete += 1;
        for(int i = 0; i< segmentsDiscrete; i++){
            batch.draw(tetherTexture,(screenWidth/2)-(tetherTexture.getWidth()/2), playerRectangle.getY() + playerRectangle.getHeight() + i*tetherRectangle.getHeight());
        }
    }

    public void grabItem(Item item){
        grabbedItemTexture = item.itemTexture;
        grabbing = true;
    }

    public void releaseItem(){
        grabbing = false;
    }

    public void translateY(float translateVal){
        playerRectangle.setY(playerRectangle.getY() + translateVal);
    }

    private void drawGeneral(SpriteBatch batch, Texture texture, Rectangle rectangle){
        batch.draw(texture, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public void update(int dropFactor, float delta){
        //itemCarrierStat.x -= dropFactor * delta;
    }

    public int getLives(){
        return lives;
    }

    public void gainLife(){
        lives++;
    }

    public void looseLife(){
        lives--;
    }
}
