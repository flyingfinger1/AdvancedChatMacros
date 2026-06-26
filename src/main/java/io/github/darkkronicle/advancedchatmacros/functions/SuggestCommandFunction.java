package io.github.darkkronicle.advancedchatmacros.functions;

import fi.dy.masa.malilib.gui.GuiBase;
import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.parser.Result;
import io.github.darkkronicle.advancedchatmacros.config.KeybindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

import java.util.List;

public class SuggestCommandFunction implements NamedFunction {

    @Override
    public String getName() {
        return "suggestCommand";
    }

    @Override
    public Result parse(ParseContext context, List<Node> input) {
        Result res = Function.parseArgument(context, input, 0);
        if (Function.shouldReturn(res)) return res;
        if (!KeybindManager.SETTING_UP && Minecraft.getInstance().player != null) {
            // 26.2: ChatScreen(String) ctor gained a flag; false = not opened from a command preview
            GuiBase.openGui(new ChatScreen(res.getContent().getString(), false));
        }
        return Result.success("");
    }

    @Override
    public IntRange getArgumentCount() {
        return IntRange.of(1);
    }
}
