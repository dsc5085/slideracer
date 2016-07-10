package dc.slideracer.level;


public final class LevelUtils {

	private LevelUtils() {
	}
	
	public static final float getProgressRatio(final float racerY, final float startY) {
		final float maxDifficultyHeight = 500;
		return (racerY - startY) / maxDifficultyHeight;
	}
	
}
