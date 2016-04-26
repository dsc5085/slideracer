package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.geometry.RectangleUtils;
import dclib.geometry.VertexUtils;

public class TerrainFactory {

	private static final float MIN_PATH_BUFFER_RATIO = 0.1f; 
	private static final float MAX_PATH_BUFFER_RATIO = 10; 
	private static final float EDGE_MAX_DEVIATION_X = 5; // TODO: Make this based off of player speed
	private static final float EDGE_MAX_INCREASE_Y = 8;
	
	private final Level level;
	private final EntityFactory entityFactory;
	private final Vector2 racerSize;

	public TerrainFactory(final Level level, final EntityFactory entityFactory, final Vector2 racerSize) {
		this.level = level;
		this.entityFactory = entityFactory;
		this.racerSize = racerSize;
	}
	
	public final List<Entity> create() {
		List<Vector2> leftCliffVertices = createLeftCliffVertices();
		List<Vector2> rightCliffVertices = createRightCliffVertices(leftCliffVertices);
		Rectangle bounds = level.getBounds();
		float minX = Float.MAX_VALUE;
		for (Vector2 vertex : leftCliffVertices) {
			minX = Math.min(vertex.x, minX);
		}
		Vector2 cornerPoint = new Vector2(minX, RectangleUtils.top(bounds));
		leftCliffVertices.add(cornerPoint);
		leftCliffVertices.add(new Vector2(minX, bounds.y));
		List<Entity> terrain = new ArrayList<Entity>();
		terrain.add(createCliff(leftCliffVertices));
		terrain.add(createCliff(rightCliffVertices));
		return terrain;
	}
	
	private List<Vector2> createLeftCliffVertices() {		
		List<Vector2> vertices = new ArrayList<Vector2>();
		Rectangle bounds = level.getBounds();
		Vector2 start = new Vector2(bounds.x, bounds.y);
		vertices.add(start);
		while (true) {
			Vector2 lastWaypoint = vertices.get(vertices.size() - 1);
			float boundsTop = RectangleUtils.top(bounds);
			float y = Math.min(lastWaypoint.y + MathUtils.random(EDGE_MAX_INCREASE_Y), boundsTop);
			float edgeDeviationX = MathUtils.random(-EDGE_MAX_DEVIATION_X, EDGE_MAX_DEVIATION_X);
			float x = lastWaypoint.x + edgeDeviationX;
			vertices.add(new Vector2(x, y));
			if (y >= boundsTop) {
				return vertices;
			}
		}
	}
	
	private List<Vector2> createRightCliffVertices(final List<Vector2> leftCliffVertices) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Rectangle bounds = level.getBounds();
		float maxX = Integer.MIN_VALUE;
		Vector2 minPathBuffer = racerSize.cpy().scl(MIN_PATH_BUFFER_RATIO);
		Vector2 maxPathBuffer =  racerSize.cpy().scl(MAX_PATH_BUFFER_RATIO);
		for (Vector2 leftPoint : leftCliffVertices) {
			float currentYToTopRatio = (leftPoint.y - bounds.y) / bounds.height;
			float pathBuffer = Interpolation.linear.apply(maxPathBuffer.x, minPathBuffer.x, currentYToTopRatio);
			float x = leftPoint.x + racerSize.x * (pathBuffer + 1);
			maxX = Math.max(x, maxX);
			vertices.add(new Vector2(x, leftPoint.y));
		}
		Vector2 topRightCornerPoint = new Vector2(maxX, RectangleUtils.top(bounds));
		vertices.add(topRightCornerPoint);
		Vector2 bottomRightCornerPoint = new Vector2(maxX, bounds.y);
		vertices.add(bottomRightCornerPoint);
		return vertices;
	}
	
	private Entity createCliff(final List<Vector2> verticesList) {
		float[] vertices = VertexUtils.toVerticesArray(verticesList);
		return entityFactory.createTerrain(vertices);
	}
	
}
