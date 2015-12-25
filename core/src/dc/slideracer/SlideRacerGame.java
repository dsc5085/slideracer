package dc.slideracer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import dc.slideracer.screens.LevelScreen;
import dclib.graphics.TextureCache;
import dclib.system.ScreenManager;

public class SlideRacerGame extends ApplicationAdapter {
	
	private final ScreenManager screenManager = new ScreenManager();
	private TextureCache textureCache;
	private PolygonSpriteBatch spriteBatch;
	
	@Override
	public final void create () {
		textureCache = createTextureCache();
		spriteBatch = new PolygonSpriteBatch();
		Rectangle worldViewport = new Rectangle(0, 0, 10, 10);
		screenManager.add(new LevelScreen(worldViewport, textureCache, spriteBatch));
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
	}

	private TextureCache createTextureCache() {
		TextureCache textureCache = new TextureCache();
		final String[] textureSubPaths = new String[] { "objects/" };
		textureCache.addTextures("textures/", textureSubPaths);
		return textureCache;
	}
	
}
