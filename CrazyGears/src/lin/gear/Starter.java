package lin.gear;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

import com.badlogic.gdx.physics.box2d.Contact;
import com.toolbar.GearCount;
import com.toolbar.Tool;

public class Starter extends Gear{
    private float rate;
    private float sumRate = 0;
    private float maxSpeed;
    private float minSpeed;
    private float defaultForce = 5000000000000000f;
    public Starter(float pX, float pY, ITextureRegion pTextureRegion,
            VertexBufferObjectManager pVertexBufferObjectManager,
            Context pContext, PhysicsWorld pPhysicsWorld, String pAssetsPath, Scene pScene) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager, GearSize.STARTER, pContext,
                pPhysicsWorld, pAssetsPath, pScene);
        pScene.attachChild(this);
        pScene.registerTouchArea(this);
        rate = 50f;
        minSpeed = 0f;
        maxSpeed = 2f;
        this.setZIndex(1);
        //setColor(0.8984f, 0.9063f, 0.9765f);
        setColor(0.5429f, 0f, 0f);
        startRotate(defaultForce, maxSpeed);
        changeCircleFixture(FilterConstants.CATEGORY_BOTTOM, FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ONE);
        mScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {
            }
            
            @Override
            public void onUpdate(float arg0) {
                if (maxSpeed-getAngularVelocity() > 0.5f) {
                    addForce(rate);
                }else if (maxSpeed-getAngularVelocity() < 0.000001f) {
                    subForce(rate);
                }
            }
        });
    }
    public Starter(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, 
            Context pContext, PhysicsWorld pPhysicsWorld, String pAssetsPath, float pRate, Float pMinSpeed, float pMaxSpeed,
            Scene pScene) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager, GearSize.STARTER, pContext,
                pPhysicsWorld, pAssetsPath, pScene);
        pScene.attachChild(this);
        pScene.registerTouchArea(this);
        rate = pRate;
        minSpeed = pMinSpeed;
        maxSpeed = pMaxSpeed;
        this.setZIndex(1);
        startRotate(defaultForce, maxSpeed);
        changeCircleFixture(FilterConstants.CATEGORY_BOTTOM, FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ONE);
        mScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {
            }
            
            @Override
            public void onUpdate(float arg0) {
                if (maxSpeed-getAngularVelocity() > 0.5f) {
                    addForce(rate);
                }else if (maxSpeed-getAngularVelocity() < 0.1f) {
                    subForce(rate);
                }
            }
        });
    }
    public void addForce(float pRate) {
        sumRate += pRate;
        mRevoluteJoint.setMaxMotorTorque(defaultForce+sumRate);
    }
    public void subForce(float pRate) {
        if (mRevoluteJoint.getMotorTorque() > defaultForce) {
            sumRate -= 50;
            mRevoluteJoint.setMaxMotorTorque(defaultForce+sumRate);
        }
    }
    public void setRate(float pRate) {
        rate = pRate;
    }
    public float getRate() {
        return rate;
    }
    public void setMaxSpeed(float pMaxSpeed) {
        maxSpeed = pMaxSpeed;
    }
    public float getMaxSpeed() {
        return maxSpeed;
    }
    public void setMinSpeed(float pMinSpeed) {
        minSpeed = pMinSpeed;
    }
    public float getMinSpeed() {
        return minSpeed;
    }

}
