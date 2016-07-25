package dc.slideracer.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dc.slideracer.collision.CollisionType;
import dc.slideracer.parts.AccelerationPart;
import dc.slideracer.parts.CollisionPart;
import dc.slideracer.parts.DamageOnCollisionPart;
import dc.slideracer.parts.FragsPart;
import dc.slideracer.parts.RacerInputPart;
import dc.slideracer.parts.SpawnOnDeathPart;
import dc.slideracer.parts.SpeedPart;
import dclib.epf.Entity;
import dclib.epf.parts.Attachment;
import dclib.epf.parts.ColorChangePart;
import dclib.epf.parts.DrawablePart;
import dclib.epf.parts.HealthPart;
import dclib.epf.parts.ParticlesPart;
import dclib.epf.parts.TimedDeathPart;
import dclib.epf.parts.TransformPart;
import dclib.epf.parts.TranslatePart;
import dclib.geometry.PolygonFactory;
import dclib.geometry.UnitConverter;
import dclib.geometry.VertexUtils;
import dclib.graphics.ConvexHullCache;
import dclib.graphics.TextureCache;

public final class EntityFactory {
	
	private static final float RACER_VELOCITY_Y = 8;

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
		PolygonRegion region = textureCache.getPolygonRegion("objects/tank");
		Polygon polygon = convexHullCache.create("objects/tank", size);
		polygon.setPosition(position.x, position.y);
		Entity entity = createBaseEntity(polygon, position.z, region);
		TranslatePart translatePart = new TranslatePart();
		translatePart.setVelocity(new Vector2(0, RACER_VELOCITY_Y));
		entity.attach(translatePart);
		entity.attach(new SpeedPart(20));
		entity.attach(new AccelerationPart(40));
		entity.attach(new RacerInputPart());
		entity.attach(new CollisionPart(CollisionType.RACER, polygon.getVertices()));
		entity.attach(new HealthPart(10));
		entity.attach(new SpawnOnDeathPart("explosion"));
		entity.attach(new FragsPart());
		List<Attachment<ParticleEffect>> particleEffects = new ArrayList<Attachment<ParticleEffect>>();
		Vector2 effectLocalPosition = new Vector2(size.x / 2, 0);
		particleEffects.add(createParticleEffect("flamejet", effectLocalPosition));
		particleEffects.add(createParticleEffect("smokejet", effectLocalPosition));
		entity.attach(new ParticlesPart(particleEffects));
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
	
	public final Entity createBackground(final Rectangle bounds) {
		final float backgroundScale = 8;
		float[] vertices = PolygonFactory.createRectangleVertices(bounds);
		Polygon polygon = VertexUtils.toPolygon(vertices);
		float[] regionVertices = VertexUtils.scale(vertices, backgroundScale * unitConverter.getPixelsPerUnit());
		PolygonRegion region = textureCache.getPolygonRegion("bgs/rock", regionVertices);
		Entity entity = createBaseEntity(polygon, -1, region);
		entity.get(DrawablePart.class).getSprite().setColor(Color.GRAY.cpy());
		return entity;
	}
	
	public final Entity createTerrain(final float[] vertices) {
		Polygon polygon = VertexUtils.toPolygon(vertices);
		float[] regionVertices = VertexUtils.scale(vertices, unitConverter.getPixelsPerUnit());
		PolygonRegion region = textureCache.getPolygonRegion("bgs/rock", regionVertices);
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
	
	private final Attachment<ParticleEffect> createParticleEffect(final String particleEffectPath, 
			final Vector2 localPosition) {
		ParticleEffect effect = new ParticleEffect();
		TextureAtlas atlas = textureCache.getAtlas("objects");
		effect.load(Gdx.files.internal("particles/" + particleEffectPath), atlas);
		effect.scaleEffect(unitConverter.getPixelsPerUnit());
		effect.start();
		return new Attachment<ParticleEffect>(effect, localPosition);
	}
	
}
