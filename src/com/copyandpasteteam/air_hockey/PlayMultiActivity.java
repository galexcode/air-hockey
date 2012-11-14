
package com.copyandpasteteam.air_hockey;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class PlayMultiActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {

	
	public enum SceneType{
		
		MENU, GAME
		
	}
	
	private GameMenu mPauseScene= new GameMenu();
	
	 public SceneType currentScene = SceneType.GAME;
	 
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 1280;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	private BitmapTextureAtlas mBitmapBeaterTextureAtlas;
	private BitmapTextureAtlas mBitmapPuckTextureAtlas;
	
	private TextureRegion mCircleBeaterTextureRegion;
	private TextureRegion mCirclePuckTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;

	private MouseJoint mMouseJointActive;
	private Body mGroundBody;
	   
	private Sprite bg;
	private Sprite goalPostTop;
	private Sprite goalPostBottom;
	   
	private TextureRegion mBgTexture;
	private TextureRegion mGPTTexture;
	private TextureRegion mGPBTexture;
	private BitmapTextureAtlas mBackgroundAtlas;

	private BitmapTextureAtlas mGoalPostBottomAtlas;
	private BitmapTextureAtlas mGoalPostTopAtlas;



	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Let's Play", Toast.LENGTH_LONG).show();

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		

		this.mBitmapPuckTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 143, 143, TextureOptions.BILINEAR);
		this.mBitmapBeaterTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 204, 204, TextureOptions.BILINEAR);
		
		this.mCirclePuckTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapPuckTextureAtlas, this, "puck.png", 0, 0); // 64x32
		this.mCircleBeaterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapBeaterTextureAtlas, this, "beater.png", 0, 0); // 64x32
		
		this.mBackgroundAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1300, 1300, TextureOptions.DEFAULT);
        mBgTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundAtlas, this, "table_bg.png", 0, 0);
        
        this.mGoalPostTopAtlas = new BitmapTextureAtlas(this.getTextureManager(), 292, 47, TextureOptions.DEFAULT);
        mGPTTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGoalPostTopAtlas, this, "goalpost-top.png", 0, 0);

        this.mGoalPostBottomAtlas = new BitmapTextureAtlas(this.getTextureManager(), 292, 47, TextureOptions.DEFAULT);
        mGPBTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGoalPostBottomAtlas, this, "goalpost-bottom.png", 0, 0);

        this.mGoalPostTopAtlas.load();
		this.mGoalPostBottomAtlas.load();
        this.mBackgroundAtlas.load();
		this.mBitmapPuckTextureAtlas.load();
		this.mBitmapBeaterTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);
		this.mScene.setOnAreaTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		this.mGroundBody = this.mPhysicsWorld.createBody(new BodyDef());

		bg = new Sprite(0, 0, this.mBgTexture, this.getVertexBufferObjectManager());
		goalPostTop = new Sprite(213, 0, this.mGPTTexture, this.getVertexBufferObjectManager());
		goalPostBottom = new Sprite(213, CAMERA_HEIGHT-46, this.mGPBTexture, this.getVertexBufferObjectManager());
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		final Rectangle groundLeft = new Rectangle(44, CAMERA_HEIGHT - 50, 206, 50, vertexBufferObjectManager);
		final Rectangle groundRight = new Rectangle(CAMERA_WIDTH-250, CAMERA_HEIGHT - 50, 206, 50, vertexBufferObjectManager);
		
		final Rectangle roofLeft = new Rectangle(0, 0, 250, 50, vertexBufferObjectManager);
		final Rectangle roofRight = new Rectangle(CAMERA_WIDTH-250, 0, 250, 50, vertexBufferObjectManager);
		
		final Rectangle left = new Rectangle(0, 0, 44, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH-44, 0, 44, CAMERA_HEIGHT, vertexBufferObjectManager);

		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roofLeft, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roofRight, BodyType.StaticBody, wallFixtureDef);
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, groundLeft, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, groundRight, BodyType.StaticBody, wallFixtureDef);
		
		right.setColor(0, 0, 0, 0);
		left.setColor(0, 0, 0, 0);
		roofLeft.setColor(0, 0, 0, 0);
		roofRight.setColor(0, 0, 0, 0);
		groundLeft.setColor(0, 0, 0, 0);
		groundRight.setColor(0, 0, 0, 0);

		this.mScene.attachChild(bg);
		this.mScene.attachChild(groundLeft);
		this.mScene.attachChild(groundRight);
		this.mScene.attachChild(roofLeft);
		this.mScene.attachChild(roofRight);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);


		this.addPuck(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
		this.addBeater(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
		this.addBeater(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
		
		this.mScene.attachChild(goalPostTop);
		this.mScene.attachChild(goalPostBottom);
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		return this.mScene;
	}

	@Override
	public void onGameCreated() {
		this.mEngine.enableVibrator(this);
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
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

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

	
	public void onBackPressed(){
    	
    	
    	switch (currentScene)
        {
             
             case MENU:
            	 
            	 finish();
                  break;
             
             case GAME:
                  mEngine.setScene(mPauseScene);
                  currentScene = SceneType.MENU;
                  break; 
                  

        }
    	
    }


	public MouseJoint createMouseJoint(final IAreaShape pFace, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		final Body body = (Body) pFace.getUserData();
		final MouseJointDef mouseJointDef = new MouseJointDef();

		final Vector2 localPoint = Vector2Pool.obtain((pTouchAreaLocalX - pFace.getWidth() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (pTouchAreaLocalY - pFace.getHeight() * 0.5f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		this.mGroundBody.setTransform(localPoint, 0);

		mouseJointDef.bodyA = this.mGroundBody;
		mouseJointDef.bodyB = body;
		mouseJointDef.dampingRatio = 0.2f;
		mouseJointDef.frequencyHz = 60;
		mouseJointDef.maxForce = (100000.0f * body.getMass());
		mouseJointDef.collideConnected = true;

		mouseJointDef.target.set(body.getWorldPoint(localPoint));
		Vector2Pool.recycle(localPoint);

		return (MouseJoint) this.mPhysicsWorld.createJoint(mouseJointDef);
	}

	private void addPuck(final float pX, final float pY) {

		Debug.d("Added Puck ");

		final Sprite puck;
		final Body body;

		puck = new Sprite(pX, pY, this.mCirclePuckTextureRegion, this.getVertexBufferObjectManager());
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, puck, BodyType.DynamicBody, FIXTURE_DEF);
			
		puck.setUserData(body);

		this.mScene.attachChild(puck);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(puck, body, true, true));
	}
	
	
	private void addBeater(final float pX, final float pY) {

		Debug.d("Added Beater ");

		final Sprite beater;
		final Body body;

		beater = new Sprite(pX, pY, this.mCircleBeaterTextureRegion, this.getVertexBufferObjectManager());
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, beater, BodyType.DynamicBody, FIXTURE_DEF);
			
		beater.setUserData(body);

		this.mScene.registerTouchArea(beater);
		this.mScene.attachChild(beater);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(beater, body, true, true));
	}

}
