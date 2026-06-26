package io.github.darkkronicle.advancedchatmacros.functions;

import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.parser.Result;
import io.github.darkkronicle.advancedchatmacros.config.KeybindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class InfoFunction implements NamedFunction {

    @Override
    public String getName() {
        return "sendInfo";
    }

    @Override
    public Result parse(ParseContext context, List<Node> input) {
        Result res = Function.parseArgument(context, input, 0);
        if (Function.shouldReturn(res)) return res;
        if (!KeybindManager.SETTING_UP && Minecraft.getInstance().player != null) {
            // 26.2: LiteralText removed -> Component.literal; sendMessage(Text, boolean) -> sendSystemMessage(Component)
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(res.getContent().getString()));
        }
        return Result.success("");
    }

    @Override
    public IntRange getArgumentCount() {
        return IntRange.of(1);
    }
}
