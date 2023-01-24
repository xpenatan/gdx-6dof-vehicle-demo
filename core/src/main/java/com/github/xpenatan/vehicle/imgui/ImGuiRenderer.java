package com.github.xpenatan.vehicle.imgui;

import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.vehicle.Vehicle;

public interface ImGuiRenderer {
    void init();
    void begin();
    void end();
    InputMultiplexer getInput();
    void renderVehicle(Vehicle vehicle);
    void dispose();
}
