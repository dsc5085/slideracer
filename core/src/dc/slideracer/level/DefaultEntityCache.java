package dc.slideracer.level;

import dclib.epf.Entity;
import dclib.epf.EntityCache;

public final class DefaultEntityCache implements EntityCache {

	private final EntityFactory entityFactory;
	
	public DefaultEntityCache(final EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}
	
	@Override
	public final Entity create(final String entityType) {
		if (entityType == "explosion") {
			return entityFactory.createExplosion();
		} else {
			throw new IllegalArgumentException("Unknown entity type: " + entityType);
		}
	}

}
