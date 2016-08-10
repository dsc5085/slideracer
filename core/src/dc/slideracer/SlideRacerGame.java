package dc.slideracer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dc.slideracer.level.LevelController;
import dc.slideracer.screens.HighScoresScreen;
import dc.slideracer.screens.LevelScreen;
import dc.slideracer.screens.TitleScreen;
import dc.slideracer.session.GameSession;
import dc.slideracer.ui.UiPack;
import dclib.eventing.DefaultListener;
import dclib.graphics.ScreenUtils;
import dclib.graphics.TextureCache;
import dclib.system.ScreenManager;
import dclib.util.PathUtils;
import dclib.util.Point;
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
		Point defaultScreenSize = new Point(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiPack = new UiPack("ui/test/uiskin.json", defaultScreenSize, "ui/ocr/ocr_32.fnt", "ui/ocr/ocr_24.fnt");
		gameSession = xmlContext.unmarshal(Gdx.files.local(GameSession.FILE_PATH));
		screenManager.add(createTitleScreen());
	}

	@Override
	public final void render () {
		ScreenUtils.clear();
		screenManager.render();
	}

	@Override
	public final void resize(final int width, final int height) {
		uiPack.scaleToScreenSize(width, height);
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
		textureCache.addTexturesAsAtlas("textures/objects/", "objects");
		String backgroundsPath = PathUtils.internalToAbsolutePath("textures/bgs");
		textureCache.addTextures(Gdx.files.absolute(backgroundsPath), "bgs");
		return textureCache;
	}

	private Screen createTitleScreen() {
		final TitleScreen titleScreen = new TitleScreen(uiPack, textureCache.getTextureRegion("bgs/slideracer_title"));
		titleScreen.addClosedListener(new DefaultListener() {
			@Override
			public void executed() {
				screenManager.swap(createHighScoresScreen());
			}
		});
		return titleScreen;
	}

	private Screen createLevelScreen() {
		final LevelController controller = new LevelController(textureCache, spriteBatch, shapeRenderer);
		final LevelScreen levelScreen = new LevelScreen(controller, uiPack);
		controller.addFinishedListener(new DefaultListener() {
			@Override
			public void executed() {
				int score = controller.getScore();
				screenManager.add(createHighScoresScreen(score));
			}
		});
		return levelScreen;
	}

	private Screen createHighScoresScreen() {
		return createHighScoresScreen(0);
	}

	private HighScoresScreen createHighScoresScreen(final int score) {
		final HighScoresScreen highScoresScreen = new HighScoresScreen(uiPack, gameSession, xmlContext, score);
		highScoresScreen.addClosedListener(new DefaultListener() {
			@Override
			public void executed() {
				screenManager.swap(createLevelScreen());
			}
		});
		return highScoresScreen;
	}

}
