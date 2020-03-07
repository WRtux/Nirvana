package nirvana.data;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.ref.WeakReference;

import javax.microedition.lcdui.Image;
import javax.microedition.media.Player;

import nirvana.control.ErrorHandler;
import nirvana.control.ErrorHandler.ProcessBreakException;
import nirvana.control.GameHandler;
import nirvana.display.DisplayWrapper;
import nirvana.game.GameVocab;
import nirvana.game.item.Item;
import nirvana.game.map.MapScene;
import nirvana.game.map.Mappic;
import wapi.betabase.BetaBase;
import wapi.betabase.reading.BetaBaseReader;
import wapi.betabase.reading.CompoundReader;
import wapi.fontin.FontIn;

public final class ResourceManager {
	
	protected static final class ResourceSet {
		
		protected static final Resource baseUtils = new Resource("data", "utils", Resource.SFX_DATABASE);
		protected static final Resource baseMaps = new Resource("data", "maps", Resource.SFX_DATABASE);
		protected static final Resource baseChars = new Resource("data", "chars", Resource.SFX_DATABASE);
		protected static final Resource baseItems = new Resource("data", "items", Resource.SFX_DATABASE);
		
		protected static final Resource embedFont = new Resource("nirvana", "font", Resource.SFX_BIN);
		
		protected static final Resource imgNirvana = new Resource("nirvana", "logo", Resource.SFX_IMAGE);
		protected static Resource imgGameLogo;
		protected static Resource imgGameTitle;
		protected static Resource imgTitleBg;
		protected static Resource imgWindowBorder;
		
		protected static Resource imgItemIcons; 
		
		protected static Resource[] maps;
		protected static Resource[] mappics;
		protected static Resource[] midis;
		
		private ResourceSet() {}
		
	}
	
	public static final class BaseSet {
		
		public static BetaBase baseUtils;
		public static BetaBase baseMaps;
		public static BetaBase baseChars;
		public static BetaBase baseItems;
		
		private BaseSet() {}
		
	}
	
	private ResourceManager() {}
	
	public static void init() {
		CompoundReader reader;
		try {
			
			//Pre-init reference preparing
			refFontData = new WeakReference(null);
			refSymbolicImages = new WeakReference[IMG_NUM];
			for(int i = 0; i < refSymbolicImages.length; i++)
				refSymbolicImages[i] = new WeakReference(null);
			refItemIcons = new WeakReference(null);
			
			//Base reading
			
			BaseSet.baseUtils = BetaBase.constructBase(ResourceSet.baseUtils.getStream());
			reader = new BetaBaseReader(BaseSet.baseUtils).with();
			reader = reader.with();
				reader = reader.with();
					DisplayWrapper.voidColorNormal = reader.readNumber();
					DisplayWrapper.bgColorNormal = reader.readNumber();
					DisplayWrapper.bgColorEmp = reader.readNumber();
					DisplayWrapper.borderColorNormal = reader.readNumber();
					DisplayWrapper.textColorNormal = reader.readNumber();
					DisplayWrapper.textColorEmp = reader.readNumber();
					DisplayWrapper.textColorAux = reader.readNumber();
				reader = reader.upper().with();
					ResourceSet.imgGameLogo = new Resource(reader.readString());
					ResourceSet.imgGameTitle = new Resource(reader.readString());
					ResourceSet.imgTitleBg = new Resource(reader.readString());
					ResourceSet.imgWindowBorder = new Resource(reader.readString());
				reader = reader.upper().with();
					ResourceSet.midis = new Resource[reader.getUnreadNum()];
					for(int i = 0; i < ResourceSet.midis.length; i++)
						ResourceSet.midis[i] = new Resource(reader.readString());
				reader = reader.upper();
			reader = reader.upper().with();
				GameVocab.gameName = reader.readString();
				GameVocab.gameVer = reader.readString();
			reader = reader.upper();
			
			BaseSet.baseMaps = BetaBase.constructBase(ResourceSet.baseMaps.getStream());
			reader = new BetaBaseReader(BaseSet.baseMaps).with();
			reader = reader.with();
				ResourceSet.maps = new Resource[reader.getUnreadNum()];
				for(int i = 0; i < ResourceSet.maps.length; i++)
					ResourceSet.maps[i] = new Resource(reader.readString());
			reader = reader.upper().with();
				ResourceSet.mappics = new Resource[reader.getUnreadNum()];
				for(int i = 0; i < ResourceSet.mappics.length; i++)
					ResourceSet.mappics[i] = new Resource(reader.readString());
			reader = reader.upper();
			
			BaseSet.baseChars = BetaBase.constructBase(ResourceSet.baseChars.getStream());
			reader = new BetaBaseReader(BaseSet.baseChars).with();
			
			BaseSet.baseItems = BetaBase.constructBase(ResourceSet.baseItems.getStream());
			reader = new BetaBaseReader(BaseSet.baseItems).with();
			ResourceSet.imgItemIcons = new Resource(reader.readString());
			reader = reader.with();
				int num = reader.getUnreadNum();
				Item.init(num);
				for(int i = 0; i < num; i++) {
				reader = reader.with();
					String name = reader.readString();
					byte category = (byte)reader.readNumber();
					int iconIndex = reader.readNumber();
					//TODO ease
					byte[] buf = reader.readBin();
					int len = buf.length / 4;
					int[] effectData = new int[len];
					DataInputStream stream = new DataInputStream(new ByteArrayInputStream(buf));
					for(int j = 0; j < len; j++) effectData[j] = stream.readInt();
					stream.close();
					Item.constructItem((short)i, category, name, iconIndex, reader.readString(), null);
				reader = reader.upper();
				}
			reader = reader.upper();
			
			//Basic object constructing
			DisplayWrapper.fontInfo = new FontIn(getEmbedFontData());
			DisplayWrapper.imgBorder = ResourceSet.imgWindowBorder.getImage();
			
			//Post-init reference preparing
			refMaps = new WeakReference[ResourceSet.maps.length];
			for(int i = 0; i < refMaps.length; i++)
				refMaps[i] = new WeakReference(null);
			refMappics = new WeakReference[ResourceSet.mappics.length];
			for(int i = 0; i < refMappics.length; i++)
				refMappics[i] = new WeakReference(null);
			
		} catch(ProcessBreakException ex) {
			throw ex;
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		GameHandler.updateStats();
	}
	
	private static WeakReference refFontData;
	public static byte[][] getEmbedFontData() {
		byte[][] fontData = (byte[][])refFontData.get();
		if(fontData == null) try {
			fontData = new byte[128][];
			DataInputStream stream = ResourceSet.embedFont.getStream();
			for(int i = 0; i < fontData.length; i++) {
				fontData[i] = new byte[stream.readByte()];
				stream.readFully(fontData[i]);
			}
			stream.close();
			refFontData = new WeakReference(fontData);
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return fontData;
	}
	
	private static final int IMG_NUM = 4;
	public static final int IMG_NIRVANA = 0;
	public static final int IMG_GAMELOGO = 1;
	public static final int IMG_GAMETITLE = 2;
	public static final int IMG_TITLEBG = 3;
	
	private static WeakReference refSymbolicImages[];
	public static Image getSymbolicImage(int index) {
		Image img = (Image)refSymbolicImages[index].get();
		if(img == null) try {
			switch(index) {
			case IMG_NIRVANA:
				img = ResourceSet.imgNirvana.getImage();
				break;
			case IMG_GAMELOGO:
				img = ResourceSet.imgGameLogo.getImage();
				break;
			case IMG_GAMETITLE:
				img = ResourceSet.imgGameTitle.getImage();
				break;
			case IMG_TITLEBG:
				img = ResourceSet.imgTitleBg.getImage();
				break;
			}
			refSymbolicImages[index] = new WeakReference(img);
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return img;
	}
	
	private static WeakReference refMidiPlayers[];
	public static Player getMidiPlayer(int index) {
		Player player = (Player)refMidiPlayers[index].get();
		int state = player.getState();
		if(player == null || state == Player.STARTED || state == Player.CLOSED) try {
			player = ResourceSet.midis[index].getMidiPlayer();
			refMidiPlayers[index] = new WeakReference(player);
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return player;
	}
	
	private static WeakReference[] refMappics;
	public static Mappic getMappic(int index) {
		Mappic mappic = (Mappic)refMappics[index].get();
		if(mappic == null) try {
			mappic = Mappic.constructMappic(ResourceSet.mappics[index].getImage());
			refMappics[index] = new WeakReference(mappic);
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return mappic;
	}
	
	private static WeakReference[] refMaps;
	public static Object getMap(int id) {
		Object map = refMaps[id].get();
		if(map == null) try {
			map = MapScene.constructMap((short)id, ResourceSet.maps[id].getStream());
			refMaps[id] = new WeakReference(map);
		} catch (Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return map;
	}
	
	public static MapScene getMapScene(short id) {
		MapScene sce = null;
		try {
			//TODO standardize
			sce = MapScene.constructScene(id, new DataInputStream(new ByteArrayInputStream(
				new byte[] {0x54, (byte)0x80, 0, 0, 0, 0}
			)));
		} catch (Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return sce;
	}
	
	private static WeakReference refItemIcons;
	public static Image getItemIcons() {
		Image img = (Image)refItemIcons.get();
		if(img == null) try {
			img = ResourceSet.imgItemIcons.getImage();
			refItemIcons = new WeakReference(img);
		} catch(Exception ex) {
			ErrorHandler.riseError(ResourceManager.class, ex);
		}
		return img;
	}
	
}
