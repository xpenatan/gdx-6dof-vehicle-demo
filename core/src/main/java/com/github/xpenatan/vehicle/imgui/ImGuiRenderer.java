package com.github.xpenatan.vehicle.imgui;

import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.vehicle.Vehicle;

public interface ImGuiRenderer {
    public void init();
    void begin();
    void end();
    InputMultiplexer getInput();
    public void renderVehicle(Vehicle vehicle);
}
