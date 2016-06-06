package dc.slideracer.epf.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;

import dc.slideracer.parts.ColorChangePart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.DrawablePart;
import dclib.util.Timer;

public final class ColorChangeSystem extends EntitySystem {

	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(ColorChangePart.class)) {
			ColorChangePart colorChangePart = entity.get(ColorChangePart.class);
			Timer changeTimer = colorChangePart.getChangeTimer();
			Color startColor = colorChangePart.getStartColor();
			Color endColor = colorChangePart.getEndColor();
			changeTimer.tick(delta);
			Color color = startColor.cpy().lerp(endColor.r, endColor.g, endColor.b, endColor.a, 
					changeTimer.getElapsedPercent());
			PolygonSprite sprite = entity.get(DrawablePart.class).getSprite();
			sprite.setColor(color);
		}
	}

}
