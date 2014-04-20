package lin.gear;

import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.toolbar.GearCount;
import com.toolbar.Tool;

import android.content.Context;

public class Generator extends Gear{
    private int touchcount = 0;
    private long firstClick = 0, virtualFirstClick = 0, secondClick = 0;
    public Generator(float pX, float pY, ITextureRegion pTextureRegion,
            VertexBufferObjectManager mVertexBufferObjectManager,
            Context pContext, PhysicsWorld pPhysicsWorld, String pAssetsPath, Scene pScene) {
        super(pX, pY, pTextureRegion, mVertexBufferObjectManager, GearSize.GENERATOR, pContext,
                pPhysicsWorld, pAssetsPath, pScene);
        this.setZIndex(9);
        pScene.registerTouchArea(this);
        pScene.attachChild(this);
        setColor(0.5429f, 0f, 0f);
        changeCircleFixture(FilterConstants.CATEGORY_BOTTOM, FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ONE);
    }

    /* (non-Javadoc)
     * @see org.andengine.entity.shape.Shape#onAreaTouched(org.andengine.input.touch.TouchEvent, float, float)
     */
    
    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
            float pTouchAreaLocalX, float pTouchAreaLocalY) {
        // TODO Auto-generated method stub
        switch (pSceneTouchEvent.getAction()) {
        case TouchEvent.ACTION_DOWN:
            touchcount++;
            if (touchcount == 1) {
                firstClick = System.currentTimeMillis();
                if (firstClick - virtualFirstClick < 500) {
                    toggleGroupIndex(this);
                    
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
                    
                }
                touchcount = 0;
                firstClick = 0;
                secondClick = 0;
            }
        }
        return true;
    }
    
}
