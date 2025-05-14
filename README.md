# Drone Show Prototype

## Overview

**Drone Show Prototype** is a Java Swing application that simulates drone light formations by animating a swarm of dots transitioning between shapes derived from image masks. It samples dark pixels from a series of images, creates `Dot` objects, and smoothly animates their positions and colors to mimic choreographed aerial displays.

## Features

* **Image Loading & Downscaling**
  Loads a sequence of X images (`frame1.jpg`–`frameX.jpg`), scales them to a fixed panel size for consistent sampling.
* **Point Sampling & Equalization**
  Extracts points based on a brightness threshold, uses adaptive spacing per image, shuffles and truncates lists to ensure equal dot counts across formations.
* **Dot Animation**
  Animates each `Dot` with:

  * Randomized start positions and delays
  * Cosine‐based easing over a fixed duration
  * Linear interpolation of color and opacity
* **Playback Control**
  A **Play** button advances to the next formation, cycling through all loaded images.
* **Customizable Parameters**
  Configure FPS, transition duration, spacing, brightness threshold, image_count, specific spacing constants, and dot radius via constants in `DronePanel.java`.

## Prerequisites

* **Java SE 8 or later** (Swing library included)
* **Image Files** Place `frame1.jpg` through `frameX.jpg` in an `images/` directory at the project root.

## Installation & Running

1. **Compile** the source files:

   ```bash
   javac Dot.java DronePanel.java DroneShowSetup.java
   ```

2. **Run** the prototype:

   ```bash
   java DroneShowSetup
   ```

## Project Structure

```text
DroneShowPrototype/         # Project root
├── Dot.java                # Represents a single animated dot
├── DronePanel.java         # Core animation panel (sampling & rendering)
├── DroneShowSetup.java     # Application entry point (JFrame and Play button)
├── images/                 # Input image masks (frame1.jpg … frame5.jpg)
└── README.md               # Project documentation
```

## Customization

* **Input Images**: Replace or add image files in `images/` directory (ensure naming matches `frame%d.jpg`).
* **Animation Settings**: Edit constants in `DronePanel.java`:

  * `IMAGE_COUNT` for the number of images in `images/`
  * `WIDTH`, `HEIGHT` for panel dimensions
  * `FPS`, `TRANSITION_SECONDS`, `MAX_DELAY` for timing
  * `DEFAULT_SPACING`, `SOCCER_SPACING` to adjust point density
  * `BRIGHTNESS_THRESHOLD` to control mask sensitivity
  * `DOT_RADIUS` for dot size
* **Formation Colors**: Update `FORMATION_COLORS` array in `DronePanel.java` to change dot colors per frame.

## Troubleshooting

* **Missing Images**: Ensure all `frameX.jpg` files exist in `images/`. Check console errors for missing file paths.
* **Slow Performance**: Reduce `FPS`, increase `spacing`, or lower `IMAGE_COUNT`.
* **Rendering Artifacts**: Verify that images match the panel’s dimensions or correct aspect ratio when sampling.

## Future Enhancements

* Add keyboard shortcuts or GUI controls for play/pause and speed adjustment.
* Update image logic to include images with different contrast, brightness, etc.
* Support dynamic loading of an arbitrary number of frames.
* Export particle states to video or image sequences.
* Integrate real drone hardware control via networking APIs.

## License

This project is open-source under the MIT License. See `LICENSE` for details.
