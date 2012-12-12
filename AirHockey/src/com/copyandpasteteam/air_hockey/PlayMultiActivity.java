
package com.copyandpasteteam.air_hockey;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class PlayMultiActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener, IOnMenuItemClickListener {

	public enum SceneType{
		
		MENU, GAME
		
	}
	
	public SceneType currentScene = SceneType.GAME; 
	 
	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 1280;

	 /* KATEGORIE fixturedef */
    public static final short CATEGORYBIT_WALL = 1;
    public static final short CATEGORYBIT_PUCK = 2;
    public static final short CATEGORYBIT_BEATER = 4;
    public static final short CATEGORYBIT_DIVIDER = 8;

    /* FILTRY KOLIZJI */
    public static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_PUCK + CATEGORYBIT_BEATER;
    public static final short MASKBITS_PUCK = CATEGORYBIT_WALL + CATEGORYBIT_PUCK + CATEGORYBIT_BEATER; 
    public static final short MASKBITS_BEATER = CATEGORYBIT_WALL + CATEGORYBIT_BEATER + CATEGORYBIT_PUCK + CATEGORYBIT_DIVIDER; 
    public static final short MASKBITS_DIVIDER = CATEGORYBIT_BEATER; 

    public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);
    public static final FixtureDef PUCK_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_PUCK, MASKBITS_PUCK, (short)0);
    public static final FixtureDef BEATER_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_BEATER, MASKBITS_BEATER, (short)0);
    public static final FixtureDef DIVIDER_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_DIVIDER, MASKBITS_DIVIDER, (short)0);


	private Font mFont; 
	private Text mGoalBottomText;
	private Text mGoalTopText;
	
	private Scene mScene; //Scena glowna 
	private MenuScene mPauseScene; //Scena po wcisnieciu przycisku pause 
	
	private PhysicsWorld mPhysicsWorld; //Swiat fizyczny

	private MouseJoint mMouseJointActive; //mousejoint do poruszania obiektami
	private Body mGroundBody; //
	
	//TEXTURY
	private Sprite bg;
	private Sprite goalPostTop;
	private Sprite goalPostBottom;
	   
	private TextureRegion mBgTexture;
	private TextureRegion mGPTTexture;
	private TextureRegion mGPBTexture;
	private TextureRegion mCircleBeaterTextureRegion;
	private TextureRegion mCirclePuckTextureRegion;
	
	private BitmapTextureAtlas mBackgroundAtlas;
	private BitmapTextureAtlas mGoalPostBottomAtlas;
	private BitmapTextureAtlas mGoalPostTopAtlas;
	private BitmapTextureAtlas mBitmapBeaterTextureAtlas;
	private BitmapTextureAtlas mBitmapPuckTextureAtlas;
	
	//Krazek
	private Sprite puck;
	private Body puckBody;
	
	//Bijak 1
	private Sprite beater;
	private Body beaterBody;
	
	//Bijak 2
	private Sprite beater2;
	private Body beaterBody2;
	
	private Body leftBody;
	private Body rightBody;
	private Body roofLeftBody;
	private Body roofRightBody;
	private Body groundRightBody;
	private Body groundLeftBody;
	
	//Obiekty umieszczone za bramka ktore po zetknieciu sie z krazkiem powoduja przyznanie punktu
	private Sprite goalPostTopCatch;
	private Sprite goalPostBottomCatch;
	private Body goalPostBottomCatchBody; 
	private Body goalPostTopCatchBody;
	
	private boolean goalTop; //jesli wpadnie gol zmienna przyjmuje wartosc true co jest sprawdzane przez metode onUpdate
	private boolean goalBottom; 
	
	private int goalTopCount; //punkty gracza grajacego u gory 
	private int goalBottomCount; //punkty gracza grajacego u dolu

	Camera camera = null;

	private ITextureRegion mMenuResetTextureRegion;
	private ITextureRegion mMenuQuitTextureRegion;
	private BitmapTextureAtlas mMenuTexture;

	private Sprite pauseButton;
	
	protected static final int MENU_RESET = 0;
    protected static final int MENU_QUIT = MENU_RESET + 1;


	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Let's Play", Toast.LENGTH_LONG).show();
		

 
        this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		Engine mEngine = new Engine(engineOptions);

		//Multitouch support
		if(MultiTouch.isSupported(this)) {
            mEngine.setTouchController(new MultiTouchController());
            if(MultiTouch.isSupportedDistinct(this)) {
                    Toast.makeText(this, "MultiTouch detected --> Drag multiple Sprites with multiple fingers!", Toast.LENGTH_LONG).show();
            } else {
                    Toast.makeText(this, "MultiTouch detected --> Drag multiple Sprites with multiple fingers!\n\n(Your device might have problems to distinguish between separate fingers.)", Toast.LENGTH_LONG).show();
            }
		} else {
            Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)", Toast.LENGTH_LONG).show();
		}
   
		return mEngine.getEngineOptions();

	}

	
	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapPuckTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 143, 143, TextureOptions.BILINEAR);
		this.mBitmapBeaterTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 204, 204, TextureOptions.BILINEAR);
		
		this.mCirclePuckTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapPuckTextureAtlas, this, "puck.png", 0, 0); 
		this.mCircleBeaterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapBeaterTextureAtlas, this, "beater.png", 0, 0); 
		
		this.mMenuTexture = new BitmapTextureAtlas(null, 256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mMenuResetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_reset.png", 0, 0);
        this.mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_quit.png", 0, 50);
        this.mEngine.getTextureManager().loadTexture(this.mMenuTexture);
         
		this.mBackgroundAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1300, 1300, TextureOptions.DEFAULT);
        mBgTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundAtlas, this, "table_bg.png", 0, 0);
        
        this.mGoalPostTopAtlas = new BitmapTextureAtlas(this.getTextureManager(), 292, 47, TextureOptions.DEFAULT);
        mGPTTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGoalPostTopAtlas, this, "goalpost-top.png", 0, 0);

        this.mGoalPostBottomAtlas = new BitmapTextureAtlas(this.getTextureManager(), 292, 47, TextureOptions.DEFAULT);
        mGPBTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGoalPostBottomAtlas, this, "goalpost-bottom.png", 0, 0);
        
        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.WHITE);
        this.mFont.load();


        this.mGoalPostTopAtlas.load();
		this.mGoalPostBottomAtlas.load();
        this.mBackgroundAtlas.load();
		this.mBitmapPuckTextureAtlas.load();
		this.mBitmapBeaterTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger()); //rejestrowanie ilosci FPS

		this.mScene = new Scene();
        this.createMenuScene();
        
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);
		this.mScene.setOnAreaTouchListener(this);
		
		//Tworzenie obiektow
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		this.mGroundBody = this.mPhysicsWorld.createBody(new BodyDef());

		bg = new Sprite(0, 0, this.mBgTexture, this.getVertexBufferObjectManager());
		goalPostTop = new Sprite(213, 0, this.mGPTTexture, this.getVertexBufferObjectManager());
		goalPostBottom = new Sprite(213, CAMERA_HEIGHT-46, this.mGPBTexture, this.getVertexBufferObjectManager());
		
		pauseButton = new Sprite(CAMERA_WIDTH/2 - mCirclePuckTextureRegion.getWidth()/2, CAMERA_HEIGHT/2 - mCirclePuckTextureRegion.getWidth()/2, this.mCirclePuckTextureRegion, this.getVertexBufferObjectManager()) {

	        @Override
	        public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	                float pTouchAreaLocalX, float pTouchAreaLocalY) {

	            if(pSceneTouchEvent.isActionDown()){
	            	Debug.i("Pause Menu Button Pressed");
	            	
	            }
	            
	            if(pSceneTouchEvent.isActionUp()){
	            	mEngine.setScene(mPauseScene);
	                currentScene = SceneType.MENU;  
	            }
	                
	            return true;
	        }

	        
	    };

	   
	    this.mScene.registerTouchArea(pauseButton);
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		final Rectangle groundLeft = new Rectangle(44, CAMERA_HEIGHT - 50, 206, 50, vertexBufferObjectManager);
		final Rectangle groundRight = new Rectangle(CAMERA_WIDTH-250, CAMERA_HEIGHT - 50, 206, 50, vertexBufferObjectManager);
		
		final Rectangle roofLeft = new Rectangle(0, 0, 250, 50, vertexBufferObjectManager);
		final Rectangle roofRight = new Rectangle(CAMERA_WIDTH-250, 0, 250, 50, vertexBufferObjectManager);
		
		final Rectangle left = new Rectangle(0, 0, 44, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH-44, 0, 44, CAMERA_HEIGHT, vertexBufferObjectManager);
		
		final Line divider = new Line(0, CAMERA_HEIGHT/2, CAMERA_WIDTH, CAMERA_HEIGHT/2, 2, vertexBufferObjectManager);

		leftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF);
		rightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF);
		
		roofLeftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, roofLeft, BodyType.StaticBody, WALL_FIXTURE_DEF);
		roofRightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, roofRight, BodyType.StaticBody, WALL_FIXTURE_DEF);
		
		groundLeftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, groundLeft, BodyType.StaticBody, WALL_FIXTURE_DEF);
		groundRightBody =PhysicsFactory.createBoxBody(this.mPhysicsWorld, groundRight, BodyType.StaticBody, WALL_FIXTURE_DEF);
		
		mGoalBottomText = new Text(15, CAMERA_HEIGHT/2 +10, this.mFont, "0", 1000, new TextOptions(AutoWrap.LETTERS, 200, HorizontalAlign.CENTER, Text.LEADING_DEFAULT), this.getVertexBufferObjectManager());
		mGoalTopText = new Text(CAMERA_WIDTH/2 +140, CAMERA_HEIGHT/2 -45, this.mFont, "0", 1000, new TextOptions(AutoWrap.LETTERS, 200, HorizontalAlign.CENTER, Text.LEADING_DEFAULT), this.getVertexBufferObjectManager());

		mGoalTopText.setRotation(180); //odwrocenie gornego napisu z wynikiem
		
		//Ukrywanie scian
		right.setColor(0, 0, 0, 0);
		left.setColor(0, 0, 0, 0);
		roofLeft.setColor(0, 0, 0, 0);
		roofRight.setColor(0, 0, 0, 0);
		groundLeft.setColor(0, 0, 0, 0);
		groundRight.setColor(0, 0, 0, 0);
		divider.setColor(0, 0, 0, 0);

		leftBody.setUserData("wall");
		rightBody.setUserData("wall");
		roofLeftBody.setUserData("wall");
		roofRightBody.setUserData("wall");
		groundLeftBody.setUserData("wall");
		groundRightBody.setUserData("wall");

		this.mScene.attachChild(bg); //dodanie tla do sceny
		
		this.mScene.attachChild(mGoalTopText); //wyswietlanie punktow dla gornego gracza
		this.mScene.attachChild(mGoalBottomText); //wyswietlanie punktow dla dolnego gracza
		this.mScene.attachChild(pauseButton);
		this.mScene.attachChild(groundLeft); //dolna lewa sciana
		this.mScene.attachChild(groundRight); //dolna prawa sciana 
		this.mScene.attachChild(roofLeft); //gorna lewa sciana 
		this.mScene.attachChild(roofRight); //gorna prawa sciana
		this.mScene.attachChild(left); //lewa sciana
		this.mScene.attachChild(right); //prawa sciana
		this.mScene.attachChild(divider);
		
		PhysicsFactory.createLineBody(mPhysicsWorld, divider, DIVIDER_FIXTURE_DEF);
		
		this.addPuck(CAMERA_WIDTH/2 - mCirclePuckTextureRegion.getWidth()/2, CAMERA_HEIGHT/2 - mCirclePuckTextureRegion.getWidth()/2); //dodanie krazka
		this.addBeater(CAMERA_WIDTH/2 - mCircleBeaterTextureRegion.getWidth()/2, CAMERA_HEIGHT/4 - mCircleBeaterTextureRegion.getWidth()/2, beater, beaterBody); //dodanie bijaka
		this.addBeater(CAMERA_WIDTH/2 - mCircleBeaterTextureRegion.getWidth()/2, CAMERA_HEIGHT -CAMERA_HEIGHT/4 - mCircleBeaterTextureRegion.getWidth()/2, beater2, beaterBody2); //dodanie bijaka 2
	
		goalPostTopCatch = new Sprite(250, -300, this.mCircleBeaterTextureRegion, this.getVertexBufferObjectManager());
		goalPostTopCatchBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, goalPostTopCatch, BodyType.StaticBody, WALL_FIXTURE_DEF);
			
		goalPostTopCatch.setUserData(goalPostTopCatchBody);
		this.mScene.attachChild(goalPostTopCatch); 

		goalPostBottomCatch = new Sprite(250, CAMERA_HEIGHT+100, this.mCircleBeaterTextureRegion, this.getVertexBufferObjectManager());
		goalPostBottomCatchBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, goalPostBottomCatch, BodyType.StaticBody, WALL_FIXTURE_DEF);
			
		goalPostTopCatch.setUserData(goalPostBottomCatchBody);
		this.mScene.attachChild(goalPostBottomCatch);
		
		this.mScene.attachChild(goalPostTop); //dodanie gornej bramki
		this.mScene.attachChild(goalPostBottom); //dodanie dolnej bramki
		
	    mPhysicsWorld.setContactListener(contactListener()); //dodanie contact listenera do sprawdzania czy wpadla bramka
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		//Nasluchiwanie na okreslone akcje w scenie
		puck.registerUpdateHandler(new IUpdateHandler(){
            @Override
            public void onUpdate(float pSecondsElapsed) {
            	
            //bramka dla gracza u gory
            if(goalTop){
            		
            		final float angle = puckBody.getAngle(); 
            		final Vector2 v2 = Vector2Pool.obtain((CAMERA_WIDTH/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (CAMERA_HEIGHT/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            		puckBody.setTransform(v2, angle); // przeniesienei krazka na srodek boiska
                    puckBody.setLinearVelocity(0, 0); //wyzerowanie sily 
            		Vector2Pool.recycle(v2);
            		
            		goalTopCount++; //Zwiekszenie wyniku o 1
            		
            		goalTop = false;
            		mGoalTopText.setText(Integer.toString(goalTopCount)); //zmiana tekstu z wynikiem na aktualny

            		
            	}
            
            //bramka dla gracza u dolu
           	if(goalBottom){
            		
            		final float angle = puckBody.getAngle(); 
            		final Vector2 v2 = Vector2Pool.obtain((CAMERA_WIDTH/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (CAMERA_HEIGHT/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            		puckBody.setTransform(v2, angle); // przeniesienei krazka na srodek boiska
                    puckBody.setLinearVelocity(0, 0); //wyzerowanie sily 
            		Vector2Pool.recycle(v2);
            		
            		goalBottom = false; 
            		goalBottomCount++; //Zwiekszenie wyniku o 1
            		
            		mGoalBottomText.setText(Integer.toString(goalBottomCount)); //zmiana tekstu z wynikiem na aktualny

            	}
 
            }
            @Override
            public void reset() {
            }
		});
		
		

		return this.mScene;
	}
	
	//MENU GRY uruchamiane po wcisnieciu przycisku PAUSE/Wstecz
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
            switch(pMenuItem.getID()) {
                    case MENU_RESET:
                    	
                    		final float angle = puckBody.getAngle(); 
                			final Vector2 v2 = Vector2Pool.obtain((CAMERA_WIDTH/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (CAMERA_HEIGHT/2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                			puckBody.setTransform(v2, angle); 
                        	puckBody.setLinearVelocity(0, 0);
                			Vector2Pool.recycle(v2);
               
                			goalBottom = false; 
                			goalTop = false; 
                			
                			goalBottomCount=0; 
                			goalTopCount=0;
                			
                			mGoalTopText.setText(Integer.toString(goalTopCount));
                			mGoalBottomText.setText(Integer.toString(goalBottomCount));
                			
                			mEngine.setScene(mScene);
                            currentScene = SceneType.GAME;
                			
                            return true;
                    case MENU_QUIT:
                            
                            this.finish();
                            
                            return true;
                    default:
                            return false;
            }
    }
	
	//Metoda do tworzenia Menu gry
    protected void createMenuScene() {
            this.mPauseScene = new MenuScene(camera);

            final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESET, this.mMenuResetTextureRegion, this.getVertexBufferObjectManager());
            resetMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            this.mPauseScene.addMenuItem(resetMenuItem);

            final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mMenuQuitTextureRegion, this.getVertexBufferObjectManager());
            quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            this.mPauseScene.addMenuItem(quitMenuItem);

            this.mPauseScene.buildAnimations();
            this.mPauseScene.setBackgroundEnabled(false);
            this.mPauseScene.setOnMenuItemClickListener(this);
    }

	
	@Override
	public void onGameCreated() {
		this.mEngine.enableVibrator(this); //pozwala na uzycie wibracji
	}
	
	//Tworzy mousejointa dla dotknietego obiektu
		@Override
		public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

			if(pSceneTouchEvent.isActionDown()) {
				final IAreaShape beater = (IAreaShape) pTouchArea;

				if(this.mMouseJointActive == null) {
					this.mEngine.vibrate(50);
					this.mMouseJointActive = this.createMouseJoint(beater, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				
				return true;
			}
			return false;
		}
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					
					return true;
					
				case TouchEvent.ACTION_MOVE:
					
					if(this.mMouseJointActive != null) {
						final Vector2 vec = Vector2Pool.obtain(pSceneTouchEvent.getX() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
						this.mMouseJointActive.setTarget(vec);
						Vector2Pool.recycle(vec);
					}
					
					
					return true;
					
				case TouchEvent.ACTION_UP:
					
					//usuniecie mouse jointa po puszczeniu bijaka
					
					if(this.mMouseJointActive != null) {
						this.mPhysicsWorld.destroyJoint(this.mMouseJointActive);
						this.mMouseJointActive = null;
					}
					
					return true;
			}
			return false;
		}
		return false;
	}


	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	
	//Nadanie grawitacji zaleznie od ulozenia telefonu - przy pomocy akcelerometru
	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	//Zmiana scen (Gra, Pauza)
	public void onBackPressed(){

    	switch (currentScene)
        {
             case MENU:
            	 
            	  mEngine.setScene(mScene);
                  currentScene = SceneType.GAME;
                  break;
             
             case GAME:
            	 
                  mEngine.setScene(mPauseScene);
                  currentScene = SceneType.MENU;

                  break; 
        }
    	
    }
	
	//Nas³uchiwanie kontaktu obiektów
	private ContactListener contactListener()
    {
		
            ContactListener contactListener = new ContactListener()
            {
            	
					@Override
                    public void beginContact(Contact contact)
                    {
                    
                    	Fixture x1 = contact.getFixtureA();
                        Fixture x2 = contact.getFixtureB();
                        
                       //Krazek wpadl do gornej bramki
                       if (x2.getBody().equals(goalPostTopCatchBody) && x1.getBody().equals(puckBody))
                       {                                             
                        	Debug.d("Goal for PlayerBottom");
                        	goalBottom = true;
                        	
                       }
                       
                       //Krazek wpadl do dolnej bramki
                       if (x2.getBody().equals(goalPostBottomCatchBody) && x1.getBody().equals(puckBody))
                       {                                             
                        	Debug.d("Goal for PlayerTop");
                        	goalTop = true;
              
                       }
                       
                       //Kontakt krazka ze sciana
                       if (x2.getBody().equals(puckBody) && x1.getBody().getUserData().equals("wall"))
                       {                          
                    	   
                        	Debug.d("Puck Wall Contact");
              
                       }
                       
                       //Kontakt krazka z bijakiem
                       if (x2.getBody().equals(beaterBody) && x1.getBody().equals(puckBody))
                       {                                             
                        	Debug.d("Beater and Puck Contact");
                        	
                       }
                           
                    }

                    @Override
                    public void endContact(Contact contact)
                    {
                           
                    }

                    @Override
                    public void preSolve(Contact contact, Manifold oldManifold)
                    {
                           
                    }

                    @Override
                    public void postSolve(Contact contact, ContactImpulse impulse)
                    {
                           
                    }
            };
            return contactListener;
    }


	//Metoda tworzaca mousejointa
	public MouseJoint createMouseJoint(final IAreaShape pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		final Body body = (Body) pFace.getUserData();
		final MouseJointDef mouseJointDef = new MouseJointDef();

		final Vector2 localPoint = Vector2Pool.obtain((pTouchAreaLocalX/pFace.getWidth()) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pTouchAreaLocalY/pFace.getHeight()) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		this.mGroundBody.setTransform(localPoint, 0);

		mouseJointDef.bodyA = this.mGroundBody;
		mouseJointDef.bodyB = body;
		mouseJointDef.dampingRatio = 0.2f;
		mouseJointDef.frequencyHz = 100;
		mouseJointDef.maxForce = (100000.0f * body.getMass());
		mouseJointDef.collideConnected = true;

		mouseJointDef.target.set(body.getWorldPoint(localPoint));
		Vector2Pool.recycle(localPoint);

		return (MouseJoint) this.mPhysicsWorld.createJoint(mouseJointDef);
	}
	
	

	//Metoda dodajaca krazek
	private void addPuck(final float pX, final float pY) {

		Debug.d("Added Puck ");

		puck = new Sprite(pX, pY, this.mCirclePuckTextureRegion, this.getVertexBufferObjectManager());
		puckBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, puck, BodyType.DynamicBody, PUCK_FIXTURE_DEF);
			
		puck.setUserData(puckBody);

		this.mScene.attachChild(puck);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(puck, puckBody, true, false));
	}
	
	//Metoda dodajaca bijak
	private void addBeater(final float pX, final float pY, Sprite beater, Body beaterBody) {

		Debug.d("Added Beater ");

		this.beater = new Sprite(pX, pY, this.mCircleBeaterTextureRegion, this.getVertexBufferObjectManager());
		this.beaterBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, this.beater, BodyType.DynamicBody, BEATER_FIXTURE_DEF);
			
		this.beater.setUserData(this.beaterBody);

		this.mScene.attachChild(this.beater);
		this.mScene.registerTouchArea(this.beater);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.beater, this.beaterBody, true, false));
	}
	
	

}
