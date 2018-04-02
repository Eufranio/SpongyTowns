package io.github.eufranio.spongytowns.interfaces;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.display.Visual;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Frani on 20/02/2018.
 */
public interface Claim extends Persistant, Permissible, Bank, Purgeable {

    default List<Claim> getChilds() {
        return null;
    }

    List<ClaimBlock> getBlocks();

    default boolean hasChilds() {
        return this.getChilds() != null && !this.getChilds().isEmpty();
    }

    default Claim getParent() {
        return null;
    }

    default boolean hasParent() {
        return this.getParent() != null;
    }

    Location<World> getSpawn();

    void setSpawn(Location<World> spawn);

    void setDisplayName(Text name);

    Text getDisplayName();

    void setName(String name);

    String getName();

    ClaimBlock claim(Location<World> location);

    default void remove() {
        SpongyTowns.getStorage().remove(this);
    }

    default Text getInfo() {
        return new InfoBuilder()
                .add(TownMessages.getInstance().TOWN_INFO.NAME.apply(ImmutableMap.of(
                        "name", this.getName()
                )).toText())
                .add(TownMessages.getInstance().TOWN_INFO.OWNER.apply(ImmutableMap.of(
                        "owner", Util.getUser(this.getOwner()).getName(),
                        "uuid", this.getOwner().toString()
                )).toText())
                .add(TownMessages.getInstance().TOWN_INFO.CHUNKS.apply(ImmutableMap.of(
                        "chunks", this.getBlocks().size()
                )).toText())
                .add(TownMessages.getInstance().TOWN_INFO.RESIDENTS.apply(ImmutableMap.of(
                        "residents", this.getMembers().size() > 0 ?
                                Text.joinWith(Text.of(TextColors.GOLD, ", "),
                                this.getMembers().stream().map(uuid -> Text.of(TextColors.YELLOW, Util.getUser(uuid).getName())).collect(Collectors.toList()))
                                :
                                Text.of(TextColors.GOLD, "None")
                )).toText())
                .add(TownMessages.getInstance().TOWN_INFO.PLOTS.apply(ImmutableMap.of(
                        "plots", this.getChilds().size() > 0 ?
                                Text.joinWith(Text.of(TextColors.GOLD, ", "),
                                this.getChilds().stream().map(Claim::getDisplayName).collect(Collectors.toList()))
                                :
                                Text.of(TextColors.GOLD, "None")
                )).toText())
                .build();
    }

    default Text getInfoHover() {
        return this.getDisplayName()
                .toBuilder()
                .onHover(TextActions.showText(this.getInfo()))
                .build();
    }

    default void sendInfo(MessageReceiver rec) {
        PaginationList.builder()
                .contents(this.getInfo())
                .padding(Text.of(
                        TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"
                ))
                .title(Text.of(TextColors.GREEN, this.getName() + "'s Info"))
                .sendTo(rec);
    }

    default boolean hasClaimed(Location<World> location) {
        return this.hasClaimed(location.getChunkPosition(), location.getExtent().getUniqueId());
    }

    default boolean hasClaimed(Vector3i chunk, UUID world) {
        return this.getBlocks().stream().anyMatch(b -> b.getLocation().equals(chunk) && b.getWorld().equals(world));
    }

    default Bank getBank() {
        return this;
    }

    default List<Player> getOnlineUsers() {
        List<Player> users = Lists.newArrayList();
        for (UUID uuid : this.getMembers()) {
            Sponge.getServer().getPlayer(uuid).ifPresent(users::add);
        }
        Sponge.getServer().getPlayer(this.getOwner()).ifPresent(users::add);
        return users;
    }

    boolean isAdmin();

    void setAdmin(boolean admin);

}
