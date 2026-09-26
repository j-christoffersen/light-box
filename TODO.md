- Fix brightness and ghosting
- Safe power supply
    - Standoffs, fuse, heat shirk, secure with glue gun or command strips. Electrical tape between hat and pi. Watch exposed wires.
- handle API failures better
- Proofread
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

Bugs:

More scene ideas
- Scrolling star wars intro
- Season/time bassed stardew thing
- "analog" clock
- Batman transition
