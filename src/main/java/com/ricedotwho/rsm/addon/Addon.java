package com.ricedotwho.rsm.addon;

import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.module.Module;

import java.util.List;

public interface Addon {

    void onInitialize();

    void onUnload();

    List<Module> getModules();

    List<ModComponent> getComponents();

    List<Command> getCommands();
}
