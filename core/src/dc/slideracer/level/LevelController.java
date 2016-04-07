package dc.slideracer.level;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.collision.system.CollisionChecker;
import dc.slideracer.collision.system.CollisionManager;
import dc.slideracer.collision.system.DamageCollisionResolver;
import dc.slideracer.epf.graphics.EntityColliderDrawer;
import dc.slideracer.epf.systems.CollisionSystem;
import dc.slideracer.epf.systems.RacerInputSystem;
import dc.slideracer.epf.systems.WaypointsSystem;
import dc.slideracer.parts.HealthPart;
import dclib.epf.DefaultEntityManager;
import dclib.epf.DefaultEntitySystemManager;
import dclib.epf.Entity;
import dclib.epf.EntityAddedListener;
import dclib.epf.EntityManager;
import dclib.epf.EntitySystemManager;
import dclib.epf.graphics.EntityDrawer;
import dclib.epf.graphics.EntitySpriteDrawer;
import dclib.epf.graphics.EntityTransformDrawer;
import dclib.epf.systems.DrawableSystem;
import dclib.eventing.DefaultListener;
import dclib.geometry.LinearUtils;
import dclib.geometry.UnitConverter;
import dclib.graphics.CameraUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.TextureCache;
import dclib.system.Advancer;

public final class LevelController {

	private static final int PIXELS_PER_UNIT = 32;

	private final Level level;
	private final EntityFactory entityFactory;
	private final TerrainFactory terrainFactory;
	private final EntityManager entityManager = new DefaultEntityManager();
	private final EntitySystemManager entitySystemManager = new DefaultEntitySystemManager(entityManager);
	private final CollisionManager collisionManager;
	private final Advancer advancer;
	private final Camera camera;
	private final EntityDrawer entitySpriteDrawer;
	private final EntityDrawer entityTransformDrawer;
	private final EntityDrawer entityColliderDrawer;

	public LevelController(final Level level, final TextureCache textureCache, final PolygonSpriteBatch spriteBatch, 
			final ShapeRenderer shapeRenderer) {
		this.level = level;
		ConvexHullCache convexHullCache = new ConvexHullCache(textureCache);
		entityFactory = new EntityFactory(textureCache, convexHullCache);
		terrainFactory = new TerrainFactory(level, entityFactory);
		entityManager.addEntityAddedListener(entityAdded());
		spawnInitialEntities();
		collisionManager = createCollisionManager();
		advancer = createAdvancer();
		Rectangle worldViewport = level.getBounds();
		final float viewportHeightToWidthRatio = 0.75f;
		worldViewport.height = worldViewport.width * viewportHeightToWidthRatio;
		camera = createCamera(worldViewport);
		entitySpriteDrawer = new EntitySpriteDrawer(spriteBatch, camera);
		entityTransformDrawer = new EntityTransformDrawer(shapeRenderer, camera, PIXELS_PER_UNIT);
		entityColliderDrawer = new EntityColliderDrawer(shapeRenderer, camera, PIXELS_PER_UNIT);
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
		List<Entity> entities = entityManager.getAll();
		entitySpriteDrawer.draw(entities);
		entityTransformDrawer.draw(entities);
		entityColliderDrawer.draw(entities);
	}

	private EntityAddedListener entityAdded() {
		return new EntityAddedListener() {
			@Override
			public void created(final Entity entity) {
				if (entity.hasActive(HealthPart.class)) {
					entity.get(HealthPart.class).addNoHealthListener(noHealth(entity));
				}
			}
		};
	}
	
	private DefaultListener noHealth(final Entity entity) {
		return new DefaultListener() {
			@Override
			public void executed() {
				entityManager.remove(entity);
			}
		};
	}

	private void addSystems() {
		UnitConverter unitConverter = new UnitConverter(PIXELS_PER_UNIT, camera);
		entitySystemManager.add(new CollisionSystem());
		entitySystemManager.add(new WaypointsSystem());
		entitySystemManager.add(new RacerInputSystem(unitConverter));
		entitySystemManager.add(new DrawableSystem(unitConverter));
	}
	
	private CollisionManager createCollisionManager() {
		CollisionChecker damageCollisionChecker = new CollisionChecker();
		damageCollisionChecker.link(CollisionType.HAZARD, CollisionType.RACER);
		return new CollisionManager(new DamageCollisionResolver(damageCollisionChecker));
	}
	
	private Advancer createAdvancer() {
		return new Advancer() {
			@Override
			protected void update(final float delta) {
				entitySystemManager.update(delta);
				collisionManager.checkCollisions(entityManager.getAll());
			}
		};
	}
	
	private Camera createCamera(final Rectangle worldViewport) {
		Camera camera = new OrthographicCamera();
		CameraUtils.setViewport(camera, worldViewport, PIXELS_PER_UNIT);
		return camera;
	}

	private void spawnInitialEntities() {
		Vector2 size = new Vector2(1, 1);
		float racerX = LinearUtils.relativeMiddle(level.getBounds().width, size.x);
		Vector3 racerPosition = new Vector3(racerX, level.getBounds().y, 1);
		Entity racer = entityFactory.createRacer(size, racerPosition);
		entityManager.add(racer);
		entityManager.addAll(terrainFactory.create());
	}
	
}
