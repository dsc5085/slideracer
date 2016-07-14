package dc.slideracer.session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class ScoreEntry implements Comparable<ScoreEntry> {

	@XmlElement
	public String name;
	@XmlElement
	public int score;
	
	public ScoreEntry() {
		// for serialization
	}
	
	public ScoreEntry(final String name, final int score) {
		this.name = name;
		this.score = score;
	}

	@Override
	public final int compareTo(final ScoreEntry other) {
    	int compareValue = Integer.compare(score, other.score);
    	if (compareValue == 0) {
    		compareValue = name.compareTo(other.name);
    	}
    	return compareValue;
	}
	
}
