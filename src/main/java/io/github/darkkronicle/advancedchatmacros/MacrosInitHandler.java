package io.github.darkkronicle.advancedchatmacros;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.darkkronicle.advancedchatcore.ModuleHandler;
import io.github.darkkronicle.advancedchatcore.chat.MessageSender;
import io.github.darkkronicle.advancedchatmacros.config.KeybindManager;
import io.github.darkkronicle.advancedchatmacros.config.MacrosConfigStorage;
import io.github.darkkronicle.advancedchatmacros.filter.KonstructFilter;
import io.github.darkkronicle.advancedchatmacros.filter.MatchFilterHandler;
import io.github.darkkronicle.advancedchatmacros.impl.FiltersImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MacrosInitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(AdvancedChatMacros.MOD_ID, new MacrosConfigStorage());
        MessageSender.getInstance().addFilter(input -> {
            if (input.equals("[[reloadMacros]]")) {
                AdvancedChatMacros.reloadFilters(true);
                return Optional.of("");
            }
            if (input.equals("[[reloadKeybinds]]")) {
                AdvancedChatMacros.reloadKeybinds(true);
                return Optional.of("");
            }
            return Optional.empty();
        });
        MessageSender.getInstance().addFilter(KonstructFilter.getInstance());

        // KommandLib (DarkKronicle's old client-command lib) and Core's CommandsHandler no longer
        // exist on 26.x, so register the command directly through Fabric's client command API.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        // 26.2: fabric command-api-v2 3.1.0 renamed ClientCommandManager -> ClientCommands
                        ClientCommands.literal("acmacros")
                                .then(ClientCommands.literal("reloadToml")
                                        .executes(context -> {
                                            AdvancedChatMacros.reloadFilters(true);
                                            return 1;
                                        }))));

        MatchFilterHandler.getInstance().load();
        MessageSender.getInstance().addFilter(MatchFilterHandler.getInstance());

        KeybindManager.getInstance().load();

        InputEventHandler.getKeybindManager().registerKeybindProvider(KeybindManager.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(KeybindManager.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(KeybindManager.getInstance());

        if (ModuleHandler.getInstance().fromId("advancedchatfilters").isPresent()) {
            FiltersImpl.getInstance().init();
        }
    }
}
