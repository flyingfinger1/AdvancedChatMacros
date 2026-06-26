package io.github.darkkronicle.advancedchatmacros;

import fi.dy.masa.malilib.event.InitializationHandler;
import io.github.darkkronicle.advancedchatmacros.config.KeybindManager;
import io.github.darkkronicle.advancedchatmacros.filter.MatchFilterHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AdvancedChatMacros implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatmacros";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // This will run after AdvancedChatCore's because of load order
        InitializationHandler.getInstance().registerInitializationHandler(new MacrosInitHandler());
    }

    /**
     * Sends a client-side info message into the chat. Replaces the old KommandLib {@code InfoUtil}
     * (which no longer exists on 26.x); 26.2's ChatComponent exposes addClientSystemMessage for this.
     */
    public static void sendChatMessage(String text) {
        sendChatMessage(text, null);
    }

    public static void sendChatMessage(String text, ChatFormatting format) {
        MutableComponent component = Component.literal(text);
        if (format != null) {
            component = component.withStyle(format);
        }
        Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(component);
    }

    /**
     * Sends a message to the server, mirroring the old {@code ClientPlayerEntity.sendChatMessage}
     * dispatch: a leading '/' is run as a command, anything else is sent as chat. On 26.x these are
     * split into {@code ClientPacketListener.sendCommand} (slash stripped) and {@code sendChat}.
     */
    public static void sendCommandOrChat(String message) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return;
        }
        if (message.startsWith("/")) {
            connection.sendCommand(message.substring(1));
        } else {
            connection.sendChat(message);
        }
    }

    public static void reloadFilters() {
        reloadFilters(Minecraft.getInstance().player != null);
    }

    public static void reloadFilters(boolean sendMessage) {
        if (sendMessage) {
            sendChatMessage("Reloading macro filters...");
        }
        MatchFilterHandler.getInstance().load();
        LOGGER.info("Filters loaded");
        if (sendMessage) {
            sendChatMessage("Done!", ChatFormatting.GREEN);
        }
    }

    public static void reloadKeybinds() {
        reloadFilters(Minecraft.getInstance().player != null);
    }

    public static void reloadKeybinds(boolean sendMessage) {
        if (sendMessage) {
            sendChatMessage("Reloading keybinds...");
        }
        KeybindManager.getInstance().load();
        LOGGER.info("Keybinds loaded");
        if (sendMessage) {
            sendChatMessage("Done!", ChatFormatting.GREEN);
        }
    }
}
