package com.ricedotwho.rsm.command.impl.itemmodifier;

public class ItemNameOverride {
    public String name;
    public boolean enabled;

    public ItemNameOverride(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }
}

