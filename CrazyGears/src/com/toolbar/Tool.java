package com.toolbar;

import java.util.List;

import lin.game.MainMenuActivity;
import lin.gear.Driver;
import lin.gear.DriversManager;
import lin.gear.Gear;
import lin.gear.GearSize;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.util.Log;

public class Tool extends TiledSprite{

	private Context mContext;
	private Scene mScene;
	private TextureRegion gearTextureRegion;
	protected TextureRegion anotherGearTextureRegion;
	private GearCount gearCount;
	private VertexBufferObjectManager mSpriteVertexBufferObject;
	protected int size;
	protected int anotherSize;
	protected PhysicsWorld physicsWorld;
	protected String path;
	protected int type; //0 for single driver
	                    //1 for Double driver 
	                    //2 for LineJoint driver
	public Tool(float pX, float pY, float pWidth, float pHeight,Scene mScene,
			TiledTextureRegion pToolTextureRegion, Context context,
			TextureRegion pGearTextureRegion,List<TiledTextureRegion> bTextureRegions,
			GearCount gearCount,VertexBufferObjectManager mTiledSpriteVertexBufferObject, int pSize,
			PhysicsWorld pPhysicsWorld, String pPath) {
		super(pX, pY, pWidth, pHeight, pToolTextureRegion, mTiledSpriteVertexBufferObject);
		mContext = context ;
		this.mScene = mScene ;
		mScene.registerTouchArea(this);
		gearTextureRegion = pGearTextureRegion;
		this.gearCount = gearCount;
		this.gearCount.setCount(gearCount.getCount());
		this.mSpriteVertexBufferObject = mTiledSpriteVertexBufferObject;
		size = pSize;
		physicsWorld = pPhysicsWorld;
		path = pPath;
		type = 0;
	}
	public Tool(float pX, float pY, float pWidth, float pHeight,Scene mScene,
            TiledTextureRegion pToolTextureRegion, Context context,
            TextureRegion internalRegion, TextureRegion externalRegion, List<TiledTextureRegion> bTextureRegions,
            GearCount gearCount,VertexBufferObjectManager mTiledSpriteVertexBufferObject, int internalSize, int externalSize,
            PhysicsWorld pPhysicsWorld, String pPath) {
	    super(pX, pY, pWidth, pHeight, pToolTextureRegion, mTiledSpriteVertexBufferObject);
	    mContext = context ;
        this.mScene = mScene ;
        mScene.registerTouchArea(this);
        gearTextureRegion = internalRegion;
        anotherGearTextureRegion = externalRegion;
        this.gearCount = gearCount;
        this.gearCount.setCount(gearCount.getCount());
        this.mSpriteVertexBufferObject = mTiledSpriteVertexBufferObject;
        size = internalSize;
        anotherSize = externalSize;
        physicsWorld = pPhysicsWorld;
        path = pPath;
        type = 1;
	}

	@Override  
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,  
            final float pTouchAreaLocalY)  
    {  
       if (pSceneTouchEvent.isActionDown())  
        {  
			if(gearCount.getCount()>0){
			    if (type == 0) {
    				final Driver driver = new Driver(pSceneTouchEvent.getX()-gearTextureRegion.getWidth()/2, 
    				        pSceneTouchEvent.getY()-gearTextureRegion.getHeight()/2,
    						gearTextureRegion, mSpriteVertexBufferObject, size, mContext, physicsWorld, path, this, gearCount, mScene);
    				/*Log.i("lin", "getx: "+driver.getX());
    				Log.i("lin", "gety: "+driver.getY());*/
    				DriversManager.drivers.add(driver);
    				driver.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			    }
			    
				this.setCurrentTileIndex(gearCount.getCount()-1);
				gearCount.setCount(gearCount.getCount()-1);
				
			}
        }
        if (pSceneTouchEvent.isActionUp())  
        {  
        }  
       else if (pSceneTouchEvent.isActionMove()){
       }
        return false;  
    } 

}


/*{
@Override
public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
	switch (pSceneTouchEvent.getAction()) {
	case TouchEvent.ACTION_DOWN:
		Toast.makeText(mContext, "down1", Toast.LENGTH_SHORT).show();
		break;

	default:
		break;
	}
		
		if(pSceneTouchEvent.isActionMove()){

			Toast.makeText(mContext, "Moving", Toast.LENGTH_SHORT).show();
			this.setPosition(pSceneTouchEvent.getX()-this.getWidth()/2, pSceneTouchEvent.getY()-this.getHeight()/2);
		}
	return super
			.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
}
}*/
