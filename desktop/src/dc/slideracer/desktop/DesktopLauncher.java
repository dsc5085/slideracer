package dc.slideracer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import dc.slideracer.SlideRacerGame;

public class DesktopLauncher {
	
	public static void main (final String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new SlideRacerGame(), config);
	}
	
}
