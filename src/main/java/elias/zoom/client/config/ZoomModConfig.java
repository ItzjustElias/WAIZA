package elias.zoom.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ZoomModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final File CONFIG_DIR = new File(CLIENT.runDirectory, "config");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "zoom_mod_config.json");
    private static final File DEFAULT_CONFIG_FILE = new File(CONFIG_DIR, "default_zoom_mod_config.json");

    public static double zoomIncrement = 0.05;
    public static double maxZoom = 0.83;
    public static double minZoom = 0.03;
    public static double zoomSmoothing = 0.05;
    public static double instantZoomIncrement = 0.1;
    public static double defaultZoomLevel = 0.23;
    public static double zoomSpeed = 0.5;
    public static boolean enableZoomSound = false;

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveConfig(); // Save default configuration if the file does not exist
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                zoomIncrement = jsonObject.get("zoomIncrement").getAsDouble();
                maxZoom = jsonObject.get("maxZoom").getAsDouble();
                minZoom = jsonObject.get("minZoom").getAsDouble();
                zoomSmoothing = jsonObject.get("zoomSmoothing").getAsDouble();
                instantZoomIncrement = jsonObject.get("instantZoomIncrement").getAsDouble();
                defaultZoomLevel = jsonObject.get("defaultZoomLevel").getAsDouble();
                zoomSpeed = jsonObject.get("zoomSpeed").getAsDouble();
                enableZoomSound = jsonObject.get("enableZoomSound").getAsBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("zoomIncrement", zoomIncrement);
        jsonObject.addProperty("maxZoom", maxZoom);
        jsonObject.addProperty("minZoom", minZoom);
        jsonObject.addProperty("zoomSmoothing", zoomSmoothing);
        jsonObject.addProperty("instantZoomIncrement", instantZoomIncrement);
        jsonObject.addProperty("defaultZoomLevel", defaultZoomLevel);
        jsonObject.addProperty("zoomSpeed", zoomSpeed);
        jsonObject.addProperty("enableZoomSound", enableZoomSound);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetToDefaults() {
        if (DEFAULT_CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(DEFAULT_CONFIG_FILE)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                zoomIncrement = jsonObject.get("zoomIncrement").getAsDouble();
                maxZoom = jsonObject.get("maxZoom").getAsDouble();
                minZoom = jsonObject.get("minZoom").getAsDouble();
                zoomSmoothing = jsonObject.get("zoomSmoothing").getAsDouble();
                instantZoomIncrement = jsonObject.get("instantZoomIncrement").getAsDouble();
                defaultZoomLevel = jsonObject.get("defaultZoomLevel").getAsDouble();
                zoomSpeed = jsonObject.get("zoomSpeed").getAsDouble();
                enableZoomSound = jsonObject.get("enableZoomSound").getAsBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If the default config file doesn't exist, set default values manually
            zoomIncrement = 0.05;
            maxZoom = 0.83;
            minZoom = 0.03;
            zoomSmoothing = 0.05;
            instantZoomIncrement = 0.1;
            defaultZoomLevel = 0.23;
            zoomSpeed = 0.5;
            enableZoomSound = false;
        }
        saveConfig(); // Save the reset configuration
    }

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Zoom Mod Configuration"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Zoom Options"))
                        .tooltip(Text.of("Configure your zoom settings here."))
                        .group(OptionGroup.createBuilder()
                                .description(OptionDescription.of(Text.of("Adjust your zoom preferences.")))
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Zoom Increment"))
                                        .description(OptionDescription.of(Text.of("Controls the zoom level increase or decrease with each zoom action. \n\nDefault value: 0.05")))
                                        .binding(zoomIncrement, () -> zoomIncrement, newVal -> zoomIncrement = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Max Zoom"))
                                        .description(OptionDescription.of(Text.of("The maximum zoom level achievable. \n\nDefault value: 0.83")))
                                        .binding(maxZoom, () -> maxZoom, newVal -> maxZoom = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Min Zoom"))
                                        .description(OptionDescription.of(Text.of("The minimum zoom level achievable. \n\nDefault value: 0.03")))
                                        .binding(minZoom, () -> minZoom, newVal -> minZoom = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Zoom Smoothing"))
                                        .description(OptionDescription.of(Text.of("Smoothing applied to the zoom transition; higher values mean smoother zooming. \n\nDefault value: 0.05")))
                                        .binding(zoomSmoothing, () -> zoomSmoothing, newVal -> zoomSmoothing = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Instant Zoom Increment"))
                                        .description(OptionDescription.of(Text.of("The zoom increment applied instantly when zooming in or out. \n\nDefault value: 0.1")))
                                        .binding(instantZoomIncrement, () -> instantZoomIncrement, newVal -> instantZoomIncrement = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Default Zoom Level"))
                                        .description(OptionDescription.of(Text.of("The default zoom level when the zoom key is pressed. \n\nDefault value: 0.23")))
                                        .binding(defaultZoomLevel, () -> defaultZoomLevel, newVal -> defaultZoomLevel = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Zoom Speed"))
                                        .description(OptionDescription.of(Text.of("The speed at which zooming occurs. \n\nDefault value: 0.5")))
                                        .binding(zoomSpeed, () -> zoomSpeed, newVal -> zoomSpeed = newVal)
                                        .controller(DoubleFieldControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Enable Zoom Sound"))
                                        .description(OptionDescription.of(Text.of("Toggle the zoom sound effect. Set to true to enable, false to disable. \n\nDefault value: false")))
                                        .binding(enableZoomSound, () -> enableZoomSound, newVal -> enableZoomSound = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}

