package lin.appliance;

import lin.gear.Generator;

import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bulb extends Appliance {
    public Bulb(float pX, float pY, float pWidth, float pHeight,
            ITiledTextureRegion pTiledTextureRegion, double pVoltage, Generator pGenerator,
            VertexBufferObjectManager pTiledSpriteVertexBufferObject) {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVoltage, 5,
                pGenerator, pTiledSpriteVertexBufferObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int judgeState(double velocity) {
        if (dead) {
            return currentState = 4;
        }
        if (velocity < 0.5f*voltage){
            work = false;
            currentState = 0;
        }else if (velocity < 0.8f*voltage){
            work = true;
            currentState = 1;
        }else if (velocity < 1.2f*voltage){
            work = true;
            currentState = 2;
        }else if (velocity < 1.5f*voltage){
            work = true;
            currentState = 3;
        }else {
            currentState = 4;
            work = false;
            dead = true;
        }
        return currentState;
    }

}
