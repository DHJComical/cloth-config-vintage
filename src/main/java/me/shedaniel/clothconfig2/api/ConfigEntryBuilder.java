package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.impl.builders.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public interface ConfigEntryBuilder {
    
    static ConfigEntryBuilder create() {
        return ConfigEntryBuilderImpl.create();
    }
    
    String getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(String resetButtonKey);
    
    IntListBuilder startIntList(String fieldNameKey, List<Integer> value);

    default IntListBuilder startIntList(CharSequence fieldNameKey, List<Integer> value) {
        return startIntList(fieldNameKey.toString(), value);
    }
    
    LongListBuilder startLongList(String fieldNameKey, List<Long> value);

    default LongListBuilder startLongList(CharSequence fieldNameKey, List<Long> value) {
        return startLongList(fieldNameKey.toString(), value);
    }
    
    FloatListBuilder startFloatList(String fieldNameKey, List<Float> value);

    default FloatListBuilder startFloatList(CharSequence fieldNameKey, List<Float> value) {
        return startFloatList(fieldNameKey.toString(), value);
    }
    
    DoubleListBuilder startDoubleList(String fieldNameKey, List<Double> value);

    default DoubleListBuilder startDoubleList(CharSequence fieldNameKey, List<Double> value) {
        return startDoubleList(fieldNameKey.toString(), value);
    }
    
    StringListBuilder startStrList(String fieldNameKey, List<String> value);

    default StringListBuilder startStrList(CharSequence fieldNameKey, List<String> value) {
        return startStrList(fieldNameKey.toString(), value);
    }
    
    SubCategoryBuilder startSubCategory(String fieldNameKey);

    default SubCategoryBuilder startSubCategory(CharSequence fieldNameKey) {
        return startSubCategory(fieldNameKey.toString());
    }
    
    SubCategoryBuilder startSubCategory(String fieldNameKey, List<AbstractConfigListEntry> entries);

    default SubCategoryBuilder startSubCategory(CharSequence fieldNameKey, List<AbstractConfigListEntry> entries) {
        return startSubCategory(fieldNameKey.toString(), entries);
    }
    
    BooleanToggleBuilder startBooleanToggle(String fieldNameKey, boolean value);

    default BooleanToggleBuilder startBooleanToggle(CharSequence fieldNameKey, boolean value) {
        return startBooleanToggle(fieldNameKey.toString(), value);
    }
    
    StringFieldBuilder startStrField(String fieldNameKey, String value);

    default StringFieldBuilder startStrField(CharSequence fieldNameKey, String value) {
        return startStrField(fieldNameKey.toString(), value);
    }
    
    ColorFieldBuilder startColorField(String fieldNameKey, int value);

    default ColorFieldBuilder startColorField(CharSequence fieldNameKey, int value) {
        return startColorField(fieldNameKey.toString(), value);
    }
    
    default ColorFieldBuilder startAlphaColorField(String fieldNameKey, int value) {
        return startColorField(fieldNameKey, value).setAlphaMode(true);
    }
    
    TextFieldBuilder startTextField(String fieldNameKey, String value);

    default TextFieldBuilder startTextField(CharSequence fieldNameKey, String value) {
        return startTextField(fieldNameKey.toString(), value);
    }
    
    TextDescriptionBuilder startTextDescription(String value);

    default TextDescriptionBuilder startTextDescription(CharSequence value) {
        return startTextDescription(value.toString());
    }
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(String fieldNameKey, Class<T> clazz, T value);

    default <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(CharSequence fieldNameKey, Class<T> clazz, T value) {
        return startEnumSelector(fieldNameKey.toString(), clazz, value);
    }
    
    <T> SelectorBuilder<T> startSelector(String fieldNameKey, T[] valuesArray, T value);

    default <T> SelectorBuilder<T> startSelector(CharSequence fieldNameKey, T[] valuesArray, T value) {
        return startSelector(fieldNameKey.toString(), valuesArray, value);
    }
    
    IntFieldBuilder startIntField(String fieldNameKey, int value);

    default IntFieldBuilder startIntField(CharSequence fieldNameKey, int value) {
        return startIntField(fieldNameKey.toString(), value);
    }
    
    LongFieldBuilder startLongField(String fieldNameKey, long value);

    default LongFieldBuilder startLongField(CharSequence fieldNameKey, long value) {
        return startLongField(fieldNameKey.toString(), value);
    }
    
    FloatFieldBuilder startFloatField(String fieldNameKey, float value);

    default FloatFieldBuilder startFloatField(CharSequence fieldNameKey, float value) {
        return startFloatField(fieldNameKey.toString(), value);
    }
    
    DoubleFieldBuilder startDoubleField(String fieldNameKey, double value);

    default DoubleFieldBuilder startDoubleField(CharSequence fieldNameKey, double value) {
        return startDoubleField(fieldNameKey.toString(), value);
    }
    
    IntSliderBuilder startIntSlider(String fieldNameKey, int value, int min, int max);

    default IntSliderBuilder startIntSlider(CharSequence fieldNameKey, int value, int min, int max) {
        return startIntSlider(fieldNameKey.toString(), value, min, max);
    }
    
    LongSliderBuilder startLongSlider(String fieldNameKey, long value, long min, long max);

    default LongSliderBuilder startLongSlider(CharSequence fieldNameKey, long value, long min, long max) {
        return startLongSlider(fieldNameKey.toString(), value, min, max);
    }
    
    KeyCodeBuilder startModifierKeyCodeField(String fieldNameKey, ModifierKeyCode value);

    default KeyCodeBuilder startModifierKeyCodeField(CharSequence fieldNameKey, ModifierKeyCode value) {
        return startModifierKeyCodeField(fieldNameKey.toString(), value);
    }
    
    default KeyCodeBuilder startKeyCodeField(String fieldNameKey, InputMappings.Input value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(String fieldNameKey, KeyBinding value) {
        InputMappings.Input key = InputMappings.Type.KEYSYM.getOrMakeInput(value.getKeyCode());
        return startKeyCodeField(fieldNameKey, key).setDefaultValue(key).setSaveConsumer(code -> {
            KeyBinding.updateKeyBindState();
            Minecraft.getMinecraft().gameSettings.saveOptions();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator);

    default <T> DropdownMenuBuilder<T> startDropdownMenu(CharSequence fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey.toString(), topCellElement, cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, DropdownBoxEntry.SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction, DropdownBoxEntry.SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
}
