package BlockChain;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class StringHasher {
	private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
	
	public static String applySha256(String input){	
		// MessageDigest.getInstance requires try/catch
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			// Apply sha256
			byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));	        
			byte[] hexChars = new byte[hashBytes.length * 2];
			
			// Process each byte as 2 hex values
		    for (int j = 0; j < hashBytes.length; j++) {
		        int v = hashBytes[j] & 0xff;
		        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
		        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0f];
		    }
		    return new String(hexChars, StandardCharsets.UTF_8);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	
}