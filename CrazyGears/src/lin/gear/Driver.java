package lin.gear;

import lin.game.ToolMainActivity;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.toolbar.GearCount;
import com.toolbar.Tool;

import android.R.bool;
import android.content.Context;

public class Driver extends Gear{
    protected boolean touchingByFinger;
    protected boolean collide;
    protected Body targetBody;
    protected MouseJointDef mouseJointDef;
    protected MouseJoint mouseJoint;
    protected Scene mScene;
    protected GearCount gearCount;
    protected Tool parentTool;
    public boolean isCombined;
    protected WeldJointDef mWeldJointDef = null;
    protected WeldJoint mWeldJoint = null;
    protected Body touchBody = null;
    protected FixtureDef touchFixtureDef;
    public Driver combineDriver = null;
    public boolean smaller = false;
    protected boolean unSetCombineAnchor = true;
    protected int touchcount = 0;
    protected long firstClick = 0, secondClick = 0, virtualFirstClick = 0;
    private float forceFactor = 1000f; 

    public Driver(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager mVertexBufferObjectManager, int pSize,
            Context pContext, PhysicsWorld pPhysicsWorld, String pAssetsPath ,Tool pParentTool,GearCount pGearCount,Scene pScene) {
        super(pX, pY, pTextureRegion, mVertexBufferObjectManager, pSize, pContext, pPhysicsWorld, pAssetsPath, pScene);
        gearBody.setUserData("Driver");
        this.setZIndex(9);
        pScene.attachChild(this);
        pScene.registerTouchArea(this);
        mScene = pScene;
        touchingByFinger = false;
        collide = false;
        gearCount = pGearCount;
        parentTool = pParentTool;
        this.getGearBody().setLinearDamping(2f);
        //mPhysicsWorld.dispose();
        
        targetBody = mPhysicsWorld.createBody(new BodyDef());
        
        targetBody.setType(BodyType.KinematicBody);
        mouseJointDef = new MouseJointDef();
        mouseJointDef.bodyA = targetBody;
        mouseJointDef.bodyB = this.getGearBody();
        mouseJointDef.dampingRatio = 0.7f;
        mouseJointDef.frequencyHz = 5f;
        mouseJointDef.maxForce = (forceFactor * this.getGearBody().getMass());
        mouseJointDef.collideConnected = false;
        isCombined = false;
        changeCircleFixture(FilterConstants.CATEGORY_BOTTOM, FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ONE);
        changeSensorFixture(FilterConstants.CATEGORY_SENSOR, FilterConstants.MASKBITS_FINGER, FilterConstants.GROUP_ZERO);
        touchFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0, true, FilterConstants.CATEGORY_FINGER, FilterConstants.MASKBITS_SENSOR, FilterConstants.GROUP_ZERO);
    }
    
    public static void combine(Driver driverA, Driver driverB, PhysicsWorld pPhysicsWorld, Scene pScene) {
        if ((driverA.isCombined || driverB.isCombined) || driverA.size == driverB.size)
            return;
        driverA.isCombined = true;
        driverB.isCombined = true;
        driverA.combineDriver = driverB;
        driverB.combineDriver = driverA;
        if (driverA.size > driverB.size) {
            driverA.smaller = false;
            driverB.smaller = true;
            pScene.unregisterTouchArea(driverB);
            //driverA.setAlpha(0.7f);
        }else {
            driverA.smaller = true;
            driverB.smaller = false;
            pScene.unregisterTouchArea(driverA);
            //driverB.setAlpha(0.7f);
        }
        
        WeldJointDef combineWeldJointDef = new WeldJointDef();
        driverA.changeCircleGroupIndex(FilterConstants.GROUP_ONE);
        driverA.setZIndex(9);
        driverA.setAlpha(1f);
        driverB.changeCircleGroupIndex(FilterConstants.GROUP_TWO);
        driverB.setZIndex(10);
        driverB.setAlpha(0.5f);
        
        if (driverB.mouseJoint != null) {
            driverB.destroyMouseJoint();
        } 
        if (driverB.touchBody != null) {
            pPhysicsWorld.destroyBody(driverB.touchBody);
            driverB.touchBody = null;
        }
        driverB.transform(driverA.getGearBody().getWorldCenter().x, driverA.getGearBody().getWorldCenter().y);
        combineWeldJointDef.initialize(driverA.getGearBody(), driverB.getGearBody(), driverA.getGearBody().getWorldCenter());
        driverA.mWeldJoint = driverB.mWeldJoint = (WeldJoint) pPhysicsWorld.createJoint(combineWeldJointDef);
        driverA.mWeldJointDef = combineWeldJointDef;
        driverB.mWeldJointDef = combineWeldJointDef;
    }
    public void destroyMouseJoint() {
        if (mouseJoint != null) {
            mPhysicsWorld.destroyJoint(mouseJoint);
            mouseJoint = null;
            
        }
    }
    
    public boolean removeDriver(Driver pDriver) {
        if (pDriver.hasDelete) {
            return true;
        }
        final EngineLock engineLock = new EngineLock(false);
        engineLock.lock();
        if (pDriver.mouseJoint != null) {
            mPhysicsWorld.destroyJoint(pDriver.mouseJoint);
            pDriver.mouseJoint = null;
        }
        if (pDriver.targetBody != null) {
            pDriver.mPhysicsWorld.destroyBody(pDriver.targetBody);
            pDriver.targetBody = null;
        }
        
        pDriver.destroyJointAndBody();
        DriversManager.drivers.remove(pDriver);
        int count = pDriver.gearCount.getCount();
        mScene.unregisterTouchArea(pDriver);
        mScene.detachChild(pDriver);
        
        pDriver.gear = null;
        pDriver.parentTool.setCurrentTileIndex(count+1);
        pDriver.gearCount.setCount(count+1);
        pDriver.hasDelete = true;
        engineLock.unlock();
        try {
            pDriver.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        
        switch (pSceneTouchEvent.getAction()) {
        case TouchEvent.ACTION_DOWN:
            touchcount++;
            if (touchcount == 1) {
                firstClick = System.currentTimeMillis();
                if (firstClick - virtualFirstClick < 500) {
                    toggleGroupIndex(this);
                    if (this.isCombined) {
                        toggleGroupIndex(this.combineDriver);
                    }
                    touchcount = 0;
                    firstClick = 0;
                    secondClick = 0;
                    virtualFirstClick = 0;
                }
            }else if (touchcount == 2){
                virtualFirstClick = secondClick = System.currentTimeMillis();
                if (firstClick != 0 && secondClick - firstClick < 500) {
                    virtualFirstClick = 0;
                    toggleGroupIndex(this);
                    if (this.isCombined) {
                        toggleGroupIndex(combineDriver);
                    }
                }
                touchcount = 0;
                firstClick = 0;
                secondClick = 0;
            }
            setAnchorAsDynamic(true);
            //gearBody.setAngularVelocity(0);
            touchingByFinger = true;
            touchBody = PhysicsFactory.createBoxBody(mPhysicsWorld, pSceneTouchEvent.getX()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                    pSceneTouchEvent.getY()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 10, 10, BodyType.DynamicBody, touchFixtureDef);
            touchBody.setUserData("touchBody");
            if (this.isCombined) {
                mouseJointDef.maxForce = (this.getGearBody().getMass() + this.combineDriver.getGearBody().getMass())*forceFactor;
            }else {
                mouseJointDef.maxForce = (forceFactor * this.getGearBody().getMass());
            }
            mouseJointDef.target.set(getGearBody().getWorldCenter());
            mouseJoint = (MouseJoint)mPhysicsWorld.createJoint(mouseJointDef);
            
            final Vector2 vec = Vector2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            mouseJoint.setTarget(vec);
            Vector2Pool.recycle(vec);
            break;
        case TouchEvent.ACTION_CANCEL:
        case TouchEvent.ACTION_OUTSIDE:
        case TouchEvent.ACTION_UP: 
            if(this.getX()+this.centerX>ToolMainActivity.CAMERA_WIDTH-50){
                if (this.isCombined) {
                    removeDriver(this.combineDriver);
                    mScene.detachChild(this.combineDriver.velocityText);
                }
                removeDriver(this);
                mScene.detachChild(velocityText);
            }
            if (hasDelete)
                return true;
            
            if (mouseJoint != null) {
                engineLock.lock();
                mPhysicsWorld.destroyJoint(mouseJoint);
                mouseJoint = null;
                engineLock.unlock();
            }   
            if (touchBody != null) {
                mPhysicsWorld.destroyBody(touchBody);
                touchBody = null;
            }
            touchingByFinger = false;
            if (!this.smaller) {
                setAnchorAsDynamic(false);
                if (this.isCombined && unSetCombineAnchor) {
                    this.combineDriver.setAnchorAsDynamic(true);
                    unSetCombineAnchor = false;
                }
            }
            break;
        case TouchEvent.ACTION_MOVE:
            final Vector2 vec2 = Vector2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            
            if (mouseJoint != null)
                mouseJoint.setTarget(vec2);
            Vector2Pool.recycle(vec2);
            if (touchBody != null) {
                
                touchBody.setTransform(pSceneTouchEvent.getX()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
                    pSceneTouchEvent.getY()/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
            }
            if (combineDriver != null && !this.isCombined) {
                combine(combineDriver, this, mPhysicsWorld, mScene);
            }
            
            break;
        default:
            break;
        }
        velocityText.setPosition(getX()+centerX-velocityText.getWidth()/2, getY()+centerY-velocityText.getHeight()/2);
        return true;
    }
    public boolean isCollide() {
        return collide;
    }
    public void setCollide(boolean pCollid) {
        collide = pCollid;
    }
      
    public boolean isTouchingByFinger() {
        return touchingByFinger;
    }
    @Override
    public void showVelocityText() {
        velocityText.setPosition(getX()+centerX-velocityText.getWidth()/2, getY()+centerY-velocityText.getHeight()/2);
        velocityText.registerUpdateHandler(velocityTextUpdateHandler);
        if (combineDriver!=null && this.size > combineDriver.size) {
            velocityText.setVisible(true);
        }
        if (combineDriver==null) {
            velocityText.setVisible(true);
        }
    }
    @Override
    public void hideVelocityText() {
        velocityText.setVisible(false);
        velocityText.unregisterUpdateHandler(velocityTextUpdateHandler);
    }
    
}
