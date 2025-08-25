package net.redbeastsam.fastswitcher;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class FastSwitcherScreen extends Screen {

    private TextFieldWidget slot1Field;
    private TextFieldWidget slot2Field;

    protected FastSwitcherScreen() {
        super(Text.of("Fast Switcher Settings"));
    }

    @Override
    protected void init() {
        // Calculate the center point for the GUI.
        int guiWidth = 220; // total width of the GUI elements (90 + 5 + 100 + 5 + 20)
        int x = (this.width - guiWidth) / 2;
        int y = this.height / 2 - 30;

        // Create and configure the text field for Slot 1
        this.slot1Field = new TextFieldWidget(this.textRenderer, x + 5, y, 90, 20, Text.of("Slot 1 Field"));
        this.slot1Field.setText(String.valueOf(FastSwitcherClient.getSlot1() + 1));
        this.addDrawableChild(this.slot1Field);

        // Create and configure the text field for Slot 2
        this.slot2Field = new TextFieldWidget(this.textRenderer, x + 5, y + 30, 90, 20, Text.of("Slot 2 Field"));
        this.slot2Field.setText(String.valueOf(FastSwitcherClient.getSlot2() + 1));
        this.addDrawableChild(this.slot2Field);

        // Button to set Slot 1
        this.addDrawableChild(ButtonWidget.builder(Text.of("Set Slot 1"), b -> {
            try {
                int slotNumber = Integer.parseInt(slot1Field.getText());
                if (slotNumber >= 1 && slotNumber <= 9) {
                    FastSwitcherClient.setSlots(slotNumber - 1, FastSwitcherClient.getSlot2());
                    this.client.player.sendMessage(Text.of("Slot 1 set to: " + slotNumber), false);
                } else {
                    this.client.player.sendMessage(Text.of("Invalid slot number! Please enter a number from 1 to 9."), false);
                }
            } catch (NumberFormatException e) {
                this.client.player.sendMessage(Text.of("Invalid input! Please enter a number."), false);
            }
        }).dimensions(x + 110, y, 100, 20).build());

        // Button to set Slot 2
        this.addDrawableChild(ButtonWidget.builder(Text.of("Set Slot 2"), b -> {
            try {
                int slotNumber = Integer.parseInt(slot2Field.getText());
                if (slotNumber >= 1 && slotNumber <= 9) {
                    FastSwitcherClient.setSlots(FastSwitcherClient.getSlot1(), slotNumber - 1);
                    this.client.player.sendMessage(Text.of("Slot 2 set to: " + slotNumber), false);
                } else {
                    this.client.player.sendMessage(Text.of("Invalid slot number! Please enter a number from 1 to 9."), false);
                }
            } catch (NumberFormatException e) {
                this.client.player.sendMessage(Text.of("Invalid input! Please enter a number."), false);
            }
        }).dimensions(x + 110, y + 30, 100, 20).build());

        // Button to close the screen
        this.addDrawableChild(ButtonWidget.builder(Text.of("Done"), b -> this.close()).dimensions(x + 5, y + 60, 205, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        // This will render all the buttons and text fields automatically since they are added as drawable children.
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackground(DrawContext context) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
