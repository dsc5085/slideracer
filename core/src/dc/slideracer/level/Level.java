package dc.slideracer.level;

import com.badlogic.gdx.math.Rectangle;

public final class Level {

	private final Rectangle bounds;
	
	public Level(final Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public final Rectangle getBounds() {
		return new Rectangle(bounds);
	}
	
}
