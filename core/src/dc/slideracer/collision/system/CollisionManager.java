package dc.slideracer.collision.system;

import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import dc.slideracer.parts.CollisionPart;
import dclib.epf.Entity;

public final class CollisionManager {
	
	private final CollisionResolver[] collisionResolvers;
	
	public CollisionManager(final CollisionResolver... collisionResolvers) {
		this.collisionResolvers = collisionResolvers;
	}

	public final void checkCollisions(final List<Entity> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity1 = entities.get(i);
			if (entity1.hasActive(CollisionPart.class)) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity entity2 = entities.get(j);
					if (entity2.hasActive(CollisionPart.class)) {
						if (collided(entity1, entity2)) {
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
	
	private boolean collided(final Entity e1, final Entity e2) {
		List<Polygon> collisionPolygons1 = e1.get(CollisionPart.class).getPolygons();
		List<Polygon> collisionPolygons2 = e2.get(CollisionPart.class).getPolygons();
		for (Polygon collisionPolygon1 : collisionPolygons1) {
			for (Polygon collisionPolygon2 : collisionPolygons2) {
				if (Intersector.overlapConvexPolygons(collisionPolygon1, collisionPolygon2)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
