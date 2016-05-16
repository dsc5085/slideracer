package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.geometry.VertexUtils;
import dclib.util.FloatRange;

public class TerrainFactory {

	private static final FloatRange EDGE_ANGLE_RANGE = new FloatRange(60, 120);
	private static final float EDGE_ANGLE_MAX_DIFF = 10;
	private static final FloatRange EDGE_Y_OFFSET_RANGE = new FloatRange(2, 6);
	
	private final Level level;
	private final EntityFactory entityFactory;
	private final Rectangle racerBounds;
	private final FloatRange beginPathBufferRange;
	private final FloatRange endPathBufferRange;

	public TerrainFactory(final Level level, final EntityFactory entityFactory, final Rectangle racerBounds) {
		this.level = level;
		this.entityFactory = entityFactory;
		this.racerBounds = racerBounds;
		float racerWidth = racerBounds.width;
		float minBeginRacerWidthRatio = 10;
		float maxBeginRacerWidthRatio = 12;
		beginPathBufferRange = new FloatRange(racerWidth * minBeginRacerWidthRatio, 
				racerWidth * maxBeginRacerWidthRatio);
		float minEndRacerWidthRatio = 1;
		float maxEndRacerWidthRatio = 2;
		endPathBufferRange =  new FloatRange(racerWidth * minEndRacerWidthRatio, racerWidth * maxEndRacerWidthRatio);
	}
	
	public final List<Entity> create() {
		List<Vector2> leftCliffVertices = createLeftCliffVertices();
		float[] rightCliffVertices = createRightCliffVertices(leftCliffVertices);
		float[] leftCliffVerticesArray = VertexUtils.toVerticesArray(leftCliffVertices);
		float minX = VertexUtils.minX(leftCliffVerticesArray);
		Vector2 topLeftCornerVertex = new Vector2(minX, racerBounds.y + level.getHeight());
		Vector2 bottomLeftCornerVertex = new Vector2(minX, racerBounds.y);
		leftCliffVerticesArray = VertexUtils.addVertices(leftCliffVerticesArray, topLeftCornerVertex, 
				bottomLeftCornerVertex);
		List<Entity> terrain = new ArrayList<Entity>();
		terrain.add(entityFactory.createTerrain(leftCliffVerticesArray));
		terrain.add(entityFactory.createTerrain(rightCliffVertices));
		return terrain;
	}
	
	private List<Vector2> createLeftCliffVertices() {
		List<Vector2> vertices = new ArrayList<Vector2>();
		Vector2 startVertex = new Vector2(racerBounds.x - beginPathBufferRange.max() / 2, racerBounds.y);
		vertices.add(startVertex);
		float levelTop = racerBounds.y + level.getHeight();
		while (true) {
			Vector2 previousVertex = vertices.get(vertices.size() - 1);
			// TODO: make this +-
			float vertexOffsetY = EDGE_Y_OFFSET_RANGE.random();
			float vertexY = previousVertex.y + vertexOffsetY;
			float edgeAngle = EDGE_ANGLE_RANGE.random();
			float vertexX = previousVertex.x + vertexOffsetY / (float)Math.tan(Math.toRadians(edgeAngle));
			vertices.add(new Vector2(vertexX, vertexY));
			if (vertexY >= levelTop) {
				return vertices;
			}
		}
	}
	
	private float[] createRightCliffVertices(final List<Vector2> leftVertices) {
		List<Edge> rightEdges = new ArrayList<Edge>();
		List<Edge> leftEdges = createEdges(leftVertices);
		Vector2 startVertex = new Vector2(racerBounds.x + beginPathBufferRange.max() / 2, racerBounds.y);
		for (Edge leftEdge : leftEdges) {
			float rightEdgeOffsetY = MathUtils.random(EDGE_Y_OFFSET_RANGE.min()) * MathUtils.randomSign();
			float rightEdgeEndY = Math.max(leftEdge.getP2().y + rightEdgeOffsetY, startVertex.y);
			float rightEdgeAngle = getRightEdgeAngle(startVertex, leftEdge, rightEdgeEndY);
			float rightEdgeEndX = (rightEdgeEndY - startVertex.y) / (float)Math.tan(rightEdgeAngle);
			Vector2 endVertex = new Vector2(rightEdgeEndX, rightEdgeEndY);
			Edge rightEdge = new Edge(startVertex, endVertex);
			rightEdges.add(rightEdge);
			startVertex = rightEdge.getP2();
		}
		List<Vector2> vertices = getVertices(rightEdges);
		float[] verticesArray = VertexUtils.toVerticesArray(vertices);
		float maxX = VertexUtils.maxX(verticesArray);
		Vector2 topRightCornerPoint = new Vector2(maxX, racerBounds.y + level.getHeight());
		Vector2 bottomRightCornerPoint = new Vector2(maxX, racerBounds.y);
		return VertexUtils.addVertices(verticesArray, topRightCornerPoint, bottomRightCornerPoint);
	}

	private float getRightEdgeAngle(final Vector2 startVertex, final Edge leftEdge, final float rightEdgeEndY) {
		FloatRange pathBufferRange = getPathBufferRange(rightEdgeEndY);
		float leftEdgeAngle = leftEdge.getAngle();
		float minXForPathBuffer = leftEdge.getP2().x + racerBounds.width + pathBufferRange.min();
		float minAngleForPathBuffer = new Vector2(minXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float minRightEdgeAngle = Math.min(minAngleForPathBuffer, leftEdgeAngle - EDGE_ANGLE_MAX_DIFF);
		minRightEdgeAngle = MathUtils.clamp(minRightEdgeAngle, EDGE_ANGLE_RANGE.min(), EDGE_ANGLE_RANGE.max());
		float maxXForPathBuffer = leftEdge.getP2().x + racerBounds.width + pathBufferRange.max();
		float maxAngleForPathBuffer = new Vector2(maxXForPathBuffer, rightEdgeEndY).sub(startVertex).angle();
		float maxRightEdgeAngle = Math.max(maxAngleForPathBuffer, leftEdgeAngle + EDGE_ANGLE_MAX_DIFF);
		maxRightEdgeAngle = MathUtils.clamp(maxRightEdgeAngle, EDGE_ANGLE_RANGE.min(), EDGE_ANGLE_RANGE.max());
		float rightEdgeAngle = MathUtils.random(minRightEdgeAngle, maxRightEdgeAngle);
		return (float)Math.toRadians(rightEdgeAngle);
	}
	
	private List<Vector2> getVertices(final List<Edge> edges) {
		List<Vector2> vertices = new ArrayList<Vector2>();
		for (Edge edge : edges) {
			vertices.add(edge.getP1());
			vertices.add(edge.getP2());
		}
		return vertices;
	}

	private FloatRange getPathBufferRange(final float vertexY) {
		float progressRatio = (vertexY - racerBounds.y) / level.getHeight();
		float minPathBuffer = Interpolation.linear.apply(beginPathBufferRange.min(), endPathBufferRange.min(), 
				progressRatio);
		float maxPathBuffer = Interpolation.linear.apply(beginPathBufferRange.max(), endPathBufferRange.max(), 
				progressRatio);
		return new FloatRange(minPathBuffer, maxPathBuffer);
	}
	
	private List<Edge> createEdges(final List<Vector2> points) {
		List<Edge> edges= new ArrayList<Edge>();
		for (int i = 0; i < points.size() - 1; i++) {
			edges.add(new Edge(points.get(i), points.get(i + 1)));
		}
		return edges;
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
