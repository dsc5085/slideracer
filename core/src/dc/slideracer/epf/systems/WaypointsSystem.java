package dc.slideracer.epf.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import dc.slideracer.parts.SpeedPart;
import dc.slideracer.parts.WaypointsPart;
import dclib.epf.Entity;
import dclib.epf.EntitySystem;
import dclib.epf.parts.TransformPart;
import dclib.geometry.LinearUtils;
import dclib.geometry.VectorUtils;

public final class WaypointsSystem extends EntitySystem {

	@Override
	public final void updateEntity(final float delta, final Entity entity) {
		if (entity.hasActive(WaypointsPart.class)) {
			WaypointsPart waypointsPart = entity.get(WaypointsPart.class);
			float distanceToEnd = getPathDistance(entity) - waypointsPart.getEndBuffer();
			float maxDistanceCovered;
			if (distanceToEnd > 0) {
				maxDistanceCovered = LinearUtils.distance(entity.get(SpeedPart.class).getSpeed(), delta);
			} else {
				maxDistanceCovered = 0;
			}
			while (waypointsPart.hasWaypoints() && maxDistanceCovered > MathUtils.FLOAT_ROUNDING_ERROR) {
				maxDistanceCovered = moveToWaypoint(entity, maxDistanceCovered);
			}
		}
	}

	private float moveToWaypoint(final Entity entity, final float maxDistanceCovered) {
		WaypointsPart waypointsPart = entity.get(WaypointsPart.class);
		Vector2 currentWaypoint = waypointsPart.getCurrentWaypoint();
		TransformPart transformPart = entity.get(TransformPart.class);
		Vector2 oldGlobalCenter = transformPart.getCenter();
		Vector2 waypointOffset = VectorUtils.offset(oldGlobalCenter, currentWaypoint);
		Vector2 offset;
		if (maxDistanceCovered < waypointOffset.len()) {
			offset = VectorUtils.lengthened(waypointOffset, maxDistanceCovered);
		} else {
			// reached waypoint
			offset = waypointOffset;
			waypointsPart.removeCurrentWaypoint();
		}
		transformPart.translate(offset);
		return maxDistanceCovered - offset.len();
	}

	private final float getPathDistance(final Entity entity) {
		WaypointsPart waypointsPart = entity.get(WaypointsPart.class);
		TransformPart transformPart = entity.get(TransformPart.class);
		List<Vector2> waypoints = waypointsPart.getWaypoints();
		List<Vector2> path = new ArrayList<Vector2>();
		path.add(transformPart.getCenter());
		path.addAll(waypoints);
		float distance = 0;
		for (int i = 1; i < path.size(); i++) {
			distance += VectorUtils.offset(path.get(i - 1), path.get(i)).len();
		}
		return distance;
	}

}
