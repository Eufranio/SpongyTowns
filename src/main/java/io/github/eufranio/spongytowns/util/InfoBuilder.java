package io.github.eufranio.spongytowns.util;

import org.spongepowered.api.text.Text;

/**
 * Created by Frani on 06/03/2018.
 */
public class InfoBuilder {

    private Text editing;

    public InfoBuilder add(Text text) {
        if (this.editing == null) {
            this.editing = Text.of(text);
        } else {
            this.editing = Text.of(this.editing, Text.NEW_LINE, text);
        }
        return this;
    }

    public Text build() {
        return editing;
    }

}