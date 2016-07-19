package dc.slideracer.epf.systems;

import com.badlogic.gdx.math.Vector2;

import dc.slideracer.parts.EmitPart;
import dclib.epf.Entity;
import dclib.epf.EntitySpawner;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.PolygonUtils;

public final class EmitSystem extends EntitySystem {

	private final EntitySpawner entitySpawner;
	
	public EmitSystem(final EntitySpawner entitySpawner) {
		this.entitySpawner = entitySpawner;
	}
	
	@Override
	public final void update(final float delta, final Entity entity) {
		if (entity.hasActive(EmitPart.class)) {
			EmitPart emitPart = entity.get(EmitPart.class);
			emitPart.update(delta);
			if (emitPart.canEmit()) {
				emit(entity);
			}
		}
	}
	
	private void emit(final Entity entity) {
		EmitPart emitPart = entity.get(EmitPart.class);
		emitPart.reset();
		Entity spawn = entitySpawner.spawn(emitPart.getEntityType());
		TransformPart transformPart = entity.get(TransformPart.class);
		TransformPart spawnTransform = spawn.get(TransformPart.class);
		spawnTransform.setRotation(transformPart.getRotation());
		Vector2 localSpawnPosition = emitPart.getLocalSpawnPosition();
		Vector2 spawnPosition = PolygonUtils.toGlobal(localSpawnPosition, transformPart.getPolygon());
		spawnTransform.setCenter(spawnPosition);
	}

}
