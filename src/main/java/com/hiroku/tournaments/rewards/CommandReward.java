package com.hiroku.tournaments.rewards;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.reward.RewardBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Reward which executes a command with the player's username inserted in place of {{player}}
 *
 * @author Hiroku
 */
public class CommandReward extends RewardBase {
	public Text displayText = Text.of("");
	public String command = "";

	public CommandReward(String arg) throws Exception {
		super(arg);

		if (arg.trim().isEmpty())
			throw new IllegalArgumentException("Missing arguments: text and command");

		if (arg.contains("text:")) {
			String subArg = arg.substring(arg.indexOf("text:") + 5).split(";")[0];
			displayText = Text.deserialize(subArg);
		}
		if (arg.contains("cmd:"))
			command = arg.substring(arg.indexOf("cmd:") + 4).split(";")[0];

		if (command.isEmpty())
			throw new IllegalArgumentException("Missing argument: cmd");
	}

	@Override
	public void give(PlayerEntity player) {
		if (command.startsWith("/"))
			command = command.replace("/", "");

		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		server.getCommandManager().handleCommand(server.getCommandSource(), command.replaceAll("\\{\\{player}}", player.getScoreboardName()));
	}

	@Override
	public String getSerializationString() {
		return "command:text:" + displayText.serialize() + ";cmd:" + command;
	}

	@Override
	public Text getDisplayText() {
		return displayText;
	}

	@Override
	public boolean visibleToAll() {
		return !displayText.toPlain().isEmpty();
	}
}
