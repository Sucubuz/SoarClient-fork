package com.soarclient.utils.language;  
  
import java.io.BufferedReader;  
import java.io.InputStreamReader;  
import java.nio.charset.StandardCharsets;  
import java.util.HashMap;  
import java.util.Map;  
  
public final class I18n {  
  
	private static final Map<String, String> translateMap = new HashMap<>();  
	private static Language currentLanguage;  
  
	private I18n() {  
	}  
  
	/**  
	 * Sets the current language and loads the corresponding language file  
	 * @param language The language to set, defaults to ENGLISH if null  
	 */  
	public static void setLanguage(Language language) {  
		// Prevent setting null language, default to ENGLISH  
		if (language == null) {  
			language = Language.ENGLISH;  
		}  
		currentLanguage = language;  
		load(language);  
	}  
  
	/**  
	 * Loads the language file for the specified language  
	 * @param language The language to load translations for  
	 */  
	private static void load(Language language) {  
  
		String resourcePath = String.format("assets/soar/languages/%s.lang", language.getId());  
		translateMap.clear();  
  
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(  
				I18n.class.getClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {  
  
			reader.lines().filter(line -> !line.isEmpty() && !line.startsWith("#")).map(line -> line.split("=", 2))  
					.filter(parts -> parts.length == 2)  
					.forEach(parts -> translateMap.put(parts[0].trim(), parts[1].trim()));  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
  
	/**  
	 * Gets the translated string for the given key  
	 * @param key The translation key  
	 * @return The translated string, or return the key if not found
	 */  
	public static String get(String key) {
        // FIXME: create a TranslatedString and use it instead
		return translateMap.getOrDefault(key, key);
	}  
  
	/**  
	 * Gets the current language, returns ENGLISH as default if currentLanguage is null  
	 * This fixes the null language bug while maintaining language switching functionality  
	 * @return The current language, never null  
	 */  
	public static Language getCurrentLanguage() {  
		// Return default language if currentLanguage is null to prevent null pointer issues  
		return currentLanguage != null ? currentLanguage : Language.ENGLISH;  
	}  
}
