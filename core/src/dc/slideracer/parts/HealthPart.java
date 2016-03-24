package dc.slideracer.parts;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import dclib.eventing.DefaultEvent;
import dclib.eventing.DefaultListener;
import dclib.eventing.EventDelegate;

@XmlRootElement
public final class HealthPart {

	private final EventDelegate<DefaultListener> noHealthDelegate = new EventDelegate<DefaultListener>();

	@XmlElement
	private float maxHealth;
	@XmlElement
	private float health;
	
	public HealthPart() {
	}
	
	public HealthPart(final float maxHealth) {
		health = maxHealth;
		this.maxHealth = maxHealth;
	}
	
	public final void addNoHealthListener(final DefaultListener listener) {
		noHealthDelegate.listen(listener);
	}
	
	public final void reset() {
		health = maxHealth;
	}
	
	public final void decrease(final float value) {
		health -= value;
		if (health <= 0) {
			noHealthDelegate.notify(new DefaultEvent());
		}
	}
	
}
