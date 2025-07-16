package com.soarclient.management.mod.impl.misc;

import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.ComboSetting;
import com.soarclient.management.mod.settings.impl.StringSetting;
import com.soarclient.skia.font.Icon;

import java.util.Arrays;

public class ClientSpoofMod extends Mod {

    private final ComboSetting modeSetting = new ComboSetting("setting.clientspoof.mode",
        "setting.clientspoof.mode.description", Icon.SECURITY, this,
        Arrays.asList("setting.clientspoof.vanilla", "setting.clientspoof.lunar1204",
            "setting.clientspoof.lunar1201", "setting.clientspoof.custom",
            "setting.clientspoof.null"),
        "setting.clientspoof.vanilla");

    private final StringSetting customSetting = new StringSetting("setting.clientspoof.custom",
        "setting.clientspoof.custom.description", Icon.EDIT, this, "feather") {
        @Override
        public boolean isVisible() {
            return modeSetting.getOption().equals("setting.clientspoof.custom");
        }
    };

    public ClientSpoofMod() {
        super("mod.clientspoof.name", "mod.clientspoof.description", Icon.SECURITY, ModCategory.MISC);
    }

    public enum Mode {
        VANILLA("setting.clientspoof.vanilla", "vanilla"),
        LUNAR_1_20_4("setting.clientspoof.lunar1204", "lunarclient:1.20.4"),
        LUNAR_1_20_1("setting.clientspoof.lunar1201", "lunarclient:1.20.1"),
        CUSTOM("setting.clientspoof.custom", null),
        NULL("setting.clientspoof.null", null);

        private final String langKey;
        private final String clientName;

        Mode(String langKey, String clientName) {
            this.langKey = langKey;
            this.clientName = clientName;
        }

        public String getLangKey() {
            return langKey;
        }

        public String getClientName() {
            return clientName;
        }

        public static Mode fromLangKey(String langKey) {
            for (Mode mode : values()) {
                if (mode.langKey.equals(langKey)) {
                    return mode;
                }
            }
            return VANILLA;
        }
    }

    public String getClientName() {
        Mode mode = Mode.fromLangKey(modeSetting.getOption());

        return switch (mode) {
            case VANILLA -> "vanilla";
            case LUNAR_1_20_4 -> "lunarclient:1.20.4";
            case LUNAR_1_20_1 -> "lunarclient:1.20.1";
            case CUSTOM -> customSetting.getValue();
            case NULL -> null;
        };
    }

    public Mode getCurrentMode() {
        return Mode.fromLangKey(modeSetting.getOption());
    }

    public ComboSetting getModeSetting() {
        return modeSetting;
    }

    public StringSetting getCustomSetting() {
        return customSetting;
    }
}
