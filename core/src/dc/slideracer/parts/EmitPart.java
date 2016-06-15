package dc.slideracer.parts;

import com.badlogic.gdx.math.Vector2;

import dclib.util.Timer;

public final class EmitPart {

	private final String entityType;
	private final Vector2 localSpawnPosition;
	private final Timer emitTimer;
	
	public EmitPart(final String entityType, final Vector2 localSpawnPosition, final Timer emitTimer) {
		this.entityType = entityType;
		this.localSpawnPosition = localSpawnPosition;
		this.emitTimer = emitTimer;
	}
	
	public final String getEntityType() {
		return entityType;		
	}
	
	public final Vector2 getLocalSpawnPosition() {
		return localSpawnPosition.cpy();
	}
	
	public final boolean canEmit() {
		return emitTimer.isElapsed();
	}
	
	public final void reset() {
		emitTimer.reset();
	}
	
	public final void update(final float delta) {
		emitTimer.tick(delta);
	}
	
}
