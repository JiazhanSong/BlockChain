package BlockChain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public Wallet(){
		generateKeyPair();	
	}
		
	public void generateKeyPair() {
		try {
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			
			// Use ECDSA for shorter key lengths and compute time
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			keyGen.initialize(ecSpec, random);
			
        	KeyPair keyPair = keyGen.generateKeyPair();
        	privateKey = keyPair.getPrivate();
        	publicKey = keyPair.getPublic();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
