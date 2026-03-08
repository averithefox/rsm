package com.ricedotwho.rsm.event.impl.client;

import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.event.Event;
import lombok.Getter;
import net.minecraft.world.entity.player.Input;

import java.util.function.Consumer;

public class InputPollEvent extends Event {
    @Getter
    private final Input clientInput;
    @Getter
    private final MutableInput input;

    public InputPollEvent(Input clientInput, MutableInput input) {
        this.clientInput = clientInput;
        this.input = input;
    }

}
