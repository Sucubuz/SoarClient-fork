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
	 * Loads language names from the dedicated language.lang file  
	 */  
	public static void loadLanguageNames() {  
		String resourcePath = "assets/soar/languages/language.lang";  
		  
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(  
				I18n.class.getClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {  
  
			reader.lines().filter(line -> !line.isEmpty() && !line.startsWith("#"))  
					.map(line -> line.split("=", 2))  
					.filter(parts -> parts.length == 2)  
					.forEach(parts -> translateMap.put(parts[0].trim(), parts[1].trim()));  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
  
	/**  
	 * Loads the language file for the specified language  
	 * @param language The language to load translations for  
	 */  
	private static void load(Language language) {  
  
		String resourcePath = String.format("assets/soar/languages/%s.lang", language.getId());  
		  
		// Don't clear translateMap to preserve language names  
		Map<String, String> tempMap = new HashMap<>(translateMap);  
  
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(  
				I18n.class.getClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {  
  
			reader.lines().filter(line -> !line.isEmpty() && !line.startsWith("#")).map(line -> line.split("=", 2))  
					.filter(parts -> parts.length == 2)  
					.forEach(parts -> tempMap.put(parts[0].trim(), parts[1].trim()));  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		  
		translateMap.clear();  
		translateMap.putAll(tempMap);  
	}  
  
	/**  
	 * Gets the translated string for the given key  
	 * @param key The translation key  
	 * @return The translated string, or "null" if not found  
	 */  
	public static String get(String key) {  
		return translateMap.getOrDefault(key, "null");  
	}  
  
	/**  
	 * Gets the current language, returns ENGLISH as default if currentLanguage is null  
	 * @return The current language, never null  
	 */  
	public static Language getCurrentLanguage() {  
		return currentLanguage != null ? currentLanguage : Language.ENGLISH;  
	}  
}