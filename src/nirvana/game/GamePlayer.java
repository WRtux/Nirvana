package nirvana.game;

import java.util.Date;
import java.util.Vector;

import nirvana.game.fighter.Character;
import nirvana.game.item.Inventory;
import nirvana.game.item.ItemStack;
import nirvana.game.map.Entity;
import nirvana.game.map.MapScene;
import nirvana.util.TilePosition.TileCoordVarible;

public final class GamePlayer {
	
	public static Date startDate;
	public static Date gameTime;
	
	public static MapScene mapIn;
	public static TileCoordVarible mapPos;
	public static Entity charaEntity;
	public static Vector characters;
	
	public static Inventory inventory;
	
	private GamePlayer() {}
	
	public static void loadRecord(int slot) {
		//TODO realize
	}
	
	public static void useItemAt(int stackIndex, int charIndex) {
		ItemStack stack = inventory.extract(stackIndex, 1, false);
		if(stack.getNum() == 0) return;
		Character character = (Character)characters.elementAt(charIndex);
		int[] effectData = stack.getStateEffect();
		character.addHealth(effectData[0]);
		character.addMana(effectData[1]);
	}
	
	public static void equipItemAt(int stackIndex, int charIndex, int slotIndex) {
		ItemStack stack = inventory.extract(stackIndex, 1, true);
		Character character = (Character)characters.elementAt(charIndex);
		ItemStack unequip = character.equip(slotIndex, stack);
		if(stack.getNum() == 0) inventory.extract(stackIndex, 1, false);
		if(unequip != null) {
			stack.merge(unequip);
			if(unequip.getNum() != 0) inventory.merge(unequip);
		}
	}
	
}
