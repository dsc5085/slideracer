package dc.slideracer.level;


public final class LevelUtils {

	private LevelUtils() {
	}
	
	public static final float getProgressRatio(final float currentY, final float startY) {
		final float maxDifficultyHeight = 500;
		return Math.min((currentY - startY) / maxDifficultyHeight, 1);
	}
	
}
