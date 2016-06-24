package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.epf.parts.TransformPart;
import dclib.geometry.RectangleUtils;

public final class TerrainSection {

	private final Entity leftCliff;
	private final Entity rightCliff;
	private final List<Entity> obstacles;

	public TerrainSection(final Entity leftCliff, final Entity rightCliff, final List<Entity> obstacles) {
		this.leftCliff = leftCliff;
		this.rightCliff = rightCliff;
		this.obstacles = obstacles;
	}
	
	public final List<Entity> getAll() {
		List<Entity> terrainPieces = new ArrayList<Entity>(obstacles);
		terrainPieces.add(leftCliff);
		terrainPieces.add(rightCliff);
		return terrainPieces;
	}
	
	public final Vector2 getLeftCliffTopVertex() {
		return getCliffTopVertex(leftCliff);
	}
	
	public final Vector2 getRightCliffTopVertex() {
		return getCliffTopVertex(rightCliff);
	}
	
	public final float getTop() {
		Rectangle boundingBox = leftCliff.get(TransformPart.class).getBoundingBox();
		return RectangleUtils.top(boundingBox);
	}
	
	private Vector2 getCliffTopVertex(final Entity cliff) {
		float[] vertices = cliff.get(TransformPart.class).getTransformedVertices();
		int topPointIndex = vertices.length - 6;
		return new Vector2(vertices[topPointIndex], vertices[topPointIndex + 1]);
	}
	
}
