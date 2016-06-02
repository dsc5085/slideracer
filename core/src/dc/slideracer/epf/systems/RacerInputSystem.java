package dc.slideracer.epf.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.parts.AccelerationPart;
import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpeedPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TranslatePart;
import dclib.geometry.VectorUtils;

public final class RacerInputSystem extends EntitySystem {
	
	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(RacerInputPart.class)) {
			if (Gdx.input.isKeyPressed(Keys.A)) {
				updateVelocity(delta, entity, -1);
			} else if (Gdx.input.isKeyPressed(Keys.S)) {
				updateVelocity(delta, entity, 1);
			}
		}
	}
	
	private void updateVelocity(final float delta, final Entity entity, final float accelerationMultiplier) {
		TranslatePart translatePart = entity.get(TranslatePart.class);
		float maxSpeed = entity.get(SpeedPart.class).getSpeed();
		float acceleration = entity.get(AccelerationPart.class).getAcceleration();
		Vector2 velocity = translatePart.getVelocity();
		velocity.x += acceleration * accelerationMultiplier * delta;
		float newVelocityLength = Math.min(velocity.len(), maxSpeed);
		Vector2 newVelocity = VectorUtils.lengthened(velocity, newVelocityLength);
		translatePart.setVelocity(newVelocity);
	}

}
