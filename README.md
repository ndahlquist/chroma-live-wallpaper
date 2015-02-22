# chroma-live-wallpaper
[![Circle CI](https://circleci.com/gh/ndahlquist/chroma-live-wallpaper.svg?style=svg&circle-token=a6e4c4b72f8ed79762ee8cabbc16231138201733)](https://circleci.com/gh/ndahlquist/chroma-live-wallpaper)

This is an Android live wallpaper inspired by the iconic "Chroma" rainbow wallpaper introduced in Ice Cream Sandwich.
- High resolution rendering will look great on even the largest screens.
- Rapid and energy-efficient GPU drawing, made possible by OpenGL ES 2.0. Drawing handled entirely in GLSL shaders, ensuring good performance.

![Screeshot](https://github.com/ndahlquist/chroma/blob/master/screenshot.jpg)

### Quickstart
- In the top-level of the repo, run the command `./gradlew installDebug`. This will download dependencies, build the APK and install it on a connected device.
- Open wallpaper settings on your device and choose 'Chroma'.

The compiled binary is also available from the [Google Play Store](https://play.google.com/store/apps/details?id=edu.stanford.nicd.chroma).
