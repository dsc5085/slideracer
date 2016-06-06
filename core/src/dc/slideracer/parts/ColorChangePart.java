package dc.slideracer.parts;

import com.badlogic.gdx.graphics.Color;

import dclib.util.Timer;

public final class ColorChangePart {

	private Color startColor;
	private Color endColor;
	private Timer changeTimer;
	
	public ColorChangePart() {
	}
	
	public ColorChangePart(final float maxChangeTime, final Color startColor, final Color endColor) {
		// TODO: No need to keep startColor.  Just use the color from the sprite
		this.startColor = startColor;
		this.endColor = endColor;
		changeTimer = new Timer(maxChangeTime);
	}
	
	public final Color getStartColor() {
		return startColor;
	}
	
	public final Color getEndColor() {
		return endColor;
	}
	
	public final Timer getChangeTimer() {
		return changeTimer;
	}
	
}
