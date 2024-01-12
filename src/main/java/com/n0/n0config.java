package com.n0;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("n0")
public interface n0config extends Config
{
	String GROUP = "pickup count";

	//i don't understand all this lol
	@ConfigItem(
			keyName = "balance",
			name = "Balance",
			description = "How much money do you have?"
	)
	default int balance()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "balance",
			name = "Balance",
			description = "How much money do you have?"
	)
	void setBalance(int balance);

}
