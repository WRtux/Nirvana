package nirvana.data;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;

import nirvana.MIDletNirvana;
import nirvana.control.ErrorHandler;
import nirvana.control.GameHandler;

public final class ConfigManager {
	
	private static final String STORE_NAME = "config";
	private static final int CONFIG_LEN = 4;
	
	private ConfigManager() {}
	
	public static void constructConfig() {
		try{
			try {
				RecordStore.deleteRecordStore(STORE_NAME);
			} catch(RecordStoreNotFoundException ex) {}
			RecordStore configStore = RecordStore.openRecordStore(STORE_NAME, true);
			if(configStore.addRecord(new byte[CONFIG_LEN], 0, CONFIG_LEN) != 1)
				throw new IllegalStateException("Config store fails.");
		} catch(Exception ex) {
			ErrorHandler.riseError(ConfigManager.class, ex);
		}
	}
	
	public static void loadConfig() {
		try {
			RecordStore configStore = RecordStore.openRecordStore(STORE_NAME, true);
			try {
				if(configStore.getRecordSize(1) != CONFIG_LEN)
					throw new InvalidRecordIDException();
				byte[] config = configStore.getRecord(1);
				MIDletNirvana.setDebugMode(config[0] == 6);
				MIDletNirvana.setFrameLength(config[1]);
				MIDletNirvana.setPerformanceMode(config[2] == 1);
				//TODO more...
				configStore.closeRecordStore();
			} catch(InvalidRecordIDException ex) {
				configStore.closeRecordStore();
				constructConfig();
				saveConfig();
			}
			if(MIDletNirvana.isDebugMode()) GameHandler.log("Nirvana is in debug mode.");
		} catch(Exception ex) {
			ErrorHandler.riseError(ConfigManager.class, ex);
		}
	}
	
	public static void saveConfig() {
		try {
			RecordStore configStore = RecordStore.openRecordStore(STORE_NAME, false);
			byte[] config = new byte[CONFIG_LEN];
			config[0] = (byte)(MIDletNirvana.isDebugMode() ? 6 : 0);
			config[1] = (byte)MIDletNirvana.getFrameLength();
			config[2] = (byte)(MIDletNirvana.isPerformanceMode() ? 1 : 0);
			//TODO config[3] = SoundManager.volumeLvl;
			configStore.setRecord(1, config, 0, config.length);
			configStore.closeRecordStore();
		} catch(Exception ex) {
			ErrorHandler.riseError(ConfigManager.class, ex);
		}
	}
	
}
