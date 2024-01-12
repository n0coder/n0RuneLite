package com.n0;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "nano"
)
public class n0plugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private n0config config;
	@Inject
	private InfoBoxManager infoBoxManager;
	private ItemCounter itemBox;

	private int incrementBalance(int amount)
	{
		int bal = config.balance() + amount;
		config.setBalance(bal);
		return bal;
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			/* this dumb code doesn't work, i don't quite understand how to work it lol
			itemBox = ItemCounter.CreateCounter(this, bal, ":)", 449);
			infoBoxManager.addInfoBox(itemBox);
			*/
				// Print out every item in the inventory
				for (Item item : client.getItemContainer(InventoryID.INVENTORY).getItems()) {
					previousInventoryItems.add(item.getId());
				}

		}
	}
	private Set<Integer> previousGroundItems = new HashSet<>();
	private Map<Integer, Integer> itemLastSeenTicks = new HashMap<>();
	private static final int RADIUS = 10; // Change this to your desired radius
	@Subscribe
	public void onGameTick(GameTick event) {
		int ticks = client.getTickCount();

		Player player = client.getLocalPlayer();
		WorldPoint playerLocation = player.getWorldLocation();

		Set<Integer> currentGroundItems = new HashSet<>();
		Tile[][][] tilesss = client.getScene().getTiles();
		for (Tile[][] tiless : tilesss)
			for (Tile[] tiles : tiless)
				for (Tile tile : tiles)
		{
			if (tile != null)
			{
				WorldPoint itemLocation = WorldPoint.fromLocal(client, tile.getLocalLocation());
				double distance = Math.sqrt(Math.pow(playerLocation.getX() - itemLocation.getX(), 2) + Math.pow(playerLocation.getY() - itemLocation.getY(), 2));

				// Only consider tiles within the radius (only care about the distance of tiles so we don't process every item)
				if (distance <= RADIUS)
				{
				List<TileItem> items = tile.getGroundItems();
				if (items != null)
				{
					for (TileItem item : items)
					{
						currentGroundItems.add(item.getId());
					}
				}
			}
		}
		}
		Set<Integer> despawns = new HashSet<>();
		// Check for items that have disappeared
		for (Integer itemId : previousGroundItems)
            if (!currentGroundItems.contains(itemId)) {
				despawns.add(itemId);
            }

		// Check for items that have expired
        itemLastSeenTicks.forEach((key, value) -> {
            int itemId = key;
            int lastSeenTicks = value;

			if (despawns.contains(itemId) ) {
				String itemDef = client.getItemDefinition(itemId).getName();
				int bal = incrementBalance(1);
				print("item", "[pickups: "+bal+"]: "+itemDef+" was picked up");
				itemLastSeenTicks.remove(itemId);
			}

            if (ticks >= lastSeenTicks) {
                itemLastSeenTicks.remove(itemId);
            }
        });

		// Update the set of previous ground items
		previousGroundItems = currentGroundItems;

	}
	private Set<Integer> previousInventoryItems = new HashSet<>();
	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int ticks = client.getTickCount();
		ItemContainer container = event.getItemContainer();
		StringBuilder items = new StringBuilder();
		// Check if the change is in the player's inventory
		if (container == client.getItemContainer(InventoryID.INVENTORY))
		{
			Set<Integer> inventoryItems = new HashSet<>();
			// Print out every item in the inventory
			for (Item item : container.getItems())
			{
				inventoryItems.add(item.getId());
			}


			for (int item : inventoryItems) {
				if (!previousInventoryItems.contains(item)) {
					itemLastSeenTicks.put(item, ticks);
					//itemAddedToInventory(item, ticks);
				}
			}
			previousInventoryItems = inventoryItems;

		}
	}
	void itemAddedToInventory(int itemId, int ticks) {
		print("ono", "an item "+itemId+" entered inventory: "+ticks);
	}
	void itemDespawned(int itemId, int ticks) {

		//currentGroundItems.contains(itemId)
		print("ono", "an item "+itemId+" despawned: "+ticks);
	}
	void print(String name, String msg) {
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, name, msg, null);
	}

	@Provides
	n0config provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(n0config.class);
	}
}
