package com.n0;

import com.google.inject.Inject;
import lombok.Getter;

import java.awt.image.BufferedImage;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

//this item counter idea was inspired by CWorldEnder's personal currency tracker
//https://github.com/CWorldEnder/personalcurrencytracker/blob/main/src/main/java/com/cworldender/BalanceCounter.java

//i don't quite understand java yet but i do know js and c# so i'm getting the hang of things already
public class ItemCounter extends Counter
{
    @Getter
    private final String name;
    @Getter
    private final int id;
    @Getter
    private int count;
    @Inject
    private static ItemManager itemManager;
    ItemCounter(Plugin plugin, int count, String pName, BufferedImage image, int Id)
    {
        super(image, plugin, count);
        name = pName;
        id = Id;
    }
    public static ItemCounter CreateCounter(Plugin plugin, int count, String pName, int Id) {
        BufferedImage image = itemManager.getImage(Id, count*100, false);
        return new ItemCounter(plugin, count, pName, image, Id);
    }
    @Override
    public String getTooltip()
    {
        return name;
    }
}