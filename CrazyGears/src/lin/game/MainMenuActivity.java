package lin.game;
 /**
 * by Adrian Nilsson (ade at ade dot se)
 * BIG IRON GAMES (bigirongames.org)
 *
 * modified from original by Nicolas Gramlich - http://code.google.com/p/andengineexamples/source/browse/src/org/anddev/andengine/examples/PhysicsExample.java
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.HashMap;
import java.util.Random;

import lin.lib.PhysicsEditorShapeLibrary;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class MainMenuActivity extends BaseGameActivity implements IOnSceneTouchListener{
	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH;
	private static int CAMERA_HEIGHT;
	private Handler mHandler;

	// ===========================================================
	// Fields
	// ===========================================================
	private BuildableBitmapTextureAtlas buildableBitmapTextureAtlas;
	//private TextureRegion mTextureRegion;
    private PhysicsEditorShapeLibrary physicsEditorShapeLibrary;
	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
    private ITextureRegion textureGear;
    private ITextureRegion textureBackground;
    DisplayMetrics metric;
    private Sprite backgroundSprite;
    Body startBody;
    Body optionsBody;
    Body aboutBody;
    RevoluteJointDef revoluteJointDefStart;
    ZoomGear startSprite;
    ZoomGear optionsSprite;
    ZoomGear aboutSprite;
    PhysicsConnector startConnector;
    Rectangle touchRectangle;

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
	    touchRectangle = new Rectangle(arg1.getX()-1, arg1.getY()-1, 2, 2, getVertexBufferObjectManager());
	    if (!startSprite.collidesWith(touchRectangle)){
	        startSprite.setScale(1.0f);
	    }else{
	        startSprite.onAreaTouched(arg1, 0, 0);
	    }
	    
	    if (!optionsSprite.collidesWith(touchRectangle)){
	        optionsSprite.setScale(1.0f);
        }else{
            optionsSprite.onAreaTouched(arg1, 0, 0);
        }
	    
	    if (!aboutSprite.collidesWith(touchRectangle)){
	        aboutSprite.setScale(1.0f);
        }else{
            aboutSprite.onAreaTouched(arg1, 0, 0);
        }
	    
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog alertDialog = new AlertDialog.Builder(this)
			.setTitle("提示").setMessage("是否退出程序？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).create();
			alertDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
    @Override
    public EngineOptions onCreateEngineOptions() {
        mHandler = new Handler();
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        CAMERA_WIDTH = metric.widthPixels;     // 屏幕宽度（像素）
        CAMERA_HEIGHT = metric.heightPixels;   // 屏幕高度（像素）
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback arg0)
            throws Exception {
        /* Manual layout of TextureRegions. */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        buildableBitmapTextureAtlas = new BuildableBitmapTextureAtlas(getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);

        textureGear= BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, getAssets(), "BigGear.png");
        textureBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(buildableBitmapTextureAtlas, getAssets(), "bg_black.png");
        try {
            buildableBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
        } catch (TextureAtlasBuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        buildableBitmapTextureAtlas.load();
        //Create the shape importer and open our shape definition file
        this.physicsEditorShapeLibrary = new PhysicsEditorShapeLibrary();
        
        this.physicsEditorShapeLibrary.open(this, "xml/BigGear.xml");
        arg0.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback arg0) throws Exception {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mScene = new Scene();
        backgroundSprite = new Sprite(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, textureBackground, getVertexBufferObjectManager());
        SpriteBackground background = new SpriteBackground(backgroundSprite);
        this.mScene.setBackground(background);
        mScene.setBackgroundEnabled(true);
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        startSprite = new ZoomGear(0, 0, textureGear, getVertexBufferObjectManager());
       
        startSprite.setPosition(400 - startSprite.getWidth()/2, 240 - startSprite.getHeight()/2);
        startSprite.setColor(0f, 1f, 1f);

        optionsSprite = new ZoomGear(0, 0, textureGear.deepCopy(), getVertexBufferObjectManager());
        optionsSprite.setPosition(400 - startSprite.getWidth()/2 + 66, 240 - startSprite.getHeight()/2 + 66);
        optionsSprite.setColor(1f, 0, 0);
        
        aboutSprite = new ZoomGear(0, 0, textureGear.deepCopy(), getVertexBufferObjectManager());
        aboutSprite.setPosition(400 - startSprite.getWidth()/2 - 66, 240 - startSprite.getHeight()/2 + 66);
        aboutSprite.setColor(1, 1, 0);

        //Get physics body
        startBody = this.physicsEditorShapeLibrary.createBody("BigGear", startSprite, this.mPhysicsWorld);
        
        //this.physicsEditorShapeLibrary.open(this, "xml/exitGear.xml");
        optionsBody = this.physicsEditorShapeLibrary.createBody("BigGear", optionsSprite, this.mPhysicsWorld);
        optionsBody.setAngularDamping(1f);
        aboutBody = this.physicsEditorShapeLibrary.createBody("BigGear", aboutSprite, mPhysicsWorld);
        aboutBody.setAngularDamping(1f);

        //Attach to physics world & scene
        this.mScene.attachChild(startSprite);
        this.mScene.attachChild(optionsSprite);
        this.mScene.attachChild(aboutSprite);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(startSprite, startBody, true, true));
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(optionsSprite, optionsBody, true, true));
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(aboutSprite, aboutBody, true, true));
        FixtureDef BallBodyFixtureDef = PhysicsFactory.createFixtureDef(20f, 0f, 0f);

        Body startAnchorBody = PhysicsFactory.createCircleBody(mPhysicsWorld, startSprite.getX()+startSprite.getWidth()/2, 
                startSprite.getY()+startSprite.getHeight()/2, 0, BodyType.StaticBody, BallBodyFixtureDef);
        Body optionsAnchorBody = PhysicsFactory.createCircleBody(mPhysicsWorld, optionsSprite.getX()+optionsSprite.getWidth()/2, 
                optionsSprite.getY()+optionsSprite.getHeight()/2, 0, BodyType.StaticBody, BallBodyFixtureDef);
        Body aboutAnchorBody = PhysicsFactory.createCircleBody(mPhysicsWorld, aboutSprite.getX()+aboutSprite.getWidth()/2, 
                aboutSprite.getY()+aboutSprite.getHeight()/2, 0, BodyType.StaticBody, BallBodyFixtureDef);
        // Revolute
    
        revoluteJointDefStart = new RevoluteJointDef();
        revoluteJointDefStart.initialize(
                startAnchorBody,
                startBody,
                startBody.getWorldCenter());
        revoluteJointDefStart.collideConnected = false;
        revoluteJointDefStart.enableMotor = true;
        revoluteJointDefStart.maxMotorTorque = 5000f;
        revoluteJointDefStart.motorSpeed = 1f;
        mPhysicsWorld.createJoint(revoluteJointDefStart);
    
        final RevoluteJointDef revoluteJointDefOptions = new RevoluteJointDef();
        revoluteJointDefOptions.initialize(
                optionsAnchorBody,
                optionsBody,
                optionsBody.getWorldCenter());
        revoluteJointDefOptions.collideConnected = false;
        optionsBody.setAngularDamping(1f);
        mPhysicsWorld.createJoint(revoluteJointDefOptions);
        
        //ballSprite2.setVisible(false);
        this.mScene.registerUpdateHandler(this.mPhysicsWorld);
        
        final RevoluteJointDef revoluteJointDefAbout = new RevoluteJointDef();
        revoluteJointDefAbout.initialize(
                aboutAnchorBody,
                aboutBody,
                aboutBody.getWorldCenter());
        revoluteJointDefAbout.collideConnected = false;
        aboutBody.setAngularDamping(1f);
        mPhysicsWorld.createJoint(revoluteJointDefAbout);
        
        this.mScene.registerUpdateHandler(this.mPhysicsWorld);
        this.mScene.setOnSceneTouchListener(this);
        mScene.registerTouchArea(startSprite);
        //mScene.setTouchAreaBindingEnabled(true);
        arg0.onCreateSceneFinished(mScene);
        
    }

    @Override
    public void onPopulateScene(Scene arg0, OnPopulateSceneCallback arg1)
            throws Exception {
        arg1.onPopulateSceneFinished();
        
    }
    class ZoomGear extends Sprite{
        //ScaleAtModifier scaleAtModifier = new ScaleAtModifier(0.5f, 1.0f, 0.9f, centerX, centerY);
        public ZoomGear(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
            super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        }
        
        @Override
        public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                float pTouchAreaLocalX, float pTouchAreaLocalY) {
            switch (pSceneTouchEvent.getAction()) {
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                this.setScale(0.90f);
                break;
            case TouchEvent.ACTION_CANCEL:
            case TouchEvent.ACTION_OUTSIDE:
            case TouchEvent.ACTION_UP:
                this.setScale(1.0f);
//              Intent intent = new Intent(MainMenuActivity.this, SelectLevelActivity.class);
//              startActivity(intent);
                break;
            default:
                break;
            }
            return false;
        }
    }

}



