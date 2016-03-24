package dc.slideracer.collision;

import dclib.epf.Entity;

public interface CollisionResolver {

	void resolve(final Entity e1, final Entity e2);
	
}
