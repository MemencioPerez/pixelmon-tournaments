package com.hiroku.tournaments.rewards;

import com.happyzleaf.tournaments.text.Text;
import com.hiroku.tournaments.api.reward.RewardBase;
import com.pixelmonmod.pixelmon.api.economy.BankAccount;
import com.pixelmonmod.pixelmon.api.economy.BankAccountProxy;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Reward that gives money to the player.
 *
 * @author Hiroku
 */
public class MoneyReward extends RewardBase {
	/**
	 * How much money should be given
	 */
	public double amount;

	public MoneyReward(String arg) throws Exception {
		super(arg);

		amount = Double.parseDouble(arg);
	}

	@Override
	public void give(PlayerEntity player) {
		Optional<? extends BankAccount> bankAccount = BankAccountProxy.getBankAccount(player.getUniqueID());
		if (bankAccount.isPresent()) {
			bankAccount.get().add(BigDecimal.valueOf(amount));
			player.sendMessage(Text.of(TextFormatting.DARK_GREEN, "You were rewarded $", amount), Util.DUMMY_UUID);
		}
	}

	@Override
	public Text getDisplayText() {
		return Text.of(TextFormatting.GOLD, "Money: ", TextFormatting.DARK_AQUA, amount);
	}

	@Override
	public boolean visibleToAll() {
		return true;
	}

	@Override
	public String getSerializationString() {
		return "cashmoney:" + amount;
	}
}
