package dc.slideracer.parts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.badlogic.gdx.math.Polygon;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.collision.PolygonPartition;
import dclib.geometry.PolygonFactory;

@XmlRootElement
public final class CollisionPart {

	@XmlElement
	private CollisionType collisionType;
	// Collision only works with convex polygons, so its necessary to keep convex partitions of the main polygon
	private final List<PolygonPartition> polygonPartitions = new ArrayList<PolygonPartition>();
	
	public CollisionPart() {
	}
	
	public CollisionPart(final CollisionType collisionType, final float[] vertices) {
		this.collisionType = collisionType;
		List<float[]> partitionsVertices = PolygonFactory.triangulate(vertices);
		for (float[] partitionVertices : partitionsVertices) {
			polygonPartitions.add(new PolygonPartition(partitionVertices));
		}
	}
	
	public final CollisionType getCollisionType() {
		return collisionType;
	}
	
	public final List<Polygon> getPolygons() {
		List<Polygon> polygons = new ArrayList<Polygon>();
		for (PolygonPartition partition : polygonPartitions) {
			polygons.add(partition.getPolygon());
		}
		return polygons;
	}
	
	public final List<PolygonPartition> getPartitions() {
		return polygonPartitions;
	}
	
}
