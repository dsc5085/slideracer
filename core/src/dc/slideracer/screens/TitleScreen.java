package dc.slideracer.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import dc.slideracer.ui.UiConstants;
import dc.slideracer.ui.UiPack;
import dclib.eventing.DefaultEvent;
import dclib.eventing.DefaultListener;
import dclib.eventing.EventDelegate;
import dclib.system.Input;
import dclib.ui.StageUtils;
import dclib.util.InputUtils;
import dclib.util.Timer;

public final class TitleScreen implements Screen {

	private final EventDelegate<DefaultListener> closedDelegate = new EventDelegate<DefaultListener>();

	private final UiPack uiPack;
	private final TextureRegion logoRegion;
	private final Stage stage;
	private final Timer transitionTimer = new Timer(UiConstants.SCREEN_TRANSITION_TIME);

	public TitleScreen(final UiPack uiPack, final TextureRegion logoRegion) {
		this.uiPack = uiPack;
		this.logoRegion = logoRegion;
		stage = createStage();
		Input.addProcessor(stage);
	}

	public final void addClosedListener(final DefaultListener listener) {
		closedDelegate.listen(listener);
	}

	@Override
	public void render(final float delta) {
		stage.act(delta);
		transitionTimer.tick(delta);
		stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		StageUtils.resize(stage, width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		Input.removeProcessor(stage);
		stage.dispose();
	}

	@Override
	public void show() {
		InputUtils.setCursorVisible(true);
	}

	private Stage createStage() {
		Stage stage = new Stage(new ScreenViewport());
		Table mainTable = createMainTable();
		stage.addActor(mainTable);
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				if (transitionTimer.isElapsed()) {
					closedDelegate.notify(new DefaultEvent());
				}
			}
		});
		return stage;
	}

	private Table createMainTable() {
		Table mainTable = uiPack.table();
		mainTable.setFillParent(true);
		mainTable.add(new Image(logoRegion)).row();
		mainTable.add(uiPack.lineBreak()).row();
		mainTable.add(uiPack.label("Click or touch to continue...")).row();
		return mainTable;
	}

}
