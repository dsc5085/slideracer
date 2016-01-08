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
			Vector2 startCoords;
			// TODO: DOesn't take into account world zoom
			float touchDeltaX = Gdx.input.getDeltaX() / unitConverter.getPixelsPerUnit();
			WaypointsPart waypointsPart = entity.get(WaypointsPart.class);
			Vector2 entityCenter = entity.get(TransformPart.class).getCenter();
			if (waypointsPart.hasWaypoints() && isTouchDeltaAndWaypointDirectionSame(
					touchDeltaX, waypointsPart, entityCenter.x)) {
				startCoords = waypointsPart.getCurrentWaypoint();
			} else {
				startCoords = entityCenter;
			}
			waypointsPart.clearWaypoints();
			Vector2 newWaypoint = startCoords.add(touchDeltaX, 0);
			System.out.println(entityCenter);
			System.out.println(newWaypoint);
			waypointsPart.addWaypoint(newWaypoint);
		}
	}
	
	private boolean isTouchDeltaAndWaypointDirectionSame(final float touchDeltaX, final WaypointsPart waypointsPart, 
			final float entityCenterX) {
		float offsetToWaypoint = waypointsPart.getCurrentWaypoint().x - entityCenterX;
		System.out.println(Math.signum(touchDeltaX) == Math.signum(offsetToWaypoint));
		return Math.signum(touchDeltaX) == Math.signum(offsetToWaypoint);
	}

}
