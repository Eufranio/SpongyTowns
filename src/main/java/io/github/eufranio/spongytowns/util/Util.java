package io.github.eufranio.spongytowns.util;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.display.PermissionMessages;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.interfaces.ClaimBlock;
import io.github.eufranio.spongytowns.permission.Options;
import io.github.eufranio.spongytowns.towns.PlotClaim;
import io.github.eufranio.spongytowns.towns.TownClaim;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Frani on 20/02/2018.
 */
public class Util {

    private static Method isOpaqueField;

    public static final Direction[] CARDINAL_SET = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static final Direction[] ORDINAL_SET = new Direction[]{Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST};

    public static UUID SERVER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static Text toText(String s) {
        return TextSerializers.FORMATTING_CODE.deserialize(s);
    }

    public static String fromText(Text t) {
        return TextSerializers.FORMATTING_CODE.serialize(t);
    }

    public static UUID getPlayerOrTarget(CommandSource sender, CommandContext context, String command) throws CommandException {
        Optional<User> opt = context.getOne("player");
        UUID uuid;
        if (opt.isPresent()) {
            if (!sender.hasPermission("spongytowns.commands." + command + ".others")) {
                throw new CommandException(PermissionMessages.getInstance().NO_PERMISSION_COMMAND.toText());
            }
            uuid = opt.get().getUniqueId();
        } else {
            if (sender instanceof ConsoleSource) {
                throw new CommandException(Text.of("This command cannot be executed via console without specifying a player!"));
            }
            uuid = ((Identifiable) sender).getUniqueId();
        }
        return uuid;
    }

    public static void error(String msg) throws CommandException {
        throw new CommandException(Text.of(msg));
    }

    public static void error(Text msg) throws CommandException {
        throw new CommandException(msg);
    }

    public static void error(TextTemplate template) throws CommandException {
        error(template.toText());
    }

    public static int getIntOption(Subject subject, Options.OptionEntry<Integer> entry) {
        return Integer.parseInt(subject.getOption(entry.getOption()).orElse(entry.getDefaultValue().toString()));
    }

    public static Optional<TownClaim> getClosestClaim(Location<World> location) {
        Chunk chunk = location.getExtent().loadChunk(location.getChunkPosition(), true).get();
        for (Direction d : CARDINAL_SET) {
            Chunk c2 = chunk.getNeighbor(d, true).get();
            Optional<TownClaim> claim = SpongyTowns.getManager().getClaimBlockAt(c2.getPosition(), c2.getWorld().getUniqueId());
            if (claim.isPresent()) return claim;
        }
        return Optional.empty();
    }

    public static Optional<ClaimBlock> getClosestPlot(Location<World> location) {
        Chunk chunk = location.getExtent().loadChunk(location.getChunkPosition(), true).get();
        for (Direction d : CARDINAL_SET) {
            Chunk c2 = chunk.getNeighbor(d, true).get();
            Optional<PlotClaim> claim = SpongyTowns.getManager().getPlotAt(c2.getPosition(), c2.getWorld().getUniqueId());
            if (claim.isPresent()) return claim.map(ClaimBlock.class::cast);
        }
        return Optional.empty();
    }

    public static List<ClaimBlock> getNearBlocks(Subject subject, Location<World> location, Claim excluding) {
        List<ClaimBlock> blocks = getNearBlocks(subject, location);
        blocks.removeAll(excluding.getBlocks());
        if (excluding.hasChilds()) {
            excluding.getChilds().forEach(child -> blocks.removeAll(child.getBlocks()));
        }
        return blocks;
    }

    public static List<ClaimBlock> getNearBlocks(Subject subject, Location<World> location) {
        List<ClaimBlock> blocks = Lists.newArrayList();
        int distance = subject == null ? Options.CHUNKS_BETWEEN_TOWNS.getDefaultValue() : getIntOption(subject, Options.CHUNKS_BETWEEN_TOWNS);
        for (int x = -distance; x < distance; x++) {
            for (int z = -distance; z < distance; z++) {
                Vector3i position = new Vector3i(location.getChunkPosition().getX() + x, 0, location.getChunkPosition().getZ() + z);
                SpongyTowns.getManager().getClaimBlockAt(position, location.getExtent().getUniqueId()).ifPresent(blocks::add);
            }
        }
        return blocks;
    }

    public static User getUser(UUID uuid) {
        Optional<Player> opt = Sponge.getServer().getPlayer(uuid);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            UserStorageService service = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            return service.get(uuid).orElse(null);
        }
    }

    public static Location<World> getVisibleLocation(World world, int x, int y, int z, boolean waterIsTransparent) {
        Location<World> location = world.getLocation(x, y, z);
        Direction direction = (isTransparent(location.getBlock(), waterIsTransparent)) ? Direction.DOWN : Direction.UP;

        while (location.getPosition().getY() >= 1 &&
                location.getPosition().getY() < world.getDimension().getBuildHeight() - 1 &&
                (!isTransparent(location.getRelative(Direction.UP).getBlock(), waterIsTransparent)
                        || isTransparent(location.getBlock(), waterIsTransparent))) {
            location = location.getRelative(direction);
        }

        return location;
    }

    // helper method for above. allows visualization blocks to sit underneath partly transparent blocks like grass and fence
    private static boolean isTransparent(BlockState blockstate, boolean waterIsTransparent) {
        if (blockstate.getType() == BlockTypes.SNOW_LAYER) {
            return false;
        }

        IBlockState iblockstate = (IBlockState) blockstate;
        Optional<MatterProperty> matterProperty = blockstate.getProperty(MatterProperty.class);
        if (!waterIsTransparent && matterProperty.isPresent() && matterProperty.get().getValue() == MatterProperty.Matter.LIQUID) {
            return false;
        }
        return !isOpaque(iblockstate);
    }

    private static boolean isOpaque(IBlockState state) {
        try {
            if (isOpaqueField == null) {
                isOpaqueField = IBlockProperties.class.getMethod("func_185914_p");
            }
            return (Boolean) isOpaqueField.invoke(state);
        } catch (Exception e) {}
        return false;
    }

    // from GP
    public static User getEventUser(Event event) {
        final Cause cause = event.getCause();
        final EventContext context = event.getContext();
        User user = null;
            user = cause.first(User.class).orElse(null);
            if (user != null && user instanceof EntityPlayer && user.getName().startsWith("[")) {
                user = null;
            }

        if (user == null) {
            // Always use owner for ticking TE's
            // See issue MinecraftPortCentral/GriefPrevention#610 for more information
            if (cause.root() instanceof TileEntity) {
                user = context.get(EventContextKeys.OWNER)
                        .orElse(context.get(EventContextKeys.NOTIFIER)
                                .orElse(context.get(EventContextKeys.CREATOR)
                                        .orElse(null)));
            } else {
                user = context.get(EventContextKeys.NOTIFIER)
                        .orElse(context.get(EventContextKeys.OWNER)
                                .orElse(context.get(EventContextKeys.CREATOR)
                                        .orElse(null)));
            }
        }

        if (user == null) {
            if (event instanceof ExplosionEvent) {
                // Check igniter
                final Living living = context.get(EventContextKeys.IGNITER).orElse(null);
                if (living != null && living instanceof User) {
                    user = (User) living;
                }
            }
        }

        return user;
    }

}
