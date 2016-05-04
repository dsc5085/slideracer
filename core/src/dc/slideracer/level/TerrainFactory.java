package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.geometry.VectorUtils;
import dclib.geometry.VertexUtils;

public class TerrainFactory {

	private static final float EDGE_MAX_DEVIATION_X = 5; // TODO: Make this based off of player speed
	private static final float EDGE_MAX_INCREASE_Y = 8;
	
	private final Level level;
	private final EntityFactory entityFactory;
	private final Rectangle racerBounds;
	private final Vector2 minPathBuffer;
	private final Vector2 maxPathBuffer;

	public TerrainFactory(final Level level, final EntityFactory entityFactory, final Rectangle racerBounds) {
		this.level = level;
		this.entityFactory = entityFactory;
		this.racerBounds = racerBounds;
		final float minPathBufferRatio = 1; 
		minPathBuffer = racerBounds.getSize(new Vector2()).scl(minPathBufferRatio);
		final float maxPathBufferRatio = 5; 
		maxPathBuffer =  racerBounds.getSize(new Vector2()).scl(maxPathBufferRatio);
	}
	
	public final List<Entity> create() {
		List<Vector2> leftCliffVertices = createLeftCliffVertices();
		float[] rightCliffVertices = createRightCliffVertices(leftCliffVertices);
		float[] leftCliffVerticesArray = VertexUtils.toVerticesArray(leftCliffVertices);
		float minX = VertexUtils.minX(leftCliffVerticesArray);
		Vector2 cornerVertex = new Vector2(minX, racerBounds.y + level.getHeight());
		leftCliffVerticesArray = VertexUtils.addVertices(leftCliffVerticesArray, cornerVertex, 
				new Vector2(minX, racerBounds.y));
		List<Entity> terrain = new ArrayList<Entity>();
		terrain.add(entityFactory.createTerrain(leftCliffVerticesArray));
		terrain.add(entityFactory.createTerrain(rightCliffVertices));
		return terrain;
	}
	
	private List<Vector2> createLeftCliffVertices() {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Vector2 pathBuffer = getPathBuffer(racerBounds.y);
		Vector2 startVertex = new Vector2(racerBounds.x - pathBuffer.x / 2, racerBounds.y);
		vertices.add(startVertex);
		float levelTop = racerBounds.y + level.getHeight();
		while (true) {
			Vector2 previousVertex = vertices.get(vertices.size() - 1);
			float edgeDeviationX = MathUtils.random(-EDGE_MAX_DEVIATION_X, EDGE_MAX_DEVIATION_X);
			float vertexX = previousVertex.x + edgeDeviationX;
			float vertexY = previousVertex.y + MathUtils.random(EDGE_MAX_INCREASE_Y);
			vertices.add(new Vector2(vertexX, vertexY));
			if (vertexY >= levelTop) {
				return vertices;
			}
		}
	}
	
	private float[] createRightCliffVertices(final List<Vector2> leftVertices) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Vector2 pathBuffer = getPathBuffer(racerBounds.y);
		Vector2 startVertex = new Vector2(racerBounds.x + pathBuffer.x / 2, racerBounds.y);
		vertices.add(startVertex);
		Vector2 topLeftVertex = leftVertices.get(leftVertices.size() - 1);
		while (true) {
			Vector2 previousVertex = vertices.get(vertices.size() - 1);
			float vertexY = previousVertex.y + MathUtils.random(EDGE_MAX_INCREASE_Y);
			if (vertexY >= topLeftVertex.y) {
				break;
			} else {
				float vertexX = getRightVertexX(vertexY, previousVertex, leftVertices);
				vertices.add(new Vector2(vertexX, vertexY));
			}
		}
		float[] verticesArray = VertexUtils.toVerticesArray(vertices);
		float maxX = VertexUtils.maxX(verticesArray);
		Vector2 topRightCornerPoint = new Vector2(maxX, racerBounds.y + level.getHeight());
		Vector2 bottomRightCornerPoint = new Vector2(maxX, racerBounds.y);
		return VertexUtils.addVertices(verticesArray, topRightCornerPoint, bottomRightCornerPoint);
	}

	private float getRightVertexX(final float rightVertexY, final Vector2 previousRightVertex, 
			final List<Vector2> leftVertices) {
		// TODO: Middle of line sometimes too small for the player to get through
		Vector2 pathBuffer = racerBounds.getSize(new Vector2()).add(getPathBuffer(rightVertexY));
		float rightVertexX = Float.MIN_VALUE;
		for (Vector2 leftVertex : leftVertices) {
			if (leftVertex.y > previousRightVertex.y && leftVertex.y < rightVertexY) {
				Vector2[] leftVertexBufferPoints = new Vector2[] { 
						leftVertex.cpy().add(pathBuffer.x, 0),
						leftVertex.cpy().add(-pathBuffer.x, 0),  
						leftVertex.cpy().add(0, pathBuffer.y), 
						leftVertex.cpy().add(0, -pathBuffer.y)};
				for (Vector2 leftVertexBufferPoint : leftVertexBufferPoints) {
					float canX = VectorUtils.getLineX(previousRightVertex, leftVertexBufferPoint, rightVertexY);
					rightVertexX = Math.max(canX, rightVertexX);
				}
			}
		}
		return rightVertexX;
	}
	
	private Vector2 getPathBuffer(final float vertexY) {
		float currentYToTopRatio = (vertexY - racerBounds.y) / level.getHeight();
		float pathBufferX = Interpolation.linear.apply(maxPathBuffer.x, minPathBuffer.x, currentYToTopRatio);
		float pathBufferY = Interpolation.linear.apply(maxPathBuffer.y, minPathBuffer.y, currentYToTopRatio);
		return new Vector2(pathBufferX, pathBufferY);
	}
	
}
