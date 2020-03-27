package nirvana.game.item;

import java.lang.ref.WeakReference;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import nirvana.control.ErrorHandler;
import nirvana.data.ResourceManager;

public final class Item {
	
	public static final byte CATE_CONSUME = 0;
	public static final byte CATE_SPECIAL = -1;
	public static final byte CATE_EQUIPBASE = 16;
	
	public static int EQUIPTYPES;
	public static final int EQUIPSLOTS = 4;
	
	//public static String[] itemTypeNames;
	//public static String[] equipTypeNames;
	
	protected static final int iconWidth = 16;
	
	private static Item[] items;
	
	private static Image imgIcons;
	private static WeakReference[] refIcons;
	
	public final short id;
	public final byte category;
	
	protected String name;
	protected int iconIndex;
	
	protected String info;
	
	protected int[] effectData;
	
	public static void init(int num) {
		items = new Item[num];
		imgIcons = ResourceManager.getItemIcons();
		if(imgIcons.getWidth() % iconWidth != 0 || imgIcons.getHeight() != iconWidth)
			ErrorHandler.riseError(Item.class, new IllegalArgumentException("Bad item icon image."));
		refIcons = new WeakReference[imgIcons.getWidth() / iconWidth];
		for(int i = 0; i < refIcons.length; i++) refIcons[i] = new WeakReference(null);
	}
	
	public static Item constructItem(short id, byte category, String name, int iconIndex, String info, int[] effectData) {
		Item item = new Item(id, category, name);
		item.iconIndex = iconIndex;
		item.info = info;
		item.effectData = effectData;
		return item;
	}
	
	public static Item getItem(short id) {
		return items[id];
	}
	
	public static Image getIcon(int index) {
		Image icon = (Image)refIcons[index].get();
		if(icon == null) {
			icon = Image.createImage(
				imgIcons,
				index * iconWidth, 0, iconWidth, iconWidth, Sprite.TRANS_NONE
			);
			refIcons[index] = new WeakReference(icon);
		}
		return icon;
	}
	
	protected Item(short id, byte category, String name) {
		this.id = id;
		this.category = category;
		this.name = name;
		this.iconIndex = 0;
		this.info = "";
		items[id] = this;
	}
	protected Item(short id, byte category) {
		this(id, category, "");
	}
	
	public boolean isUsable() {
		return this.category == CATE_CONSUME;
	}
	public boolean isEquip() {
		return (this.category >= CATE_EQUIPBASE && this.category < CATE_EQUIPBASE + EQUIPTYPES);
	}
	
	public int getEquipType() {
		if(!this.isEquip()) return -1;
		return this.category - CATE_EQUIPBASE;
	}
	public int getEquipSlot() {
		if(!this.isEquip()) return -1;
		//TODO
		return this.category - CATE_EQUIPBASE;
	}
	
	public String getName() {
		return this.name;
	}
	public Image getIcon() {
		return getIcon(this.iconIndex);
	}
	
	public String getInfo() {
		return this.info;
	}
	
	public int[] getEffectData() {
		return this.effectData;
	}
	
}
