package com.github.xpenatan.vehicle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.github.xpenatan.vehicle.imgui.ImGuiRenderer;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneModel;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainClass extends ApplicationAdapter {

    private SceneManager sceneManager;

    private DirectionalLight defaultLight;
    private SceneSkybox skybox;

    private Cubemap environmentCubemap;
    private Cubemap diffuseCubemap;
    private Cubemap specularCubemap;

    private CameraInputController cameraControl;
    private PerspectiveCamera camera;

    private Texture brdfLUT;

    private btDiscreteDynamicsWorld world;
    private btDefaultCollisionConfiguration collisionConfiguration;
    private btCollisionDispatcher dispatcher;
    private btBroadphaseInterface broadphase;
    private btSequentialImpulseConstraintSolver solver;
    private DebugDrawer debugDrawer;

    private btRigidBody groundBody;

    private Vehicle vehicle;

    private ImGuiRenderer imGuiRenderer;

    public boolean worldDebug = false;

    public MainClass() {
    }

    public MainClass(ImGuiRenderer imGuiRenderer) {
        this.imGuiRenderer = imGuiRenderer;
    }

    @Override
    public void create() {
        Bullet.init();
        if(imGuiRenderer != null) {
            imGuiRenderer.init();
        }
        sceneManager = new SceneManager();

        DirectionalShadowLight defaultLight = new DirectionalShadowLight();
        defaultLight.direction.set(2, -3, 2).nor();
        defaultLight.color.set(Color.WHITE);
        defaultLight.intensity = 3f;
        sceneManager.environment.add(defaultLight);

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(defaultLight);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        sceneManager.environment.add(defaultLight);

        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0f));

        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.setSkyBox(skybox = new SceneSkybox(environmentCubemap));
        sceneManager.setAmbientLight(1f);

        sceneManager.setEnvironmentRotation(180);

        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.far = 1000f;
        camera.position.y = 18;
        camera.lookAt(0,0, -20);

        cameraControl = new CameraInputController(camera) {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }
        };

        sceneManager.setCamera(camera);

        if(imGuiRenderer != null) {
            Gdx.input.setInputProcessor(new InputMultiplexer(imGuiRenderer.getInput(), cameraControl));
        }
        else {
            Gdx.input.setInputProcessor(new InputMultiplexer(cameraControl));
        }

        initBulletWorld();
        createGround();

        vehicle = new Vehicle();
        vehicle.createModel(world, sceneManager);
    }

    private void createGround() {
        Vector3 size = new Vector3(50, 1.2f, 50);
        ModelBuilder mb = new ModelBuilder();
        mb.begin();

        Texture texture = new Texture(Gdx.files.internal("textures/ground.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion textureRegion = new TextureRegion(texture);
        textureRegion.setV2(5f);
        textureRegion.setU2(5f);
        Material material = new Material("material",
            PBRTextureAttribute.createBaseColorTexture(textureRegion)
        );
        MeshPartBuilder meshBuilder = mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
        BoxShapeBuilder.build(meshBuilder, size.x * 2f, size.y * 2f, size.z * 2);
        Model model = mb.end();

        SceneModel sceneModel = new SceneModel();
        sceneModel.model = model;
        sceneModel.name = "ground";

        Scene scene = new Scene(sceneModel);

        scene.modelInstance.transform.setToTranslation(0,-4, 0);

        sceneManager.addScene(scene);
        btDefaultMotionState motionState = new btDefaultMotionState(scene.modelInstance.transform);
        btBoxShape shape = new btBoxShape(size);
        groundBody = new btRigidBody(0, motionState, shape);
        groundBody.setFriction(1f);
        world.addRigidBody(groundBody);
    }

    private void initBulletWorld() {
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

    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            worldDebug = !worldDebug;
        }

        camera.update();
        if(cameraControl != null){
            cameraControl.update();
        }

        if(imGuiRenderer != null && worldDebug) {
            imGuiRenderer.begin();
            imGuiRenderer.renderVehicle(vehicle);
        }

        vehicle.update();

        Vector3 vehiclePosition = vehicle.getVehiclePosition();

        camera.position.x = vehiclePosition.x;
        camera.position.z = vehiclePosition.z + 20;

        sceneManager.update(deltaTime);
        sceneManager.render();

        world.stepSimulation(Gdx.graphics.getDeltaTime());
        if(worldDebug) {
            debugDrawer.begin(camera);
            world.debugDrawWorld();
            debugDrawer.end();
        }

        if(imGuiRenderer != null && worldDebug) {
            imGuiRenderer.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();

        world.dispose();
        collisionConfiguration.dispose();
        dispatcher.dispose();
        broadphase.dispose();
        solver.dispose();
        debugDrawer.dispose();
    }
}
