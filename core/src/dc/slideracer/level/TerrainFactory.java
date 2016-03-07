package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dclib.epf.Entity;
import dclib.geometry.RectangleUtils;
import dclib.geometry.VertexUtils;

public class TerrainFactory {
	
	private static final float PATH_MIN_WIDTH = 2;
	private static final float HORIZONTAL_MARGIN = 1;
	private static final float EDGE_MAX_DEVIATION_X = 5; // TODO: Make this based off of player speed
	private static final float EDGE_MAX_INCREASE_Y = 8;
	
	private final Level level;
	private final EntityFactory entityFactory;

	public TerrainFactory(final Level level, final EntityFactory entityFactory) {
		this.level = level;
		this.entityFactory = entityFactory;
	}
	
	public final List<Entity> create() {
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
