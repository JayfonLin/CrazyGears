package com.toolbar;

import java.util.ArrayList;
import java.util.List;

import lin.game.ToolMainActivity;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.*;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


import android.content.Context;


public class ToolbarManager extends Sprite {
	
	private int CTool = 0 ;
	private float xposition = 0 ;
	private float yposition = 0 ;
	private final Scene mScene;
	private Context mContext ;
	private VertexBufferObjectManager mSpriteVertexBufferObject;
	private List<TiledTextureRegion> bTextureRegions = new ArrayList<TiledTextureRegion>();
	
	public ToolbarManager(float pX, float pY, Scene scene, Context mContext, TextureRegion toolBarTextureRegion,
			List<TiledTextureRegion> bTextureRegion,VertexBufferObjectManager mSpriteVertexBufferObject){
		//super(xp, yp, textureRegion, mSpriteVertexBufferObject);
		super(pX, pY, 50, ToolMainActivity.CAMERA_HEIGHT-50, toolBarTextureRegion, mSpriteVertexBufferObject);
		this.setZIndex(2);
		mScene = scene;
		this.mContext = mContext;
		this.bTextureRegions.addAll(bTextureRegion);
		this.mSpriteVertexBufferObject = mSpriteVertexBufferObject;
	}
	public void addTool(TextureRegion mtextureRegion,int gearNum,GearCount gearCount, int pSize, PhysicsWorld pPhysicsWorld, String pPath){
		//change the picture size
		final Tool Splash = new Tool(xposition, yposition, 50, 50, mScene, bTextureRegions.get(gearNum), mContext,
		        mtextureRegion, bTextureRegions, gearCount, mSpriteVertexBufferObject, pSize, pPhysicsWorld, pPath);
		Splash.setCurrentTileIndex(gearCount.getCount());
		this.attachChild(Splash);
		yposition = yposition+50;							//change the picture instance
		CTool++ ;
	}
	/*public void addTool(TextureRegion internalRegion, TextureRegion externalRegion, int internalSize, int externalSize,
            int gearNum, GearCount gearCount, PhysicsWorld pPhysicsWorld, String path){
	    final Tool Splash = new Tool(xposition, yposition, 50, 50, mScene, bTextureRegions.get(gearNum), mContext, 
	            internalRegion, externalRegion, bTextureRegions, gearCount, mSpriteVertexBufferObject, internalSize, 
	            externalSize, pPhysicsWorld, path);

        Splash.setCurrentTileIndex(gearCount.getCount());
        mScene.attachChild(Splash);
        xposition = xposition+50 ;                          //change the picture instance
        CTool++ ;
    }*/
	public void addGearTextureRegion(TextureRegion textureRegion){
	}

}
