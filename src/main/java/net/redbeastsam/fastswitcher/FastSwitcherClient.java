package net.redbeastsam.fastswitcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import net.redbeastsam.fastswitcher.FastSwitcherScreen;

public class FastSwitcherClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static KeyBinding settingsKey;

    private static boolean toggled = false;
    private static int slot1 = -1;
    private static int slot2 = -1;
    private static boolean flip = false;

    // This is the file where we will save our settings.
    private static final File settingsFile = new File(MinecraftClient.getInstance().runDirectory, "fastswitcher_slots.cfg");

    @Override
    public void onInitializeClient() {
        // Register keybinds (configurable in Controls menu)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fastswitcher.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, // default: R
                "category.fastswitcher"
        ));

        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fastswitcher.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P, // default: P
                "category.fastswitcher"
        ));

        // Load the slots when the mod initializes
        loadSlots();

        // Tick loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Check toggle key press
            while (toggleKey.wasPressed()) {
                toggled = !toggled;
            }

            // Open the settings GUI when the key is pressed
            while (settingsKey.wasPressed()) {
                if (client != null) {
                    client.setScreen(new FastSwitcherScreen());
                }
            }

            // If toggled, rapidly swap between slots
            if (toggled && slot1 != -1 && slot2 != -1) {
                client.player.getInventory().selectedSlot = flip ? slot1 : slot2;
                flip = !flip;
            }
        });
    }

    // Allow other classes to set slots
    public static void setSlots(int s1, int s2) {
        slot1 = s1;
        slot2 = s2;
        saveSlots(slot1, slot2);
    }

    /**
     * Gets the index of the first slot.
     *
     * @return The index of slot 1.
     */
    public static int getSlot1() {
        return slot1;
    }

    /**
     * Gets the index of the second slot.
     *
     * @return The index of slot 2.
     */
    public static int getSlot2() {
        return slot2;
    }

    /**
     * Loads the slot numbers from the file.
     */
    private static void loadSlots() {
        if (settingsFile.exists()) {
            try {
                Scanner reader = new Scanner(settingsFile);
                if (reader.hasNextLine()) {
                    slot1 = Integer.parseInt(reader.nextLine());
                    if (reader.hasNextLine()) {
                        slot2 = Integer.parseInt(reader.nextLine());
                        System.out.println("FastSwitcher settings loaded.");
                    }
                }
                reader.close();
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                System.err.println("Failed to load FastSwitcher settings!");
            }
        }
    }

    /**
     * Saves the current slot numbers to a file.
     *
     * @param s1 The first slot index.
     * @param s2 The second slot index.
     */
    private static void saveSlots(int s1, int s2) {
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }
            FileWriter writer = new FileWriter(settingsFile);
            writer.write(s1 + "\n");
            writer.write(s2 + "\n");
            writer.close();
            System.out.println("FastSwitcher settings saved.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save FastSwitcher settings!");
        }
    }
}
