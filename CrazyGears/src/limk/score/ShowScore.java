package limk.score;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.runnable.RunnableHandler;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.os.Handler;
import android.util.Log;

public class ShowScore extends MyDialog {

	private ArrayList<Sprite> starSprites;
	private ArrayList<Sprite> outlineSprites;
	private Handler mHandler;
	private int score;
	private Font mFont;
	private Scene mScene;
	
	
	private Runnable showScore = new Runnable() {

		@Override
		public void run() {
				//Thread.sleep(500);
		    Log.i("lin", "showScore");
		    mScene.registerUpdateHandler(new IUpdateHandler() {
		        private boolean label1 = true, label2 = true, label3 = true, label4 = true;
		        private float time1 = 0, time2 = 0, time3 = 0, time4 = 0;
		        int i1 = 0, i2 = 0;
		        Text thisText;
		        boolean isShowText = false;
                @Override
                public void reset() {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void onUpdate(float arg0) {
                    // TODO Auto-generated method stub
                    time1 += arg0;
                    
                    if (time1 > 0.5f){
                        //Log.i("lin", "time1");
                        
                        time2 += arg0;
                        if (label2 && i1 < starSprites.size()) {
                            Log.i("lin", "show score time2");
                            label2 = false;
                            Sprite sprite = starSprites.get(i1);
                            sprite.registerEntityModifier(new MoveModifier(0.5f, sprite
                                    .getX(), outlineSprites.get(i1).getX(), sprite
                                    .getY(), outlineSprites.get(i1).getY(),
                                    EaseStrongOut.getInstance()));
                            sprite.registerEntityModifier(new AlphaModifier(0.5f,
                                    sprite.getAlpha(), 1f, EaseStrongOut.getInstance()));
                            sprite.registerEntityModifier(new ScaleAtModifier(0.5f,
                                    20f, 1f, sprite.getWidth() / 2,
                                    sprite.getHeight() / 2, EaseStrongOut.getInstance()));
                            i1++;
                            time2 = 0;
                        }else {
                            if (time2 > 0.8f){
                                label2 = true;
                            }
                        }
                        if (i1 >= starSprites.size()){
                            //label1 = false;
                            time3 += arg0;
                            if (time3 > 0.5f && label3){
                                Log.i("lin", "show score time3");
                                if (!isShowText){
                                    isShowText = true;
                                    thisText = new Text(dialogSprite.getWidth() / 2,
                                            dialogSprite.getHeight() / 2, mFont,
                                            "   "+Integer.toString(i2)+"   ", mVertexBufferObjectManager);
                                    dialogSprite.attachChild(thisText);
                                    i2+=20;
                                }
                                time4 += arg0;
                                if(time4 > 0.01f && i2 < score) {
                                    
                                    thisText.setText(Integer.toString(i2));
                                    time4 = 0;
                                    i2+=20;
                                    //thisText.setVisible(false);
                                }
                                if (i2 >=score){
                                    
                                    thisText.setText(Integer.toString(score));
                                    label3 = false;
                                }
                                
                            }else if (!label3){
                                mScene.unregisterUpdateHandler(this);
                            }
                        }
                    }
                }
            });
		}
	};

	public ShowScore(Handler pHandler, ITextureRegion starRegion,
			ITextureRegion outlineRegion, int width, int height, Scene mScene,
			float pX, float pY, ITextureRegion pDialogRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, int score,
			int starNum, Font pFont) {
		super(width, height, mScene, pX, pY, pDialogRegion,
				pVertexBufferObjectManager);
		this.mHandler = pHandler;
		this.mScene = mScene;
		this.score = score;
		this.mFont = pFont;
		outlineSprites = new ArrayList<Sprite>();
		starSprites = new ArrayList<Sprite>();

		for (int i = 0; i < 3; i++) {
			outlineSprites.add(new Sprite(dialogSprite.getWidth() / 2
					- outlineRegion.getWidth() * 2.5f + i * 2
					* outlineRegion.getWidth(), 20, outlineRegion,
					pVertexBufferObjectManager));
		}
		for (int i = 0; i < starNum; i++) {
			Sprite newSprite = new Sprite(width + 100, height - 100,
					starRegion, pVertexBufferObjectManager);
			newSprite.setAlpha(0);
			newSprite.setScale(20f);
			starSprites.add(newSprite);
		}

		for (int i = 0; i < 3; i++) {
			dialogSprite.attachChild(outlineSprites.get(i));
		}
		for (int i = 0; i < starNum; i++) {
			dialogSprite.attachChild(starSprites.get(i));
		}
	}

	@Override
	public void showAndHide() {
		if (isShown) {
			/**
			 * 使用ScaleAtModifier效果隐藏对话框
			 */
			dialogSprite.registerEntityModifier(new MoveYModifier(0.2f,
					dialogSprite.getY(), -200 - dialogSprite.getHeight()));
			isShown = false;
			rectangle.setAlpha(0);
		} else {
			/**
			 * 使用ScaleAtModifier效果显示对话框
			 */
			dialogSprite
					.registerEntityModifier(new MoveYModifier(0.5f, -200
							- dialogSprite.getHeight(), height / 2
							- dialogSprite.getHeight() / 2, EaseBounceOut
							.getInstance()));
			isShown = true;
			/**
			 * 设置对话框下方的Scene变灰
			 */
			rectangle.setAlpha(0.3f);
			mHandler.post(showScore);
		}

	}

}
