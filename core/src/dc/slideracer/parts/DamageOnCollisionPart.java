package dc.slideracer.parts;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class DamageOnCollisionPart {

	@XmlElement
	private float damage;
	
	public DamageOnCollisionPart() {
	}
	
	public DamageOnCollisionPart(final float damage) {
		this.damage = damage;
	}
	
	public final float getDamage() {
		return damage;
	}

}
