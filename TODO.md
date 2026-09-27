- README gif
- Fix brightness and ghosting
- Safe power supply
    - Standoffs, fuse, heat shirk, secure with glue gun or command strips. Electrical tape between hat and pi. Watch exposed wires.
- handle API failures better
- Proofread
- Optimization: load fonts once at startup
- Optimization: draw directly onto the waiting buffer?
- Optimization: we don't copy into offscreen buffer until t_frame has passed
- SURF SCENE (need to figure out cloudflare issues)
    - try later
    - use postman CLI?
    - proxy to a cloud function? Running chromium, if needed?
- Adjust brightness while running
    - Daemon reads from settings file and adjusts
- Optimization: use led_canvas_set_pixels instead of set_pixel
- Optimization: use limit_refresh_rate_hz
- Fix rPi wifi disconnects
- Other kernel optimizations
- Use headless

From Claude:
Logging: replace the 20+ System.out/printStackTrace calls with SLF4J plus a simple backend.
Error handling: RenderLoop calls System.exit(1) on any exception thrown during a frame. For something that runs on a wall 24/7, it's more mature to catch per scene, skip to the next scene and log. This also covers your "handle API failures better" TODO.
@SneakyThrows on network calls hides checked exceptions. Handle them explicitly, and reuse one HttpClient instead of creating one per request.
Formatting: use consistent field naming (_sceneManager isn't idiomatic Java) and fix the ;; in SceneManager. Adding Spotless (google-java-format) to Gradle keeps formatting consistent.
Performance: the led_canvas_set_pixels bulk call from your TODO would replace 2,048 JNI crossings per frame. If you measure before and after and put the numbers in the README, that's a strong talking point for a performance-minded company.
Housekeeping: add a LICENSE (the fonts have their own licenses, so keep fonts/AUTHORS). Merge java into main so visitors land on the Java code. Right now main is presumably the TS version.

Bugs:

More scene ideas
- Scrolling star wars intro
- Season/time bassed stardew thing
- "analog" clock
- Batman transition
