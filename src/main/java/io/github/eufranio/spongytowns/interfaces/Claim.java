package io.github.eufranio.spongytowns.interfaces;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.TownMessages;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.permission.Resident;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.InfoBuilder;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
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

    String getTeam();

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

    default int getTax() {
        Options.OptionEntry<Integer> entry = this instanceof Town ? Options.DAILY_TAX_TOWN : Options.DAILY_TAX_PLOT;
        int value = Util.getIntOption(this.getOwnerUser(), entry);

        Options.OptionEntry<Integer> perBlockCost = this instanceof Town ? Options.DAILY_TAX_TOWNCLAIM : Options.DAILY_TAX_PLOTCLAIM;
        value = value + (this.getBlocks().size() > 0 ? (this.getBlocks().size() * Util.getIntOption(this.getOwnerUser(), perBlockCost)) : 0);

        Options.OptionEntry<Integer> perResident = Options.DAILY_TAX_RESIDENT;
        value = value + (this.getMembers().size() > 0 ? (this.getMembers().size() * Util.getIntOption(this.getOwnerUser(), perResident)) : 0);

        return value;
    }

    default Optional<ClaimBlock> getBlockAt(Location<World> location) {
        return this.getBlocks().stream()
                .filter(cb -> cb.getLocation().equals(location.getChunkPosition()))
                .findFirst();
    }

    default boolean hasPermission(User user) {
        return user.getUniqueId().equals(this.getOwner()) || this.getMembers().contains(user.getUniqueId());
    }

    default void sendDenyMessage(TextTemplate template, Player player) {
        player.sendMessage(template.apply(ImmutableMap.of(
                "claim", this.getInfoHover()
        )).toText());
    }

}
