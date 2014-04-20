package lin.appliance;

import java.util.ArrayList;

import lin.game.ToolMainActivity;
import lin.gear.Generator;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.R.integer;
import android.util.Log;

public abstract class Appliance extends TiledSprite{
    int currentState = 0;
    double voltage;
    int stateNum;
    public boolean dead = false;
    public boolean work = false;
    Generator generator;
    ITiledTextureRegion mTiledTextureRegion;
    Text voltage_text;
    
    public Appliance(float pX, float pY, float pWidth, float pHeight,
            ITiledTextureRegion pTiledTextureRegion, double pVoltage, int pStateNum, Generator pGenerator,
            VertexBufferObjectManager pTiledSpriteVertexBufferObject) {
        super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
                pTiledSpriteVertexBufferObject);
        setZIndex(1);
        mTiledTextureRegion = pTiledTextureRegion;
        voltage = pVoltage;
        stateNum = pStateNum;
        generator = pGenerator;
        voltage_text = new Text(this.getWidth()/4, -30, ToolMainActivity.FONT_DEFAULT_32BOLD,
                ""+(int)voltage+"V", pTiledSpriteVertexBufferObject);
        this.attachChild(voltage_text);
    }
    public void updateState(){
        /*if (currentState != 0)
            Log.d("lin", "state: "+currentState);*/
        if (dead == true) {
            changeState(currentState);
            return;
        }
        judgeState(generator.getAngularVelocity());
        changeState(currentState);
    }
    public void changeState(int pState){
        currentState = pState;
        setCurrentTileIndex(currentState);
    }
    public abstract int judgeState(double velocity);
    
}
