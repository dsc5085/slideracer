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
import dclib.util.Maths;

public final class RacerInputSystem extends EntitySystem {
	
	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(RacerInputPart.class)) {
			float maxSpeed = entity.get(SpeedPart.class).getSpeed(); 
			if (Gdx.input.isKeyPressed(Keys.A)) {
				updateVelocity(delta, entity, -maxSpeed);
			} else if (Gdx.input.isKeyPressed(Keys.S)) {
				updateVelocity(delta, entity, maxSpeed);
			} else {
				updateVelocity(delta, entity, 0);
			}
		}
	}
	
	private void updateVelocity(final float delta, final Entity entity, final float finalVelocityX) {
		TranslatePart translatePart = entity.get(TranslatePart.class);
		float acceleration = entity.get(AccelerationPart.class).getAcceleration() * delta;
		Vector2 velocity = translatePart.getVelocity();
		if (Maths.distance(velocity.x, finalVelocityX) <= acceleration) {
			velocity.x = finalVelocityX;
		} else if (velocity.x > finalVelocityX) {
			velocity.x -= acceleration;
		} else {
			velocity.x += acceleration;
		}
		translatePart.setVelocity(velocity);
	}

}
