# WAIZA (What Am I Looking At)

![https://imgur.com/XBCJqHt](https://i.imgur.com/XBCJqHt.png)

**WAIZA** is a Minecraft mod that enhances your gameplay by providing customizable zoom functionality. It allows players to fine-tune their zoom experience, including zoom levels, speed, smoothing, and optional sound effects.

## Features

- **Customizable Zoom**: Modify zoom levels, speed, and smoothing for a personalized experience.
- **Optional Sound Effects**: Toggle a zoom sound effect for added immersion.
- **Easy Configuration**: Adjust settings directly through the configuration file.

## Configuration Properties

Below are the configuration properties available in the mod, along with their default values:

```properties
default_zoom_level=0.23
enable_zoom_sound=false
instant_zoom_increment=0.1
max_zoom=0.83
min_zoom=0.03
zoom_increment=0.05
zoom_smoothing=0.05
zoom_speed=0.5
```
## Property Descriptions
- **default_zoom_level**: The default zoom level when the zoom key is pressed. (Default: 0.23)
- **enable_zoom_sound**: Toggle the zoom sound effect. Set to true to enable, false to disable. (Default: false)
- **instant_zoom_increment**: The zoom increment applied instantly when zooming in or out. (Default: 0.1)
- **max_zoom**: The maximum zoom level achievable. (Default: 0.83)
- **min_zoom**: The minimum zoom level achievable. (Default: 0.03)
- **zoom_increment**: Controls the zoom level increase or decrease with each zoom action. (Default: 0.05)
- **zoom_smoothing**: Smoothing applied to the zoom transition; higher values mean smoother zooming. (Default: 0.05)
- **zoom_speed**: The speed at which zooming occurs. (Default: 0.5)
  
## Requirements
- **Minecraft**: Compatible with the Fabric mod loader.
- **Fabric API**: The mod requires the [Fabric API](https://modrinth.com/mod/fabric-api) to function correctly. Please ensure you have it installed.
  
## Installation
- Install Fabric Mod Loader: Ensure you have Fabric installed for your Minecraft version.
- Install Fabric API: Download and place the Fabric API .jar file into your mods folder.
- Install WAIZA: Place the WAIZA mod .jar file into your mods folder.
  
### Configure: 
Launch the game once to generate the configuration file, then adjust settings in the ZoomMod/config.properties file as needed.

## Usage
1. Press the assigned zoom key (C by default) to zoom in and out during gameplay.
2. Adjust the zoom settings via the configuration file to tailor the zoom experience to your liking.

## languages supported:
WAIZA supports the following languages:

- English
- Spanish
- Italian
- French
- German
- More to come...
   
## License
This mod is licensed under Creative Commons Zero v1.0 Universal license. You are free to share, copy, and redistribute the material in any medium or format, and to adapt, remix, transform, and build upon the material for any purpose, even commercially, under the following terms:

**Attribution**: You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
