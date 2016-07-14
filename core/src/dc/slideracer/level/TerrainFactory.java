package dc.slideracer.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.geometry.RectangleUtils;
import dclib.geometry.VectorUtils;
import dclib.geometry.VertexUtils;
import dclib.util.FloatRange;
import dclib.util.Maths;

public class TerrainFactory {

	private static final FloatRange EDGE_ANGLE_RANGE = new FloatRange(75, 105);
	
	private final EntityFactory entityFactory;
	private final Rectangle racerBounds;
	private final float startY;
	private final FloatRange edgeYOffsetRange;
	private final FloatRange beginPathBufferRange;
	private final FloatRange endPathBufferRange;
	// This is so the obstacle looks conjoined with the cliff
	private final float obstacleBaseDepth;

	public TerrainFactory(final EntityFactory entityFactory, final Rectangle racerBounds) {
		edgeYOffsetRange = new FloatRange(2 * racerBounds.height, 4 * racerBounds.height);
		beginPathBufferRange = new FloatRange(5 * racerBounds.width, 6 * racerBounds.width);
		endPathBufferRange =  new FloatRange(2 * racerBounds.width, 3 * racerBounds.width);
		this.entityFactory = entityFactory;
		this.racerBounds = racerBounds;
		startY = racerBounds.y;
		obstacleBaseDepth = racerBounds.width;
	}
	
	public final TerrainSection create(final float height) {
		float pathBuffer = getPathBufferRange(startY).max() / 2;
		Vector2 leftCliffStartVertex = new Vector2(racerBounds.x - pathBuffer, racerBounds.y);
		Vector2 rightCliffStartVertex = new Vector2(RectangleUtils.right(racerBounds) + pathBuffer, racerBounds.y);
		return create(leftCliffStartVertex, rightCliffStartVertex, height);
	}
	
	public final TerrainSection create(final Vector2 leftCliffStartVertex, final Vector2 rightCliffStartVertex, 
			final float height) {
		final float outsideEdgeBuffer = racerBounds.width * 10;
		float bottom = leftCliffStartVertex.y;
		float top = bottom + height;
		List<Vector2> leftCliffEdgeVertices = createLeftCliffEdgeVertices(leftCliffStartVertex, top);
		float[] leftCliffEdgeVerticesArray = VertexUtils.toArray(leftCliffEdgeVertices);
		float leftOutsideEdgeX = VertexUtils.minX(leftCliffEdgeVerticesArray) - outsideEdgeBuffer;
		List<Vector2> leftCliffVertices = createCliffVertices(leftCliffEdgeVertices, leftOutsideEdgeX);
		Entity leftCliff = createTerrain(leftCliffVertices);
		List<Vector2> rightCliffEdgeVertices = createRightCliffEdgeVertices(rightCliffStartVertex, 
				leftCliffEdgeVertices);
		float[] rightCliffEdgeVerticesArray = VertexUtils.toArray(rightCliffEdgeVertices);
		float rightOutsideEdgeX = VertexUtils.maxX(rightCliffEdgeVerticesArray) + outsideEdgeBuffer;
		List<Vector2> rightCliffVertices = createCliffVertices(rightCliffEdgeVertices, rightOutsideEdgeX);
		Entity rightCliff = createTerrain(rightCliffVertices);
		List<Entity> obstacles = createObstacles(leftCliffVertices, rightCliffVertices, leftCliffStartVertex.y, top);
		float backgroundWidth = rightOutsideEdgeX - leftOutsideEdgeX;
		Rectangle backgroundBounds = new Rectangle(leftOutsideEdgeX, bottom, backgroundWidth, height);
		Entity background = entityFactory.createBackground(backgroundBounds);
		return new TerrainSection(leftCliff, rightCliff, background, obstacles);
	}
	
	private List<Vector2> createLeftCliffEdgeVertices(final Vector2 startVertex, final float terrainTop) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(startVertex);
		while (true) {
			Vector2 previousVertex = vertices.get(vertices.size() - 1);
			float vertexOffsetY = edgeYOffsetRange.random();
			float vertexY = Math.min(previousVertex.y + vertexOffsetY, terrainTop);
			float edgeAngle = EDGE_ANGLE_RANGE.random();
			float vertexX = previousVertex.x + vertexOffsetY / (float)Math.tan(Math.toRadians(edgeAngle));
			vertices.add(new Vector2(vertexX, vertexY));
			if (vertexY >= terrainTop) {
				return vertices;
			}
		}
	}
	
	private List<Vector2> createRightCliffEdgeVertices(final Vector2 startVertex, final List<Vector2> leftVertices) {
		List<Edge> rightEdges = new ArrayList<Edge>();
		List<Edge> leftEdges = createEdges(leftVertices);
		Vector2 edgeStartVertex = startVertex.cpy();
		for (Edge leftEdge : leftEdges) {
			float rightEdgeEndY;
			if (leftEdges.indexOf(leftEdge) == leftEdges.size() - 1) {
				rightEdgeEndY = leftEdge.getEnd().y;
			} else {
				float rightEdgeOffsetY = MathUtils.random(edgeYOffsetRange.min()) * MathUtils.randomSign();
				rightEdgeEndY = Math.max(leftEdge.getEnd().y + rightEdgeOffsetY, edgeStartVertex.y);
			}
			float rightEdgeAngle = getRightEdgeAngle(edgeStartVertex, leftEdge, rightEdgeEndY);
			float rightEdgeEndX = edgeStartVertex.x + (rightEdgeEndY - edgeStartVertex.y) / (float)Math.tan(rightEdgeAngle);
			Vector2 endVertex = new Vector2(rightEdgeEndX, rightEdgeEndY);
			Edge rightEdge = new Edge(edgeStartVertex, endVertex);
			rightEdges.add(rightEdge);
			edgeStartVertex = rightEdge.getEnd();
		}
		return getVertices(rightEdges);
	}

	private float getRightEdgeAngle(final Vector2 startVertex, final Edge leftEdge, final float rightEdgeEndY) {
		final float angleMaxDiff = 10;
		FloatRange pathBufferRange = getPathBufferRange(rightEdgeEndY);
		float leftEdgeAngle = leftEdge.getAngle();
		float minXForPathBuffer = leftEdge.getEnd().x + racerBounds.width + pathBufferRange.min();
		float minAngleForPathBuffer = new Vector2(minXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float minRightEdgeAngle = Math.min(minAngleForPathBuffer, leftEdgeAngle - angleMaxDiff);
		float maxXForPathBuffer = leftEdge.getEnd().x + racerBounds.width + pathBufferRange.max();
		float maxAngleForPathBuffer = new Vector2(maxXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float maxRightEdgeAngle = Math.max(maxAngleForPathBuffer, leftEdgeAngle + angleMaxDiff);
		float rightEdgeAngle = MathUtils.random(minRightEdgeAngle, maxRightEdgeAngle);
		float clampedRightEdgeAngle = MathUtils.clamp(rightEdgeAngle, EDGE_ANGLE_RANGE.min(), EDGE_ANGLE_RANGE.max());
		return (float)Math.toRadians(clampedRightEdgeAngle);
	}

	private FloatRange getPathBufferRange(final float vertexY) {
		float progressRatio = LevelUtils.getProgressRatio(vertexY, startY);
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
			final List<Vector2> rightCliffVertices, final float terrainBottom, final float terrainTop) {
		final float obstacleHeight = racerBounds.height * 1.5f;
		final FloatRange yOffsetRange = new FloatRange(5 * racerBounds.height, 10 * racerBounds.height);
		List<Entity> obstacles = new ArrayList<Entity>();
		float obstacleY = terrainBottom + racerBounds.height;
		while (true) {
			obstacleY += yOffsetRange.random();
			FloatRange obstacleYRange = new FloatRange(obstacleY, obstacleY + obstacleHeight);
			if (obstacleYRange.max() >= terrainTop) {
				return obstacles;
			}
			List<Entity> newObstacles = createObstaclePair(leftCliffVertices, rightCliffVertices, obstacleYRange);
			obstacles.addAll(newObstacles);
		}
	}

	private List<Entity> createObstaclePair(final List<Vector2> leftCliffVertices, 
			final List<Vector2> rightCliffVertices, final FloatRange obstacleYRange) {
		final float obstaclePathBuffer = racerBounds.width;
		List<Entity> obstacles = new ArrayList<Entity>();
		FloatRange pathRangeBottom = getPathRange(obstacleYRange.min(), leftCliffVertices, rightCliffVertices);
		float gapWidth = racerBounds.width + obstaclePathBuffer;
		float maxGapX = pathRangeBottom.max() - gapWidth;
		float gapX = MathUtils.random(pathRangeBottom.min(), maxGapX);
		FloatRange pathRange = getPathRange(obstacleYRange.min(), leftCliffVertices, rightCliffVertices); 
		Entity leftObstacle = createObstacle(obstacleYRange, gapX, pathRange.min() - obstacleBaseDepth);
		obstacles.add(leftObstacle);
		Entity rightObstacle = createObstacle(obstacleYRange, gapX + gapWidth, pathRange.max() + obstacleBaseDepth);
		obstacles.add(rightObstacle);
		return obstacles;
	}
	
	private Entity createObstacle(final FloatRange obstacleYRange, final float obstacleInnerX, 
			final float obstacleOuterX) {
		List<Vector2> intermediateVertices = createIntermediateVerticesWithSetY(obstacleYRange);
		setObstacleVerticesX(obstacleInnerX, obstacleOuterX, intermediateVertices);
		List<Vector2> vertices = new ArrayList<Vector2>();
		vertices.add(new Vector2(obstacleOuterX, obstacleYRange.min()));
		vertices.addAll(intermediateVertices);
		vertices.add(new Vector2(obstacleOuterX, obstacleYRange.max()));
		return entityFactory.createTerrain(VertexUtils.toArray(vertices));
	}

	private List<Vector2> createIntermediateVerticesWithSetY(
			final FloatRange obstacleYRange) {
		final float numIntermediateVertices = MathUtils.random(1, 5);
		List<Vector2> intermediateVertices = new ArrayList<Vector2>();
		for (int i = 0; i < numIntermediateVertices; i++) {
			intermediateVertices.add(new Vector2(0, obstacleYRange.random()));
		}
		Collections.sort(intermediateVertices, new Comparator<Vector2>() {
			@Override
			public final int compare(final Vector2 v1, final Vector2 v2) {
				return Float.compare(v1.y, v2.y);
			}
		});
		return intermediateVertices;
	}

	private void setObstacleVerticesX(final float obstacleInnerX,
			final float obstacleOuterX, final List<Vector2> intermediateVertices) {
		final float sculptOuterXRatio = 0.5f; 
		int middleVertexIndex = MathUtils.floor(intermediateVertices.size() / 2f);
		float sculptOuterX = Interpolation.linear.apply(obstacleInnerX, obstacleOuterX, sculptOuterXRatio);
		intermediateVertices.get(middleVertexIndex).x = obstacleInnerX;
		for (int i = 1; i <= middleVertexIndex; i++) {
			int upperVertexIndex = middleVertexIndex + i;
			if (upperVertexIndex < intermediateVertices.size()) {
				float lastVertexX = intermediateVertices.get(upperVertexIndex - 1).x;
				intermediateVertices.get(upperVertexIndex).x = MathUtils.random(lastVertexX, sculptOuterX);
			}
			int lowerVertexIndex = middleVertexIndex - i;
			if (lowerVertexIndex >= 0) {
				float lastVertexX = intermediateVertices.get(lowerVertexIndex + 1).x;
				intermediateVertices.get(lowerVertexIndex).x = MathUtils.random(lastVertexX, sculptOuterX);
			}
		}
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
		float[] verticesArray = VertexUtils.toArray(vertices);
		return entityFactory.createTerrain(verticesArray);
	}
	
	private List<Vector2> getVertices(final List<Edge> edges) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		for (Edge edge : edges) {
			vertices.add(edge.getStart());
			vertices.add(edge.getEnd());
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
	
	private class Edge {
		
		private final Vector2 start;
		private final Vector2 end;
		
		public Edge(final Vector2 start, final Vector2 end) {
			this.start = start;
			this.end = end;
		}
		
		public final Vector2 getStart() {
			return start.cpy();
		}
		
		public final Vector2 getEnd() {
			return end.cpy();
		}
		
		public final float getAngle() {
			return getEnd().sub(start).angle();
		}
		
	}
	
}
