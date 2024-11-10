package com.hiroku.tournaments.listeners;

import com.pixelmonmod.pixelmon.api.events.PokedexEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DexListener
{
	@SubscribeEvent
	public void onDex(PokedexEvent event)
	{
		EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(event.uuid);
		if (event.pokemon == null || player == null)
			return;
		if (event.pokemon.hasSpecFlag("rental"))
			event.setCanceled(true);
	}
}