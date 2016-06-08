package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.geometry.PolygonFactory;
import dclib.geometry.RectangleUtils;
import dclib.geometry.VectorUtils;
import dclib.geometry.VertexUtils;
import dclib.util.FloatRange;
import dclib.util.Maths;

public class TerrainFactory {

	private static final FloatRange EDGE_ANGLE_RANGE = new FloatRange(60, 120);
	
	private final Level level;
	private final EntityFactory entityFactory;
	private final Rectangle racerBounds;
	private final FloatRange edgeYOffsetRange;
	private final FloatRange beginPathBufferRange;

	public TerrainFactory(final Level level, final EntityFactory entityFactory, final Rectangle racerBounds) {
		this.level = level;
		this.entityFactory = entityFactory;
		this.racerBounds = racerBounds;
		edgeYOffsetRange = new FloatRange(2 * racerBounds.height, 6 * racerBounds.height);
		beginPathBufferRange = new FloatRange(10 * racerBounds.width, 12 * racerBounds.width);
	}
	
	public final List<Entity> create() {
		List<Entity> terrain = new ArrayList<Entity>();
		float outsideEdgeBuffer = racerBounds.width * 10;
		List<Vector2> leftCliffEdgeVertices = createLeftCliffEdgeVertices();
		float[] leftCliffEdgeVerticesArray = VertexUtils.toVerticesArray(leftCliffEdgeVertices);
		float leftOutsideEdgeX = VertexUtils.minX(leftCliffEdgeVerticesArray) - outsideEdgeBuffer;
		List<Vector2> leftCliffVertices = createCliffVertices(leftCliffEdgeVertices, leftOutsideEdgeX);
		terrain.add(createTerrain(leftCliffVertices));
		List<Vector2> rightCliffEdgeVertices = createRightCliffEdgeVertices(leftCliffEdgeVertices);
		float[] rightCliffEdgeVerticesArray = VertexUtils.toVerticesArray(rightCliffEdgeVertices);
		float rightOutsideEdgeX = VertexUtils.maxX(rightCliffEdgeVerticesArray) + outsideEdgeBuffer;
		List<Vector2> rightCliffVertices = createCliffVertices(rightCliffEdgeVertices, rightOutsideEdgeX);
		terrain.add(createTerrain(rightCliffVertices));
		terrain.addAll(createObstacles(leftCliffVertices, rightCliffVertices));
		return terrain;
	}
	
	private List<Vector2> createLeftCliffEdgeVertices() {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Vector2 startVertex = new Vector2(racerBounds.x - beginPathBufferRange.max() / 2, racerBounds.y);
		vertices.add(startVertex);
		float levelTop = racerBounds.y + level.getHeight();
		while (true) {
			Vector2 previousVertex = vertices.get(vertices.size() - 1);
			float vertexOffsetY = edgeYOffsetRange.random();
			float vertexY = previousVertex.y + vertexOffsetY;
			float edgeAngle = EDGE_ANGLE_RANGE.random();
			float vertexX = previousVertex.x + vertexOffsetY / (float)Math.tan(Math.toRadians(edgeAngle));
			vertices.add(new Vector2(vertexX, vertexY));
			if (vertexY >= levelTop) {
				return vertices;
			}
		}
	}
	
	private List<Vector2> createRightCliffEdgeVertices(final List<Vector2> leftVertices) {
		List<Edge> rightEdges = new ArrayList<Edge>();
		List<Edge> leftEdges = createEdges(leftVertices);
		Vector2 startVertex = new Vector2(racerBounds.x + beginPathBufferRange.max() / 2, racerBounds.y);
		for (Edge leftEdge : leftEdges) {
			float rightEdgeOffsetY = MathUtils.random(edgeYOffsetRange.min()) * MathUtils.randomSign();
			float rightEdgeEndY = Math.max(leftEdge.getP2().y + rightEdgeOffsetY, startVertex.y);
			float rightEdgeAngle = getRightEdgeAngle(startVertex, leftEdge, rightEdgeEndY);
			float rightEdgeEndX = startVertex.x + (rightEdgeEndY - startVertex.y) / (float)Math.tan(rightEdgeAngle);
			Vector2 endVertex = new Vector2(rightEdgeEndX, rightEdgeEndY);
			Edge rightEdge = new Edge(startVertex, endVertex);
			rightEdges.add(rightEdge);
			startVertex = rightEdge.getP2();
		}
		return getVertices(rightEdges);
	}

	private float getRightEdgeAngle(final Vector2 startVertex, final Edge leftEdge, final float rightEdgeEndY) {
		final float angleMaxDiff = 10;
		FloatRange pathBufferRange = getPathBufferRange(rightEdgeEndY);
		float leftEdgeAngle = leftEdge.getAngle();
		float minXForPathBuffer = leftEdge.getP2().x + racerBounds.width + pathBufferRange.min();
		float minAngleForPathBuffer = new Vector2(minXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float minRightEdgeAngle = Math.min(minAngleForPathBuffer, leftEdgeAngle - angleMaxDiff);
		float maxXForPathBuffer = leftEdge.getP2().x + racerBounds.width + pathBufferRange.max();
		float maxAngleForPathBuffer = new Vector2(maxXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float maxRightEdgeAngle = Math.max(maxAngleForPathBuffer, leftEdgeAngle + angleMaxDiff);
		float rightEdgeAngle = MathUtils.random(minRightEdgeAngle, maxRightEdgeAngle);
		float clampedRightEdgeAngle = MathUtils.clamp(rightEdgeAngle, EDGE_ANGLE_RANGE.min(), EDGE_ANGLE_RANGE.max());
		return (float)Math.toRadians(clampedRightEdgeAngle);
	}

	private FloatRange getPathBufferRange(final float vertexY) {
		final FloatRange endPathBufferRange =  new FloatRange(10 * racerBounds.width, 12 * racerBounds.width);
		float progressRatio = (vertexY - racerBounds.y) / level.getHeight();
		// TODO: Buffer not getting respected, not fitting
		float minPathBuffer = Interpolation.linear.apply(beginPathBufferRange.min(), endPathBufferRange.min(), 
				progressRatio);
		float maxPathBuffer = Interpolation.linear.apply(beginPathBufferRange.max(), endPathBufferRange.max(), 
				progressRatio);
		return new FloatRange(minPathBuffer, maxPathBuffer);
	}
	
	private List<Vector2> createCliffVertices(final List<Vector2> cliffEdgeVertices, final float outsideEdgeX) {
		List<Vector2> cliffVertices = new ArrayList<Vector2>(cliffEdgeVertices);
		float topY = cliffVertices.get(cliffVertices.size() - 1).y;
		Vector2 topOutsideVertex = new Vector2(outsideEdgeX, topY);
		cliffVertices.add(topOutsideVertex);
		Vector2 bottomOutsideVertex = new Vector2(outsideEdgeX, cliffVertices.get(0).y);
		cliffVertices.add(bottomOutsideVertex);
		return cliffVertices;
	}
	
	private List<Entity> createObstacles(final List<Vector2> leftCliffVertices, 
			final List<Vector2> rightCliffVertices) {
		final FloatRange yOffsetRange = new FloatRange(5 * racerBounds.height, 10 * racerBounds.height);
		List<Entity> obstacles = new ArrayList<Entity>();
		float levelTop = racerBounds.y + level.getHeight();
		float obstacleY = RectangleUtils.top(racerBounds);
		while (true) {
			obstacleY += yOffsetRange.random();
			float obstacleTop = obstacleY + racerBounds.height;
			if (obstacleTop >= levelTop) {
				return obstacles;
			}
			List<Entity> newObstacles = createObstacle(leftCliffVertices, rightCliffVertices, obstacleY);
			obstacles.addAll(newObstacles);
		}
	}

	private List<Entity> createObstacle(final List<Vector2> leftCliffVertices, final List<Vector2> rightCliffVertices, 
			final float obstacleY) {
		// TODO: Make this better
		final float obstaclePathBuffer = 1;
		List<Entity> obstacles = new ArrayList<Entity>();
		FloatRange pathRangeBottom = getPathRange(obstacleY, leftCliffVertices, rightCliffVertices);
		float gapWidth = (obstaclePathBuffer + 1) * racerBounds.width;
		float maxGapX = pathRangeBottom.max() - gapWidth;
		float gapX = MathUtils.random(pathRangeBottom.min(), maxGapX);
		float leftObstacleX = getMinX(leftCliffVertices);
		float[] leftObstacleVertices = PolygonFactory.createRectangleVertices(leftObstacleX, obstacleY, 
				gapX - leftObstacleX, racerBounds.height);
		obstacles.add(entityFactory.createTerrain(leftObstacleVertices));
		float rightObstacleX = gapX + gapWidth;
		float rightObstacleEndX = getMaxX(rightCliffVertices);
		float[] rightObstacleVertices = PolygonFactory.createRectangleVertices(rightObstacleX, obstacleY, 
				rightObstacleEndX - rightObstacleX, racerBounds.height);
		obstacles.add(entityFactory.createTerrain(rightObstacleVertices));
		return obstacles;
	}
	
	private FloatRange getPathRange(final float y, final List<Vector2> leftCliffVertices, 
			final List<Vector2> rightCliffVertices) {
		float minX = getCliffX(y, leftCliffVertices);
		float maxX = getCliffX(y, rightCliffVertices);
		return new FloatRange(minX, maxX);
	}
	
	private float getCliffX(final float y, final List<Vector2> cliffVertices) {
		for (int i = 0; i < cliffVertices.size() - 1; i++) {
			Vector2 currentVertex = cliffVertices.get(i);
			Vector2 nextVertex = cliffVertices.get(i + 1);
			if (Maths.isBetween(y, currentVertex.y, nextVertex.y)) {
				return VectorUtils.getLineX(currentVertex, nextVertex, y);
			}
		}
		throw new IllegalArgumentException("Could not calculate cliff x");
	}
	
	private Entity createTerrain(final List<Vector2> vertices) {
		float[] verticesArray = VertexUtils.toVerticesArray(vertices);
		return entityFactory.createTerrain(verticesArray);
	}
	
	private List<Vector2> getVertices(final List<Edge> edges) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		for (Edge edge : edges) {
			vertices.add(edge.getP1());
			vertices.add(edge.getP2());
		}
		return vertices;
	}
	
	private List<Edge> createEdges(final List<Vector2> points) {
		List<Edge> edges= new ArrayList<Edge>();
		for (int i = 0; i < points.size() - 1; i++) {
			edges.add(new Edge(points.get(i), points.get(i + 1)));
		}
		return edges;
	}
	
	private final float getMinX(final List<Vector2> vertices) {
		float minX = vertices.get(0).x;
		for (Vector2 vertex : vertices) {
			minX = Math.min(vertex.x, minX);
		}
		return minX;
	}
	
	private final float getMaxX(final List<Vector2> vertices) {
		float maxX = vertices.get(0).x;
		for (Vector2 vertex : vertices) {
			maxX = Math.max(vertex.x, maxX);
		}
		return maxX;
	}
	
	private class Edge {
		
		private final Vector2 p1;
		private final Vector2 p2;
		
		public Edge(final Vector2 p1, final Vector2 p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		public final Vector2 getP1() {
			return p1.cpy();
		}
		
		public final Vector2 getP2() {
			return p2.cpy();
		}
		
		public final float getAngle() {
			return getP2().sub(p1).angle();
		}
		
	}
	
}
