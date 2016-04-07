package dc.slideracer.collision;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import dclib.geometry.VertexUtils;

public final class PolygonPartition {
	
	private final Polygon polygon;
	private final Vector2 localPosition;
	
	public PolygonPartition(final float[] vertices) {
		polygon = VertexUtils.toPolygon(vertices);
		localPosition = new Vector2(polygon.getX(), polygon.getY());
	}
	
	public final Polygon getPolygon() {
		return polygon;
	}
	
	public final Vector2 getLocalPosition() {
		return new Vector2(localPosition);
	}
	
}
