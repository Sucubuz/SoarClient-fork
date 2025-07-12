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
    private ComboSetting languageSetting = new ComboSetting("setting.language", "setting.language.description",  
            Icon.LANGUAGE, this, Arrays.asList("English", "中文"), "English");  
  
    private Screen modMenu;  
  
    public ModMenuSettings() {  
        super("mod.modmenu.name", "mod.modmenu.description", Icon.MENU, ModCategory.MISC);  
  
        instance = this;  
        this.setHidden(true);  
        this.setEnabled(true);  
          
        // init language 
        initializeLanguageSetting();  
    }  
  
    private void initializeLanguageSetting() {  
        Language currentLang = I18n.getCurrentLanguage();  
        if (currentLang != null) {  
            String langOption = currentLang == Language.CHINESE ? "中文" : "English";  
            languageSetting.setOption(langOption);  
            previousLanguageOption = langOption;  
        }  
    }  
  
    public final EventBus.EventListener<ClientTickEvent> onClientTick = event -> {  
  
        if (keybindSetting.isPressed()) {  
  
            if (modMenu == null) {  
                modMenu = new GuiModMenu().build();  
            }  
  
            client.setScreen(modMenu);  
        }  
  
        // Language Handler 
        handleLanguageChange();  
    };  
  
    private void handleLanguageChange() {  
        String currentLanguageOption = languageSetting.getOption();  
           
        if (!currentLanguageOption.equals(previousLanguageOption)) {  
            Language targetLanguage = currentLanguageOption.equals("中文") ? Language.CHINESE : Language.ENGLISH;  
              
            if (I18n.getCurrentLanguage() != targetLanguage) {  
                I18n.setLanguage(targetLanguage);  
                  
                // rebuild modui  
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