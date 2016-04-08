package dc.slideracer.epf.systems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.parts.MoveWithCameraPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.UnitConverter;

public final class MoveWithCameraSystem extends EntitySystem {

	private final Camera camera;
	private final UnitConverter unitConverter;
	private Vector3 cameraLastPosition;
	
	public MoveWithCameraSystem(final Camera camera, final UnitConverter unitConverter) {
		this.camera = camera;
		this.unitConverter = unitConverter;
		cameraLastPosition = camera.position.cpy();
	}
	
	public final void setCameraLastPosition(final Vector3 cameraLastPosition) {
		this.cameraLastPosition = cameraLastPosition;
	}
	
	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.has(MoveWithCameraPart.class)) {
			Vector3 cameraOffset = camera.position.cpy().sub(cameraLastPosition);
			Vector2 cameraWorldOffset = unitConverter.toWorldUnits(cameraOffset.x, cameraOffset.y);
			entity.get(TransformPart.class).translate(cameraWorldOffset);
		}
	}
	
}
