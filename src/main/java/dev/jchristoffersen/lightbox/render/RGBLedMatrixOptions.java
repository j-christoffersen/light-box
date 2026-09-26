package dev.jchristoffersen.lightbox.render;

import com.sun.jna.Structure;

@Structure.FieldOrder({
    "hardware_mapping",
    "rows",
    "cols",
    "chain_length",
    "parallel",
    "pwm_bits",
    "pwm_lsb_nanoseconds",
    "pwm_dither_bits",
    "brightness",
    "scan_mode",
    "row_address_type",
    "multiplexing",
    "disable_hardware_pulsing",
    "show_refresh_rate",
    "inverse_colors",
    "led_rgb_sequence",
    "pixel_mapper_config",
    "panel_type",
    "limit_refresh_rate_hz",
    "disable_busy_waiting",
})
public class RGBLedMatrixOptions extends Structure {
    public String hardware_mapping;
    public int rows;
    public int cols;
    public int chain_length;
    public int parallel;
    public int pwm_bits;
    public int pwm_lsb_nanoseconds;
    public int pwm_dither_bits;
    public int brightness;
    public int scan_mode;
    public int row_address_type;
    public int multiplexing;
    public boolean disable_hardware_pulsing;
    public boolean show_refresh_rate;
    public boolean inverse_colors;
    public String led_rgb_sequence;
    public String pixel_mapper_config;
    public String panel_type;
    public int limit_refresh_rate_hz;
    public boolean disable_busy_waiting;

    public static class ByReference extends RGBLedMatrixOptions implements Structure.ByReference {}
}
