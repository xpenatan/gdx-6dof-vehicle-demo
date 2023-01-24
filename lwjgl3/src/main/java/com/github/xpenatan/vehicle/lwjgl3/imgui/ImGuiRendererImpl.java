package com.github.xpenatan.vehicle.lwjgl3.imgui;

import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.imgui.core.ImDrawData;
import com.github.xpenatan.imgui.core.ImGui;
import com.github.xpenatan.imgui.core.enums.ImGuiConfigFlags;
import com.github.xpenatan.imgui.gdx.ImGuiGdxImpl;
import com.github.xpenatan.imgui.gdx.ImGuiGdxInputMultiplexer;
import com.github.xpenatan.vehicle.Vehicle;
import com.github.xpenatan.vehicle.imgui.ImGuiRenderer;

public class ImGuiRendererImpl implements ImGuiRenderer {

    private ImGuiGdxImpl impl;
    private ImGuiGdxInputMultiplexer input;

    @Override
    public void init() {
        ImGui.init();

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
        ImGui6DofSpring2Constraint.render("Front Left Wheel", vehicle.frontLeftConstraint);
        ImGui6DofSpring2Constraint.render("Front Right Wheel", vehicle.frontRightConstraint);
        ImGui6DofSpring2Constraint.render("Back Left Wheel", vehicle.backLeftConstraint);
        ImGui6DofSpring2Constraint.render("Back Right Wheel", vehicle.backRightConstraint);
    }

    @Override
    public void dispose() {
        impl.dispose();
        ImGui.dispose();
    }
}
