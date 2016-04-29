package dc.slideracer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dc.slideracer.level.Level;
import dc.slideracer.screens.LevelScreen;
import dclib.graphics.TextureCache;
import dclib.system.ScreenManager;
import dclib.util.PathUtils;

public class SlideRacerGame extends ApplicationAdapter {
	
	private final ScreenManager screenManager = new ScreenManager();
	private TextureCache textureCache;
	private PolygonSpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	
	@Override
	public final void create () {
		textureCache = createTextureCache();
		spriteBatch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();
		Level level = new Level(80);
		screenManager.add(new LevelScreen(level, textureCache, spriteBatch, shapeRenderer));
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
	
}
