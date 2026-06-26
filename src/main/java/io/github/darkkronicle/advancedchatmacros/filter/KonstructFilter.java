package io.github.darkkronicle.advancedchatmacros.filter;

import io.github.darkkronicle.Konstruct.NodeException;
import io.github.darkkronicle.Konstruct.functions.Variable;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.NodeProcessor;
import io.github.darkkronicle.Konstruct.parser.ParseResult;
import io.github.darkkronicle.Konstruct.reader.builder.InputNodeBuilder;
import io.github.darkkronicle.Konstruct.type.DoubleObject;
import io.github.darkkronicle.Konstruct.type.IntegerObject;
import io.github.darkkronicle.advancedchatcore.interfaces.IStringFilter;
import io.github.darkkronicle.advancedchatcore.konstruct.AdvancedChatKonstruct;
import io.github.darkkronicle.advancedchatmacros.config.MacrosConfigStorage;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.util.Optional;
import java.util.function.Supplier;

public class KonstructFilter implements IStringFilter {

    @Getter
    private NodeProcessor processor;

    private final static KonstructFilter INSTANCE = new KonstructFilter();

    public static KonstructFilter getInstance() {
        return INSTANCE;
    }

    private KonstructFilter() {
        Minecraft client = Minecraft.getInstance();
        processor = AdvancedChatKonstruct.getInstance().copy();

        addDoubleProperty(processor, "x", () -> client.player.getX());
        addDoubleProperty(processor, "y", () -> client.player.getY());
        addDoubleProperty(processor, "z", () -> client.player.getZ());
        addIntProperty(processor, "blockX", () -> client.player.getBlockX());
        addIntProperty(processor, "blockY", () -> client.player.getBlockY());
        addIntProperty(processor, "blockZ", () -> client.player.getBlockZ());
    }

    private void addIntProperty(NodeProcessor processor, String name, Supplier<Integer> variable) {
        processor.addVariable(name, Variable.of(() -> new IntegerObject(variable.get())));
    }

    private void addDoubleProperty(NodeProcessor processor, String name, Supplier<Double> variable) {
        processor.addVariable(name, Variable.of(() -> new DoubleObject(variable.get())));
    }

    @Override
    public Optional<String> filter(String input) {
        if (!MacrosConfigStorage.General.KONSTRUCT_ENABLED.config.getBooleanValue()) {
            return Optional.empty();
        }
        try {
            Node node = new InputNodeBuilder(input).build();
            ParseResult result = processor.parse(node);
            return Optional.of(result.getResult().getContent().getString());
        } catch (NodeException e) {
            return Optional.empty();
        }
    }
}
