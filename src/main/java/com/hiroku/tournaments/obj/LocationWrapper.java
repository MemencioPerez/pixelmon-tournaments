package com.hiroku.tournaments.obj;

import com.happyzleaf.tournaments.text.Text;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocationWrapper {
	public final RegistryKey<World> dimensionKey;
	public final Vector3d position;
	public final Vector2f rotation;

	public LocationWrapper(RegistryKey<World> dimensionKey, Vector3d position, Vector2f rotation) {
		this.dimensionKey = dimensionKey;
		this.position = position;
		this.rotation = rotation;
	}

	public LocationWrapper(World world, Vector3d position, Vector2f rotation) {
		this.dimensionKey = world == null ? null : world.getDimensionKey();
		this.position = position;
		this.rotation = rotation;
	}

	public LocationWrapper(Entity at) {
		this(checkNotNull(at, "at").getEntityWorld(), at.getPositionVec(), at.getPitchYaw());
	}

	public void sendPlayer(PlayerEntity player) {
		if (this.dimensionKey == null) {
			// TODO: proper error? When would this happen?
			player.sendMessage(Text.ERROR, Util.DUMMY_UUID);
			return;
		}

		if (player.getServer() != null) {
			Optional<ServerWorld> world = Optional.ofNullable(player.getServer().getWorld(this.dimensionKey));
			if (world.isPresent()) {
				Vector3d position = this.position == null ? player.getPositionVec() : this.position;
				Vector2f rotation = this.rotation == null ? player.getPitchYaw() : this.rotation;
				((ServerPlayerEntity) player).teleport(world.get(), position.getX(), position.getY(), position.getZ(), rotation.x, rotation.y);
			}
		}
	}
}
