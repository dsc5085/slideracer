package dc.slideracer.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class GameSession {

	public static final String FILE_PATH = "gamesession";
	private static final int MAX_HIGH_SCORES = 10;
	
	@XmlElementWrapper
	private List<ScoreEntry> highScores;
	
	public GameSession() {
		// For serialization
	}
	
	public final List<ScoreEntry> getSortedHighScores() {
		List<ScoreEntry> sortedHighScores = new ArrayList<ScoreEntry>(highScores);
		Collections.sort(sortedHighScores);
		return sortedHighScores;
	}
	
	public final boolean canAddHighScore(final int score) {
		return score > 0 && (highScores.size() < MAX_HIGH_SCORES || score > getLowestHighScore().score);
	}
	
	public final void addHighScore(final ScoreEntry scoreEntry) {
		if (!canAddHighScore(scoreEntry.score)) {
			throw new IllegalArgumentException("Could not add score of " + scoreEntry.score
					+ " because it does not qualify as a high score");
		}
		if (highScores.size() > MAX_HIGH_SCORES) {
			highScores.remove(getLowestHighScore());
		}
		highScores.add(scoreEntry);
	}
	
	private ScoreEntry getLowestHighScore() {
		return getSortedHighScores().get(0);
	}
	
}