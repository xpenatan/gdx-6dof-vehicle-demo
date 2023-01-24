package com.github.xpenatan.vehicle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpring2Constraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRotationalLimitMotor2;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Vehicle {

    private static Vector3 TEMP = new Vector3();

    private ModelInstance bodyModelInstance;
    private ModelInstance wheelFrontLModelInstance;
    private ModelInstance wheelFrontRModelInstance;
    private ModelInstance wheelBackLModelInstance;
    private ModelInstance wheelBackRModelInstance;

    private btRigidBody vehicleBody;
    private btRigidBody wheelFrontLBody;
    private btRigidBody wheelFrontRBody;
    private btRigidBody wheelBackLBody;
    private btRigidBody wheelBackRBody;

    public btGeneric6DofSpring2Constraint frontLeftConstraint;
    public btGeneric6DofSpring2Constraint frontRightConstraint;
    public btGeneric6DofSpring2Constraint backLeftConstraint;
    public btGeneric6DofSpring2Constraint backRightConstraint;

    public float enginePower = 60f;
    public float wheelForceLimit = 10000;
    public float steerForce = 100;
    public float wheelMass = 300f;
    public float bodyMass = 4000;
    public float wheelFriction = 1;

    private Vector3 vehiclePosition = new Vector3();

    public Vector3 getVehiclePosition() {
        bodyModelInstance.transform.getTranslation(vehiclePosition);
        return vehiclePosition;
    }

    public void createModel(btDynamicsWorld world, SceneManager sceneManager) {


        SceneAsset sceneAsset = new GLBLoader().load(Gdx.files.internal("models/vehicle/glTF/VehicleDemo6DOF.glb"));
        Scene bodyScene = new Scene(sceneAsset.scene);

        bodyModelInstance = bodyScene.modelInstance;
        bodyModelInstance.nodes.removeIndex(1);
        bodyModelInstance.transform.translate(0, 1,0);

        Node bodyNode = bodyModelInstance.nodes.get(0);
        bodyNode.translation.set(0, 0.6f,-0.5f);
        bodyModelInstance.calculateTransforms();

        Scene frontLWheel = getWheel(sceneAsset);
        Scene frontRWheel = getWheel(sceneAsset);
        Scene backLWheel = getWheel(sceneAsset);
        Scene backRWheel = getWheel(sceneAsset);

        wheelFrontLModelInstance = frontLWheel.modelInstance;
        wheelFrontRModelInstance = frontRWheel.modelInstance;
        wheelBackLModelInstance = backLWheel.modelInstance;
        wheelBackRModelInstance = backRWheel.modelInstance;

        wheelFrontLModelInstance.transform.translate(1.414f, 0.85f, 1.468f);
        wheelFrontRModelInstance.transform.translate(-1.414f, 0.85f, 1.468f);
        wheelBackLModelInstance.transform.translate(1.35f, 0.85f, -1.89f);
        wheelBackRModelInstance.transform.translate(-1.35f, 0.85f, -1.89f);

        wheelFrontRModelInstance.nodes.get(0).globalTransform.rotate(Vector3.Y, -180);
        wheelBackRModelInstance.nodes.get(0).globalTransform.rotate(Vector3.Y, -180);

        sceneManager.addScene(bodyScene);
        sceneManager.addScene(frontLWheel);
        sceneManager.addScene(frontRWheel);
        sceneManager.addScene(backLWheel);
        sceneManager.addScene(backRWheel);

        // Create Physics

        // Create vehicle body
        BoundingBox box = new BoundingBox();
        bodyModelInstance.calculateBoundingBox(box);
        btBoxShape shape = new btBoxShape(new Vector3(box.getWidth()/2f, box.getHeight()/2f, box.getDepth()/2f));
        btCompoundShape compoundShape = new btCompoundShape();
        compoundShape.addChildShape(bodyNode.localTransform, shape);
        compoundShape.calculateLocalInertia(bodyMass, TEMP.setZero());
        vehicleBody = new btRigidBody(bodyMass, new TestMotionState(bodyModelInstance.transform), compoundShape, TEMP);
        world.addRigidBody(vehicleBody);

        float height = 1.13f;
        float radius = 0.84f;

        Vector3 wheelSize = new Vector3(radius, height / 2.0f, radius);

        //Create Front Left Wheel Body
        btCylinderShape wheelFrontLShape = new btCylinderShape(wheelSize);
        btCompoundShape wheelFLCompoundShape = new btCompoundShape();
        Matrix4 wheelFL = new Matrix4();
        wheelFL.rotate(Vector3.Z, 90);
        wheelFrontLShape.setMargin(0.4f);
        wheelFLCompoundShape.addChildShape(wheelFL, wheelFrontLShape);
        wheelFLCompoundShape.calculateLocalInertia(wheelMass, TEMP.setZero());
        wheelFrontLBody = new btRigidBody(wheelMass, new TestMotionState(wheelFrontLModelInstance.transform), wheelFLCompoundShape, TEMP);
        wheelFrontLBody.setFriction(wheelFriction);
        world.addRigidBody(wheelFrontLBody);

        //Create Front Right Wheel Body
        btCylinderShape wheelFrontRShape = new btCylinderShape(wheelSize);
        btCompoundShape wheelFRCompoundShape = new btCompoundShape();
        Matrix4 wheelFR = new Matrix4();
        wheelFR.rotate(Vector3.Z, 90);
        wheelFrontRShape.setMargin(0.4f);
        wheelFRCompoundShape.addChildShape(wheelFR, wheelFrontRShape);
        wheelFRCompoundShape.calculateLocalInertia(wheelMass, TEMP.setZero());
        wheelFrontRBody = new btRigidBody(wheelMass, new TestMotionState(wheelFrontRModelInstance.transform), wheelFRCompoundShape, TEMP);
        wheelFrontRBody.setFriction(wheelFriction);
        world.addRigidBody(wheelFrontRBody);

        //Create Back Left Wheel Body
        btCylinderShape wheelBackLShape = new btCylinderShape(wheelSize);
        btCompoundShape wheelBLCompoundShape = new btCompoundShape();
        Matrix4 wheelBL = new Matrix4();
        wheelBL.rotate(Vector3.Z, 90);
        wheelBackLShape.setMargin(0.4f);
        wheelBLCompoundShape.addChildShape(wheelBL, wheelBackLShape);
        wheelBLCompoundShape.calculateLocalInertia(wheelMass, TEMP.setZero());
        wheelBackLBody = new btRigidBody(wheelMass, new TestMotionState(wheelBackLModelInstance.transform), wheelBLCompoundShape, TEMP);
        wheelBackLBody.setFriction(wheelFriction);
        world.addRigidBody(wheelBackLBody);

        //Create Back Right Wheel Body
        btCylinderShape wheelBackRShape = new btCylinderShape(wheelSize);
        btCompoundShape wheelBRCompoundShape = new btCompoundShape();
        Matrix4 wheelBR = new Matrix4();
        wheelBR.rotate(Vector3.Z, 90);
        wheelBackRShape.setMargin(0.4f);
        wheelBRCompoundShape.addChildShape(wheelBR, wheelBackRShape);
        wheelBRCompoundShape.calculateLocalInertia(wheelMass, TEMP.setZero());
        wheelBackRBody = new btRigidBody(wheelMass, new TestMotionState(wheelBackRModelInstance.transform), wheelBRCompoundShape, TEMP);
        wheelBackRBody.setFriction(wheelFriction);
        world.addRigidBody(wheelBackRBody);

        Matrix4 mat = new Matrix4();
        frontLeftConstraint = new btGeneric6DofSpring2Constraint(vehicleBody, wheelFrontLBody, new Matrix4(wheelFrontLModelInstance.transform).translate(0, -1, 0), mat);
        world.addConstraint(frontLeftConstraint, true);

        frontRightConstraint = new btGeneric6DofSpring2Constraint(vehicleBody, wheelFrontRBody, new Matrix4(wheelFrontRModelInstance.transform).translate(0, -1, 0), mat);
        world.addConstraint(frontRightConstraint, true);

        backLeftConstraint = new btGeneric6DofSpring2Constraint(vehicleBody, wheelBackLBody, new Matrix4(wheelBackLModelInstance.transform).translate(0, -1, 0), mat);
        world.addConstraint(backLeftConstraint, true);

        backRightConstraint = new btGeneric6DofSpring2Constraint(vehicleBody, wheelBackRBody, new Matrix4(wheelBackRModelInstance.transform).translate(0, -1, 0), mat);
        world.addConstraint(backRightConstraint, true);

        initConstraintValues();
        steer(0f);
        rearSteer(0f);
        enableMotor(true);

        float debugSize = 1.4f;
        frontLeftConstraint.setDbgDrawSize(debugSize);
        frontRightConstraint.setDbgDrawSize(debugSize);
        backLeftConstraint.setDbgDrawSize(debugSize);
        backRightConstraint.setDbgDrawSize(debugSize);
    }

    private void initConstraintValues() {

        frontLeftConstraint.enableMotor(4, true);
        frontRightConstraint.enableMotor(4, true);
        backLeftConstraint.enableMotor(4, true);
        backRightConstraint.enableMotor(4, true);

        frontLeftConstraint.setMaxMotorForce(3, wheelForceLimit);
        frontRightConstraint.setMaxMotorForce(3, wheelForceLimit);
        backLeftConstraint.setMaxMotorForce(3, wheelForceLimit);
        backRightConstraint.setMaxMotorForce(3, wheelForceLimit);

        frontLeftConstraint.setMaxMotorForce(4, wheelForceLimit);
        frontRightConstraint.setMaxMotorForce(4, wheelForceLimit);
        backLeftConstraint.setMaxMotorForce(4, wheelForceLimit);
        backRightConstraint.setMaxMotorForce(4, wheelForceLimit);

        // upper < lower means free
        // upper == lower means locked
        // upper > lower means limited
        for(int i = 0; i < 6; i++) {
            // Lock all axis
            frontLeftConstraint.setLimit(i, 0f, 0f);
            frontRightConstraint.setLimit(i, 0f, 0f);
            backLeftConstraint.setLimit(i, 0f, 0f);
            backRightConstraint.setLimit(i, 0f, 0f);
        }
        frontLeftConstraint.setLimit(3, 1, -1f);
        frontRightConstraint.setLimit(3, 1, -1f);
        backLeftConstraint.setLimit(3, 1, -1f);
        backRightConstraint.setLimit(3, 1, -1f);

        frontLeftConstraint.setLimit(1, -0.4f, 0.2f);
        frontRightConstraint.setLimit(1, -0.4f, 0.2f);
        backLeftConstraint.setLimit(1, -0.4f, 0.2f);
        backRightConstraint.setLimit(1, -0.4f, 0.2f);

        frontLeftConstraint.enableSpring(1, true);
        frontRightConstraint.enableSpring(1, true);
        backLeftConstraint.enableSpring(1, true);
        backRightConstraint.enableSpring(1, true);

        float frontStiffness = 10000f;
        float backStiffness = 8000;
        float damping = 4000f;

        frontLeftConstraint.setStiffness(1, frontStiffness);
        frontLeftConstraint.setDamping(1, damping);
        frontLeftConstraint.setEquilibriumPoint(1, -1);
        frontRightConstraint.setStiffness(1, frontStiffness);
        frontRightConstraint.setDamping(1, damping);
        frontRightConstraint.setEquilibriumPoint(1, -1);

        backLeftConstraint.setStiffness(1, backStiffness);
        backLeftConstraint.setDamping(1, damping);
        backLeftConstraint.setEquilibriumPoint(1, -1);
        backRightConstraint.setStiffness(1, backStiffness);
        backRightConstraint.setDamping(1, damping);
        backRightConstraint.setEquilibriumPoint(1, -1);

    }

    private Scene getWheel(SceneAsset sceneAsset) {
        Scene wheelScene = new Scene(sceneAsset.scene);
        ModelInstance wheelModelInstance = wheelScene.modelInstance;
        wheelModelInstance.nodes.removeIndex(0);
        wheelModelInstance.nodes.get(0).localTransform.idt();
        wheelModelInstance.nodes.get(0).globalTransform.idt();
        return wheelScene;
    }

    boolean isUpBeenPressed = false;
    boolean isDownBeenPressed = false;
    boolean isLeftBeenPressed = false;
    boolean isRightBeenPressed = false;
    boolean isLeftRearBeenPressed = false;
    boolean isRightRearBeenPressed = false;

    boolean isUpBeenReleased = false;
    boolean isDownBeenReleased = false;
    boolean isLeftBeenReleased = false;
    boolean isRightBeenReleased = false;
    boolean isLeftRearBeenReleased = false;
    boolean isRightRearBeenReleased = false;

    public void update() {


        boolean isUpPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean isDownPressed = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean isLeftRearPressed = Gdx.input.isKeyPressed(Input.Keys.Q);
        boolean isRightRearPressed = Gdx.input.isKeyPressed(Input.Keys.E);


        if(isUpBeenPressed && !isUpPressed) {
            isUpBeenReleased = true;
        }
        if(isDownBeenPressed && !isDownPressed) {
            isDownBeenReleased = true;
        }
        if(isLeftBeenPressed && !isLeftPressed) {
            isLeftBeenReleased = true;
        }
        if(isRightBeenPressed && !isRightPressed) {
            isRightBeenReleased = true;
        }
        if(isLeftRearBeenPressed && !isLeftRearPressed) {
            isLeftRearBeenReleased = true;
        }
        if(isRightRearBeenPressed && !isRightRearPressed) {
            isRightRearBeenReleased = true;
        }
        isUpBeenPressed = isUpPressed;
        isDownBeenPressed = isDownPressed;
        isLeftBeenPressed = isLeftPressed;
        isRightBeenPressed = isRightPressed;
        isLeftRearBeenPressed = isLeftRearPressed;
        isRightRearBeenPressed = isRightRearPressed;

        if(isUpPressed) {
            accelerate(1);
        }
        else if(isDownPressed) {
            reverse(1);
        }

        if(isUpBeenReleased || isDownBeenReleased) {
            accelerate(0);
        }

        if(isLeftPressed) {
            steer(-1);
        }
        else if(isRightPressed) {
            steer(1);
        }

        if(isLeftBeenReleased || isRightBeenReleased) {
            steer(0);
        }

        if(isLeftRearPressed) {
            rearSteer(-1);
        }
        else if(isRightRearPressed) {
            rearSteer(1);
        }

        if(isLeftRearBeenReleased || isRightRearBeenReleased) {
            rearSteer(0);
        }

        isUpBeenReleased = false;
        isDownBeenReleased = false;
        isLeftBeenReleased = false;
        isRightBeenReleased = false;
        isLeftRearBeenReleased = false;
        isRightRearBeenReleased = false;
    }

    public void enableMotor(boolean enable) {
        frontLeftConstraint.getRigidBodyB().activate();
        frontRightConstraint.getRigidBodyB().activate();
        backLeftConstraint.getRigidBodyB().activate();
        backRightConstraint.getRigidBodyB().activate();

        frontLeftConstraint.enableMotor(3, enable);
        frontRightConstraint.enableMotor(3, enable);
        backLeftConstraint.enableMotor(3, enable);
        backRightConstraint.enableMotor(3, enable);
    }

    public void steer(float amount) {
        float angle = 0.623599f; //# 30 degrees

        if(amount < 0.1 && amount > -0.1) {
            frontLeftConstraint.setLimit(4, 0f, 0f);
            frontRightConstraint.setLimit(4, 0f, 0f);
        }
        else {
            frontLeftConstraint.getRigidBodyB().activate();
            frontRightConstraint.getRigidBodyB().activate();
            if(amount > 0) {
                btRotationalLimitMotor2 leftLimitMotor = frontLeftConstraint.getRotationalLimitMotor(1);
                btRotationalLimitMotor2 rightLimitMotor = frontRightConstraint.getRotationalLimitMotor(1);
                frontLeftConstraint.setLimit(4, leftLimitMotor.getLoLimit(), angle * amount);
                frontRightConstraint.setLimit(4, rightLimitMotor.getLoLimit(), angle * amount);
            }
            else {
                btRotationalLimitMotor2 leftLimitMotor = frontLeftConstraint.getRotationalLimitMotor(1);
                btRotationalLimitMotor2 rightLimitMotor = frontRightConstraint.getRotationalLimitMotor(1);
                frontLeftConstraint.setLimit(4, angle * amount, leftLimitMotor.getHiLimit());
                frontRightConstraint.setLimit(4, angle * amount, rightLimitMotor.getHiLimit());
            }
            frontLeftConstraint.setTargetVelocity(4, amount * steerForce);
            frontRightConstraint.setTargetVelocity(4, amount * steerForce);
        }
    }

    public void rearSteer(float amount) {
        float angle = 0.523599f; //# 30 degrees

        if(amount < 0.1 && amount > -0.1) {
            backLeftConstraint.setLimit(4, 0f, 0f);
            backRightConstraint.setLimit(4, 0f, 0f);
        }
        else {
            backLeftConstraint.getRigidBodyB().activate();
            backRightConstraint.getRigidBodyB().activate();

            backLeftConstraint.setLimit(4, -angle, angle);
            backRightConstraint.setLimit(4, -angle, angle);

            backLeftConstraint.setTargetVelocity(4, amount * steerForce);
            backRightConstraint.setTargetVelocity(4, amount * steerForce);
        }
    }

    public void accelerate(float amount) {
        if(amount == 0) {
            enableMotor(false);
        }
        else {
            enableMotor(true);

            frontLeftConstraint.setTargetVelocity(3, -amount * enginePower);
            frontRightConstraint.setTargetVelocity(3, -amount * enginePower);
            backLeftConstraint.setTargetVelocity(3, -amount * enginePower);
            backRightConstraint.setTargetVelocity(3, -amount * enginePower);
        }
    }

    public void reverse(float amount) {
        if(amount == 0) {
            enableMotor(false);
        }
        else {
            enableMotor(true);

            frontLeftConstraint.setTargetVelocity(3, amount * enginePower);
            frontRightConstraint.setTargetVelocity(3, amount * enginePower);
            backLeftConstraint.setTargetVelocity(3, amount * enginePower);
            backRightConstraint.setTargetVelocity(3, amount * enginePower);
        }
    }

    public void handBrake(float amount) {
        enableMotor(true);

        frontLeftConstraint.setTargetVelocity(3, amount);
        frontRightConstraint.setTargetVelocity(3, amount);
        backLeftConstraint.setTargetVelocity(3, amount);
        backRightConstraint.setTargetVelocity(3, amount);
    }
}
