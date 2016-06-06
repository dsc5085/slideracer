package dc.slideracer.epf.systems;

import dc.slideracer.parts.TimedDeathPart;
import dclib.epf.Entity;
import dclib.epf.EntityManager;
import dclib.epf.EntitySystem;

public final class TimedDeathSystem extends EntitySystem {

	private final EntityManager entityManager;
	
	public TimedDeathSystem(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(TimedDeathPart.class)) {
			TimedDeathPart timedDeathPart = entity.get(TimedDeathPart.class);
			timedDeathPart.update(delta);
			if (timedDeathPart.isDead()) {
				entityManager.remove(entity);
			}
		}
	}

}
