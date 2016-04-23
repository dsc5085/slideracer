package dc.slideracer.epf.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpeedPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TranslatePart;

public final class RacerInputSystem extends EntitySystem {
	
	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(RacerInputPart.class)) {
			TranslatePart translatePart = entity.get(TranslatePart.class);
			Vector2 velocity = translatePart.getVelocity();
			float speed = entity.get(SpeedPart.class).getSpeed();
			velocity.x = getVelocityXMultiplier() * speed;
			translatePart.setVelocity(velocity);
		}
	}
	
	private float getVelocityXMultiplier() {
		float moveVelocityX = 0;
		if (Gdx.input.isKeyPressed(Keys.A)) {
			moveVelocityX = -1;
		} else if (Gdx.input.isKeyPressed(Keys.S)) {
			moveVelocityX = 1;
		}
		return moveVelocityX;
	}

}
