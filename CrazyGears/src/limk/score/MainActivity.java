package limk.score;

import java.io.IOException;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseStrongOut;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends BaseGameActivity {

	// ===========================================================
	// Constants
	// ===========================================================
	protected static int CAMERA_WIDTH;
	protected static int CAMERA_HEIGHT;
	public DisplayMetrics metric;
	protected static int FONT_SIZE = 24;

	protected static int LEVELS = 30;
	protected static int LEVEL_COLUMNS_PER_SCREEN = 4;
	protected static int LEVEL_ROWS_PER_SCREEN = 3;
	protected static int LEVEL_PADDING = 50;

	// ===========================================================
	// Fields
	// ===========================================================
	private Scene mScene;
	private Camera mCamera;

	private ITexture starTexture;
	private ITextureRegion starRegion;
	private ITexture starOutlineTexture;
	private ITextureRegion starOutlineRegion;
	private ITexture yesButtonTexture;
	private ITextureRegion yesButtonRegion;
	private ShowScore mScore;
	private Handler mHandler;
	private ITexture dialogTexture;
	private ITextureRegion dialogRegion;
	private Font mFont;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Intent intent = new Intent(MainActivity.this,
			// MenuMainActivity.class);
			// startActivity(intent);
			finish();

		}
		return false;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.d("onCreateEngineOptions", "  ");
		metric = new DisplayMetrics();
		mHandler = new Handler();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		CAMERA_WIDTH = metric.widthPixels;
		CAMERA_HEIGHT = metric.heightPixels;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);

	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback arg0)
			throws IOException {

		mFont = FontFactory.createFromAsset(getFontManager(),
				getTextureManager(), 256, 256, TextureOptions.DEFAULT,
				getAssets(), "font/Flubber.ttf", 32f, true,
				Color.YELLOW_ABGR_PACKED_INT);
		mFont.load();

		dialogTexture = new AssetBitmapTexture(getTextureManager(),
				getAssets(), "gfx/dialog.png");
		dialogRegion = TextureRegionFactory.extractFromTexture(dialogTexture);
		dialogTexture.load();
		starTexture = new AssetBitmapTexture(getTextureManager(), getAssets(),
				"gfx/star_1.png");
		starRegion = TextureRegionFactory.extractFromTexture(starTexture);
		starTexture.load();

		starOutlineTexture = new AssetBitmapTexture(getTextureManager(),
				getAssets(), "gfx/star_outline.png");
		starOutlineRegion = TextureRegionFactory
				.extractFromTexture(starOutlineTexture);
		starOutlineTexture.load();
		yesButtonTexture = new AssetBitmapTexture(getTextureManager(),
				getAssets(), "gfx/yes.png");
		yesButtonRegion = TextureRegionFactory
				.extractFromTexture(yesButtonTexture);
		yesButtonTexture.load();
		arg0.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback arg0) throws IOException {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		mScene = new Scene();
		mScene.getBackground().setColor(new Color(0, 0, 1));
		mScene.attachChild(new Entity()); // First Layer
		mScene.attachChild(new Entity()); // Second Layer

		mScore = new ShowScore(mHandler, starRegion, starOutlineRegion,
				CAMERA_WIDTH, CAMERA_HEIGHT, mScene, CAMERA_WIDTH / 2,
				CAMERA_HEIGHT / 2, dialogRegion,
				getVertexBufferObjectManager(), 1000, 2, mFont);
		Sprite yesButton = new Sprite(0, 0, yesButtonRegion,
				getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					Log.d("Yes", "YesButtonClick");
					mScore.showAndHide();
				}
				return true;
			}
		};
		yesButton
				.setPosition(0, mScore.getHeight() - yesButton.getHeight() / 2);
		mScore.addBtton(yesButton);

		mScore.showAndHide();
		arg0.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene arg0, OnPopulateSceneCallback arg1)
			throws IOException {
		arg1.onPopulateSceneFinished();
	}

}
