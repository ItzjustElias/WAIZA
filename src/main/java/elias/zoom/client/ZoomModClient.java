package elias.zoom.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class ZoomModClient implements ClientModInitializer {

    private static final String CONFIG_DIR = new File("").getAbsolutePath() + "/WAIZA-Config";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";

    private static boolean CHECKED_KEYBINDING = false;
    private static boolean currentlyZoomed;
    private static KeyBinding zoomKeyBinding;
    private static boolean originalSmoothCameraEnabled;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static double ZOOM_INCREMENT;
    private static double MAX_ZOOM;
    private static double MIN_ZOOM;
    private static double ZOOM_SMOOTHING;
    private static double INSTANT_ZOOM_INCREMENT;
    private static double DEFAULT_ZOOM_LEVEL;
    private static double ZOOM_SPEED;

    private static boolean ENABLE_ZOOM_SOUND;

    private static double targetZoomLevel;
    private static double zoomLevel;

    private static boolean keyPressedLastFrame = false;

    @Override
    public void onInitializeClient() {
        createConfigIfNotExist();
        loadConfig();

        zoomKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.zoom"
        ));
        currentlyZoomed = false;
        originalSmoothCameraEnabled = false;

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!CHECKED_KEYBINDING && screen instanceof TitleScreen) {
                checkKeyBinding(zoomKeyBinding);
                CHECKED_KEYBINDING = true;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                updateZoom();
                manageSmoothCamera();
            }
        });
    }

    private void createConfigIfNotExist() {
        File configDir = new File(CONFIG_DIR);
        File configFile = new File(CONFIG_FILE);

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Properties props = new Properties();

                // Set default values
                props.setProperty("zoom_increment", "0.05");
                props.setProperty("max_zoom", "0.83");
                props.setProperty("min_zoom", "0.03");
                props.setProperty("zoom_smoothing", "0.05");
                props.setProperty("instant_zoom_increment", "0.1");
                props.setProperty("default_zoom_level", "0.23");
                props.setProperty("zoom_speed", "0.5"); // Default zoom speed
                props.setProperty("enable_zoom_sound", "false"); // Default zoom sound disable

                FileWriter writer = new FileWriter(configFile);
                props.store(writer, "ZoomMod Configuration");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig() {
        Properties props = new Properties();

        try {
            FileReader reader = new FileReader(CONFIG_FILE);
            props.load(reader);
            reader.close();

            // Load properties
            ZOOM_INCREMENT = Double.parseDouble(props.getProperty("zoom_increment", "0.05"));
            MAX_ZOOM = Double.parseDouble(props.getProperty("max_zoom", "0.83"));
            MIN_ZOOM = Double.parseDouble(props.getProperty("min_zoom", "0.03"));
            ZOOM_SMOOTHING = Double.parseDouble(props.getProperty("zoom_smoothing", "0.05"));
            INSTANT_ZOOM_INCREMENT = Double.parseDouble(props.getProperty("instant_zoom_increment", "0.1"));
            DEFAULT_ZOOM_LEVEL = Double.parseDouble(props.getProperty("default_zoom_level", "0.23"));
            ZOOM_SPEED = Double.parseDouble(props.getProperty("zoom_speed", "0.5")); // Load zoom speed
            ENABLE_ZOOM_SOUND = Boolean.parseBoolean(props.getProperty("enable_zoom_sound", "true")); // Load zoom sound setting

            targetZoomLevel = DEFAULT_ZOOM_LEVEL;
            zoomLevel = targetZoomLevel;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkKeyBinding(KeyBinding keyBinding) {
        int count = 0;
        for (KeyBinding otherKeyBinding : mc.options.allKeys) {
            if (Objects.equals(otherKeyBinding.getBoundKeyTranslationKey(), keyBinding.getBoundKeyTranslationKey())) {
                count++;
            }
            if (count > 1) {
                MutableText title = Text.translatable("keybind.title.zoom");
                MutableText desc = Text.translatable("keybind.desc.zoom");
                displayToast(title, desc);
            }
        }
    }

    public static void displayToast(MutableText title, MutableText description) {
        SystemToast toast = new SystemToast(
                SystemToast.Type.LOW_DISK_SPACE,
                Text.of(title),
                Text.of(description)
        );
        MinecraftClient client = MinecraftClient.getInstance();
        ToastManager toastManager = client.getToastManager();
        toastManager.add(toast);
    }

    public static boolean isZooming() {
        return zoomKeyBinding.isPressed();
    }

    public static double getZoomLevel() {
        return zoomLevel;
    }

    public static void zoomIn() {
        if (zoomLevel < MAX_ZOOM) {
            targetZoomLevel = Math.min(targetZoomLevel + INSTANT_ZOOM_INCREMENT, MAX_ZOOM);
        }
    }

    public static void zoomOut() {
        if (zoomLevel > MIN_ZOOM) {
            targetZoomLevel = Math.max(targetZoomLevel - INSTANT_ZOOM_INCREMENT, MIN_ZOOM);
        }
    }

    private static void updateZoom() {
        boolean isPressed = isZooming();

        // Handle zoom level change based on key press state
        if (isPressed && !keyPressedLastFrame) {
            targetZoomLevel = Math.min(targetZoomLevel + ZOOM_INCREMENT, MAX_ZOOM);
            if (ENABLE_ZOOM_SOUND) playZoomSound(); // Play zoom in sound
        } else if (!isPressed && keyPressedLastFrame) {
            // Reset to default zoom level when key is released
            targetZoomLevel = DEFAULT_ZOOM_LEVEL;
        }

        // Apply smoothing effect to the zoom level
        double zoomStep = ZOOM_SMOOTHING * ZOOM_SPEED; // Apply zoom speed multiplier
        if (zoomLevel < targetZoomLevel) {
            zoomLevel = Math.min(zoomLevel + zoomStep, targetZoomLevel);
        } else if (zoomLevel > targetZoomLevel) {
            zoomLevel = Math.max(zoomLevel - zoomStep, targetZoomLevel);
        }

        keyPressedLastFrame = isPressed;
    }

    private static void playZoomSound() {
        if (mc.player != null && mc.world != null) {
            mc.world.playSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),  // The player's current position
                    SoundEvents.UI_TOAST_OUT,  // The sound event
                    SoundCategory.PLAYERS,     // The sound category
                    0.5F,                      // Volume
                    1.0F,                      // Pitch
                    false                      // Whether the sound is distant
            );
        }
    }


    public static void manageSmoothCamera() {
        if (zoomStarting()) {
            zoomStarted();
            enableSmoothCamera();
        }

        if (zoomStopping()) {
            zoomStopped();
            resetSmoothCamera();
        }
    }

    private static boolean isSmoothCamera() {
        return mc.options.smoothCameraEnabled;
    }

    private static void enableSmoothCamera() {
        mc.options.smoothCameraEnabled = true;
    }

    private static void disableSmoothCamera() {
        mc.options.smoothCameraEnabled = false;
    }

    private static boolean zoomStarting() {
        return isZooming() && !currentlyZoomed;
    }

    private static boolean zoomStopping() {
        return !isZooming() && currentlyZoomed;
    }

    private static void zoomStarted() {
        originalSmoothCameraEnabled = isSmoothCamera();
        currentlyZoomed = true;
    }

    private static void zoomStopped() {
        currentlyZoomed = false;
    }

    private static void resetSmoothCamera() {
        if (originalSmoothCameraEnabled) {
            enableSmoothCamera();
        } else {
            disableSmoothCamera();
        }
    }
}
