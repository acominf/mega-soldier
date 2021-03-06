package com.mygdx.game.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MegaSoldier;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Jonathan on 25/05/2017.
 */

public class SoldadoMalo extends Enemy {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    public static boolean touch;

    public SoldadoMalo(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("enemies"), i * 192, 0, 192, 164));
        walkAnimation = new Animation(0.2f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 33 / MegaSoldier.PPM, 33 / MegaSoldier.PPM);
        setToDestroy = false;
        destroyed = false;
        touch = false;

    }

    public void update(float dt)
    {
        stateTime += dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(getB2body());
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("enemies")), 768, 0, 192, 164 );
            stateTime = 0;
        }
        else if(!destroyed) {
            getB2body().setLinearVelocity(getVelocity());
            setPosition(getB2body().getPosition().x - getWidth() / 2, getB2body().getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }


    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        setB2body(world.createBody(bdef));

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(15/MegaSoldier.PPM);
        fdef.filter.categoryBits = MegaSoldier.ENEMY_BIT;
        fdef.filter.maskBits = MegaSoldier.GROUND_BIT |
                               MegaSoldier.OBJETO_BIT |
                               MegaSoldier.BRICK_BIT |
                               MegaSoldier.ENEMY_BIT |
                               MegaSoldier.OBJECT_BIT |
                               MegaSoldier.PLAYER_BIT;

        fdef.shape = shape;
        getB2body().createFixture(fdef).setUserData(this);

        //  Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-3.5f, 17).scl(1 / MegaSoldier.PPM);
        vertice[1] = new Vector2(3.5f, 17).scl(1 / MegaSoldier.PPM);
        vertice[2] = new Vector2(-2, 13).scl(1 / MegaSoldier.PPM);
        vertice[3] = new Vector2(2, 13).scl(1 / MegaSoldier.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.5f;
        fdef.filter.categoryBits = MegaSoldier.ENEMY_HEAD_BIT;
        getB2body().createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch)
    {
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }


    @Override
    public void hitOnHead() {
        setToDestroy = true;
        Hud.addScore(500);
        MegaSoldier.manager.get("Audio/Music/enemyDied.wav", Music.class).play();
    }

    public static void setTouch(boolean touch)
    {
        SoldadoMalo.touch = touch;
    }
    public static boolean getTouch()
    {
        return touch;
    }
}
