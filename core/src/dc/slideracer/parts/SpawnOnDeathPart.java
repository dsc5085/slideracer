package dc.slideracer.parts;

public final class SpawnOnDeathPart {

	private final String entityType;
	
	public SpawnOnDeathPart(final String entityType) {
		this.entityType = entityType;
	}
	
	public final String getEntityType() {
		return entityType;
	}
	
}
