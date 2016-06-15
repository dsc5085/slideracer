package dc.slideracer.parts;

import dclib.util.Timer;

public final class TimedDeathPart {

	private final Timer deathTimer;
	
	public TimedDeathPart(final float deathTime) {
		deathTimer = new Timer(deathTime);
	}
	
	public final boolean isDead() {
		return deathTimer.isElapsed();
	}

	public final void update(final float delta) {
		deathTimer.tick(delta);
	}
	
}
