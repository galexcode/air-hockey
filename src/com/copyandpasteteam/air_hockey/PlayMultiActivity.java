package com.copyandpasteteam.air_hockey;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.WorldManifold;

public class PlayMultiActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IAccelerationListener, ContactListener {

    private int CAMERA_WIDTH = 720;
    private int CAMERA_HEIGHT = 1280;
    
    private BitmapTextureAtlas beaterTexture;
    private BitmapTextureAtlas puckTexture;
    private ITextureRegion beaterRegion;
    private ITextureRegion puckRegion;
    private Scene mScene;
    private Camera camera;
    
    private Sprite beaterSprite;
    private Sprite puckSprite;
    private Body puckBody;
    private Body beaterBody;
    
    private PhysicsWorld mPhysicsWorld;

    private FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.6f, 0.0f, 0.0f);
    final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(1, 0.0f, 0.0f);

    
    @Override
    public EngineOptions onCreateEngineOptions() {
        // TODO Auto-generated method stub

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    protected void onCreateResources() {
        // TODO Auto-generated method stub
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        beaterTexture = new BitmapTextureAtlas(this.getTextureManager(), 204, 204, TextureOptions.BILINEAR);
        beaterRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(beaterTexture, getAssets(), "beater.png", 0, 0);
        
        beaterTexture.load();
        
        puckTexture = new BitmapTextureAtlas(this.getTextureManager(), 204, 204, TextureOptions.BILINEAR);
        puckRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(puckTexture, getAssets(), "puck.png", 0, 0);
        
        puckTexture.load();
       // enableAccelerationSensor(this);

    }

    @Override
    protected Scene onCreateScene() {
    
        this.mScene = new Scene();
        this.mScene.setBackground(new Background(0.5f, 0, 0.5f));
        this.mScene.setOnSceneTouchListener(this);
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

        final float centerX = (CAMERA_WIDTH - this.beaterRegion.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.beaterRegion.getHeight()) / 2;
		
		puckSprite = new Sprite(centerX, centerY, this.puckRegion, this.getVertexBufferObjectManager());
        
        beaterSprite = new Sprite(centerX, centerY, this.beaterRegion, this.getVertexBufferObjectManager()){

        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

            switch(pSceneTouchEvent.getAction()){
                case TouchEvent.ACTION_MOVE:
                    //Here 'body' refers to the Body object associated with this sprite
                	beaterBody.setTransform(pSceneTouchEvent.getX()/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                				pSceneTouchEvent.getY() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                                      beaterBody.getAngle());
                	
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    
        beaterBody = PhysicsFactory.createCircleBody(mPhysicsWorld, beaterSprite,  BodyType.DynamicBody, FIXTURE_DEF);
        beaterBody.setUserData("smiley");
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(beaterSprite, beaterBody, true, false));

        puckBody = PhysicsFactory.createCircleBody(mPhysicsWorld, puckSprite,  BodyType.DynamicBody, FIXTURE_DEF);
        puckBody.setUserData("puck");
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(puckSprite, puckBody, true, false));
        
        final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 30, CAMERA_WIDTH, 30, vertexBufferObjectManager);
        final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 30, vertexBufferObjectManager);
        final Rectangle left = new Rectangle(0, 0, 30, CAMERA_HEIGHT, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(CAMERA_WIDTH - 30, 0, 30, CAMERA_HEIGHT, vertexBufferObjectManager);

        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData("wall");
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData("wall");

        mScene.attachChild(beaterSprite);
        mScene.attachChild(puckSprite);
        
        mScene.attachChild(right);
        mScene.attachChild(left);
        mScene.attachChild(roof);
        mScene.attachChild(ground);

        this.mScene.registerUpdateHandler(this.mPhysicsWorld);
        mPhysicsWorld.setContactListener(this);
        
        mScene.setOnSceneTouchListener(this);
        mScene.registerTouchArea(beaterSprite);
        mScene.setTouchAreaBindingOnActionDownEnabled(true);
        
        return mScene;
    }

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

    
}