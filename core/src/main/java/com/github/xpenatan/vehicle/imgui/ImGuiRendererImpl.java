package com.github.xpenatan.vehicle.imgui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.imgui.core.ImDrawData;
import com.github.xpenatan.imgui.core.ImGui;
import com.github.xpenatan.imgui.core.ImGuiBoolean;
import com.github.xpenatan.imgui.core.ImGuiFloat;
import com.github.xpenatan.imgui.core.enums.ImGuiConfigFlags;
import com.github.xpenatan.imgui.gdx.ImGuiGdxImpl;
import com.github.xpenatan.imgui.gdx.ImGuiGdxInputMultiplexer;
import com.github.xpenatan.vehicle.BulletWorld;
import com.github.xpenatan.vehicle.Vehicle;
import com.github.xpenatan.vehicle.imgui.ImGuiRenderer;

public class ImGuiRendererImpl implements ImGuiRenderer {

    private ImGuiGdxImpl impl;
    private ImGuiGdxInputMultiplexer input;

    private ImGuiBoolean frontLeftWheel;
    private ImGuiBoolean frontRightWheel;
    private ImGuiBoolean backLeftWheel;
    private ImGuiBoolean backRightWheel;

    private ImGuiBoolean TEMP_BOOL;
    private ImGuiFloat TEMP;

    @Override
    public void init() {
        ImGui.init();

        frontLeftWheel = new ImGuiBoolean();
        frontRightWheel = new ImGuiBoolean();
        backLeftWheel = new ImGuiBoolean();
        backRightWheel = new ImGuiBoolean();
        TEMP_BOOL = new ImGuiBoolean();
        TEMP = new ImGuiFloat();

        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            // Not possible to have ini filename with webgl
            ImGui.GetIO().setIniFilename(null);
        }

        ImGui.GetIO().SetConfigFlags(ImGuiConfigFlags.DockingEnable);
        ImGui.GetIO().SetDockingFlags(false, true, false, false);

        input = new ImGuiGdxInputMultiplexer();
        impl = new ImGuiGdxImpl();
    }

    @Override
    public void begin() {
        impl.update();
    }

    @Override
    public void end() {
        ImGui.Render();
        ImDrawData drawData = ImGui.GetDrawData();
        impl.render(drawData);
    }

    @Override
    public InputMultiplexer getInput() {
        return input;
    }

    @Override
    public void renderVehicle(Vehicle vehicle) {

        ImGui.Begin("Config");

        if(ImGui.Button("Reset Position")) {
            vehicle.resetPosition();
        }

        TEMP_BOOL.setValue(BulletWorld.DEBUG);
        if(ImGui.Checkbox("Debug", TEMP_BOOL)) {
            BulletWorld.DEBUG = TEMP_BOOL.getValue();
        }

        ImGui.Checkbox("F. Left Wheel", frontLeftWheel);
        ImGui.Checkbox("F. Right Wheel", frontRightWheel);
        ImGui.Checkbox("B. Left Wheel", backLeftWheel);
        ImGui.Checkbox("B. Right Wheel", backRightWheel);

        TEMP.setValue(vehicle.wheelFriction);
        if(ImGui.DragFloat("Wheel Friction", TEMP, 0.01f)) {
            vehicle.setWheelFriction(TEMP.getValue());
        }
        TEMP.setValue(vehicle.enginePower);
        if(ImGui.DragFloat("Engine Power", TEMP, 0.1f)) {
            vehicle.enginePower = TEMP.getValue();
        }
        TEMP.setValue(vehicle.maxEnginePower);
        if(ImGui.DragFloat("Max Engine Power", TEMP, 0.1f)) {
            vehicle.setMaxEnginePower(TEMP.getValue());
        }
        TEMP.setValue(vehicle.maxTurnAngleDeg);
        if(ImGui.DragFloat("Max Turn Angle", TEMP, 0.1f)) {
            vehicle.maxTurnAngleDeg = TEMP.getValue();
        }
        ImGui.End();

        if(frontLeftWheel.getValue()) {
            ImGui6DofSpring2Constraint.render("Front Left Wheel", vehicle.frontLeftConstraint);
        }
        if(frontRightWheel.getValue()) {
            ImGui6DofSpring2Constraint.render("Front Right Wheel", vehicle.frontRightConstraint);
        }
        if(backLeftWheel.getValue()) {
            ImGui6DofSpring2Constraint.render("Back Left Wheel", vehicle.backLeftConstraint);
        }
        if(backRightWheel.getValue()) {
            ImGui6DofSpring2Constraint.render("Back Right Wheel", vehicle.backRightConstraint);
        }
    }

    @Override
    public void dispose() {
        impl.dispose();
        ImGui.dispose();
    }
}
