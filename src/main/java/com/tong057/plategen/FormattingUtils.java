package com.tong057.plategen;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class FormattingUtils {

    // Comparator considering Polish letters
    public static Comparator<String> polishComparator = (s1, s2) -> {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("pl-PL"));
        return collator.compare(s1, s2);
    };

    public static TextFormatter<Integer> setNumericTextFormatter() {
        return new TextFormatter<>(
            new IntegerStringConverter(),
            0,
            change -> (change.getControlNewText().matches("\\d*") && change.getControlNewText().length() <= 4) ? change : null
    );
    }

    public static TextFormatter<String> setLimitedTextFormatter() {
        return new TextFormatter<>(
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.length() <= 12) {
                        return change;
                    }
                    return null;
                }
        );
    }

    public static TextFormatter<String> setCustomPlateTextFormatter(String voivodeshipLetter) {
        UnaryOperator<TextFormatter.Change> customPlateTextFormatter = change -> {
            String newText = change.getControlNewText();

            // Replace the first character with voivodeshipLetter
            if (newText.length() == 1) {
                char firstChar = newText.charAt(0);
                change.setText(change.getText().replace(Character.toString(firstChar), voivodeshipLetter));
            }

            // Check the second character (should be a digit)
            if (newText.length() == 2) {
                char secondChar = newText.charAt(1);
                if (!Character.isDigit(secondChar)) {
                    change.setText(change.getText().replace(Character.toString(secondChar), ""));
                }
            }

            // Replace the third character with a space
            if (newText.length() == 3) {
                char thirdChar = newText.charAt(2);
                change.setText(change.getText().replace(Character.toString(thirdChar), " "));
            }

            if (newText.length() > 3) {
                char c = newText.charAt(newText.length() - 1);
                if (!isValidChar(c)) {
                    change.setText(change.getText().replace(Character.toString(c), ""));
                }
            }

            // Limit the total number of characters
            if (newText.length() > 8) {
                return null;
            }

            return change;
        };

        // Convert the value for display in the text field
        StringConverter<String> converter = new DefaultStringConverter();
        // Apply input character restrictions and conversion
        return new TextFormatter<>(converter, null, customPlateTextFormatter);
    }

    private static boolean isValidChar(char c) {
        String[] allowedLetters = PlateGenerator.TABLE_LETTERS;
        String[] allowedNumbers = PlateGenerator.TABLE_NUMBERS;

        // New array with sufficient size to combine both arrays
        String[] allowedChars = new String[allowedLetters.length + allowedNumbers.length];
        System.arraycopy(allowedLetters, 0, allowedChars, 0, allowedLetters.length);
        System.arraycopy(allowedNumbers, 0, allowedChars, allowedLetters.length, allowedNumbers.length);

        for (String allowedChar : allowedChars) {
            if (Character.toString(c).equalsIgnoreCase(allowedChar)) {
                return true;
            }
        }
        return false;
    }


}
