package dc.slideracer.parts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.models.CollisionType;
import dclib.geometry.BayazitDecomposer;
import dclib.geometry.PolygonFactory;
import dclib.geometry.PolygonUtils;
import dclib.geometry.VertexUtils;

@XmlRootElement
public final class CollisionPart {

	@XmlElement
	private CollisionType collisionType;
	private final List<Polygon> collisionPolygons = new ArrayList<Polygon>();
	
	public CollisionPart() {
	}
	
	public CollisionPart(final CollisionType collisionType, final float[] vertices) {
		this.collisionType = collisionType;
		List<Vector2> verticesList = VertexUtils.toVerticesList(vertices);
		List<List<Vector2>> partitions = BayazitDecomposer.convexPartition(verticesList);
		for (List<Vector2> partition : partitions) {
			float[] partitionArray = VertexUtils.toVerticesArray(partition);
			collisionPolygons.add(VertexUtils.toPolygon(partitionArray));
		}
	}
	
	public final CollisionType getCollisionType() {
		return collisionType;
	}
	
	public final List<Polygon> getCollisionPolygons(final Polygon polygon) {
		List<Polygon> transformedCollisionPolygons = new ArrayList<Polygon>();
		for (Polygon collisionPolygon : collisionPolygons) {
			Polygon transformedCollisionPolygon = PolygonFactory.copy(collisionPolygon);
			transformedCollisionPolygon.setScale(polygon.getScaleX(), polygon.getScaleY());
			transformedCollisionPolygon.setRotation(polygon.getRotation());
			Vector2 globalPosition = PolygonUtils.toGlobal(collisionPolygon.getX(), collisionPolygon.getY(), polygon);
			transformedCollisionPolygon.setPosition(globalPosition.x, globalPosition.y);
			transformedCollisionPolygons.add(transformedCollisionPolygon);
		}
		return transformedCollisionPolygons;
	}
	
}
