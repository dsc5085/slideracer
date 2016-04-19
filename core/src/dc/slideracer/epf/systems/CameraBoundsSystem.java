package dc.slideracer.epf.systems;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.collision.Bound;
import dc.slideracer.parts.CameraBoundsPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.RectangleUtils;
import dclib.graphics.CameraUtils;

public class CameraBoundsSystem extends EntitySystem {
	
	private final Camera camera;
	private final float pixelsPerUnit;
	
	public CameraBoundsSystem(final Camera camera, final float pixelsPerUnit) {
		this.camera = camera;
		this.pixelsPerUnit = pixelsPerUnit;
	}

	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.has(CameraBoundsPart.class)) {
			Rectangle viewport = CameraUtils.getViewport(camera, pixelsPerUnit);
			TransformPart transformPart = entity.get(TransformPart.class);
			Rectangle entityBoundingBox = transformPart.getBoundingBox();
			List<Bound> bounds = Bound.getViolatedBounds(entityBoundingBox, viewport);
			Vector2 newPosition = transformPart.getPosition();
			
			for (Bound checkedBound : Bound.values()) {
				if (bounds.contains(checkedBound)) {
					switch (checkedBound) {
					case LEFT:
						newPosition.x = viewport.x + MathUtils.FLOAT_ROUNDING_ERROR;
						break;
					case RIGHT:
						newPosition.x = RectangleUtils.right(viewport) - entityBoundingBox.width;
						break;
					case TOP:
						newPosition.y = RectangleUtils.top(viewport) - entityBoundingBox.height;
						break;
					case BOTTOM:
						newPosition.y = viewport.y + MathUtils.FLOAT_ROUNDING_ERROR;
						break;
					}
				}
			}
			
			entity.get(TransformPart.class).setPosition(newPosition);
		}
	}

}
