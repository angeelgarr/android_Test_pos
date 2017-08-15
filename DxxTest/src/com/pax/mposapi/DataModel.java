package com.pax.mposapi;


/**
 * this class describes some data models. 
 * For instance, some information about if data is encrypted or not
 */
public class DataModel {
	static final int  MPOS_RET_SENSITIVE_CIPHER_DUKPTDES = -0x110000;
	
	/**
	 * encrytion mode 
	 */
	public static enum EncryptionMode {
		/**
		 * cleartext
		 */
		CLEAR,
		/**
		 * sensitive data encrypted using dukpt des 
		 */
		SENSITIVE_CIPHER_DUKPTDES
	}
	
	/**
	 * data with encryption mode
	 */
	public static class DataWithEncryptionMode {
		/**
		 * data encryption mode
		 */
		public EncryptionMode encMode;
		/**
		 * data
		 */
		public byte[] data;
		
		/**
		 * DataWithEncryptionMode constructor
		 * @param encMode
		 * 	data encryption mode
		 * @param data
		 * 	data
		 */
		public DataWithEncryptionMode(EncryptionMode encMode, byte[] data) {
			this.encMode = encMode;
			this.data = data;
		}
	}
	
}
