package lin.gear;

import java.text.DecimalFormat;
import java.util.ArrayList;

import lin.game.ToolMainActivity;
import lin.lib.PhysicsEditorShapeLibrary;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.toolbar.GearCount;
import com.toolbar.Tool;

public class Gear extends Sprite{
    public static int numberOfGears; //initialize to 0 in MainActivity
    public int size;
    public int id;
    protected float radius;
    protected float sensorRadius;
    public Text velocityText;
    //private Color gColor;
    protected Body gearBody;
    protected Body anchorBody;

    protected PhysicsEditorShapeLibrary physicsEditorShapeLibrary;
    protected float centerX, centerY;
    protected RevoluteJointDef mRevoluteJointDef;
    protected RevoluteJoint mRevoluteJoint;
    protected PhysicsWorld mPhysicsWorld;
    protected PhysicsConnector gearConnector;
    protected FixtureDef anchorFixtureDef;
    protected IUpdateHandler velocityTextUpdateHandler;

    protected Gear gear;
    protected Scene mScene;
    protected boolean hasDelete = false;
    protected EngineLock engineLock = new EngineLock(false);
    public Gear(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int pSize,
            Context pContext, PhysicsWorld pPhysicsWorld, String pAssetsPath, Scene mScene) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		gear = this;
		this.mScene = mScene;
        this.id = numberOfGears++;
        centerX = this.getWidth()/2;
        centerY = this.getHeight()/2;
        size = pSize;
        mPhysicsWorld = pPhysicsWorld;
        anchorFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0, 0, false, FilterConstants.CATEGORY_ANCHOR, FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ZERO);
        this.anchorBody = PhysicsFactory.createCircleBody(mPhysicsWorld, this.getX()+centerX, this.getY()+centerY, 0, BodyType.StaticBody, anchorFixtureDef); 
        physicsEditorShapeLibrary = new PhysicsEditorShapeLibrary();
        physicsEditorShapeLibrary.open(pContext, pAssetsPath);
        switch (size) {
        case GearSize.SMALL:
            gearBody = physicsEditorShapeLibrary.createBody("style1_size50", this, pPhysicsWorld);
            //this.setColor(0, 0.75f, 0.75f);
            this.setColor(0.66015625f, 0.66015625f, 0.66015625f);
            break;
        case GearSize.MIDDLE:
            gearBody = physicsEditorShapeLibrary.createBody("style2_size100", this, pPhysicsWorld);
            //this.setColor(0, 1, 1);
            break;
        case GearSize.BIG:
            gearBody = physicsEditorShapeLibrary.createBody("style3_size150", this, pPhysicsWorld);
            this.setColor(0.66015625f, 0.66015625f, 0.66015625f);
            break;
        case GearSize.SUPER_BIG:
            gearBody = physicsEditorShapeLibrary.createBody("style4_size200", this, pPhysicsWorld);
            //this.setColor(0, 1, 1);
            break;
        case GearSize.GENERATOR:
            gearBody = physicsEditorShapeLibrary.createBody("generator", this, pPhysicsWorld);
            //this.setColor(0, 0, 1);
            break;
        case GearSize.STARTER:
            gearBody = physicsEditorShapeLibrary.createBody("starter", this, pPhysicsWorld);
            //this.setColor(1, 1, 0);
            break;
        default:
            break;
        }
        for (Fixture fixture: gearBody.getFixtureList()) {
            if (fixture.getShape() instanceof CircleShape) {
                if (fixture.isSensor()) {
                    sensorRadius = fixture.getShape().getRadius();
                }else {
                    radius = fixture.getShape().getRadius();
                }
            }
        }
        gearBody.setAngularDamping(0.4f);
        gearConnector = new PhysicsConnector(this, gearBody);
        pPhysicsWorld.registerPhysicsConnector(gearConnector);
        mRevoluteJointDef = new RevoluteJointDef();
        mRevoluteJointDef.initialize(gearBody, anchorBody, gearBody.getWorldCenter());
        mRevoluteJointDef.collideConnected = false;
        mRevoluteJointDef.enableMotor = true;
        mRevoluteJoint = (RevoluteJoint) pPhysicsWorld.createJoint(mRevoluteJointDef);
        velocityText = new Text(0, 0, ToolMainActivity.PlokFont, " 0 ", pVertexBufferObjectManager);
        
        velocityTextUpdateHandler = new IUpdateHandler() {
            float seconds = 0.0f;
            //DecimalFormat df = new DecimalFormat("000"); 
            @Override
            public void reset() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onUpdate(float arg0) {
                seconds += arg0;
                if (seconds > 0.5f) {
                    int ve = (int)Math.round(getAngularVelocity());
                    String string = String.valueOf(ve);
                    System.out.println(string);
                    velocityText.setText(string);
                    seconds = 0;
                }
            }
        };
        velocityText.setZIndex(11);
        mScene.attachChild(velocityText);
        hideVelocityText();
    }
  
    
    public RevoluteJoint getRevoluteJoint() {
        
        return mRevoluteJoint;
    }
    public void setAnchorAsDynamic(boolean pDynamic) {
        if (pDynamic)
            anchorBody.setType(BodyType.DynamicBody);
        else
            anchorBody.setType(BodyType.StaticBody);
    }
    
    public float getRadius() {
        return radius;
    }
    public float getSensorRadius() {
        return sensorRadius;
    }
    public void setSensor(boolean pSensor) {
        ArrayList<Fixture> fixtureList = gearBody.getFixtureList();
        for (Fixture fixture: fixtureList) {
            fixture.setSensor(pSensor);
        } 
        if (anchorBody != null) {
            fixtureList = anchorBody.getFixtureList();
            for (Fixture fixture: fixtureList) {
                fixture.setSensor(pSensor);
            }
        }
    }
    
    public void changeCircleFixture(short pCategoryBits, short pMaskBits, short pGroupIndex) {
        ArrayList<Fixture> fixtureList = gearBody.getFixtureList();
        com.badlogic.gdx.physics.box2d.Filter filter = new com.badlogic.gdx.physics.box2d.Filter();
        filter.categoryBits = pCategoryBits;
        filter.maskBits = pMaskBits;
        filter.groupIndex = pGroupIndex;
        for (Fixture fixture: fixtureList) { 
            if (!fixture.isSensor())
                fixture.setFilterData(filter);
        }
    }
    public void changeSensorFixture(short pCategoryBits, short pMaskBits, short pGroupIndex) {
        ArrayList<Fixture> fixtureList = gearBody.getFixtureList();
        com.badlogic.gdx.physics.box2d.Filter filter = new com.badlogic.gdx.physics.box2d.Filter();
        filter.categoryBits = pCategoryBits;
        filter.maskBits = pMaskBits;
        filter.groupIndex = pGroupIndex;
        for (Fixture fixture: fixtureList) { 
            if (fixture.isSensor())
                fixture.setFilterData(filter);
        }
    }
    public short getCircleGroupIndex() {
        ArrayList<Fixture> fixtureList = gearBody.getFixtureList();
        for (Fixture fixture: fixtureList) { 
            if (!fixture.isSensor())
                return fixture.getFilterData().groupIndex;
        }
        return (short)0;
    }
    public void changeCircleGroupIndex(short pGroupIndex) {
        ArrayList<Fixture> fixtureList = gearBody.getFixtureList();
        com.badlogic.gdx.physics.box2d.Filter filter = new com.badlogic.gdx.physics.box2d.Filter();
        for (Fixture fixture: fixtureList) { 
            if (!fixture.isSensor()) {
                filter.categoryBits = fixture.getFilterData().categoryBits;
                filter.maskBits = fixture.getFilterData().maskBits;
                filter.groupIndex = pGroupIndex;
                fixture.setFilterData(filter);
            }
        }
    }
    
    public void startRotate(float maxMotorTorque, float motorspeed) {
        mRevoluteJoint.setMaxMotorTorque(maxMotorTorque);
        mRevoluteJoint.setMotorSpeed(motorspeed);
    }
    public void stopRotate() {
        mRevoluteJoint.setMotorSpeed(0f);
    }
   
    public int getID() {
        return id;
    }
    
    public void transform(TouchEvent touchEvent) {
        Vector2 vectorGear = Vector2Pool.obtain((touchEvent.getX())/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
                (touchEvent.getY())/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
        gearBody.setTransform(vectorGear, gearBody.getAngle());
        Vector2Pool.recycle(vectorGear);
    }
    public void transform(float x, float y) {
        Vector2 vectorGear = Vector2Pool.obtain(x, y);
        gearBody.setTransform(vectorGear, gearBody.getAngle());
        Vector2Pool.recycle(vectorGear);
    }

    public void destroyJointAndAnchorBody() {
        if (mRevoluteJoint != null) { 
            mPhysicsWorld.destroyJoint(mRevoluteJoint);
            mRevoluteJoint = null;
        }
        if (anchorBody != null) {
            mPhysicsWorld.destroyBody(anchorBody);
            anchorBody = null;
        }
       
    }
    public void destroyJointAndBody() {
        if (mRevoluteJoint != null) { 
            mPhysicsWorld.destroyJoint(mRevoluteJoint);
            mRevoluteJoint = null;
        }
        if (anchorBody != null) {
            mPhysicsWorld.destroyBody(anchorBody);
            anchorBody = null;
        }
        
        if (gearBody != null){
            mPhysicsWorld.destroyBody(gearBody);
            gearBody = null;
        }
    }
    public boolean toggleGroupIndex(Gear gear) {
        if (gear.getCircleGroupIndex() == FilterConstants.GROUP_ONE) {
            gear.changeCircleGroupIndex(FilterConstants.GROUP_TWO);
            gear.setZIndex(10);
            gear.setAlpha(0.5f);
        }else {
            gear.setZIndex(9);
            gear.changeCircleGroupIndex(FilterConstants.GROUP_ONE);
            gear.setAlpha(1f);
        }
        return true;
    }
    public void restoreAnchorBodyAndJoint() {
        anchorBody = PhysicsFactory.createCircleBody(mPhysicsWorld, this.getX()+centerX, this.getY()+centerY, 0, BodyType.StaticBody, anchorFixtureDef);
        mRevoluteJointDef.initialize(gearBody, anchorBody, gearBody.getWorldCenter());
        mRevoluteJointDef.collideConnected = false;
        mRevoluteJointDef.enableMotor = true;
        mRevoluteJoint = (RevoluteJoint) mPhysicsWorld.createJoint(mRevoluteJointDef);
    }
    public Body getGearBody() {
        return gearBody;
    }
    public boolean isDelete() {
        return hasDelete;
    }


    public float getAngularVelocity() {
        return Math.abs(gearBody.getAngularVelocity());
    }
    public void showVelocityText() {
        velocityText.setPosition(getX()+centerX-velocityText.getWidth()/2, getY()+centerY-velocityText.getHeight()/2);
        velocityText.setVisible(true);
        velocityText.registerUpdateHandler(velocityTextUpdateHandler);
    }
    public void hideVelocityText() {
        velocityText.setVisible(false);
        velocityText.unregisterUpdateHandler(velocityTextUpdateHandler);
    }
}
