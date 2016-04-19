package dc.slideracer.collision;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import dclib.geometry.RectangleUtils;

// TODO: Rename to Edge?
public enum Bound {
	
	LEFT, RIGHT, BOTTOM, TOP;
	
	public static final boolean isOutOfBounds(final Rectangle collisionBox, final Rectangle boundsBox, 
			final List<Bound> checkedBounds) {
		for (Bound bound : getViolatedBounds(collisionBox, boundsBox)) {
			if (checkedBounds.contains(bound)) {
				return true;
			}
		}
		return false;
	}
	
	public static final List<Bound> getViolatedBounds(final Rectangle collisionBox, final Rectangle boundsBox) {
		List<Bound> bounds = new ArrayList<Bound>();
		if (collisionBox.x < boundsBox.x) {
			bounds.add(Bound.LEFT);
		} else if (RectangleUtils.right(collisionBox) > RectangleUtils.right(boundsBox)) {
			bounds.add(Bound.RIGHT);
		}
		if (collisionBox.y < boundsBox.y) {
			bounds.add(Bound.BOTTOM);
		} else if (RectangleUtils.top(collisionBox) > RectangleUtils.top(boundsBox)) {
			bounds.add(Bound.TOP);
		}
		return bounds;
	}
	
}
