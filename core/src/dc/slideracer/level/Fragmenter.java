package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import dclib.epf.Entity;
import dclib.epf.parts.ColorChangePart;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.TimedDeathPart;
import dclib.epf.parts.TransformPart;
import dclib.epf.parts.TranslatePart;
import dclib.geometry.PolygonFactory;
import dclib.geometry.PolygonUtils;
import dclib.geometry.UnitConverter;
import dclib.geometry.VectorUtils;
import dclib.graphics.RegionFactory;

public final class Fragmenter {
	
	private Fragmenter() {
	}
	
	public static final List<Entity> createFrags(final PolygonRegion region, final Polygon parentPolygon, 
			final FragParams params, final UnitConverter unitConverter) {
		List<PolygonSprite> fragSprites = createFragSprites(region, params.width, params.height);
		return createFrags(region.getRegion().getRegionWidth(), region.getRegion().getRegionHeight(), parentPolygon, 
				fragSprites, params, unitConverter);
	}
	
	private static final List<Entity> createFrags(final int regionWidth, final int regionHeight, 
			final Polygon parentPolygon, final List<PolygonSprite> fragSprites, final FragParams params, 
			final UnitConverter unitConverter) {
		List<Entity> frags = new ArrayList<Entity>();
		Vector2 scale = unitConverter.toPixelUnits(PolygonUtils.size(parentPolygon))
				.scl(1.0f / regionWidth, 1.0f / regionHeight);
		for (PolygonSprite fragSprite : fragSprites) {
			Entity frag = createFrag(fragSprite, parentPolygon, scale, params, unitConverter);
			frags.add(frag);
		}
		return frags;
	}
	
	private static final Entity createFrag(final PolygonSprite fragSprite, final Polygon parentPolygon, 
			final Vector2 scale, final FragParams params, final UnitConverter unitConverter) {
		Entity entity = new Entity();
		Polygon fragPolygon = new Polygon();
		fragPolygon.setRotation(parentPolygon.getRotation());
		Vector2 fragSize = unitConverter.toWorldUnits(fragSprite.getWidth(), fragSprite.getHeight()).scl(scale);
		fragPolygon.setVertices(PolygonFactory.createRectangleVertices(fragSize.x, fragSize.y));
		Vector2 worldPosition = unitConverter.toWorldUnits(fragSprite.getX(), fragSprite.getY()).scl(scale);
		Vector2 globalPosition = PolygonUtils.toGlobal(worldPosition.x, worldPosition.y, parentPolygon);
		fragPolygon.setPosition(globalPosition.x, globalPosition.y);
		entity.attach(new TransformPart(fragPolygon, params.z));
		entity.attach(new DrawablePart(fragSprite.getRegion()));
		Vector2 velocity = calculateVelocity(parentPolygon, fragPolygon, params.speedModifier);
		TranslatePart translatePart = new TranslatePart();
		translatePart.setVelocity(velocity);
		entity.attach(translatePart);
		entity.attach(new TimedDeathPart(params.fadeTime));
		entity.attach(new ColorChangePart(params.fadeTime, Color.WHITE.cpy(), Color.CLEAR.cpy()));
		return entity;
	}
	
	private static final Vector2 calculateVelocity(final Polygon parentPolygon, final Polygon childPolygon, 
			final float speedModifier) {
		Vector2 offset = VectorUtils.offset(getFragOrigin(parentPolygon), PolygonUtils.center(childPolygon));
		return VectorUtils.lengthened(offset, offset.len() * speedModifier);
	}
	
	private static final List<PolygonSprite> createFragSprites(final PolygonRegion polygonRegion, final int fragWidth, 
			final int fragHeight) {
		List<PolygonSprite> fragSprites = new ArrayList<PolygonSprite>();
		TextureRegion textureRegion = polygonRegion.getRegion();
		for (int x = 0; x < textureRegion.getRegionWidth(); x += fragWidth) {
			for (int y = 0; y < textureRegion.getRegionHeight(); y += fragHeight) {
				int width = Math.min(fragWidth, textureRegion.getRegionWidth() - x);
				int height = Math.min(fragHeight, textureRegion.getRegionHeight() - y);
				TextureRegion fragRegion = new TextureRegion(polygonRegion.getRegion(), x, y, width, height);
				PolygonSprite fragSprite = new PolygonSprite(RegionFactory.createPolygonRegion(fragRegion));
				fragSprite.setOrigin(0, 0);
				fragSprite.setPosition(x, textureRegion.getRegionHeight() - y - height);
				fragSprites.add(fragSprite);
			}
		}
		return fragSprites;
	}
	
	private static final Vector2 getFragOrigin(final Polygon polygon) {
		return PolygonUtils.center(polygon);
	}
	
}
