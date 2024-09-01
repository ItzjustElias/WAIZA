package elias.zoom.client;

import elias.zoom.client.config.ZoomModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
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

import java.util.Objects;

public class ZoomModClient implements ClientModInitializer {

    private static boolean CHECKED_KEYBINDING = false;
    private static boolean currentlyZoomed;
    private static KeyBinding zoomKeyBinding;
    private static boolean originalSmoothCameraEnabled;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static double targetZoomLevel;
    private static double zoomLevel;

    private static boolean keyPressedLastFrame = false;

    @Override
    public void onInitializeClient() {
        // Load configuration
        ZoomModConfig.loadConfig();

        // Register key binding
        zoomKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.zoom"
        ));
        currentlyZoomed = false;
        originalSmoothCameraEnabled = false;

        // Check key binding on title screen
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!CHECKED_KEYBINDING && screen instanceof TitleScreen) {
                checkKeyBinding(zoomKeyBinding);
                CHECKED_KEYBINDING = true;
            }
        });

        // Update zoom on client tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                updateZoom();
                manageSmoothCamera();
            }
        });

        // Register config screen with Mod Menu
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            new ModMenuIntegration(); // Initialize ModMenuIntegration to register the config screen
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
        ToastManager toastManager = mc.getToastManager();
        toastManager.add(toast);
    }

    public static boolean isZooming() {
        return zoomKeyBinding.isPressed();
    }

    public static double getZoomLevel() {
        return zoomLevel;
    }

    public static void zoomIn() {
        if (zoomLevel < ZoomModConfig.maxZoom) {
            targetZoomLevel = Math.min(targetZoomLevel + ZoomModConfig.instantZoomIncrement, ZoomModConfig.maxZoom);
        }
    }

    public static void zoomOut() {
        if (zoomLevel > ZoomModConfig.minZoom) {
            targetZoomLevel = Math.max(targetZoomLevel - ZoomModConfig.instantZoomIncrement, ZoomModConfig.minZoom);
        }
    }

    private static void updateZoom() {
        boolean isPressed = isZooming();

        // Handle zoom level change based on key press state
        if (isPressed && !keyPressedLastFrame) {
            targetZoomLevel = Math.min(targetZoomLevel + ZoomModConfig.zoomIncrement, ZoomModConfig.maxZoom);
            if (ZoomModConfig.enableZoomSound) playZoomSound(); // Play zoom in sound
        } else if (!isPressed && keyPressedLastFrame) {
            // Reset to default zoom level when key is released
            targetZoomLevel = ZoomModConfig.defaultZoomLevel;
        }

        // Apply smoothing effect to the zoom level
        double zoomStep = ZoomModConfig.zoomSmoothing * ZoomModConfig.zoomSpeed; // Apply zoom speed multiplier
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
