package com.ricedotwho.rsm.ui.itemmodifier;

import com.ricedotwho.rsm.command.impl.itemmodifier.ItemModifierStore;
import com.ricedotwho.rsm.command.impl.itemmodifier.ItemNameOverride;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.ui.clickgui.api.FatalityColours;
import com.ricedotwho.rsm.ui.clickgui.impl.module.settings.impl.TextInput;
import com.ricedotwho.rsm.utils.render.render2d.NVGUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class ItemModifierRow {
    private static final float WIDTH = 818f;
    private static final float HEIGHT = 30f;
    private static final float GAP = 5f;
    private static final float BOX_HEIGHT = 20f;
    private static final float UUID_WIDTH = 300f;
    private static final float NAME_WIDTH = 365f;
    private static final float BUTTON_WIDTH = 70f;
    private static final float DELETE_WIDTH = 63f;

    private static ItemModifierRow selected = null;

    private final String uuid;
    private final ItemNameOverride value;
    private final TextInput nameInput;
    private boolean writingName = false;

    public ItemModifierRow(String uuid, ItemNameOverride value) {
        this.uuid = uuid;
        this.value = value;
        this.nameInput = new TextInput(value.name, 12, false, 128);
    }

    public String getUuid() {
        return uuid;
    }

    public boolean click(double mouseX, double mouseY, int button) {
        float nameX = GAP + UUID_WIDTH + GAP;
        float enabledX = nameX + NAME_WIDTH + GAP;
        float deleteX = enabledX + BUTTON_WIDTH + GAP;

        if (NVGUtils.isHovering(mouseX, mouseY, nameX, GAP, NAME_WIDTH, BOX_HEIGHT)) {
            selected = this;
            writingName = true;
            nameInput.click((float) (mouseX - (nameX + 5f)), button);
        } else if (writingName && selected == this) {
            commitName();
            writingName = false;
            selected = null;
        }

        if (button == 0 && NVGUtils.isHovering(mouseX, mouseY, enabledX, GAP, BUTTON_WIDTH, BOX_HEIGHT)) {
            value.toggle();
            ItemModifierStore.save();
        }

        if (button == 0 && NVGUtils.isHovering(mouseX, mouseY, deleteX, GAP, DELETE_WIDTH, BOX_HEIGHT)) {
            ItemModifierStore.remove(uuid);
            return true;
        }

        return false;
    }

    public boolean charTyped(char typedChar) {
        return writingName && nameInput.charTyped(typedChar);
    }

    public boolean keyTyped(KeyEvent event) {
        if (!writingName) {
            return false;
        }

        int key = event.key();
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            nameInput.setValue(value.name);
            writingName = false;
            selected = null;
            return true;
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            commitName();
            writingName = false;
            selected = null;
            return true;
        }

        return nameInput.keyTyped(event);
    }

    public void render(GuiGraphics gfx, float x, float y, double mouseX, double mouseY) {
        NVGUtils.drawOutlineRect(x, y, WIDTH, HEIGHT, 1f, FatalityColours.GROUP_OUTLINE);
        NVGUtils.drawRect(x, y, WIDTH, HEIGHT, FatalityColours.GROUP_FILL);

        float uuidX = x + GAP;
        float nameX = uuidX + UUID_WIDTH + GAP;
        float enabledX = nameX + NAME_WIDTH + GAP;
        float deleteX = enabledX + BUTTON_WIDTH + GAP;

        NVGUtils.drawRect(uuidX, y + GAP, UUID_WIDTH, BOX_HEIGHT, new Colour(40, 40, 40));
        NVGUtils.drawText(uuid, uuidX + 5f, y + HEIGHT / 2f - 2f, 11, FatalityColours.TEXT, NVGUtils.JOSEFIN);

        Colour nameColour = inputColour(writingName, NVGUtils.isHovering(mouseX, mouseY, nameX, y + GAP, NAME_WIDTH, BOX_HEIGHT));
        NVGUtils.drawRect(nameX, y + GAP, NAME_WIDTH, BOX_HEIGHT, nameColour);
        nameInput.render(nameX + 5f, y + HEIGHT / 2f - 4f, writingName);

        boolean enabledHovered = NVGUtils.isHovering(mouseX, mouseY, enabledX, y + GAP, BUTTON_WIDTH, BOX_HEIGHT);
        Colour enabledColour = value.enabled
                ? (enabledHovered ? FatalityColours.SELECTED.darker() : FatalityColours.SELECTED)
                : (enabledHovered ? FatalityColours.GROUP_OUTLINE.brighter() : FatalityColours.GROUP_OUTLINE);

        NVGUtils.drawRect(enabledX, y + GAP, BUTTON_WIDTH, BOX_HEIGHT, 5f, enabledColour);
        String enabledText = value.enabled ? "On" : "Off";
        NVGUtils.drawText(enabledText, enabledX + (BUTTON_WIDTH - NVGUtils.getTextWidth(enabledText, 12, NVGUtils.JOSEFIN)) / 2f,
                y + GAP + NVGUtils.getTextHeight(12, NVGUtils.JOSEFIN) / 2f, 12, FatalityColours.TEXT, NVGUtils.JOSEFIN);

        boolean deleteHovered = NVGUtils.isHovering(mouseX, mouseY, deleteX, y + GAP, DELETE_WIDTH, BOX_HEIGHT);
        NVGUtils.drawRect(deleteX, y + GAP, DELETE_WIDTH, BOX_HEIGHT, 5f,
                deleteHovered ? FatalityColours.SELECTED.brighter() : FatalityColours.SELECTED);
        String deleteText = "Delete";
        NVGUtils.drawText(deleteText, deleteX + (DELETE_WIDTH - NVGUtils.getTextWidth(deleteText, 12, NVGUtils.JOSEFIN)) / 2f,
                y + GAP + NVGUtils.getTextHeight(12, NVGUtils.JOSEFIN) / 2f, 12, FatalityColours.TEXT, NVGUtils.JOSEFIN);
    }

    public void commitPendingEdits() {
        if (!writingName) {
            return;
        }

        commitName();
        writingName = false;
        if (selected == this) {
            selected = null;
        }
    }

    private void commitName() {
        String nextName = nameInput.getValue().trim();
        if (nextName.isBlank()) {
            nameInput.setValue(value.name);
            return;
        }

        if (!nextName.equals(value.name)) {
            value.name = nextName;
            ItemModifierStore.save();
        }
    }

    private Colour inputColour(boolean writing, boolean hovering) {
        if (writing) {
            return new Colour(60, 60, 60);
        }
        if (hovering) {
            return new Colour(50, 50, 50);
        }
        return new Colour(40, 40, 40);
    }
}

