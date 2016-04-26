package dc.slideracer.level;

import com.badlogic.gdx.math.Rectangle;

public final class Level {

	// TODO: Should just be width.  Y is not an accounted for dimension now.
	private final Rectangle bounds;
	
	public Level(final Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public final Rectangle getBounds() {
		return new Rectangle(bounds);
	}
	
}
