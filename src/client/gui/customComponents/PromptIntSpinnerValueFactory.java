package client.gui.customComponents;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class PromptIntSpinnerValueFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {
    public PromptIntSpinnerValueFactory(int min, int max, int init, String prompt) {
        super(min, max, init);
        setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer num) {
                return prompt + ": " + num;
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string.replaceAll("[^0-9]", ""));
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }
}
