package dc.slideracer.epf.systems;

import java.util.List;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.collision.PolygonPartition;
import dc.slideracer.parts.CollisionPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.PolygonUtils;

public final class CollisionSystem extends EntitySystem {

	@Override
	public final void update(final float delta, final Entity entity) {
		if (entity.has(CollisionPart.class)) {
			Polygon polygon = entity.get(TransformPart.class).getPolygon();
			List<PolygonPartition> partitions = entity.get(CollisionPart.class).getPartitions();
			for (PolygonPartition partition : partitions) {
				Polygon partitionPolygon = partition.getPolygon();
				partitionPolygon.setScale(polygon.getScaleX(), polygon.getScaleY());
				partitionPolygon.setRotation(polygon.getRotation());
				Vector2 globalPosition = PolygonUtils.toGlobal(partition.getLocalPosition(), polygon);
				partitionPolygon.setPosition(globalPosition.x, globalPosition.y);
			}
		}
	}
	
}
