package me.shedaniel.clothconfig2.impl.forge;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.ColorFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleListBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntListBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import me.shedaniel.clothconfig2.impl.builders.SelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.StringFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.StringListBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ForgeConfigScreenFactory {
    private static final String FALLBACK_CATEGORY = "text.cloth-config.config";
    private static final String FORGE_GENERAL_CATEGORY_ELEMENT = "forgeCfg";
    private static final String FORGE_CLIENT_CATEGORY_ELEMENT = "forgeClientCfg";
    private static final String FORGE_CHUNK_LOADING_CATEGORY_ELEMENT = "forgeChunkLoadingCfg";
    private static final String FORGE_VERSION_CHECK_CATEGORY_ELEMENT = "forgeVersionCheckCfg";
    private static final String FORGE_CHUNK_LOADING_MOD_CATEGORY_ELEMENT = "forgeChunkLoadingModCfg";
    private static final String FORGE_CHUNK_LOADING_CONFIG_ID = "chunkLoader";

    private ForgeConfigScreenFactory() {
    }

    public static GuiScreen createDefaultConfigScreen(GuiScreen parentScreen, String modId, String title) {
        SaveState saveState = new SaveState();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(title)
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> saveAndPostConfigEvents(modId, saveState));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        Class<?>[] configClasses = ConfigManager.getModConfigClasses(modId);
        if (configClasses.length == 0) {
            addText(builder.getOrCreateCategory(FALLBACK_CATEGORY), "No Forge @Config entries were found for " + modId + ".");
            return builder.build();
        }

        boolean addedEntries = false;
        for (Class<?> configClass : configClasses) {
            IConfigElement root = net.minecraftforge.common.config.ConfigElement.from(configClass);
            if (root != null) {
                addedEntries |= addRootElement(builder, entryBuilder, root, saveState);
            }
        }

        if (!addedEntries) {
            addText(builder.getOrCreateCategory(FALLBACK_CATEGORY), "No visible Forge @Config entries were found for " + modId + ".");
        }

        return builder.build();
    }

    public static GuiScreen createFromGuiConfig(GuiConfig guiConfig) {
        SaveState saveState = new SaveState();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(guiConfig.parentScreen)
                .setTitle(guiConfig.title)
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> saveAndPostConfigEvents(guiConfig.modID, saveState));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        boolean addedEntries = addGuiConfigElements(builder, entryBuilder, guiConfig, saveState);
        if (!addedEntries) {
            addText(builder.getOrCreateCategory(FALLBACK_CATEGORY), "No visible Forge config entries were found for " + guiConfig.modID + ".");
        }

        return builder.build();
    }

    private static boolean addGuiConfigElements(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, GuiConfig guiConfig, SaveState saveState) {
        if (ForgeVersion.MOD_ID.equals(guiConfig.modID)) {
            return addForgeOwnedElements(builder, entryBuilder, guiConfig, saveState);
        }

        List<IConfigElement> elements = visibleChildren(guiConfig.configElements);
        if (!elements.isEmpty() && elements.stream().noneMatch(IConfigElement::isProperty)) {
            boolean added = false;
            for (IConfigElement element : elements) {
                ConfigCategory category = builder.getOrCreateCategory(displayName(element));
                added |= addElements(
                        category,
                        entryBuilder,
                        element.getChildElements(),
                        guiConfig.allRequireWorldRestart || element.requiresWorldRestart(),
                        guiConfig.allRequireMcRestart || element.requiresMcRestart(),
                        guiConfig.configID,
                        saveState);
            }
            return added;
        } else {
            String categoryName = titleWithSubtitle(guiConfig);
            ConfigCategory category = builder.getOrCreateCategory(categoryName);
            return addElements(category, entryBuilder, elements,
                    guiConfig.allRequireWorldRestart,
                    guiConfig.allRequireMcRestart,
                    guiConfig.configID,
                    saveState);
        }
    }

    private static boolean addForgeOwnedElements(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, GuiConfig guiConfig, SaveState saveState) {
        boolean added = false;
        for (IConfigElement element : visibleChildren(guiConfig.configElements)) {
            switch (element.getName()) {
                case FORGE_GENERAL_CATEGORY_ELEMENT:
                    added |= addForgeCategory(
                            builder,
                            entryBuilder,
                            element,
                            new ConfigElement(ForgeModContainer.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                            Configuration.CATEGORY_GENERAL,
                            guiConfig,
                            saveState);
                    break;
                case FORGE_CLIENT_CATEGORY_ELEMENT:
                    added |= addForgeCategory(
                            builder,
                            entryBuilder,
                            element,
                            new ConfigElement(ForgeModContainer.getConfig().getCategory(Configuration.CATEGORY_CLIENT)).getChildElements(),
                            Configuration.CATEGORY_CLIENT,
                            guiConfig,
                            saveState);
                    break;
                case FORGE_CHUNK_LOADING_CATEGORY_ELEMENT:
                    added |= addForgeCategory(
                            builder,
                            entryBuilder,
                            element,
                            createForgeChunkLoadingElements(),
                            FORGE_CHUNK_LOADING_CONFIG_ID,
                            guiConfig,
                            saveState);
                    break;
                case FORGE_VERSION_CHECK_CATEGORY_ELEMENT:
                    added |= addForgeCategory(
                            builder,
                            entryBuilder,
                            element,
                            createForgeVersionCheckElements(),
                            ForgeModContainer.VERSION_CHECK_CAT,
                            guiConfig,
                            saveState);
                    break;
                default:
                    ConfigCategory category = builder.getOrCreateCategory(displayName(element));
                    AbstractConfigListEntry entry = createEntry(
                            entryBuilder,
                            element,
                            guiConfig.allRequireWorldRestart,
                            guiConfig.allRequireMcRestart,
                            guiConfig.configID,
                            saveState);
                    if (entry != null) {
                        category.addEntry(entry);
                        added = true;
                    }
                    break;
            }
        }
        return added;
    }

    private static boolean addForgeCategory(
            ConfigBuilder builder,
            ConfigEntryBuilder entryBuilder,
            IConfigElement categoryElement,
            List<IConfigElement> elements,
            String configId,
            GuiConfig guiConfig,
            SaveState saveState) {
        ConfigCategory category = builder.getOrCreateCategory(displayName(categoryElement));
        boolean forceWorldRestart = guiConfig.allRequireWorldRestart || categoryElement.requiresWorldRestart();
        boolean forceMcRestart = guiConfig.allRequireMcRestart || categoryElement.requiresMcRestart();
        if (ForgeModContainer.VERSION_CHECK_CAT.equals(configId)) {
            forceWorldRestart = true;
            forceMcRestart = true;
        }
        boolean added = addElements(
                category,
                entryBuilder,
                elements,
                forceWorldRestart,
                forceMcRestart,
                configId,
                saveState);
        if (!added) {
            addText(category, "No visible Forge config entries were found in " + displayName(categoryElement) + ".");
            return true;
        }
        return true;
    }

    private static List<IConfigElement> createForgeChunkLoadingElements() {
        List<IConfigElement> elements = new ArrayList<>();
        elements.add(new DummyCategoryElement(
                FORGE_CHUNK_LOADING_MOD_CATEGORY_ELEMENT,
                "forge.configgui.ctgy.forgeChunkLoadingModConfig",
                createForgeChunkLoadingModElements()));
        elements.addAll(new ConfigElement(ForgeChunkManager.getDefaultsCategory()).getChildElements());
        return elements;
    }

    private static List<IConfigElement> createForgeChunkLoadingModElements() {
        List<IConfigElement> elements = new ArrayList<>();
        for (net.minecraftforge.common.config.ConfigCategory category : ForgeChunkManager.getModCategories()) {
            elements.add(new ConfigElement(category));
        }
        return elements;
    }

    private static List<IConfigElement> createForgeVersionCheckElements() {
        net.minecraftforge.common.config.ConfigCategory cfg = ForgeModContainer.getConfig().getCategory(ForgeModContainer.VERSION_CHECK_CAT);
        Map<String, Property> values = new HashMap<>(cfg.getValues());
        values.remove("Global");

        Property global = ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, "Global", true);
        List<Property> props = new ArrayList<>();
        for (ModContainer mod : ForgeVersion.gatherMods().keySet()) {
            values.remove(mod.getModId());
            props.add(ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, mod.getModId(), true));
        }
        props.addAll(values.values());
        props.sort(Comparator.comparing(Property::getName));

        List<IConfigElement> elements = new ArrayList<>();
        elements.add(new ConfigElement(global));
        for (Property prop : props) {
            elements.add(new ConfigElement(prop));
        }
        return elements;
    }

    private static boolean addRootElement(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, IConfigElement root, SaveState saveState) {
        List<IConfigElement> children = visibleChildren(root);
        if (children.isEmpty()) {
            return false;
        }

        boolean rootWorldRestart = root.requiresWorldRestart();
        boolean rootMcRestart = root.requiresMcRestart();
        boolean allChildrenAreCategories = children.stream().noneMatch(IConfigElement::isProperty);
        if (allChildrenAreCategories) {
            boolean added = false;
            for (IConfigElement child : children) {
                ConfigCategory category = builder.getOrCreateCategory(displayName(child));
                added |= addElements(category, entryBuilder, child.getChildElements(),
                        rootWorldRestart || child.requiresWorldRestart(),
                        rootMcRestart || child.requiresMcRestart(),
                        null,
                        saveState);
            }
            return added;
        }

        ConfigCategory category = builder.getOrCreateCategory(displayName(root));
        return addElements(category, entryBuilder, children, rootWorldRestart, rootMcRestart, null, saveState);
    }

    private static boolean addElements(
            ConfigCategory category,
            ConfigEntryBuilder entryBuilder,
            List<IConfigElement> elements,
            boolean forceWorldRestart,
            boolean forceMcRestart,
            String configId,
            SaveState saveState) {
        boolean added = false;
        for (IConfigElement element : visibleChildren(elements)) {
            AbstractConfigListEntry entry = createEntry(entryBuilder, element, forceWorldRestart, forceMcRestart, configId, saveState);
            if (entry != null) {
                category.addEntry(entry);
                added = true;
            }
        }
        return added;
    }

    private static AbstractConfigListEntry createEntry(
            ConfigEntryBuilder builder,
            IConfigElement element,
            boolean forceWorldRestart,
            boolean forceMcRestart,
            String configId,
            SaveState saveState) {
        if (!element.isProperty()) {
            SubCategoryBuilder subCategory = builder.startSubCategory(displayName(element)).setExpanded(false);
            tooltip(element).ifPresent(subCategory::setTooltip);
            boolean added = false;
            for (IConfigElement child : visibleChildren(element.getChildElements())) {
                AbstractConfigListEntry childEntry = createEntry(
                        builder,
                        child,
                        forceWorldRestart || element.requiresWorldRestart(),
                        forceMcRestart || element.requiresMcRestart(),
                        configId,
                        saveState);
                if (childEntry != null) {
                    subCategory.add(childEntry);
                    added = true;
                }
            }
            return added ? subCategory.build() : null;
        }

        if (element.getArrayEntryClass() != null) {
            return unsupportedEntry(element, "Custom Forge config GUI entries are not supported by the Cloth Config replacement.");
        }

        try {
            return element.isList()
                    ? createListEntry(builder, element, forceWorldRestart, forceMcRestart, configId, saveState)
                    : createValueEntry(builder, element, forceWorldRestart, forceMcRestart, configId, saveState);
        } catch (RuntimeException e) {
            return unsupportedEntry(element, "Unable to convert Forge config entry: " + e.getMessage());
        }
    }

    private static AbstractConfigListEntry createValueEntry(
            ConfigEntryBuilder builder,
            IConfigElement element,
            boolean forceWorldRestart,
            boolean forceMcRestart,
            String configId,
            SaveState saveState) {
        String name = displayName(element);
        boolean requiresWorldRestart = forceWorldRestart || element.requiresWorldRestart();
        boolean requiresRestart = forceMcRestart || element.requiresMcRestart();
        switch (element.getType()) {
            case BOOLEAN: {
                ValueState<Boolean> value = new ValueState<>(parseBoolean(element.get(), element.getDefault()));
                BooleanToggleBuilder toggle = builder.startBooleanToggle(name, value.value);
                tooltip(element).ifPresent(toggle::setTooltip);
                defaultValue(element, Boolean.class).ifPresent(toggle::setDefaultValue);
                toggle.setSaveConsumer(newValue -> saveValue(element, value, newValue, requiresRestart, configId, saveState));
                if (requiresRestart) {
                    toggle.requireRestart();
                }
                return editable(toggle.build(), requiresWorldRestart);
            }
            case INTEGER: {
                ValueState<Integer> value = new ValueState<>(parseInt(element.get(), element.getDefault()));
                int min = parseInt(element.getMinValue(), Integer.MIN_VALUE);
                int max = parseInt(element.getMaxValue(), Integer.MAX_VALUE);
                if (element.hasSlidingControl()) {
                    IntSliderBuilder slider = builder.startIntSlider(name, value.value, min, max);
                    tooltip(element).ifPresent(slider::setTooltip);
                    defaultValue(element, Integer.class).ifPresent(slider::setDefaultValue);
                    slider.setSaveConsumer(newValue -> saveValue(element, value, newValue, requiresRestart, configId, saveState));
                    if (requiresRestart) {
                        slider.requireRestart();
                    }
                    return editable(slider.build(), requiresWorldRestart);
                }
                IntFieldBuilder field = builder.startIntField(name, value.value).setMin(min).setMax(max);
                tooltip(element).ifPresent(field::setTooltip);
                defaultValue(element, Integer.class).ifPresent(field::setDefaultValue);
                field.setSaveConsumer(newValue -> saveValue(element, value, newValue, requiresRestart, configId, saveState));
                if (requiresRestart) {
                    field.requireRestart();
                }
                return editable(field.build(), requiresWorldRestart);
            }
            case DOUBLE: {
                ValueState<Double> value = new ValueState<>(parseDouble(element.get(), element.getDefault()));
                DoubleFieldBuilder field = builder.startDoubleField(name, value.value)
                        .setMin(parseDouble(element.getMinValue(), -Double.MAX_VALUE))
                        .setMax(parseDouble(element.getMaxValue(), Double.MAX_VALUE));
                tooltip(element).ifPresent(field::setTooltip);
                defaultValue(element, Double.class).ifPresent(field::setDefaultValue);
                field.setSaveConsumer(newValue -> saveValue(element, value, newValue, requiresRestart, configId, saveState));
                if (requiresRestart) {
                    field.requireRestart();
                }
                return editable(field.build(), requiresWorldRestart);
            }
            case COLOR: {
                if (hasValidValues(element)) {
                    return createStringSelector(builder, element, requiresWorldRestart, requiresRestart, configId, saveState);
                }
                ValueState<Integer> value = new ValueState<>(parseColor(element.get(), element.getDefault()));
                ColorFieldBuilder color = builder.startColorField(name, value.value);
                tooltip(element).ifPresent(color::setTooltip);
                color.setDefaultValue(parseColor(element.getDefault(), 0));
                color.setSaveConsumer(newValue -> {
                    if (!Objects.equals(value.value, newValue)) {
                        element.set(String.format(Locale.ROOT, "%06x", newValue & 0xFFFFFF));
                        value.value = newValue;
                        saveState.mark(configId, requiresRestart);
                    }
                });
                if (requiresRestart) {
                    color.requireRestart();
                }
                return editable(color.build(), requiresWorldRestart);
            }
            case MOD_ID:
            case STRING:
                if (hasValidValues(element)) {
                    return createStringSelector(builder, element, requiresWorldRestart, requiresRestart, configId, saveState);
                }
                ValueState<String> value = new ValueState<>(Objects.toString(element.get(), ""));
                StringFieldBuilder field = builder.startStrField(name, value.value);
                tooltip(element).ifPresent(field::setTooltip);
                defaultValue(element, String.class).ifPresent(field::setDefaultValue);
                Pattern pattern = element.getValidationPattern();
                if (pattern != null) {
                    field.setErrorSupplier(newValue -> pattern.matcher(newValue).matches() ? Optional.empty() : Optional.of(I18n.format("fml.configgui.tooltip.invalidValue")));
                }
                field.setSaveConsumer(newValue -> saveValue(element, value, newValue, requiresRestart, configId, saveState));
                if (requiresRestart) {
                    field.requireRestart();
                }
                return editable(field.build(), requiresWorldRestart);
            default:
                return unsupportedEntry(element, "Unsupported Forge config type: " + element.getType());
        }
    }

    private static AbstractConfigListEntry createStringSelector(
            ConfigEntryBuilder builder,
            IConfigElement element,
            boolean requiresWorldRestart,
            boolean requiresRestart,
            String configId,
            SaveState saveState) {
        String[] values = element.getValidValues();
        ValueState<String> selected = new ValueState<>(Objects.toString(element.get(), values.length > 0 ? values[0] : ""));
        SelectorBuilder<String> selector = builder.startSelector(displayName(element), values, selected.value);
        tooltip(element).ifPresent(selector::setTooltip);
        defaultValue(element, String.class).ifPresent(selector::setDefaultValue);
        String[] displayValues = element.getValidValuesDisplay();
        if (displayValues != null && displayValues.length == values.length) {
            selector.setNameProvider(value -> {
                for (int i = 0; i < values.length; i++) {
                    if (Objects.equals(values[i], value)) {
                        return I18n.format(displayValues[i]);
                    }
                }
                return value;
            });
        }
        selector.setSaveConsumer(value -> saveValue(element, selected, value, requiresRestart, configId, saveState));
        if (requiresRestart) {
            selector.requireRestart();
        }
        return editable(selector.build(), requiresWorldRestart);
    }

    private static AbstractConfigListEntry createListEntry(
            ConfigEntryBuilder builder,
            IConfigElement element,
            boolean forceWorldRestart,
            boolean forceMcRestart,
            String configId,
            SaveState saveState) {
        String name = displayName(element);
        boolean requiresWorldRestart = forceWorldRestart || element.requiresWorldRestart();
        boolean requiresRestart = forceMcRestart || element.requiresMcRestart();
        boolean fixedLength = element.isListLengthFixed();
        switch (element.getType()) {
            case BOOLEAN: {
                ValueState<List<String>> value = new ValueState<>(toStringList(element.getList()));
                StringListBuilder list = builder.startStrList(name, value.value)
                        .setDeleteButtonEnabled(!fixedLength)
                        .setInsertInFront(!fixedLength)
                        .setCellErrorSupplier(newValue -> isBoolean(newValue) ? Optional.empty() : Optional.of(I18n.format("text.cloth-config.error.not_valid_boolean")))
                        .setSaveConsumer(values -> saveList(element, value, values, requiresRestart, configId, saveState));
                tooltip(element).ifPresent(list::setTooltip);
                list.setDefaultValue(toStringList(element.getDefaults()));
                if (requiresRestart) {
                    list.requireRestart();
                }
                return editable(list.build(), requiresWorldRestart);
            }
            case INTEGER: {
                ValueState<List<Integer>> value = new ValueState<>(toIntegerList(element.getList()));
                IntListBuilder list = builder.startIntList(name, value.value)
                        .setDeleteButtonEnabled(!fixedLength)
                        .setInsertInFront(!fixedLength)
                        .setMin(parseInt(element.getMinValue(), Integer.MIN_VALUE))
                        .setMax(parseInt(element.getMaxValue(), Integer.MAX_VALUE))
                        .setSaveConsumer(values -> saveList(element, value, values, requiresRestart, configId, saveState));
                tooltip(element).ifPresent(list::setTooltip);
                list.setDefaultValue(toIntegerList(element.getDefaults()));
                if (requiresRestart) {
                    list.requireRestart();
                }
                return editable(list.build(), requiresWorldRestart);
            }
            case DOUBLE: {
                ValueState<List<Double>> value = new ValueState<>(toDoubleList(element.getList()));
                DoubleListBuilder list = builder.startDoubleList(name, value.value)
                        .setDeleteButtonEnabled(!fixedLength)
                        .setInsertInFront(!fixedLength)
                        .setMin(parseDouble(element.getMinValue(), -Double.MAX_VALUE))
                        .setMax(parseDouble(element.getMaxValue(), Double.MAX_VALUE))
                        .setSaveConsumer(values -> saveList(element, value, values, requiresRestart, configId, saveState));
                tooltip(element).ifPresent(list::setTooltip);
                list.setDefaultValue(toDoubleList(element.getDefaults()));
                if (requiresRestart) {
                    list.requireRestart();
                }
                return editable(list.build(), requiresWorldRestart);
            }
            case COLOR:
            case MOD_ID:
            case STRING: {
                ValueState<List<String>> value = new ValueState<>(toStringList(element.getList()));
                StringListBuilder list = builder.startStrList(name, value.value)
                        .setDeleteButtonEnabled(!fixedLength)
                        .setInsertInFront(!fixedLength)
                        .setSaveConsumer(values -> saveList(element, value, values, requiresRestart, configId, saveState));
                tooltip(element).ifPresent(list::setTooltip);
                Pattern pattern = element.getValidationPattern();
                if (pattern != null) {
                    list.setCellErrorSupplier(newValue -> pattern.matcher(newValue).matches() ? Optional.empty() : Optional.of(I18n.format("fml.configgui.tooltip.invalidValue")));
                }
                list.setDefaultValue(toStringList(element.getDefaults()));
                if (requiresRestart) {
                    list.requireRestart();
                }
                return editable(list.build(), requiresWorldRestart);
            }
            default:
                return unsupportedEntry(element, "Unsupported Forge list config type: " + element.getType());
        }
    }

    private static AbstractConfigListEntry editable(AbstractConfigListEntry entry, boolean requiresWorldRestart) {
        entry.setEditable(Minecraft.getMinecraft().world == null || !requiresWorldRestart);
        return entry;
    }

    private static void saveAndPostConfigEvents(String modId, SaveState saveState) {
        try {
            if (!saveState.hasChanges()) {
                return;
            }

            if (Loader.isModLoaded(modId)) {
                boolean isWorldRunning = Minecraft.getMinecraft().world != null;
                for (Map.Entry<String, Boolean> change : saveState.changes.entrySet()) {
                    ConfigChangedEvent event = new ConfigChangedEvent.OnConfigChangedEvent(modId, change.getKey(), isWorldRunning, change.getValue());
                    MinecraftForge.EVENT_BUS.post(event);
                    if (!event.getResult().equals(Event.Result.DENY)) {
                        MinecraftForge.EVENT_BUS.post(new ConfigChangedEvent.PostConfigChangedEvent(modId, change.getKey(), isWorldRunning, change.getValue()));
                    }
                }
            }

            ConfigManager.sync(modId, Config.Type.INSTANCE);
        } finally {
            saveState.reset();
        }
    }

    private static <T> void saveValue(IConfigElement element, ValueState<T> original, T value, boolean requiresRestart, String configId, SaveState saveState) {
        if (!Objects.equals(original.value, value)) {
            element.set(value);
            original.value = value;
            saveState.mark(configId, requiresRestart);
        }
    }

    private static <T> void saveList(IConfigElement element, ValueState<List<T>> original, List<T> values, boolean requiresRestart, String configId, SaveState saveState) {
        if (!Objects.equals(original.value, values)) {
            element.set(values.toArray());
            original.value = new ArrayList<>(values);
            saveState.mark(configId, requiresRestart);
        }
    }

    private static AbstractConfigListEntry unsupportedEntry(IConfigElement element, String reason) {
        return new TextListEntry(displayName(element), reason, 0xAAAAAA, () -> tooltip(element));
    }

    private static void addText(ConfigCategory category, String text) {
        category.addEntry(new TextListEntry(text, text, 0xAAAAAA));
    }

    private static String titleWithSubtitle(GuiConfig guiConfig) {
        if (guiConfig.titleLine2 == null || guiConfig.titleLine2.trim().isEmpty()) {
            return guiConfig.title;
        }
        return guiConfig.title + " > " + guiConfig.titleLine2;
    }

    private static List<IConfigElement> visibleChildren(IConfigElement element) {
        return element == null ? new ArrayList<>() : visibleChildren(element.getChildElements());
    }

    private static List<IConfigElement> visibleChildren(List<IConfigElement> elements) {
        if (elements == null) {
            return new ArrayList<>();
        }
        return elements.stream().filter(Objects::nonNull).filter(IConfigElement::showInGui).collect(Collectors.toList());
    }

    private static String displayName(IConfigElement element) {
        String languageKey = element.getLanguageKey();
        if (languageKey != null && !languageKey.isEmpty()) {
            String translated = I18n.format(languageKey);
            if (!translated.equals(languageKey)) {
                return translated;
            }
        }
        return element.getName();
    }

    private static Optional<String[]> tooltip(IConfigElement element) {
        String languageKey = element.getLanguageKey();
        if (languageKey != null && !languageKey.isEmpty()) {
            String tooltipKey = languageKey + ".tooltip";
            String translatedTooltip = I18n.format(tooltipKey);
            if (!translatedTooltip.equals(tooltipKey)) {
                return Optional.of(translatedTooltip.split("\\\\n|\\n"));
            }
        }
        String comment = element.getComment();
        return comment == null || comment.trim().isEmpty() ? Optional.empty() : Optional.of(comment.split("\\\\n|\\n"));
    }

    private static boolean hasValidValues(IConfigElement element) {
        return element.getValidValues() != null && element.getValidValues().length > 0;
    }

    private static <T> Optional<T> defaultValue(IConfigElement element, Class<T> type) {
        Object value = convert(element.getDefault(), type);
        return type.isInstance(value) ? Optional.of(type.cast(value)) : Optional.empty();
    }

    private static Object convert(Object value, Class<?> type) {
        if (type == Boolean.class) {
            return parseBoolean(value, false);
        }
        if (type == Integer.class) {
            return parseInt(value, 0);
        }
        if (type == Double.class) {
            return parseDouble(value, 0.0);
        }
        if (type == String.class) {
            return Objects.toString(value, "");
        }
        return value;
    }

    private static boolean parseBoolean(Object value, Object fallback) {
        String string = Objects.toString(value, Objects.toString(fallback, "false"));
        return Boolean.parseBoolean(string);
    }

    private static int parseInt(Object value, Object fallback) {
        try {
            return Integer.parseInt(Objects.toString(value));
        } catch (RuntimeException e) {
            return Integer.parseInt(Objects.toString(fallback, "0"));
        }
    }

    private static double parseDouble(Object value, Object fallback) {
        try {
            return Double.parseDouble(Objects.toString(value));
        } catch (RuntimeException e) {
            return Double.parseDouble(Objects.toString(fallback, "0.0"));
        }
    }

    private static int parseColor(Object value, Object fallback) {
        String raw = Objects.toString(value, Objects.toString(fallback, "0"))
                .replace("#", "")
                .replace("0x", "")
                .replace("0X", "");
        try {
            return Integer.parseInt(raw, 16);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    private static List<String> toStringList(Object[] values) {
        return Arrays.stream(values == null ? new Object[0] : values).map(value -> Objects.toString(value, "")).collect(Collectors.toList());
    }

    private static List<Integer> toIntegerList(Object[] values) {
        return Arrays.stream(values == null ? new Object[0] : values).map(value -> parseInt(value, 0)).collect(Collectors.toList());
    }

    private static List<Double> toDoubleList(Object[] values) {
        return Arrays.stream(values == null ? new Object[0] : values).map(value -> parseDouble(value, 0.0)).collect(Collectors.toList());
    }

    private static final class SaveState {
        private final Map<String, Boolean> changes = new LinkedHashMap<>();

        private boolean hasChanges() {
            return !changes.isEmpty();
        }

        private void mark(String configId, boolean requiresMcRestart) {
            changes.merge(configId, requiresMcRestart, Boolean::logicalOr);
        }

        private void reset() {
            changes.clear();
        }
    }

    private static final class ValueState<T> {
        private T value;

        private ValueState(T value) {
            this.value = value;
        }
    }
}
