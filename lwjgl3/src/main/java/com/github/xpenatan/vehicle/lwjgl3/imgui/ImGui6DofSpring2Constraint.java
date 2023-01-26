package com.github.xpenatan.vehicle.lwjgl3.imgui;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpring2Constraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRotationalLimitMotor2;
import com.badlogic.gdx.physics.bullet.dynamics.btTranslationalLimitMotor2;
import com.github.xpenatan.imgui.core.ImGui;
import com.github.xpenatan.imgui.core.ImGuiBoolean;
import com.github.xpenatan.imgui.core.ImGuiFloat;
import com.github.xpenatan.imgui.core.enums.ImGuiTableFlags;
import com.github.xpenatan.imgui.core.enums.ImGuiTreeNodeFlags;

public class ImGui6DofSpring2Constraint {

    private static int childStartId = 9231;
    private static int childId = childStartId;

    private static final ImGuiBoolean TMP_BOOLEAN_01 = new ImGuiBoolean();
    private static final ImGuiBoolean TMP_BOOLEAN_02 = new ImGuiBoolean();
    private static final ImGuiBoolean TMP_BOOLEAN_03 = new ImGuiBoolean();
    private static final ImGuiFloat TMP_FLOAT_01 = new ImGuiFloat();
    private static final ImGuiFloat TMP_FLOAT_02 = new ImGuiFloat();
    private static final ImGuiFloat TMP_FLOAT_03 = new ImGuiFloat();

    public static final boolean [] TMP_BOOLEAN3 = new boolean[3];

    private static final Vector3 TMP_VEC3_01 = new Vector3();

    private static final float DRAG_PRECISION = 0.01f;

    private static final String floatXFormat = "X:%.02f";
    private static final String floatYFormat = "Y:%.02f";
    private static final String floatZFormat = "Z:%.02f";

    public static void render(String text, btGeneric6DofSpring2Constraint constraint) {
        childId = childStartId;

        btTranslationalLimitMotor2 translationalLimitMotor = constraint.getTranslationalLimitMotor();
        btRotationalLimitMotor2 rotationalLimitMotorX = constraint.getRotationalLimitMotor(0);
        btRotationalLimitMotor2 rotationalLimitMotorY = constraint.getRotationalLimitMotor(1);
        btRotationalLimitMotor2 rotationalLimitMotorZ = constraint.getRotationalLimitMotor(2);

        ImGui.Begin(text);
        boolean r1 = renderTranslationLimitMotor(constraint, translationalLimitMotor);
        boolean r2 = renderRotationalLimitMotor(rotationalLimitMotorX, rotationalLimitMotorY, rotationalLimitMotorZ);
        if(r1 || r2) {
            btRigidBody rigidBodyB = constraint.getRigidBodyB();
            rigidBodyB.activate();
        }
        ImGui.End();
    }

    private static boolean renderTranslationLimitMotor(btGeneric6DofSpring2Constraint constraint, btTranslationalLimitMotor2 tMotor) {
        if(ImGui.CollapsingHeader("Translation Limit Motor", ImGuiTreeNodeFlags.DefaultOpen)) {
            return renderTLimitMotor(tMotor);
        }
        return false;
    }

    private static boolean renderTLimitMotor(btTranslationalLimitMotor2 tMotor) {
        boolean ret = false;
        if (ImGui.BeginTable("translationMotor", 2, ImGuiTableFlags.RowBg.or(ImGuiTableFlags.SizingStretchProp))) {
            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Enable Motor");
            ImGui.TableSetColumnIndex(1);
            boolean[] enableMotor = tMotor.getEnableMotor();
            if(renderCheckbox(enableMotor)) {
                tMotor.setEnableMotor(enableMotor);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Servo Motor");
            ImGui.TableSetColumnIndex(1);
            boolean[] servoMotor = tMotor.getServoMotor();
            if(renderCheckbox(servoMotor)) {
                tMotor.setServoMotor(servoMotor);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Enable Spring");
            ImGui.TableSetColumnIndex(1);
            boolean[] enableSpring = tMotor.getEnableSpring();
            if(renderCheckbox(enableSpring)) {
                tMotor.setEnableSpring(enableSpring);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Stiffness L.");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Spring Stiffness Limited");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            boolean[] springStiffnessLimited = tMotor.getSpringStiffnessLimited();
            if(renderCheckbox(springStiffnessLimited)) {
                tMotor.setSpringStiffnessLimited(springStiffnessLimited);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Damping L.");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Spring Damping Limited");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            boolean[] springDampingLimited = tMotor.getSpringDampingLimited();
            if(renderCheckbox(springDampingLimited)) {
                tMotor.setSpringDampingLimited(springDampingLimited);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Upper Distance");
            ImGui.TableSetColumnIndex(1);
            Vector3 upperLimit = tMotor.getUpperLimit();
            if(renderTextView(upperLimit)) {
                tMotor.setUpperLimit(upperLimit);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Lower Distance");
            ImGui.TableSetColumnIndex(1);
            Vector3 lowerLimit = tMotor.getLowerLimit();
            if(renderTextView(lowerLimit)) {
                tMotor.setLowerLimit(lowerLimit);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Restitution");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Bounce");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            Vector3 bounce = tMotor.getBounce();
            if(renderTextView(bounce)) {
                tMotor.setBounce(bounce);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("StopERP");
            ImGui.TableSetColumnIndex(1);
            Vector3 stopERP = tMotor.getStopERP();
            if(renderTextView(stopERP)) {
                tMotor.setStopERP(stopERP);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("StopCFM");
            ImGui.TableSetColumnIndex(1);
            Vector3 stopCFM = tMotor.getStopCFM();
            if(renderTextView(stopCFM)) {
                tMotor.setStopCFM(stopCFM);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("MotorERP");
            ImGui.TableSetColumnIndex(1);
            Vector3 motorERP = tMotor.getMotorERP();
            if(renderTextView(motorERP)) {
                tMotor.setMotorERP(motorERP);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("MotorCFM");
            ImGui.TableSetColumnIndex(1);
            Vector3 motorCFM = tMotor.getMotorCFM();
            if(renderTextView(motorCFM)) {
                tMotor.setMotorCFM(motorCFM);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Servo Target");
            ImGui.TableSetColumnIndex(1);
            Vector3 servoTarget = tMotor.getServoTarget();
            if(renderTextView(servoTarget)) {
                tMotor.setServoTarget(servoTarget);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Stiffness");
            ImGui.TableSetColumnIndex(1);
            Vector3 springStiffness = tMotor.getSpringStiffness();
            if(renderTextView(springStiffness)) {
                tMotor.setSpringStiffness(springStiffness);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Damping");
            ImGui.TableSetColumnIndex(1);
            Vector3 springDamping = tMotor.getSpringDamping();
            if(renderTextView(springDamping)) {
                tMotor.setSpringDamping(springDamping);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Equilibrium Point");
            ImGui.TableSetColumnIndex(1);
            Vector3 equilibriumPoint = tMotor.getEquilibriumPoint();
            if(renderTextView(equilibriumPoint)) {
                tMotor.setEquilibriumPoint(equilibriumPoint);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Target Velocity");
            ImGui.TableSetColumnIndex(1);
            Vector3 targetVelocity = tMotor.getTargetVelocity();
            if(renderTextView(targetVelocity)) {
                tMotor.setTargetVelocity(targetVelocity);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Max Motor Force");
            ImGui.TableSetColumnIndex(1);
            Vector3 maxMotorForce = tMotor.getMaxMotorForce();
            if(renderTextView(maxMotorForce)) {
                tMotor.setMaxMotorForce(maxMotorForce);
                ret = true;
            }

            ImGui.EndTable();
        }
        return ret;
    }

    private static boolean renderRotationalLimitMotor(btRotationalLimitMotor2 rotationalLimitMotorX, btRotationalLimitMotor2 rotationalLimitMotorY, btRotationalLimitMotor2 rotationalLimitMotorZ) {
        if(ImGui.CollapsingHeader("Rotational Limit Motor", ImGuiTreeNodeFlags.DefaultOpen)) {
            return renderRLimitMotor(rotationalLimitMotorX, rotationalLimitMotorY, rotationalLimitMotorZ);
        }
        return false;
    }

    private static boolean renderRLimitMotor(btRotationalLimitMotor2 rotationalLimitMotorX, btRotationalLimitMotor2 rotationalLimitMotorY, btRotationalLimitMotor2 rotationalLimitMotorZ) {
        boolean ret = false;
        if (ImGui.BeginTable("rotationalMotor", 2, ImGuiTableFlags.RowBg.or(ImGuiTableFlags.SizingStretchProp))) {
            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Enable Motor");
            ImGui.TableSetColumnIndex(1);
            TMP_BOOLEAN3[0] = rotationalLimitMotorX.getEnableMotor();
            TMP_BOOLEAN3[1] = rotationalLimitMotorY.getEnableMotor();
            TMP_BOOLEAN3[2] = rotationalLimitMotorZ.getEnableMotor();
            if(renderCheckbox(TMP_BOOLEAN3)) {
                rotationalLimitMotorX.setEnableMotor(TMP_BOOLEAN3[0]);
                rotationalLimitMotorY.setEnableMotor(TMP_BOOLEAN3[1]);
                rotationalLimitMotorZ.setEnableMotor(TMP_BOOLEAN3[2]);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Servo Motor");
            ImGui.TableSetColumnIndex(1);
            TMP_BOOLEAN3[0] = rotationalLimitMotorX.getServoMotor();
            TMP_BOOLEAN3[1] = rotationalLimitMotorY.getServoMotor();
            TMP_BOOLEAN3[2] = rotationalLimitMotorZ.getServoMotor();
            if(renderCheckbox(TMP_BOOLEAN3)) {
                rotationalLimitMotorX.setServoMotor(TMP_BOOLEAN3[0]);
                rotationalLimitMotorY.setServoMotor(TMP_BOOLEAN3[1]);
                rotationalLimitMotorZ.setServoMotor(TMP_BOOLEAN3[2]);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Enable Spring");
            ImGui.TableSetColumnIndex(1);
            TMP_BOOLEAN3[0] = rotationalLimitMotorX.getEnableSpring();
            TMP_BOOLEAN3[1] = rotationalLimitMotorY.getEnableSpring();
            TMP_BOOLEAN3[2] = rotationalLimitMotorZ.getEnableSpring();
            if(renderCheckbox(TMP_BOOLEAN3)) {
                rotationalLimitMotorX.setEnableSpring(TMP_BOOLEAN3[0]);
                rotationalLimitMotorY.setEnableSpring(TMP_BOOLEAN3[1]);
                rotationalLimitMotorZ.setEnableSpring(TMP_BOOLEAN3[2]);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Stiffness L.");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Spring Stiffness Limited");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            TMP_BOOLEAN3[0] = rotationalLimitMotorX.getSpringStiffnessLimited();
            TMP_BOOLEAN3[1] = rotationalLimitMotorY.getSpringStiffnessLimited();
            TMP_BOOLEAN3[2] = rotationalLimitMotorZ.getSpringStiffnessLimited();
            if(renderCheckbox(TMP_BOOLEAN3)) {
                rotationalLimitMotorX.setSpringStiffnessLimited(TMP_BOOLEAN3[0]);
                rotationalLimitMotorY.setSpringStiffnessLimited(TMP_BOOLEAN3[1]);
                rotationalLimitMotorZ.setSpringStiffnessLimited(TMP_BOOLEAN3[2]);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Damping L.");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Spring Damping Limited");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            TMP_BOOLEAN3[0] = rotationalLimitMotorX.getSpringDampingLimited();
            TMP_BOOLEAN3[1] = rotationalLimitMotorY.getSpringDampingLimited();
            TMP_BOOLEAN3[2] = rotationalLimitMotorZ.getSpringDampingLimited();
            if(renderCheckbox(TMP_BOOLEAN3)) {
                rotationalLimitMotorX.setSpringDampingLimited(TMP_BOOLEAN3[0]);
                rotationalLimitMotorY.setSpringDampingLimited(TMP_BOOLEAN3[1]);
                rotationalLimitMotorZ.setSpringDampingLimited(TMP_BOOLEAN3[2]);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Upper Angle");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getHiLimit();
            TMP_VEC3_01.y = rotationalLimitMotorY.getHiLimit();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getHiLimit();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setHiLimit(TMP_VEC3_01.x);
                rotationalLimitMotorY.setHiLimit(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setHiLimit(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Lower Angle");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getLoLimit();
            TMP_VEC3_01.y = rotationalLimitMotorY.getLoLimit();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getLoLimit();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setLoLimit(TMP_VEC3_01.x);
                rotationalLimitMotorY.setLoLimit(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setLoLimit(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Restitution");
            if(ImGui.IsItemHovered()) {
                ImGui.BeginTooltip();
                ImGui.Text("Bounce");
                ImGui.EndTooltip();
            }
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getBounce();
            TMP_VEC3_01.y = rotationalLimitMotorY.getBounce();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getBounce();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setBounce(TMP_VEC3_01.x);
                rotationalLimitMotorY.setBounce(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setBounce(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("StopERP");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getStopERP();
            TMP_VEC3_01.y = rotationalLimitMotorY.getStopERP();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getStopERP();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setStopERP(TMP_VEC3_01.x);
                rotationalLimitMotorY.setStopERP(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setStopERP(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("StopCFM");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getStopCFM();
            TMP_VEC3_01.y = rotationalLimitMotorY.getStopCFM();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getStopCFM();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setStopCFM(TMP_VEC3_01.x);
                rotationalLimitMotorY.setStopCFM(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setStopCFM(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("MotorERP");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getMotorERP();
            TMP_VEC3_01.y = rotationalLimitMotorY.getMotorERP();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getMotorERP();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setMotorERP(TMP_VEC3_01.x);
                rotationalLimitMotorY.setMotorERP(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setMotorERP(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("MotorCFM");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getMotorCFM();
            TMP_VEC3_01.y = rotationalLimitMotorY.getMotorCFM();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getMotorCFM();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setMotorCFM(TMP_VEC3_01.x);
                rotationalLimitMotorY.setMotorCFM(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setMotorCFM(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Servo Target");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getServoTarget();
            TMP_VEC3_01.y = rotationalLimitMotorY.getServoTarget();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getServoTarget();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setServoTarget(TMP_VEC3_01.x);
                rotationalLimitMotorY.setServoTarget(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setServoTarget(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Stiffness");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getSpringStiffness();
            TMP_VEC3_01.y = rotationalLimitMotorY.getSpringStiffness();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getSpringStiffness();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setSpringStiffness(TMP_VEC3_01.x);
                rotationalLimitMotorY.setSpringStiffness(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setSpringStiffness(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Spring Damping");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getSpringDamping();
            TMP_VEC3_01.y = rotationalLimitMotorY.getSpringDamping();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getSpringDamping();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setSpringDamping(TMP_VEC3_01.x);
                rotationalLimitMotorY.setSpringDamping(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setSpringDamping(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Equilibrium Point");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getEquilibriumPoint();
            TMP_VEC3_01.y = rotationalLimitMotorY.getEquilibriumPoint();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getEquilibriumPoint();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setEquilibriumPoint(TMP_VEC3_01.x);
                rotationalLimitMotorY.setEquilibriumPoint(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setEquilibriumPoint(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Target Velocity");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getTargetVelocity();
            TMP_VEC3_01.y = rotationalLimitMotorY.getTargetVelocity();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getTargetVelocity();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setTargetVelocity(TMP_VEC3_01.x);
                rotationalLimitMotorY.setTargetVelocity(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setTargetVelocity(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            ImGui.AlignTextToFramePadding();
            ImGui.Text("Max Motor Force");
            ImGui.TableSetColumnIndex(1);
            TMP_VEC3_01.x = rotationalLimitMotorX.getMaxMotorForce();
            TMP_VEC3_01.y = rotationalLimitMotorY.getMaxMotorForce();
            TMP_VEC3_01.z = rotationalLimitMotorZ.getMaxMotorForce();
            if(renderTextView(TMP_VEC3_01)) {
                rotationalLimitMotorX.setMaxMotorForce(TMP_VEC3_01.x);
                rotationalLimitMotorY.setMaxMotorForce(TMP_VEC3_01.y);
                rotationalLimitMotorZ.setMaxMotorForce(TMP_VEC3_01.z);
                ret = true;
            }

            ImGui.EndTable();
        }
        return ret;
    }

    private static boolean renderCheckbox(boolean[] values) {
        boolean ret = false;
        TMP_BOOLEAN_01.setValue(values[0]);
        TMP_BOOLEAN_02.setValue(values[1]);
        TMP_BOOLEAN_03.setValue(values[2]);
        ImGui.PushID(childId);
        if(ImGui.Checkbox("X", TMP_BOOLEAN_01)) {
            boolean value = TMP_BOOLEAN_01.getValue();
            values[0] = value;
            ret = true;
        }
        ImGui.SameLine();
        if(ImGui.Checkbox("Y", TMP_BOOLEAN_02)) {
            boolean value = TMP_BOOLEAN_02.getValue();
            values[1] = value;
            ret = true;
        }
        ImGui.SameLine();
        if(ImGui.Checkbox("Z", TMP_BOOLEAN_03)) {
            boolean value = TMP_BOOLEAN_03.getValue();
            values[2] = value;
            ret = true;
        }

        ImGui.PopID();
        childId++;
        return ret;
    }

    private static boolean renderTextView(Vector3 vecOut) {
        boolean ret = false;
        ImGui.PushID(childId);
        ImGui.PushItemWidth(ImGui.GetContentRegionAvail().getX()/3);
        TMP_FLOAT_01.setValue(vecOut.x);
        if(ImGui.DragFloat("##edX", TMP_FLOAT_01, DRAG_PRECISION, 0, 0, floatXFormat)) {
            vecOut.x = TMP_FLOAT_01.getValue();
            ret = true;
        }
        ImGui.SameLine(0,2);
        TMP_FLOAT_02.setValue(vecOut.y);
        if(ImGui.DragFloat("##edY", TMP_FLOAT_02, DRAG_PRECISION, 0, 0, floatYFormat)) {
            vecOut.y = TMP_FLOAT_02.getValue();
            ret = true;
        }
        ImGui.SameLine(0,2);
        TMP_FLOAT_03.setValue(vecOut.z);
        if(ImGui.DragFloat("##edZ", TMP_FLOAT_03, DRAG_PRECISION, 0, 0, floatZFormat)) {
            vecOut.z = TMP_FLOAT_03.getValue();
            ret = true;
        }
        ImGui.PopItemWidth();

        ImGui.PopID();
        childId++;
        return ret;
    }
}
