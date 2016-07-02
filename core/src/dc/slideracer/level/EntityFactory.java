package dc.slideracer.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.parts.AccelerationPart;
import dc.slideracer.parts.CollisionPart;
import dc.slideracer.parts.ColorChangePart;
import dc.slideracer.parts.DamageOnCollisionPart;
import dc.slideracer.parts.EmitPart;
import dc.slideracer.parts.FragsPart;
import dc.slideracer.parts.HealthPart;
import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpawnOnDeathPart;
import dc.slideracer.parts.SpeedPart;
import dc.slideracer.parts.TimedDeathPart;
import dclib.epf.Entity;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.TransformPart;
import dclib.epf.parts.TranslatePart;
import dclib.geometry.UnitConverter;
import dclib.geometry.VertexUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.RegionFactory;
import dclib.graphics.TextureCache;
import dclib.util.Timer;

public final class EntityFactory {
	
	private static final float RACER_VELOCITY_Y = 3;

	private final UnitConverter unitConverter;
	private final TextureCache textureCache;
	private final ConvexHullCache convexHullCache;
	
	public EntityFactory(final UnitConverter unitConverter, final TextureCache textureCache, 
			final ConvexHullCache convexHullCache) {
		this.unitConverter = unitConverter;
		this.textureCache = textureCache;
		this.convexHullCache = convexHullCache;
	}
	
	public final Entity createRacer(final Vector2 size, final Vector3 position) {
//		float[] vertices = PolygonFactory.createRectangleVertices(size.x, size.y);
//		Polygon polygon = VertexUtils.toPolygon(vertices);
//		PolygonRegion region = textureCache.getPolygonRegion("bgs/rock");
//		float[] regionVert = PolygonFactory.createRectangleVertices(2, 2, 8, 8);
//		region = RegionFactory.createPolygonRegion(region.getRegion(), regionVert);
		PolygonRegion region = textureCache.getPolygonRegion("objects/tank");
		Polygon polygon = convexHullCache.create("objects/tank", size);
		polygon.setPosition(position.x, position.y);
		Entity entity = createBaseEntity(polygon, position.z, region);
		TranslatePart translatePart = new TranslatePart();
		translatePart.setVelocity(new Vector2(0, RACER_VELOCITY_Y));
		entity.attach(translatePart);
		entity.attach(new SpeedPart(10));
		entity.attach(new AccelerationPart(10));
		entity.attach(new RacerInputPart());
		entity.attach(new CollisionPart(CollisionType.RACER, polygon.getVertices()));
		entity.attach(new HealthPart(10));
		entity.attach(new SpawnOnDeathPart("explosion"));
		entity.attach(new FragsPart());
		entity.attach(new EmitPart("smoke", new Vector2(size.x / 2, size.y / 5), new Timer(0.2f)));
		return entity;
	}

	public final Entity createExplosion() {
		PolygonRegion region = textureCache.getPolygonRegion("objects/explosion");
		Vector2 size = new Vector2(1, 1);
		Polygon polygon = convexHullCache.create("objects/explosion", size);
		Entity entity = createBaseEntity(polygon, 1, region);
		entity.attach(new ColorChangePart(1, Color.WHITE.cpy(), Color.CLEAR.cpy()));
		entity.attach(new TimedDeathPart(1));
		return entity;
	}

	public final Entity createSmoke() {
		PolygonRegion region = textureCache.getPolygonRegion("objects/smoke");
		Vector2 size = new Vector2(0.3f, 0.3f);
		Polygon polygon = convexHullCache.create("objects/smoke", size);
		Entity entity = createBaseEntity(polygon, 1.1f, region);
		entity.attach(new ColorChangePart(1, Color.WHITE.cpy(), Color.CLEAR.cpy()));
		entity.attach(new TimedDeathPart(1));
		TranslatePart translatePart = new TranslatePart();
		translatePart.setVelocity(new Vector2(0, RACER_VELOCITY_Y));
		entity.attach(translatePart);
		return entity;
	}
	
	public final Entity createTerrain(final float[] vertices) {
		PolygonRegion region = textureCache.getPolygonRegion("bgs/rock");
		Polygon polygon = VertexUtils.toPolygon(vertices);
		float[] regionVertices = VertexUtils.scaleVertices(vertices, unitConverter.getPixelsPerUnit());
		region = RegionFactory.createPolygonRegion(region.getRegion(), regionVertices);
		Entity entity = createBaseEntity(polygon, 0, region);
		entity.attach(new CollisionPart(CollisionType.HAZARD, polygon.getVertices()));
		entity.attach(new DamageOnCollisionPart(100));
		return entity;
	}
	
	private final Entity createBaseEntity(final Polygon polygon, final float z, final PolygonRegion region) {
		Entity entity = new Entity();
		entity.attach(new TransformPart(polygon, z));
		DrawablePart drawablePart = new DrawablePart(region);
		entity.attach(drawablePart);
		return entity;
	}
	
}
