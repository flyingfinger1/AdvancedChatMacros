package io.github.darkkronicle.advancedchatmacros.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.FileUtils;
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatmacros.AdvancedChatMacros;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import io.github.darkkronicle.advancedchatmacros.config.options.BooleanTomlOption;
import io.github.darkkronicle.advancedchatmacros.config.options.TomlOption;
import io.github.darkkronicle.advancedchatmacros.util.TomlUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MacrosConfigStorage implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = "config.toml";
    private static final int CONFIG_VERSION = 1;

    public static class General {

        public static String NAME = "general";

        private static String translate(String key) {
            return "advancedchatmacros.config.general." + key;
        }

        public static SaveableConfig<BooleanTomlOption> KONSTRUCT_ENABLED = SaveableConfig.fromConfig("konstructEnabled",
                new BooleanTomlOption(translate("konstructenabled"), true, translate("info.konstructenabled")));

        public static SaveableConfig<BooleanTomlOption> PREVENT_MACRO_RECURSION = SaveableConfig.fromConfig("preventRecursion",
                new BooleanTomlOption(translate("preventrecursion"), true, translate("info.preventrecursion")));

        public static ImmutableList<SaveableConfig<? extends TomlOption<?>>> OPTIONS = ImmutableList.of(KONSTRUCT_ENABLED, PREVENT_MACRO_RECURSION);
    }

    public static void loadFromFile() {

        File configFile = FileUtils.getConfigDirectory().resolve("advancedchat").resolve(AdvancedChatMacros.MOD_ID).resolve(CONFIG_FILE_NAME).toFile();
        if (!configFile.exists()) {
            try {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(AdvancedChatCore.getResource("default_config.toml"), configFile);
                AdvancedChatMacros.LOGGER.info("default_config.toml was successfully created!");
            } catch (IOException | URISyntaxException e) {
                AdvancedChatMacros.LOGGER.warn("Default configuration failed to copy! No comments will be present in the file.", e);
            }
            return;
        }
        if (configFile.isFile() && configFile.canRead()) {
            FileConfig config = TomlUtils.loadFile(configFile);
            config.load();
            Optional<Config> general = config.getOptional(ConfigStorage.General.NAME);
            if (general.isEmpty()) {
                config.close();
                return;
            }
            for (SaveableConfig<? extends TomlOption<?>> option : General.OPTIONS) {
                try {
                    option.config.setValueFromToml(config.getOptional(Arrays.asList(General.NAME, option.key)));
                } catch (ClassCastException e) {
                    AdvancedChatMacros.LOGGER.warn("Error getting value " + option.key, e);
                }
            }
            config.close();
        }
    }

    public static void saveFromFile() {
        File dir = FileUtils.getConfigDirectory().resolve("advancedchat").resolve(AdvancedChatMacros.MOD_ID).toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            File file = dir.toPath().resolve(CONFIG_FILE_NAME).toFile();
            FileConfig config = TomlUtils.loadFile(file);
            config.load();
            for (SaveableConfig<? extends TomlOption<?>> option : General.OPTIONS) {
                config.set(Arrays.asList(General.NAME, option.key), option.config.getToml());
            }
            config.save();
            config.close();
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveFromFile();
    }
}
