package limk.score;

import lin.game.ToolMainActivity;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBounceOut;

import android.util.Log;

public class MyDialog extends Entity {

	protected boolean isShown;
	protected Scene mScene;
	public Sprite dialogSprite;
	public Rectangle rectangle;
	public int width;
	public int height;
	protected VertexBufferObjectManager mVertexBufferObjectManager;

	/**
	 * 新建一个对话框
	 * 
	 * @param width
	 *            传入的参数CAMERA_WIDTH
	 * @param height
	 *            传入的参数CAMERA_HEIGHT
	 * @param mScene
	 *            当前的Scene
	 * @param pX
	 *            对话框的X坐标
	 * @param pY
	 *            对话框的Y坐标
	 * @param pDialogRegion
	 *            对话框的背景图片
	 * @param pVertexBufferObjectManager
	 */
	public MyDialog(int width, int height, Scene mScene, float pX, float pY,
			ITextureRegion pDialogRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		this.mScene = mScene;
		isShown = false;
		this.width = width;
		this.height = height;
		this.mVertexBufferObjectManager = pVertexBufferObjectManager;
		/**
		 * 新建一个长方形，当对话框显示的时候屏蔽对话框底层所有的点击响应
		 */
		rectangle = new Rectangle(0, 0, ToolMainActivity.CAMERA_WIDTH, ToolMainActivity.CAMERA_HEIGHT,
				pVertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.d("RectangleOnTouch", Boolean.toString(isShown));
				return isShown;
			}
		};
		
		rectangle.setColor(1, 1, 1);
		rectangle.setAlpha(0f);

		/**
		 * 要弹出的对话框的Sprite
		 */
		dialogSprite = new Sprite(pX, pY, pDialogRegion,
				pVertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.d("DialogOnTouch", Boolean.toString(isShown));
				return true;
			};
		};
		dialogSprite.setPosition(pX - dialogSprite.getWidth() / 2, -200
				- dialogSprite.getHeight());
		rectangle.setZIndex(100);
		mScene.attachChild(rectangle);
		
		rectangle.attachChild(dialogSprite);
		mScene.sortChildren();

		mScene.registerTouchArea(dialogSprite);
		mScene.registerTouchArea(rectangle);

	}

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
		}
	}

	/**
	 * 往对话框中添加一个Button的Sprite，其中button的响应以及
	 * 位置在传入的Sprite新建的时候给定，使用对dialog的相对位置
	 * 
	 * @param buttonSprite
	 */
	public void addBtton(Sprite buttonSprite) {
	    buttonSprite.setZIndex(101);
		dialogSprite.attachChild(buttonSprite);

		//mScene.unregisterTouchArea(rectangle);
		//mScene.unregisterTouchArea(dialogSprite);

		mScene.registerTouchArea(buttonSprite);
		//mScene.registerTouchArea(dialogSprite);
		//mScene.registerTouchArea(rectangle);
	}

	/**
	 * 期待后期实现……往对话框中添加文本
	 * 
	 * @param pX
	 * @param pY
	 * @param pText
	 * @param pFont
	 */
	public void addText(int pX, int pY, String pText, Font pFont) {
		Text newText = new Text(pX, pY, pFont, pText,
				mVertexBufferObjectManager);
		dialogSprite.attachChild(newText);
		return;
	}

	/**
	 * 获取对话框的宽度
	 */
	public float getWidth() {
		return dialogSprite.getWidth();
	}

	/**
	 * 获取对话框的高度
	 */
	public float getHeight() {
		return dialogSprite.getHeight();
	}
}
