package dc.slideracer.epf.graphics;

import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;

import dc.slideracer.parts.CollisionPart;
import dclib.epf.Entity;
import dclib.epf.graphics.EntityDrawer;
import dclib.geometry.VertexUtils;

public final class EntityColliderDrawer implements EntityDrawer {

	private final ShapeRenderer shapeRenderer;
	private final Camera camera;
	private final float pixelsPerUnit;
	
	public EntityColliderDrawer(final ShapeRenderer shapeRenderer, final Camera camera, final float pixelsPerUnit) {
		this.shapeRenderer = shapeRenderer;
		this.camera = camera;
		this.pixelsPerUnit = pixelsPerUnit;
	}
	
	@Override
	public final void draw(final List<Entity> entities) {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		for (Entity entity : entities) {
			if (entity.hasActive(CollisionPart.class)) {
				CollisionPart collisionPart = entity.get(CollisionPart.class);
				for (Polygon collisionPolygon : collisionPart.getPolygons()) {
					float[] transformedVertices = collisionPolygon.getTransformedVertices();
					float[] vertices = VertexUtils.scaleVertices(transformedVertices, pixelsPerUnit);
					shapeRenderer.polygon(vertices);
				}
			}
		}
		shapeRenderer.end();
	}

}
