package dc.slideracer.collision;

import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import dc.slideracer.parts.CollisionTypePart;
import dclib.epf.Entity;
import dclib.epf.parts.TransformPart;

public final class CollisionManager {
	
	private final CollisionResolver[] collisionResolvers;
	
	public CollisionManager(final CollisionResolver... collisionResolvers) {
		this.collisionResolvers = collisionResolvers;
	}

	public final void checkCollisions(final List<Entity> entities) {
		// TODO: Check for concave polygons and throw an exception if they are present
		for (int i = 0; i < entities.size(); i++) {
			Entity entity1 = entities.get(i);
			if (entity1.hasActive(CollisionTypePart.class)) {
				Polygon polygon1 = entity1.get(TransformPart.class).getPolygon();
				for (int j = i + 1; j < entities.size(); j++) {
					Entity entity2 = entities.get(j);
					if (entity2.hasActive(CollisionTypePart.class)) {
						Polygon polygon2 = entity2.get(TransformPart.class).getPolygon();
						if (Intersector.overlapConvexPolygons(polygon1, polygon2)) {
							for (CollisionResolver collisionResolver : collisionResolvers) {
								collisionResolver.resolve(entity1, entity2);
								collisionResolver.resolve(entity2, entity1);
							}
						}
					}
				}
			}
		}
	}
	
}
