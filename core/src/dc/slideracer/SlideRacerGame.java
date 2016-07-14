package dc.slideracer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dc.slideracer.level.LevelController;
import dc.slideracer.screens.HighScoresScreen;
import dc.slideracer.screens.LevelScreen;
import dc.slideracer.session.GameSession;
import dc.slideracer.ui.UiPack;
import dclib.eventing.DefaultListener;
import dclib.graphics.TextureCache;
import dclib.system.ScreenManager;
import dclib.util.PathUtils;
import dclib.util.XmlContext;

public class SlideRacerGame extends ApplicationAdapter {
	
	private final ScreenManager screenManager = new ScreenManager();
	private final XmlContext xmlContext = new XmlContext(XmlBindings.BOUND_CLASSES);
	private TextureCache textureCache;
	private PolygonSpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	private UiPack uiPack;
	private GameSession gameSession;
	
	@Override
	public final void create () {
		textureCache = createTextureCache();
		spriteBatch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();
		uiPack = new UiPack("ui/test/uiskin.json", "ui/ocr/ocr_32.fnt", "ui/ocr/ocr_24.fnt");
		gameSession = xmlContext.unmarshal(Gdx.files.local(GameSession.FILE_PATH));
		LevelScreen levelScreen = createLevelScreen();
		screenManager.add(levelScreen);
	}

	@Override
	public final void render () {
		screenManager.render();
	}
	
	@Override
	public final void resize(final int width, final int height) {
		screenManager.resize(width, height);
	}

	@Override
	public final void dispose() {
		uiPack.dispose();
		textureCache.dispose();
		screenManager.dispose();
		spriteBatch.dispose();
		shapeRenderer.dispose();
	}

	private TextureCache createTextureCache() {
		TextureCache textureCache = new TextureCache();
		textureCache.addTexturesAsAtlus("textures/objects/", "objects");
		String backgroundsPath = PathUtils.internalToAbsolutePath("textures/bgs");
		textureCache.addTextures(Gdx.files.absolute(backgroundsPath), "bgs");
		return textureCache;
	}
	
	private LevelScreen createLevelScreen() {
		final LevelController controller = new LevelController(textureCache, spriteBatch, shapeRenderer);
		final LevelScreen levelScreen = new LevelScreen(controller, uiPack);
		controller.addFinishedListener(new DefaultListener() {
			@Override
			public void executed() {
				int score = controller.getScore();
				HighScoresScreen highScoresScreen = createHighScoresScreen(score);
				screenManager.swap(levelScreen, highScoresScreen);
			}
		});
		return levelScreen;
	}
	
	private HighScoresScreen createHighScoresScreen(final int score) {
		HighScoresScreen highScoresScreen = new HighScoresScreen(uiPack, gameSession, xmlContext, score);
		setupHighScoresScreen(highScoresScreen);
		return highScoresScreen;
	}
	
	private void setupHighScoresScreen(final HighScoresScreen highScoresScreen) {
		highScoresScreen.addClosedListener(new DefaultListener() {
			@Override
			public void executed() {
				LevelScreen levelScreen = createLevelScreen();
				screenManager.swap(highScoresScreen, levelScreen);
			}
		});
	}
	
}
