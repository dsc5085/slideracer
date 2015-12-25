package dc.slideracer.entitysystems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.WaypointsPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.UnitConverter;

public final class RacerInputSystem extends EntitySystem {

	private final UnitConverter unitConverter;
	
	public RacerInputSystem(final UnitConverter unitConverter) {
		this.unitConverter = unitConverter;
	}
	
	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(RacerInputPart.class)) {
			Vector2 waypoint = unitConverter.toWorldCoords(Gdx.input.getX(), 0);
			waypoint.y =  entity.get(TransformPart.class).getCenter().y;
			WaypointsPart waypointsPart = entity.get(WaypointsPart.class);
			waypointsPart.clearWaypoints();
			waypointsPart.addWaypoint(waypoint);
		}
	}

}
