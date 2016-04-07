package dc.slideracer.parts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.collision.PolygonPartition;
import dclib.geometry.BayazitDecomposer;
import dclib.geometry.VertexUtils;

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
		List<Vector2> verticesList = VertexUtils.toVerticesList(vertices);
		List<List<Vector2>> partitionsVertices = BayazitDecomposer.convexPartition(verticesList);
		for (List<Vector2> partitionVertices : partitionsVertices) {
			float[] partitionVerticesArray = VertexUtils.toVerticesArray(partitionVertices);
			polygonPartitions.add(new PolygonPartition(partitionVerticesArray));
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
