package dc.slideracer.parts;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import dc.slideracer.models.CollisionType;

@XmlRootElement
public final class CollisionTypePart {

	@XmlElement
	private CollisionType collisionType;
	
	public CollisionTypePart() {
	}
	
	public CollisionTypePart(final CollisionType collisionType) {
		this.collisionType = collisionType;
	}
	
	public final CollisionType getCollisionType() {
		return collisionType;
	}
	
}
