# LightBox

![LightBox Game of Life](docs/demo.gif)

Raspberry Pi based Pixel-art style display that cycles through custom apps.

Maybe inspired by [TYDBYT](https://tidbyt.com/?srsltid=AU7gw4WfCfyjbUMqWveqrx8zOHccvySlT2Cnz3E5brFxxSw6OB7yuwQ5)

## Development

To run software using a local web UI, run:

```
./gradlew runLocal
```

### Architecture overview
<p align="center">
  <img src="docs/excalidraw_arch_overview.png" alt="Arch overview" width="500">
</p>

To put it concisely, the RenderLoop is the main entry point that runs on timing defined by FrameClock to pull frames from SceneManager and push them to LedMatrix. SceneManager is responsibile for managing the various Scenes (Apps) and moving between them with Transitions.

### File structure overview
- bmp/ - utils for rendering bitmap images
- core/ - core componenets including the render loop
- render/ - components relevant to rendering onto the LED Matrix or emulator
- scene/ - Scene and Transition core functionality and utils
- scenes/ - implementations for various Scenes and Transitions
- text/ - utils for rendering text using .bdf font files
- tools/ - dev tooling for running and testing various components

## Configuration

This code is built on top of [hzeller's library](https://github.com/hzeller/rpi-rgb-led-matrix) and so many of the suggestions there apply to running this project. Ones I've found to be important are:
- Set isolcpus=3 in the /boot/firmware/cmdline.txt

## Deploying and Running
The Pi needs to be accesible via ssh to deploy. In the gradle script, it should have the alias `lightbox`
```
./gradlew deploy
```

For now, to run you will need to ssh into the pi and run via
```
sudo ~/code/light-box-build/bin/light-box
```
