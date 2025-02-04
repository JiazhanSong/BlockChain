package BlockChain;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.security.*;

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
	
	// Create ECDSA Signature and returns result as bytes.
	public static byte[] createECDSASig(PrivateKey privateKey, String input) {
		try {
			Signature dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			dsa.update(input.getBytes());
			return dsa.sign();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	// Calculate merkle root.
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
			ArrayList<String> previousLevelIds = new ArrayList<String>();
			for(Transaction transaction : transactions) {
				previousLevelIds.add(transaction.txnId);
			}
			ArrayList<String> currentLevelIds = previousLevelIds;
			
			// Process by tree level, combine two hashes into one
			while(currentLevelIds.size() > 1) {
				currentLevelIds = new ArrayList<String>();
				for(int i=1; i < previousLevelIds.size(); i += 2) {
					currentLevelIds.add(applySha256(previousLevelIds.get(i-1) + previousLevelIds.get(i)));
				}
				
				// Handle odd length case, append last hash value
				if (previousLevelIds.size() % 2 == 1) {
					currentLevelIds.add(previousLevelIds.get(previousLevelIds.size()-1));
				}
				
				previousLevelIds = currentLevelIds;
			}
			return (currentLevelIds.size() == 1) ? currentLevelIds.get(0) : "";
		}
}