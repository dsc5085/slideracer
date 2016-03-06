package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import dclib.geometry.RectangleUtils;
import dclib.geometry.UnitConverter;
import dclib.geometry.VertexUtils;
import dclib.graphics.CameraUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.TextureCache;
import dclib.system.Advancer;

// TODO: cleanup
public final class LevelController {

	private static final int PIXELS_PER_UNIT = 32;
	private static final float PATH_MIN_WIDTH = 2;
	private static final float HORIZONTAL_MARGIN = 1;
	private static final float EDGE_MAX_DEVIATION_X = 5; // TODO: Make this based off of player speed
	private static final float EDGE_MAX_INCREASE_Y = 8;
	
	private final Level level;
	private final EntityFactory entityFactory;
	private final EntityManager entityManager = new DefaultEntityManager();
	private final EntitySystemManager entitySystemManager = new DefaultEntitySystemManager(entityManager);
	private final Advancer advancer;
	private final Camera camera;
	private final EntityDrawer entityDrawer;

	public LevelController(final Level level, final TextureCache textureCache, final PolygonSpriteBatch spriteBatch) {
		this.level = level;
		ConvexHullCache convexHullCache = new ConvexHullCache(textureCache);
		entityFactory = new EntityFactory(textureCache, convexHullCache);
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
		entityManager.addAll(createTerrain());
	}
	
	private List<Entity> createTerrain() {
		List<Vector2> leftCliffVertices = createLeftCliffVertices();
		List<Vector2> rightCliffVertices = createRightCliffVertices(leftCliffVertices);
		List<Entity> terrain = new ArrayList<Entity>();
		terrain.add(createCliff(leftCliffVertices));
		terrain.add(createCliff(rightCliffVertices));
		return terrain;
	}
	
	private List<Vector2> createLeftCliffVertices() {		
		List<Vector2> vertices = new ArrayList<Vector2>();
		Rectangle bounds = level.getBounds();
		vertices.add(new Vector2(bounds.x, bounds.y));
		Vector2 start = new Vector2(bounds.x + HORIZONTAL_MARGIN, bounds.y);
		vertices.add(start);
		while (true) {
			Vector2 lastWaypoint = vertices.get(vertices.size() - 1);
			float boundsTop = RectangleUtils.top(bounds);
			float y = Math.min(lastWaypoint.y + MathUtils.random(EDGE_MAX_INCREASE_Y), boundsTop);
			float minX = Math.max(lastWaypoint.x - EDGE_MAX_DEVIATION_X, bounds.x + HORIZONTAL_MARGIN);
			float maxX = Math.min(lastWaypoint.x + EDGE_MAX_DEVIATION_X, 
					RectangleUtils.right(bounds) - HORIZONTAL_MARGIN - PATH_MIN_WIDTH);
			float x = MathUtils.random(minX, maxX);
			vertices.add(new Vector2(x, y));
			if (y >= boundsTop) {
				Vector2 cornerPoint = new Vector2(bounds.x, boundsTop);
				vertices.add(cornerPoint);
				return vertices;
			}
		}
	}
	
	private List<Vector2> createRightCliffVertices(final List<Vector2> leftCliffVertices) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Rectangle bounds = level.getBounds();
		float rightEdgeStartX = RectangleUtils.right(bounds) - HORIZONTAL_MARGIN;
		float pathStartWidth = rightEdgeStartX - leftCliffVertices.get(0).x;
		float boundsRight = RectangleUtils.right(bounds);
		for (Vector2 leftPoint : leftCliffVertices) {
			float widthAlpha = (leftPoint.y - bounds.y) / bounds.height;
			float width = Interpolation.linear.apply(pathStartWidth, PATH_MIN_WIDTH, widthAlpha);
			float x = Math.min(leftPoint.x + width, boundsRight - HORIZONTAL_MARGIN);
			vertices.add(new Vector2(x, leftPoint.y));
		}
		Vector2 topRightCornerPoint = new Vector2(boundsRight, RectangleUtils.top(bounds));
		vertices.add(topRightCornerPoint);
		Vector2 bottomRightCornerPoint = new Vector2(boundsRight, bounds.y);
		vertices.add(bottomRightCornerPoint);
		return vertices;
	}
	
	private Entity createCliff(final List<Vector2> edge) {
		float[] verticesArray = VertexUtils.toVerticesArray(edge);
		Vector3 position = new Vector3(VertexUtils.minX(verticesArray), VertexUtils.minY(verticesArray), 0);
		return entityFactory.createTerrain(position, verticesArray);
	}
	
}
