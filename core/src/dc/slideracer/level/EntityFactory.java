package dc.slideracer.level;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.parts.CollisionPart;
import dc.slideracer.parts.DamageOnCollisionPart;
import dc.slideracer.parts.HealthPart;
import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpeedPart;
import dc.slideracer.parts.WaypointsPart;
import dclib.epf.Entity;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.TransformPart;
import dclib.epf.parts.TranslatePart;
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
	
	public final Entity createRacer(final Vector2 size, final Vector3 position) {
		PolygonRegion region = textureCache.getPolygonRegion("objects/tank");
		Polygon polygon = convexHullCache.create("objects/tank", size);
		polygon.setPosition(position.x, position.y);
		Entity entity = createBaseEntity(polygon, position.z, "objects/tank", region);
		TranslatePart translatePart = new TranslatePart();
		translatePart.setVelocity(new Vector2(0, 1));
		entity.attach(translatePart);
		entity.attach(new SpeedPart(10));
		entity.attach(new WaypointsPart());
		entity.attach(new RacerInputPart());
		entity.attach(new CollisionPart(CollisionType.RACER, polygon.getVertices()));
		entity.attach(new HealthPart(10));
		return entity;
	}
	
	public final Entity createTerrain(final float[] vertices) {
		PolygonRegion region = textureCache.getPolygonRegion("bgs/rock");
		Polygon polygon = VertexUtils.toPolygon(vertices);
		float[] regionVertices = VertexUtils.scaleVertices(vertices, 32, 32);
		region = RegionFactory.createPolygonRegion(region.getRegion(), regionVertices);
		Entity entity = createBaseEntity(polygon, 0, "bgs/rock", region);
		entity.attach(new CollisionPart(CollisionType.HAZARD, polygon.getVertices()));
		entity.attach(new DamageOnCollisionPart(100));
		return entity;
	}
	
	private final Entity createBaseEntity(final Polygon polygon, final float z, final String regionName, 
			final PolygonRegion region) {
		Entity entity = new Entity();
		entity.attach(new TransformPart(polygon, z));
		DrawablePart drawablePart = new DrawablePart(region);
		entity.attach(drawablePart);
		return entity;
	}
	
}
