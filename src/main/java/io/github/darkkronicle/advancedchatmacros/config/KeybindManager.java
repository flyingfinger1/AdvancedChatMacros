package io.github.darkkronicle.advancedchatmacros.config;

import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.util.FileUtils;
import io.github.darkkronicle.Konstruct.NodeException;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.MultipleNodeProcessor;
import io.github.darkkronicle.Konstruct.parser.MultipleNodeSettings;
import io.github.darkkronicle.Konstruct.parser.NodeProcessor;
import io.github.darkkronicle.Konstruct.type.BooleanObject;
import io.github.darkkronicle.advancedchatcore.AdvancedChatCore;
import io.github.darkkronicle.advancedchatmacros.AdvancedChatMacros;
import io.github.darkkronicle.advancedchatmacros.CommandKeybind;
import io.github.darkkronicle.advancedchatmacros.filter.KonstructFilter;
import io.github.darkkronicle.advancedchatmacros.functions.CommandFunction;
import io.github.darkkronicle.advancedchatmacros.functions.CopyFunction;
import io.github.darkkronicle.advancedchatmacros.functions.InfoFunction;
import io.github.darkkronicle.advancedchatmacros.functions.SuggestCommandFunction;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeybindManager implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {

    private final static KeybindManager INSTANCE = new KeybindManager();

    public static KeybindManager getInstance() {
        return INSTANCE;
    }

    public static boolean SETTING_UP = true;

    private List<CommandKeybind> keybinds = new ArrayList<>();
    private File configDirectory = FileUtils.getConfigDirectory().resolve("advancedchat").resolve(AdvancedChatMacros.MOD_ID).toFile();
    private File keyBindFile = FileUtils.getConfigDirectory().resolve("advancedchat").resolve(AdvancedChatMacros.MOD_ID).resolve("keybinds.knst").toFile();
    private File exampleKeybinds = FileUtils.getConfigDirectory().resolve("advancedchat").resolve(AdvancedChatMacros.MOD_ID).resolve("example_keybinds.knst").toFile();

    public void load() {
        keybinds.clear();
        configDirectory.mkdirs();

        if (keyBindFile.exists()) {
            loadFile();
        } else if (!exampleKeybinds.exists()) {
            // Copy examples if filters and the example filters don't exist
            try {
                org.apache.commons.io.FileUtils.copyInputStreamToFile(AdvancedChatCore.getResource("example_keybinds.knst"), exampleKeybinds);
                AdvancedChatMacros.LOGGER.info("example_keybinds.knst was successfully created!");
            } catch (IOException | URISyntaxException e) {
                AdvancedChatMacros.LOGGER.warn("Example Keybinds failed to copy!", e);
            }
        }

        // Clear the old ones and redo
        InputEventHandler.getKeybindManager().updateUsedKeys();
    }

    private void loadFile() {
        SETTING_UP = true;
        List<String> lines;
        try {
            lines = Files.readAllLines(keyBindFile.toPath());
        } catch (IOException e) {
            AdvancedChatMacros.LOGGER.warn("Could not read keybinds.knst!", e);
            return;
        }
        String text = String.join("\n", lines);
        NodeProcessor processor = KonstructFilter.getInstance().getProcessor().copy();
        processor.addVariable("ready", () -> new BooleanObject(!SETTING_UP));
        processor.addFunction(new CommandFunction());
        processor.addFunction(new CopyFunction());
        processor.addFunction(new InfoFunction());
        processor.addFunction(new SuggestCommandFunction());
        MultipleNodeProcessor multiple;
        try {
            multiple = MultipleNodeProcessor.fromString(processor, MultipleNodeSettings.DEFAULT, text);
        } catch (NodeException e) {
            AdvancedChatMacros.LOGGER.warn("Malformed Konstruct in keybinds.knst!", e);
            SETTING_UP = false;
            return;
        }
        for (Node node : multiple.getNodes()) {
            loadKeybind(processor, node);
        }
        SETTING_UP = false;
    }

    private void loadKeybind(NodeProcessor processor, Node config) {
        CommandKeybind keybind = CommandKeybind.fromNode(processor, config);
        if (keybind != null) {
            keybinds.add(keybind);
        }
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (CommandKeybind keybind : keybinds) {
            manager.addKeybindToMap(keybind.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {

    }
}
