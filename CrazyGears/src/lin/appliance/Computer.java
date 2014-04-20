package lin.appliance;

import lin.gear.Generator;

import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Computer extends Appliance {

    public Computer(float pX, float pY, float pWidth, float pHeight,
            ITiledTextureRegion pTiledTextureRegion, double pVoltage,
            int pStateNum, Generator pGenerator,
            VertexBufferObjectManager pTiledSpriteVertexBufferObject) {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVoltage, 3,
                pGenerator, pTiledSpriteVertexBufferObject);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int judgeState(double velocity) {
        if (velocity < 0.5f*voltage){
            work = false;
            currentState = 0;
        }else if (velocity < 1.5f*voltage){
            work = true;
            currentState = 1;
        }else {
            work = false;
            currentState = 2;
            dead = true;
        }
        
        return currentState;
    }

}
