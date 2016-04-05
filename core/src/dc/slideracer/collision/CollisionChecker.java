package dc.slideracer.collision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dc.slideracer.models.CollisionType;
import dc.slideracer.parts.CollisionPart;
import dclib.epf.Entity;

// TODO: COmbine this with collision resolver
public final class CollisionChecker {

	// TODO: create a multimap class?
	private final Map<CollisionType, Set<CollisionType>> colliderTypeToTargetTypeMap
		= new HashMap<CollisionType, Set<CollisionType>>();

	public final void link(final CollisionType colliderType, final CollisionType targetType) {
		if (!colliderTypeToTargetTypeMap.containsKey(colliderType)) {
			colliderTypeToTargetTypeMap.put(colliderType, new HashSet<CollisionType>());
		}
		colliderTypeToTargetTypeMap.get(colliderType).add(targetType);
	}
	
	public final boolean canCollide(final Entity collider, final Entity target) {
		if (collider.has(CollisionPart.class) && target.has(CollisionPart.class)) {
			CollisionType colliderType = collider.get(CollisionPart.class).getCollisionType();
			CollisionType targetType = target.get(CollisionPart.class).getCollisionType();
			Set<CollisionType> collisionTargetTypes = colliderTypeToTargetTypeMap.get(colliderType);
			return collisionTargetTypes != null && collisionTargetTypes.contains(targetType);
		}
		return false;
	}

}
