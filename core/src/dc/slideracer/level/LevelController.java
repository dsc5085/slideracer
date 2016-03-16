package dc.slideracer.level;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.entitysystems.RacerInputSystem;
import dc.slideracer.entitysystems.WaypointsSystem;
import dclib.epf.DefaultEntityManager;
import dclib.epf.DefaultEntitySystemManager;
import dclib.epf.Entity;
import dclib.epf.EntityManager;
import dclib.epf.EntitySystemManager;
import dclib.epf.systems.DrawableSystem;
import dclib.epf.util.EntityDrawer;
import dclib.geometry.UnitConverter;
import dclib.graphics.CameraUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.TextureCache;
import dclib.system.Advancer;

public final class LevelController {

	private static final int PIXELS_PER_UNIT = 32;

	private final EntityFactory entityFactory;
	private final TerrainFactory terrainFactory;
	private final EntityManager entityManager = new DefaultEntityManager();
	private final EntitySystemManager entitySystemManager = new DefaultEntitySystemManager(entityManager);
	private final Advancer advancer;
	private final Camera camera;
	private final EntityDrawer entityDrawer;

	public LevelController(final Level level, final TextureCache textureCache, final PolygonSpriteBatch spriteBatch) {
		ConvexHullCache convexHullCache = new ConvexHullCache(textureCache);
		entityFactory = new EntityFactory(textureCache, convexHullCache);
		terrainFactory = new TerrainFactory(level, entityFactory);
		spawnInitialEntities();
		advancer = createAdvancer();
		Rectangle bounds = level.getBounds();
		final float viewportHeightToWidthRatio = 0.75f;
		Rectangle worldViewport = new Rectangle(bounds.x, bounds.y, bounds.width, 
				bounds.width * viewportHeightToWidthRatio);
		camera = createCamera(worldViewport);
		entityDrawer = new EntityDrawer(entityManager, spriteBatch, camera);
		addSystems();
	}
	
	public final void dispose() {
		entityManager.dispose();
		entitySystemManager.dispose();
	}

	public final void update(final float delta) {
		advancer.advance(delta);
		camera.update();
	}
	
	public final void draw() {
		entityDrawer.draw();
	}

	private void addSystems() {
		UnitConverter unitConverter = new UnitConverter(PIXELS_PER_UNIT, camera);
		entitySystemManager.add(new WaypointsSystem());
		entitySystemManager.add(new RacerInputSystem(unitConverter));
		entitySystemManager.add(new DrawableSystem(unitConverter));
	}
	
	private Advancer createAdvancer() {
		return new Advancer() {
			@Override
			protected void update(final float delta) {
				entitySystemManager.update(delta);
			}
		};
	}
	
	private Camera createCamera(final Rectangle worldViewport) {
		Camera camera = new OrthographicCamera();
		CameraUtils.setViewport(camera, worldViewport, PIXELS_PER_UNIT);
		return camera;
	}

	private void spawnInitialEntities() {
		Entity racer = entityFactory.createRacer(new Vector3(0, 0, 1));
		entityManager.add(racer);
		entityManager.addAll(terrainFactory.create());
	}
	
}
