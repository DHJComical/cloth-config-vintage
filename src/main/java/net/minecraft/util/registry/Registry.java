package net.minecraft.util.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class Registry<T> {
    public static final Registry<Item> ITEM = new Registry<>(Item.REGISTRY, net.minecraft.init.Items.AIR);
    public static final Registry<Block> BLOCK = new Registry<>(Block.REGISTRY, net.minecraft.init.Blocks.AIR);

    private final RegistryNamespaced<ResourceLocation, T> delegate;
    private final T fallback;

    private Registry(RegistryNamespaced<ResourceLocation, T> delegate, T fallback) {
        this.delegate = delegate;
        this.fallback = fallback;
    }

    public Optional<T> getValue(ResourceLocation id) {
        return Optional.ofNullable(delegate.getObject(id));
    }

    public T getOrDefault(ResourceLocation id) {
        return getValue(id).orElse(fallback);
    }

    public ResourceLocation getKey(T value) {
        return delegate.getNameForObject(value);
    }

    public Stream<T> stream() {
        return delegate.getKeys().stream().map(delegate::getObject).filter(Objects::nonNull);
    }
}
