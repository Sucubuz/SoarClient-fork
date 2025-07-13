package com.soarclient.management.mod.impl.settings;  
  
import java.util.Arrays;  
  
import org.lwjgl.glfw.GLFW;  
  
import com.soarclient.event.EventBus;  
import com.soarclient.event.client.ClientTickEvent;  
import com.soarclient.gui.modmenu.GuiModMenu;  
import com.soarclient.libraries.material3.hct.Hct;  
import com.soarclient.management.mod.Mod;  
import com.soarclient.management.mod.ModCategory;  
import com.soarclient.management.mod.settings.impl.BooleanSetting;  
import com.soarclient.management.mod.settings.impl.ComboSetting;  
import com.soarclient.management.mod.settings.impl.HctColorSetting;  
import com.soarclient.management.mod.settings.impl.KeybindSetting;  
import com.soarclient.management.mod.settings.impl.NumberSetting;  
import com.soarclient.skia.font.Icon;  
import com.soarclient.utils.language.I18n;  
import com.soarclient.utils.language.Language;  
  
import net.minecraft.client.gui.screen.Screen;  
import net.minecraft.client.util.InputUtil;  
  
public class ModMenuSettings extends Mod {  
  
    private static ModMenuSettings instance;  
    private String previousLanguageOption = "English";  
    private boolean languageInitialized = false;  
  
    private KeybindSetting keybindSetting = new KeybindSetting("setting.keybind", "setting.keybind.description",  
            Icon.KEYBOARD, this, InputUtil.fromKeyCode(GLFW.GLFW_KEY_RIGHT_SHIFT, 0));  
    private BooleanSetting darkModeSetting = new BooleanSetting("setting.darkmode", "setting.darkmode.description",  
            Icon.DARK_MODE, this, false);  
    private HctColorSetting hctColorSetting = new HctColorSetting("setting.color", "setting.color.description",  
            Icon.PALETTE, this, Hct.from(220, 26, 6));  
    private BooleanSetting blurSetting = new BooleanSetting("setting.blur", "setting.blur.description", Icon.LENS_BLUR,  
            this, true);  
    private NumberSetting blurIntensitySetting = new NumberSetting("setting.blurintensity",  
            "setting.blurintensity.description", Icon.BLUR_LINEAR, this, 5, 1, 20, 1);  
      
    // 使用固定的语言选项，避免I18n初始化问题  
    private ComboSetting languageSetting = new ComboSetting("setting.language", "setting.language.description",  
            Icon.LANGUAGE, this, Arrays.asList("English", "中文", "日本語"), "English");  
  
    private Screen modMenu;  
  
    public ModMenuSettings() {  
        super("mod.modmenu.name", "mod.modmenu.description", Icon.MENU, ModCategory.MISC);  
  
        instance = this;  
        this.setHidden(true);  
        this.setEnabled(true);  
    }  
  
    private void initializeLanguageSetting() {  
        Language currentLang = I18n.getCurrentLanguage();  
          
        if (currentLang == null) {  
            currentLang = Language.ENGLISH;  
            I18n.setLanguage(currentLang);  
        }  
          
        String langOption = getLanguageOptionFromEnum(currentLang);  
        languageSetting.setOption(langOption);  
        previousLanguageOption = langOption;  
    }  
  
    private String getLanguageOptionFromEnum(Language language) {  
        switch (language) {  
            case CHINESE:  
                return "中文";  
            case JAPANESE:  
                return "日本語";  
            default:  
                return "English";  
        }  
    }  
  
    private Language getLanguageEnumFromOption(String option) {  
        switch (option) {  
            case "中文":  
                return Language.CHINESE;  
            case "日本語":  
                return Language.JAPANESE;  
            default:  
                return Language.ENGLISH;  
        }  
    }  
  
    public final EventBus.EventListener<ClientTickEvent> onClientTick = event -> {  
          
        // 只初始化一次，避免重复UI  
        if (!languageInitialized && I18n.getCurrentLanguage() != null) {  
            initializeLanguageSetting();  
            languageInitialized = true;  
        }  
  
        if (keybindSetting.isPressed()) {  
            if (modMenu == null) {  
                modMenu = new GuiModMenu().build();  
            }  
            client.setScreen(modMenu);  
        }  
  
        handleLanguageChange();  
    };  
  
    private void handleLanguageChange() {  
        String currentLanguageOption = languageSetting.getOption();  
          
        if (currentLanguageOption == null || currentLanguageOption.isEmpty()) {  
            return;  
        }  
          
        if (!currentLanguageOption.equals(previousLanguageOption)) {  
            Language targetLanguage = getLanguageEnumFromOption(currentLanguageOption);  
              
            if (I18n.getCurrentLanguage() != targetLanguage) {  
                I18n.setLanguage(targetLanguage);  
                  
                if (modMenu != null) {  
                    modMenu = null;  
                }  
            }  
              
            previousLanguageOption = currentLanguageOption;  
        }  
    }  
  
    @Override  
    public void onDisable() {  
        this.setEnabled(true);  
    }  
  
    public static ModMenuSettings getInstance() {  
        return instance;  
    }  
  
    public BooleanSetting getDarkModeSetting() {  
        return darkModeSetting;  
    }  
  
    public HctColorSetting getHctColorSetting() {  
        return hctColorSetting;  
    }  
  
    public BooleanSetting getBlurSetting() {  
        return blurSetting;  
    }  
  
    public NumberSetting getBlurIntensitySetting() {  
        return blurIntensitySetting;  
    }  
  
    public ComboSetting getLanguageSetting() {  
        return languageSetting;  
    }  
  
    public Screen getModMenu() {  
        return modMenu;  
    }  
}