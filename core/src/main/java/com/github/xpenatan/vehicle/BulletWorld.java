package com.github.xpenatan.vehicle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

public class BulletWorld {

    private btDiscreteDynamicsWorld world;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private btSequentialImpulseConstraintSolver solver;
    private DebugDrawer debugDrawer;

    public static boolean DEBUG = false;

    public void init() {
        Bullet.init();

        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);

        Vector3 worldAabbMin = new Vector3(-1000,-1000,-1000);
        Vector3 worldAabbMax = new Vector3(1000,1000,1000);
        int maxProxies = 1024;
        btAxisSweep3 b = new btAxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        broadphase = b;
        solver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

        int debugFlags = btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE |
                btIDebugDraw.DebugDrawModes.DBG_DrawConstraints |
                btIDebugDraw.DebugDrawModes.DBG_DrawConstraintLimits;
        debugDrawer = new DebugDrawer();
        world.setDebugDrawer(debugDrawer);
        debugDrawer.setDebugMode(debugFlags);

        world.setGravity(new Vector3(0, -9.8f, 0));
    }

    public void render(Camera camera) {
        world.stepSimulation(Gdx.graphics.getDeltaTime());
        if(DEBUG) {
            debugDrawer.begin(camera);
            world.debugDrawWorld();
            debugDrawer.end();
        }
    }

    public btDynamicsWorld getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
        collisionConfiguration.dispose();
        dispatcher.dispose();
        broadphase.dispose();
        solver.dispose();
        debugDrawer.dispose();
    }
}
