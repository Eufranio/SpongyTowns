package io.github.eufranio.spongytowns.util;

import com.google.common.collect.Lists;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

/**
 * Created by Frani on 06/03/2018.
 */
public class InfoBuilder {

    private List<Text> editing = Lists.newArrayList();
    private Text title;
    private Text header;

    public InfoBuilder add(List<Text> text) {
        this.editing.addAll(text);
        return this;
    }

    public InfoBuilder add(Text text) {
        this.editing.add(text);
        return this;
    }

    public InfoBuilder add(Text.Builder text) {
        this.editing.add(text.toText());
        return this;
    }

    public InfoBuilder title(String title) {
        this.title = Util.toText(title);
        return this;
    }

    public InfoBuilder title(Text title) {
        this.title = title;
        return this;
    }

    public InfoBuilder header(String header) {
        this.header = Util.toText(header);
        return this;
    }

    public InfoBuilder header(Text header) {
        this.header = header;
        return this;
    }

    public void sendTo(MessageReceiver receiver) {
        PaginationList.Builder b = PaginationList.builder()
                .contents(this.editing)
                .padding(Text.of(
                        TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                ));
        if (this.header != null) b = b.header(this.header);
        if (this.title != null) b = b.title(Text.of(TextColors.GREEN, this.title));
        b.sendTo(receiver);
    }

    public static InfoBuilder create() {
        return new InfoBuilder();
    }

    public Text build() {
        Text initial = this.editing.remove(0);
        for (Text text : this.editing) {
            initial = Text.of(initial, Text.NEW_LINE, text);
        }
        return initial;
    }

}