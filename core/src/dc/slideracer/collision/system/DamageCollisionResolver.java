package dc.slideracer.collision.system;

import dc.slideracer.parts.DamageOnCollisionPart;
import dc.slideracer.parts.HealthPart;
import dclib.epf.Entity;

public class DamageCollisionResolver implements CollisionResolver {

	private final CollisionChecker collisionChecker;
	
	public DamageCollisionResolver(final CollisionChecker collisionChecker) {
		this.collisionChecker = collisionChecker;
	}
	
	@Override
	public final void resolve(final Entity collider, final Entity target) {
		if (collider.has(DamageOnCollisionPart.class) && target.has(HealthPart.class)) {
			if (collisionChecker.canCollide(collider, target)) {
				float damage = collider.get(DamageOnCollisionPart.class).getDamage();
				target.get(HealthPart.class).decrease(damage);
			}
		}
	}

}
