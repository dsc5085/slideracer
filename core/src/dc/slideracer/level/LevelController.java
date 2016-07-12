package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.collision.system.CollisionChecker;
import dc.slideracer.collision.system.CollisionManager;
import dc.slideracer.collision.system.DamageCollisionResolver;
import dc.slideracer.epf.graphics.EntityColliderDrawer;
import dc.slideracer.epf.systems.CollisionSystem;
import dc.slideracer.epf.systems.ColorChangeSystem;
import dc.slideracer.epf.systems.EmitSystem;
import dc.slideracer.epf.systems.RacerInputSystem;
import dc.slideracer.epf.systems.TimedDeathSystem;
import dc.slideracer.parts.FragsPart;
import dc.slideracer.parts.HealthPart;
import dc.slideracer.parts.SpawnOnDeathPart;
import dclib.epf.DefaultEntityManager;
import dclib.epf.DefaultEntitySystemManager;
import dclib.epf.Entity;
import dclib.epf.EntityAddedListener;
import dclib.epf.EntityCache;
import dclib.epf.EntityManager;
import dclib.epf.EntityRemovedListener;
import dclib.epf.EntitySpawner;
import dclib.epf.EntitySystemManager;
import dclib.epf.graphics.EntityDrawer;
import dclib.epf.graphics.EntitySpriteDrawer;
import dclib.epf.graphics.EntityTransformDrawer;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.TransformPart;
import dclib.epf.systems.DrawableSystem;
import dclib.epf.systems.TranslateSystem;
import dclib.eventing.DefaultListener;
import dclib.geometry.PolygonUtils;
import dclib.geometry.RectangleUtils;
import dclib.geometry.UnitConverter;
import dclib.graphics.CameraUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.TextureCache;
import dclib.system.Advancer;
import dclib.util.FloatRange;
import dclib.util.Maths;

public final class LevelController {

	private static final int PIXELS_PER_UNIT = 32;
	private static final Vector2 VIEWPORT_SIZE = new Vector2(10 * PIXELS_PER_UNIT, 7.5f * PIXELS_PER_UNIT);
	private static final float TERRAIN_SECTION_HEIGHT = 4 * VIEWPORT_SIZE.y / PIXELS_PER_UNIT;
	private static final Vector3 RACER_START_POSITION = new Vector3(0, 0, 1);
	private static final Vector2 RACER_SIZE = new Vector2(1.5f, 1.5f);
	
	private boolean isRunning = true;
	private float score = 0;
	private final EntityFactory entityFactory;
	private final EntityCache entityCache;
	private final TerrainFactory terrainFactory;
	private final EntityManager entityManager = new DefaultEntityManager();
	private final EntitySpawner entitySpawner;
	private final EntitySystemManager entitySystemManager = new DefaultEntitySystemManager(entityManager);
	private final Advancer advancer;
	private final Camera camera;
	private final UnitConverter unitConverter;
	private final List<EntityDrawer> entityDrawers = new ArrayList<EntityDrawer>();
	private CollisionManager collisionManager;
	private final List<TerrainSection> terrainSections = new ArrayList<TerrainSection>();
	private Entity racer;
	private float oldRacerY;

	public LevelController(final TextureCache textureCache, final PolygonSpriteBatch spriteBatch, 
			final ShapeRenderer shapeRenderer) {
		entityManager.addEntityAddedListener(entityAdded());
		entityManager.addEntityRemovedListener(entityRemoved());
		advancer = createAdvancer();
		camera = createCamera();
		unitConverter = new UnitConverter(PIXELS_PER_UNIT, camera);
		ConvexHullCache convexHullCache = new ConvexHullCache(textureCache);
		Rectangle racerBounds = new Rectangle(RACER_START_POSITION.x, RACER_START_POSITION.y, RACER_SIZE.x, 
				RACER_SIZE.y);
		entityFactory = new EntityFactory(unitConverter, textureCache, convexHullCache);
		terrainFactory = new TerrainFactory(entityFactory, racerBounds);
		entityCache = new DefaultEntityCache(entityFactory);
		entitySpawner = new EntitySpawner(entityCache, entityManager);
		entityDrawers.add(new EntitySpriteDrawer(spriteBatch, camera));
		entityDrawers.add(new EntityTransformDrawer(shapeRenderer, camera, PIXELS_PER_UNIT));
		entityDrawers.add(new EntityColliderDrawer(shapeRenderer, camera, PIXELS_PER_UNIT));
		addSystems();
		setupLevel();
	}
	
	public final void toggleRunning() {
		isRunning = !isRunning;
	}
	
	public final int getScore() {
		return MathUtils.floor(score);
	}

	public final void dispose() {
		entityManager.dispose();
	}

	public final void update(final float delta) {
		if (isRunning) {
			advancer.advance(delta);
			updateTerrain();
		}
	}

	public final void draw() {
		List<Entity> entities = entityManager.getAll();
		for (EntityDrawer entityDrawer : entityDrawers) {
			entityDrawer.draw(entities);
		}
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

	private EntityRemovedListener entityRemoved() {
		return new EntityRemovedListener() {
			@Override
			public void removed(final Entity entity) {
				spawnOnDeath(entity);
				fragment(entity);
				if (entity == racer) {
					dispose();
					setupLevel();
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
		entitySystemManager.add(new TranslateSystem());
		entitySystemManager.add(new CollisionSystem());
		entitySystemManager.add(new RacerInputSystem());
		entitySystemManager.add(new TimedDeathSystem(entityManager));
		entitySystemManager.add(new ColorChangeSystem());
		entitySystemManager.add(new DrawableSystem(unitConverter));
		entitySystemManager.add(new EmitSystem(entitySpawner));
	}
	
	private Advancer createAdvancer() {
		return new Advancer() {
			@Override
			protected void update(final float delta) {
				oldRacerY = racer.get(TransformPart.class).getPosition().y;
				entitySystemManager.update(delta);
				collisionManager.checkCollisions(entityManager.getAll());
				updateCamera();
				updateScore();
			}
		};
	}
	
	private Camera createCamera() {
		Camera camera = new OrthographicCamera();
		camera.viewportWidth = VIEWPORT_SIZE.x;
		camera.viewportHeight = VIEWPORT_SIZE.y;
		return camera;
	}
	
	private void spawnOnDeath(final Entity entity) {
		if (entity.hasActive(SpawnOnDeathPart.class)) {
			String entityTypeName = entity.get(SpawnOnDeathPart.class).getEntityType();
			Entity spawn = entitySpawner.spawn(entityTypeName);
			TransformPart spawnTransform = spawn.get(TransformPart.class);
			Vector2 entityCenter = entity.get(TransformPart.class).getCenter();
			Vector2 position = PolygonUtils.relativeCenter(entityCenter, spawnTransform.getSize());
			spawnTransform.setPosition(position);
		}
	}
	
	private void fragment(final Entity entity) {
		if (entity.hasActive(FragsPart.class)) {
			DrawablePart drawablePart = entity.get(DrawablePart.class);
			TransformPart transformPart = entity.get(TransformPart.class);
			Polygon polygon = transformPart.getPolygon();
			FragParams fragParams = new FragParams();
			fragParams.fadeTime = 2;
			fragParams.height = 8;
			fragParams.width = 8;
			fragParams.speedModifier = 20;
			fragParams.z = transformPart.getZ();
			List<Entity> frags = Fragmenter.createFrags(drawablePart.getSprite().getRegion(), polygon, fragParams, 
					unitConverter);
			entityManager.addAll(frags);
		}
	}
	
	private void setupLevel() {
		setupCollisionManager();
		spawnInitialEntities();
	}
	
	private void setupCollisionManager() {
		CollisionChecker damageCollisionChecker = new CollisionChecker();
		damageCollisionChecker.link(CollisionType.HAZARD, CollisionType.RACER);
		collisionManager = new CollisionManager(new DamageCollisionResolver(damageCollisionChecker));
	}

	private void spawnInitialEntities() {
		// TODO: More precise start vertex x
		Vector2 leftCliffStartVertex = new Vector2(RACER_START_POSITION.x - 2, RACER_START_POSITION.y);
		Vector2 rightCliffStartVertex = new Vector2(RACER_START_POSITION.x + 2, RACER_START_POSITION.y);
		TerrainSection terrainSection = terrainFactory.create(leftCliffStartVertex, rightCliffStartVertex, 
				TERRAIN_SECTION_HEIGHT);
		add(terrainSection);
		racer = entityFactory.createRacer(RACER_SIZE, RACER_START_POSITION);
		entityManager.add(racer);
	}
	
	private void updateCamera() {
		Vector2 worldViewportSize = unitConverter.toWorldUnits(camera.viewportWidth, camera.viewportHeight);
		TransformPart racerTransformPart = racer.get(TransformPart.class);
		float newCameraX = racerTransformPart.getCenter().x  - worldViewportSize.x / 2;
		float newCameraY = racerTransformPart.getPosition().y;
		Rectangle newViewport = new Rectangle(newCameraX, newCameraY, worldViewportSize.x, worldViewportSize.y);
		CameraUtils.setViewport(camera, newViewport, PIXELS_PER_UNIT);
		camera.update();
	}
	
	private void updateTerrain() {
		TerrainSection topTerrainSection = terrainSections.get(terrainSections.size() - 1);
		Rectangle viewport = CameraUtils.getViewport(camera, unitConverter.getPixelsPerUnit());
		if (Maths.distance(topTerrainSection.getTop(), RectangleUtils.top(viewport)) < TERRAIN_SECTION_HEIGHT) {
			for (TerrainSection terrainSection : new ArrayList<TerrainSection>(terrainSections)) {
				if (terrainSection.getTop() < viewport.y){
					entityManager.removeAll(terrainSection.getAll());
					terrainSections.remove(terrainSection);
				}
			}
			Vector2 leftCliffStartVertex = topTerrainSection.getLeftCliffTopVertex();
			Vector2 rightCliffStartVertex = topTerrainSection.getRightCliffTopVertex();
			TerrainSection newTerrainSection = terrainFactory.create(leftCliffStartVertex, rightCliffStartVertex, 
					TERRAIN_SECTION_HEIGHT);
			add(newTerrainSection);
		}
	}

	private void updateScore() {
		final FloatRange progressScoreMultiplier = new FloatRange(1, 5);
		float racerY = racer.get(TransformPart.class).getPosition().y;
		float progressRatio = LevelUtils.getProgressRatio(racerY, RACER_START_POSITION.y);
		float scoreMultiplier = Interpolation.linear.apply(
				progressScoreMultiplier.min(), progressScoreMultiplier.max(), progressRatio);
		float scoreBaseIncrease = racer.get(TransformPart.class).getPosition().y - oldRacerY;
		score += scoreBaseIncrease * scoreMultiplier;
	}
	
	private void add(final TerrainSection terrainSection) {
		terrainSections.add(terrainSection);
		entityManager.addAll(terrainSection.getAll());
	}
	
}
