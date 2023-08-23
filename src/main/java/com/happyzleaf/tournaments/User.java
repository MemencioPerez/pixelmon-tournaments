package com.happyzleaf.tournaments;

import com.mojang.authlib.GameProfile;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author happyz
 */
public class User {
	public final UUID id;
	public final GameProfile profile;

	public User(UUID id) {
		this.id = checkNotNull(id, "id");

		// TODO: This might be null T.T
		this.profile = ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(id);
	}

	public User(GameProfile profile) {
		this.id = checkNotNull(profile, "profile").getId();
		this.profile = profile;
	}

	public User(PlayerEntity player) {
		this.id = checkNotNull(player, "player").getUniqueID();
		this.profile = player.getGameProfile();
	}

	public PlayerEntity getPlayer() {
		return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(id);
	}

	public boolean isOnline() {
		return getPlayer() != null;
	}

	public String getName() {
		return profile.getName();
	}

	public void sendMessage(Text text) {
		PlayerEntity player = getPlayer();
		if (player != null) {
			player.sendMessage(text, Util.DUMMY_UUID);
		}
	}

	public PlayerPartyStorage getParty() {
		return StorageProxy.getParty(id);
	}
}
