package dc.slideracer.level;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpeedPart;
import dc.slideracer.parts.WaypointsPart;
import dclib.epf.Entity;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.TransformPart;
import dclib.geometry.VertexUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.RegionFactory;
import dclib.graphics.TextureCache;

public final class EntityFactory {
	
	private final TextureCache textureCache;
	private final ConvexHullCache convexHullCache;
	
	public EntityFactory(final TextureCache textureCache, final ConvexHullCache convexHullCache) {
		this.textureCache = textureCache;
		this.convexHullCache = convexHullCache;
	}
	
	public final Entity createRacer(final Vector3 position) {
		Entity entity = createBaseEntity(new Vector2(1, 1), position, "objects/tank");
		entity.attach(new SpeedPart(10));
		entity.attach(new WaypointsPart());
		entity.attach(new RacerInputPart());
		return entity;
	}
	
	public final Entity createTerrain(final Vector3 position, final float[] vertices) {
		Vector2 size = VertexUtils.bounds(vertices).getSize(new Vector2());
		return createBaseEntity(size, position, "bgs/rock", vertices);
	}
	
	private final Entity createBaseEntity(final Vector2 size, final Vector3 position, final String regionName) {
		return createBaseEntity(size, position, regionName, null);
	}
	
	private final Entity createBaseEntity(final Vector2 size, final Vector3 position, final String regionName, 
			final float[] vertices) {
		Entity entity = new Entity();
		Polygon polygon;
		PolygonRegion region = textureCache.getPolygonRegion(regionName);
		if (vertices != null) {
			polygon = new Polygon(vertices);
			float[] regionVertices = VertexUtils.scaleVertices(vertices, 32, 32);
			region = RegionFactory.createPolygonRegion(region.getRegion(), regionVertices);
		} else {
			polygon = convexHullCache.create(regionName, size);
		}
		entity.attach(new TransformPart(polygon, position));
		DrawablePart drawablePart = new DrawablePart(region);
		entity.attach(drawablePart);
		return entity;
	}
	
}
