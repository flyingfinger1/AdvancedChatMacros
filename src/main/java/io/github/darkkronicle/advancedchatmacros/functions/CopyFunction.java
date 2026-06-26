package io.github.darkkronicle.advancedchatmacros.functions;

import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.parser.Result;
import io.github.darkkronicle.advancedchatmacros.config.KeybindManager;
import net.minecraft.client.Minecraft;

import java.util.List;

public class CopyFunction implements NamedFunction {

    @Override
    public String getName() {
        return "copy";
    }

    @Override
    public Result parse(ParseContext context, List<Node> input) {
        Result res = Function.parseArgument(context, input, 0);
        if (Function.shouldReturn(res)) return res;
        if (!KeybindManager.SETTING_UP) {
            // 26.2: MinecraftClient.keyboard -> Minecraft.keyboardHandler
            Minecraft.getInstance().keyboardHandler.setClipboard(res.getContent().getString());
        }
        return Result.success("");
    }

    @Override
    public IntRange getArgumentCount() {
        return IntRange.of(1);
    }
}
