package pdf.anime.fastsellcmi.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.Map;

public class PlaceholderUtils {

    /**
     * Replaces simple string placeholders in a Component.
     *
     * @param component    The original Component containing placeholders.
     * @param placeholders A map where keys are the placeholder strings
     *                     and values are their string replacements.
     * @return A new Component with all specified placeholders replaced.
     */
    public static Component replacePlaceholders(Component component, Map<String, String> placeholders) {
        if (component == null || placeholders == null || placeholders.isEmpty()) {
            return component;
        }

        Component mutableComponent = component;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholderKey = entry.getKey();
            String replacementValue = entry.getValue();

            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                    .matchLiteral(placeholderKey)
                    .replacement(Component.text(replacementValue))
                    .build();

            mutableComponent = mutableComponent.replaceText(replacementConfig);
        }

        return mutableComponent;
    }

    /**
     * Replaces a single simple string placeholder in a Component.
     *
     * @param component        The original Component containing the placeholder.
     * @param placeholderKey   The exact placeholder string to match (e.g., "{total}").
     * @param replacementValue The string to replace the placeholder with.
     * @return A new Component with the specified placeholder replaced.
     */
    public static Component replacePlaceholder(Component component, String placeholderKey, String replacementValue) {
        if (component == null || placeholderKey == null || replacementValue == null) {
            return component;
        }

        TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                .matchLiteral(placeholderKey)
                .replacement(Component.text(replacementValue))
                .build();

        return component.replaceText(replacementConfig);
    }
}
