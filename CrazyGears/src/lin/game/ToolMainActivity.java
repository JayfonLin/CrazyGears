package lin.game;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import limk.score.ShowScore;
import lin.appliance.Appliance;
import lin.appliance.Bulb;
import lin.appliance.Computer;
import lin.gear.Driver;
import lin.gear.DriversManager;
import lin.gear.FilterConstants;
import lin.gear.GearSize;
import lin.gear.Generator;
import lin.gear.Starter;
import lin.person.Person;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCircularInOut;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.toolbar.GearCount;
import com.toolbar.ToolbarManager;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater.Filter;
import android.widget.Toast;

public class ToolMainActivity extends BaseGameActivity implements
        IClickDetectorListener, IScrollDetectorListener, IOnSceneTouchListener {
    public static int CAMERA_WIDTH;

    public static int CAMERA_HEIGHT;

    public static Font FONT_DEFAULT_32BOLD, flubberFont, PlokFont;
    
    public static boolean game_over = false;
    
    public static boolean win = false;

    private Sprite info_velocity_button, trash_button;

    private Camera mCamera;

    public Body leftWallBody1, leftWallBody2;

    private BuildableBitmapTextureAtlas mTexture0;

    private BuildableBitmapTextureAtlas mTexture1;

    private BuildableBitmapTextureAtlas backgroundAtlas;
    
    private BuildableBitmapTextureAtlas tiledAtlas;

    private TextureRegion mTextureRegion1, mTextureRegion2, mTextureRegion3,
            mTextureRegion4, trashRegion, light, pc;
    
    private TiledTextureRegion buttonRegion;

    private TextureRegion toolBarRegion;

    private TextureRegion mStarterRegion, generatorRegion, generatorRegion2;
    
    private TiledTextureRegion bulbTiledTextureRegion, pcTiledTextureRegion, personTiledTextureRegion;
    
    private Bulb bulb;
    
    private Computer computer;
    
    private Person person;

    private ITextureRegion textureBackground;

    private Sprite backgroundSprite;

    private TiledTextureRegion mTiledRegion1, mTiledRegion2, mTiledRegion3,
            mTiledRegion4;

    private Scene mScene;

    private ToolbarManager toolSprite;

    private List<TiledTextureRegion> bTextureRegion = new ArrayList<TiledTextureRegion>();

    private Handler mhandler;
    
    private Text lightText, pcText;
    
    protected static int FONT_SIZE = 24;

    private GearCount gearCount0 = new GearCount(9);

    private GearCount gearCount1 = new GearCount(9);
    
    private GearCount gearCount2 = new GearCount(9);

    private GearCount gearCount3 = new GearCount(9);

    private String path = "xml/CircleGears.xml";

    private PhysicsWorld physicsWorld;

    private DisplayMetrics metrics;

    private Starter starter;

    private Generator pc_generator, bulb_generator;

    private Body touchBody;

    private boolean isShowMessage = false;

    private SurfaceScrollDetector mScrollDetector;

    private ClickDetector mClickDetector;

    private float distanceX;

    private boolean isChange;

    private Driver driverA, driverB;
    
    private ArrayList<Appliance> appliances = new ArrayList<Appliance>();
    
    private ITexture starTexture;
    private ITextureRegion starRegion;
    private ITexture starOutlineTexture;
    private ITextureRegion starOutlineRegion;
    private ITexture yesButtonTexture;
    private ITextureRegion yesButtonRegion;
    private ShowScore mScore;
    private ITexture dialogTexture;
    private ITextureRegion dialogRegion;
    private Font mFont;
    private boolean isShownGameOver = false;
    private Context context = this;
    public HUD mHud;

    @Override
    public EngineOptions onCreateEngineOptions() {
        mhandler = new Handler();
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        CAMERA_WIDTH = metrics.widthPixels;
        CAMERA_HEIGHT = metrics.heightPixels;
        mHud = new HUD();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        mCamera.setHUD(mHud);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                this.mCamera);

    }

    @Override
    public void onCreateResources(
            OnCreateResourcesCallback pOnCreateResourcesCallback)
            throws Exception {
        FontFactory.setAssetBasePath("font/");
        flubberFont = FontFactory.createFromAsset(getFontManager(),
                getTextureManager(), 256, 256, this.getAssets(), "Flubber.ttf",
                32f, true, Color.WHITE_ABGR_PACKED_INT);
        flubberFont.load();
        PlokFont = FontFactory.createFromAsset(getFontManager(),
                getTextureManager(), 256, 256, TextureOptions.BILINEAR,
                getAssets(), "Plok.ttf", 32, true, Color.WHITE_ABGR_PACKED_INT);
        PlokFont.load();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mTexture0 = new BuildableBitmapTextureAtlas(
                mEngine.getTextureManager(), 2048, 2048);
        this.mTexture1 = new BuildableBitmapTextureAtlas(
                mEngine.getTextureManager(), 1024, 1024);
        backgroundAtlas = new BuildableBitmapTextureAtlas(
                mEngine.getTextureManager(), 2048, 2048);
        tiledAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 1024, 1024);
        this.mTextureRegion1 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "style1_size50.png");
        this.mTextureRegion2 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "style2_size100.png");
        this.mTextureRegion3 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "style3_size150.png");
        this.mTextureRegion4 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "style4_size200.png");
        buttonRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mTexture0, this, "info_button.png", 2, 1);
        buttonRegion.setCurrentTileIndex(1);
        trashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mTexture0, this, "trashButton.png");
        toolBarRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                mTexture0, this, "toolbar.png");
        
        this.mStarterRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "starter.png");
        this.generatorRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "generator.png");
        this.generatorRegion2 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(this.mTexture0, this, "generator.png");
        this.mTiledRegion1 = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mTexture1, this, "tiled_size50.png", 10, 1);
        this.mTiledRegion2 = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mTexture1, this, "tiled_size100.png", 10, 1);
        this.mTiledRegion3 = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mTexture1, this, "tiled_size150.png", 10, 1);
        this.mTiledRegion4 = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(mTexture1, this, "tiled_size200.png", 10, 1);
        bulbTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(tiledAtlas, this, "tiled_bulb.png", 5, 1);
        pcTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(tiledAtlas, this, "tiled_pc.png", 3, 1);
        personTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createTiledFromAsset(tiledAtlas, this, "tiled_person.png", 3, 1);
        textureBackground = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(backgroundAtlas, this, "bg_black.png");
        try {
            mTexture1
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 0, 0));
            mTexture0
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 0, 0));
            backgroundAtlas
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 0, 0));
            tiledAtlas
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 0, 0));
        } catch (TextureAtlasBuilderException e) {
            e.printStackTrace();
        }

        bTextureRegion.add(mTiledRegion1);
        bTextureRegion.add(mTiledRegion2);
        bTextureRegion.add(mTiledRegion3);
        bTextureRegion.add(mTiledRegion4);
        this.mEngine.getTextureManager().loadTexture(this.mTexture0);
        this.mEngine.getTextureManager().loadTexture(this.mTexture1);
        backgroundAtlas.load();
        tiledAtlas.load();
        FONT_DEFAULT_32BOLD = FontFactory.create(mEngine.getFontManager(),
                mEngine.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32f, true,
                Color.BLACK_ABGR_PACKED_INT);
        FONT_DEFAULT_32BOLD.prepareLetters("1234567890V".toCharArray());
        FONT_DEFAULT_32BOLD.load();
        
        mFont = FontFactory.createFromAsset(getFontManager(),
                getTextureManager(), 256, 256, TextureOptions.DEFAULT,
                getAssets(), "Flubber.ttf", 32f, true,
                Color.YELLOW_ABGR_PACKED_INT);
        mFont.load();
        dialogTexture = new AssetBitmapTexture(getTextureManager(),
                getAssets(), "gfx/GearBackGround2.png");
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
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
            throws Exception {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        this.mScene = new Scene();
        backgroundSprite = new Sprite(-CAMERA_WIDTH, 0, CAMERA_WIDTH * 2,
                CAMERA_HEIGHT, textureBackground,
                getVertexBufferObjectManager());
        mScene.attachChild(backgroundSprite);
        lightText = new Text(70, 30, FONT_DEFAULT_32BOLD, "10~35V", getVertexBufferObjectManager());
        mScene.attachChild(lightText);
        pcText = new Text(40, 240, FONT_DEFAULT_32BOLD, "25~75V", getVertexBufferObjectManager());
        mScene.attachChild(pcText);
        final FixtureDef WALL_FIXTURE_DEF1 = PhysicsFactory.createFixtureDef(0,
                0.5f, 0.5f, false, FilterConstants.CATEGORY_DEFAULT,
                FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_ONE);
        final FixtureDef WALL_FIXTURE_DEF2 = PhysicsFactory.createFixtureDef(0,
                0.5f, 0.5f, false, FilterConstants.CATEGORY_DEFAULT,
                FilterConstants.MASKBITS_ZERO, FilterConstants.GROUP_TWO);
        final Rectangle left1 = new Rectangle(CAMERA_WIDTH * 0.14f, 0, 2f,
                CAMERA_HEIGHT, this.getVertexBufferObjectManager());
        left1.setColor(0.5f, 0.5f, 0.5f);
        final Rectangle left2 = new Rectangle(CAMERA_WIDTH * 0.14f, 0, 2f,
                CAMERA_HEIGHT, this.getVertexBufferObjectManager());
        left2.setColor(0.5f, 0.5f, 0.5f);
        physicsWorld = new PhysicsWorld(new Vector2(1, 0), true);
        leftWallBody1 = PhysicsFactory.createBoxBody(this.physicsWorld, left1,
                BodyType.StaticBody, WALL_FIXTURE_DEF1);
        left1.setZIndex(12);
        leftWallBody2 = PhysicsFactory.createBoxBody(this.physicsWorld, left2,
                BodyType.StaticBody, WALL_FIXTURE_DEF2);
        left2.setZIndex(13);
        /*Sprite personSprite = new Sprite(-0.5035f*CAMERA_WIDTH, 0.5f*CAMERA_HEIGHT, person, getVertexBufferObjectManager());
        mScene.attachChild(personSprite);*/
        
        mScene.registerUpdateHandler(physicsWorld);
       
        // SpriteBackground(backgroundSprite);
        // this.mScene.setBackground(background);
        // mScene.setBackgroundEnabled(true);
        mScene.setX(CAMERA_WIDTH);
        
        isChange = false;
        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.mClickDetector = new ClickDetector(this);
        this.mScene.setOnSceneTouchListener(this);

        starter = new Starter(
                CAMERA_WIDTH - mStarterRegion.getWidth() / 2 - 30,
                CAMERA_HEIGHT / 2 - mStarterRegion.getHeight() / 2,
                mStarterRegion, getVertexBufferObjectManager(), this,
                physicsWorld, path, mScene);
        bulb_generator = new Generator(0.160f*CAMERA_WIDTH, 0.2354f*CAMERA_HEIGHT, generatorRegion,
                getVertexBufferObjectManager(), this, physicsWorld, path,
                mScene);
        /*generator2 = new Generator(100f, 100f, generatorRegion2, 
                getVertexBufferObjectManager(), this, physicsWorld, path, mScene);*/
        pc_generator = new Generator(0.156f*CAMERA_WIDTH, 0.7250f*CAMERA_HEIGHT, generatorRegion2, 
                getVertexBufferObjectManager(), this, physicsWorld, path, mScene);
        //generator.setColor(0.5f, 0.7f, 0.4f, 0.7f);
        toolSprite = new ToolbarManager(CAMERA_WIDTH - 50, 0, mScene,
                getApplicationContext(), toolBarRegion, bTextureRegion,
                getVertexBufferObjectManager());
        mScene.attachChild(toolSprite);
        toolSprite.addTool(mTextureRegion1, 0, gearCount0, GearSize.SMALL,
                physicsWorld, path);
        toolSprite.addTool(mTextureRegion2, 1, gearCount1, GearSize.MIDDLE,
                physicsWorld, path);
        toolSprite.addTool(mTextureRegion3, 2, gearCount2, GearSize.BIG,
                physicsWorld, path);
        toolSprite.addTool(mTextureRegion4, 3, gearCount3, GearSize.SUPER_BIG,
                physicsWorld, path);
        bulb = new Bulb(-0.644f*CAMERA_WIDTH, 0.052083f*CAMERA_HEIGHT, 
                bulbTiledTextureRegion.getWidth(), bulbTiledTextureRegion.getHeight(), 
                bulbTiledTextureRegion, 20, bulb_generator, getVertexBufferObjectManager());
        mScene.attachChild(bulb);
        computer = new Computer(-0.9227f*CAMERA_WIDTH, 0.5104f*CAMERA_HEIGHT, 
                pcTiledTextureRegion.getWidth()*1.5f, pcTiledTextureRegion.getHeight()*1.5f, 
                pcTiledTextureRegion, 50, 3, pc_generator, getVertexBufferObjectManager()); //788, 245
        mScene.attachChild(computer);
        appliances.add(bulb);
        appliances.add(computer);
        person = new Person(-0.5035f*CAMERA_WIDTH, 0.5f*CAMERA_HEIGHT, 
                personTiledTextureRegion.getWidth(), personTiledTextureRegion.getHeight(),
                personTiledTextureRegion, appliances, getVertexBufferObjectManager());
        mScene.attachChild(person);
        mScene.setTouchAreaBindingOnActionMoveEnabled(true);
        mScene.setTouchAreaBindingOnActionDownEnabled(true);
        mScene.setOnAreaTouchTraversalFrontToBack();
        info_velocity_button = new TiledSprite(CAMERA_WIDTH - 50,
                CAMERA_HEIGHT - 50, 50, 50, buttonRegion,
                getVertexBufferObjectManager()) {
            // Dialog dialog = new
            // Dialog(getApplicationContext());
            Rectangle unClickableScene;

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                    float pTouchAreaLocalX, float pTouchAreaLocalY) {
                switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (!isShowMessage) {
                        isShowMessage = true;
                        this.setCurrentTileIndex(0);
                        starter.showVelocityText();
                        pc_generator.showVelocityText();
                        bulb_generator.showVelocityText();
                        for (Driver driver : DriversManager.drivers) {
                            driver.showVelocityText();
                        }
                        unClickableScene = new Rectangle(0, 0, CAMERA_WIDTH,
                                CAMERA_HEIGHT, getVertexBufferObjectManager()) {

                            /**
                             * (non-Javadoc)
                             * 
                             * @see org.andengine.entity.shape .Shape
                             *      #onAreaTouched(org.andengine
                             *      .input.touch.TouchEvent, float, float)
                             */
                            @Override
                            public boolean onAreaTouched(
                                    TouchEvent pSceneTouchEvent,
                                    float pTouchAreaLocalX,
                                    float pTouchAreaLocalY) {
                                info_velocity_button.onAreaTouched(
                                        pSceneTouchEvent, pTouchAreaLocalX,
                                        pTouchAreaLocalY);
                                return true;
                            }

                        };
                        unClickableScene.setColor(0f, 0f, 0f, 0.2f);
                        unClickableScene.setZIndex(100);

                        mScene.attachChild(unClickableScene);
                        mScene.registerTouchArea(unClickableScene);
                    } else {
                        isShowMessage = false;
                        this.setCurrentTileIndex(1);
                        starter.hideVelocityText();
                        bulb_generator.hideVelocityText();
                        pc_generator.hideVelocityText();
                        for (Driver driver : DriversManager.drivers) {
                            driver.hideVelocityText();
                        }
                        if (unClickableScene != null) {
                            mScene.unregisterTouchArea(unClickableScene);
                            mScene.detachChild(unClickableScene);
                            unClickableScene = null;
                        }
                    }
                    break;

                }
                return true;
            }

        };
        trash_button = new Sprite(CAMERA_WIDTH-50, 200, 50, 50, trashRegion, getVertexBufferObjectManager()) {
            
            /* (non-Javadoc)
             * @see org.andengine.entity.shape.Shape#onAreaTouched(org.andengine.input.touch.TouchEvent, float, float)
             */
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                    float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Driver driver;
                for (int i = 0; i != DriversManager.drivers.size();) {
                    driver = DriversManager.drivers.get(i);

                    if (driver == null || driver.getGearBody() == null) {
                        //System.out.println("driver.getGearBody() is null!!");
                        continue;
                    }
                    if (driver.isCombined) {
                        driver.removeDriver(driver.combineDriver);
                        mScene.detachChild(driver.combineDriver.velocityText);
                    }
                    driver.removeDriver(driver);
                    mScene.detachChild(driver);
                }
                Log.i("lin", "drivers size: "+DriversManager.drivers);
                return true;
            }
            
        };
        mScene.attachChild(info_velocity_button);
        mScene.attachChild(trash_button);
        mScene.registerTouchArea(info_velocity_button);
        mScene.registerTouchArea(trash_button);
        info_velocity_button.setZIndex(4);
        trash_button.setZIndex(4);
        // mScene.setOnAreaTouchTraversalBackToFront();
        mScene.registerUpdateHandler(new IUpdateHandler() {
            float time = 0;
            float time1 = 0, time2 = 0;
            boolean label1 = true, label2 = true;
            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float arg0) {
                time += arg0;
                if (time > 0.1) {
                    mScene.sortChildren();
                    bulb.updateState();
                    computer.updateState();
                    person.update();
                    if (person.cry) {
                        game_over = true;
                        win = false;
                    }else if (person.satisfied) {
                        game_over = true;
                        win = true;
                    }else {
                        game_over = false;
                        win = false;
                    }
                    if (game_over && !isShownGameOver){
                        time1 += arg0;
                        
                        if (time1 > 0f && label1){
                            label1 = false;
                            /*mScene.registerEntityModifier(new  MoveXModifier(2f, mScene.getX(), CAMERA_WIDTH, 
                                EaseLinear.getInstance()));*/
                        }
                       
                        if (!label1){
                            time2 += arg0;
                            if (time2 > 0.5f && label2){
                                label2 = false;
                                isShownGameOver = true;
                                if (win){
                                    Log.i("lin", "win!");
                                    mScore.showAndHide();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "You Lose!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        
                    }
                    
                    time = 0;
                }
            }
        });
        physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.isTouching() && contact.getFixtureA().isSensor()
                        && contact.getFixtureB().isSensor()) {
                    driverA = getDriverByBody(contact.getFixtureA().getBody());
                    driverB = getDriverByBody(contact.getFixtureB().getBody());
                    if (driverA != null) {
                        driverB = getTouchingDriver();
                        if (driverB != null) {
                            if (driverB.equals(driverA)|| driverB.size == driverA.size || 
                                    driverA.isCombined || driverB.isCombined) {
                                return;
                            }
                            driverB.combineDriver = driverA;
                        }
                    }
                    if (driverB != null) {
                        driverA = getTouchingDriver();
                        if (driverA != null) {
                            if (driverB.equals(driverA)|| driverB.size == driverA.size ||
                                    driverA.isCombined || driverB.isCombined) {
                                return;
                            }
                            driverA.combineDriver = driverB;
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void postSolve(Contact arg0, ContactImpulse arg1) {

            }

            @Override
            public void preSolve(Contact arg0, Manifold arg1) {

            }
        });
        
        mScene.attachChild(new Entity()); // First Layer
        mScene.attachChild(new Entity()); // Second Layer

        mScore = new ShowScore(mhandler, starRegion, starOutlineRegion,
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
                .setPosition(0, mScore.getHeight() - yesButton.getHeight()/2 );
        mScore.addBtton(yesButton);

        //mScore.showAndHide();
        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    @Override
    public void onPopulateScene(Scene pScene,
            OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        mhandler.postDelayed(runnable, 1500);
        /*mhandler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                game_over = true;
                win = true;
            }
        }, 10000);*/
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
    private Runnable runnable = new Runnable() {
        
        @Override
        public void run() {
            // TODO Auto-generated method stub
            mScene.registerEntityModifier(new MoveXModifier(3f, CAMERA_WIDTH, 0, EaseLinear.getInstance()));
        }
    };
    public Driver getDriverByBody(Body body) {
        if (body == null)
            return null;
        // System.out.println("drivers number: "+DriversManager.drivers.size());
        for (Driver driver : DriversManager.drivers) {
            if (driver.getGearBody() == null) {
                //System.out.println("driver.getGearBody() is null!!");
                return null;
            }
            if (driver.getGearBody().equals(body)) {
                return driver;
            }
        }
        return null;
    }

    public Driver getTouchingDriver() {
        for (Driver driver : DriversManager.drivers) {
            if (driver.isTouchingByFinger())
                return driver;
        }
        return null;
    }

    @Override
    public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
        //Log.d("offsetDistance", Float.toString(1.0f));
        mClickDetector.onTouchEvent(arg1);
        mScrollDetector.onTouchEvent(arg1);
        return true;
    }

    @Override
    public void onScroll(ScrollDetector arg0, int arg1, float arg2, float arg3) {
        //Log.d("offsetDistance", Float.toString(arg2));
        if ((mScene.getX() + arg2) >= 0
                && (mScene.getX() + arg2) <= CAMERA_WIDTH) {
            mScene.setX(mScene.getX() + arg2);
        }
        distanceX += arg2;
    }

    @Override
    public void onScrollFinished(ScrollDetector arg0, int arg1, float arg2,
            float arg3) {
        System.out.println(isChange);
        System.out.println(distanceX);
        System.out.println(mScene.getX());
        if ((distanceX < -CAMERA_WIDTH / 5) && !isChange) {

            mScene.registerEntityModifier(new MoveXModifier(0.3f,
                    mScene.getX(), 0, EaseCircularInOut.getInstance()));
            isChange = true;
        } else if ((distanceX > CAMERA_WIDTH / 5) && isChange) {

            mScene.registerEntityModifier(new MoveXModifier(0.3f,
                    mScene.getX(), CAMERA_WIDTH, EaseCircularInOut
                            .getInstance()));
            isChange = false;
        } else {
            mScene.registerEntityModifier(new MoveXModifier(0.3f,
                    mScene.getX(), (isChange ? 0 : -1) * (-CAMERA_WIDTH),
                    EaseCircularInOut.getInstance()));
        }
    }

    @Override
    public void onScrollStarted(ScrollDetector arg0, int arg1, float arg2,
            float arg3) {
        this.distanceX = 0;
    }

    @Override
    public void onClick(ClickDetector arg0, int arg1, float arg2, float arg3) {

    }
}
