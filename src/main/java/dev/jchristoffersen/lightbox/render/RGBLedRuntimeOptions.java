package dev.jchristoffersen.lightbox.render;

import com.sun.jna.Structure;

@Structure.FieldOrder({
    "gpio_slowdown",
    "rp1_pio",
    "daemon",
    "drop_privileges",
    "do_gpio_init",
    "drop_priv_user",
    "drop_priv_group",
})
public class RGBLedRuntimeOptions extends Structure {
    public int gpio_slowdown;
    public int rp1_pio;
    public int daemon;
    public int drop_privileges;
    public boolean do_gpio_init;
    public String drop_priv_user;
    public String drop_priv_group;
}
