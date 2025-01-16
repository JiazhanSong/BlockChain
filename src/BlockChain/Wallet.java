package BlockChain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

public class Wallet {
	public String name;
	public PrivateKey privateKey;
	public PublicKey publicKey;
	// Unspent UTXOs owned by this wallet. Needs to be updated from BlockChainMain
	public HashMap<String,TxnOutput> UTXOs = new HashMap<String,TxnOutput>();
	
	public Wallet(String name){
		this.name = name;
		generateKeyPair();	
	}
	
	public void printBalance() {
		System.out.println(name + "'s balance is: " + getBalance());
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
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Collect all owned output transactions
	public float getBalance() {
		float total = 0;	
        for (HashMap.Entry<String, TxnOutput> item: BlockChainMain.UTXOs.entrySet()) {
        	TxnOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
            	UTXOs.put(UTXO.id,UTXO);
            	total += UTXO.value;
            }
        }  
		return total;
	}
	
	// Generates and returns a new transaction from this wallet.
	public Transaction sendFunds(PublicKey recipient, float value) {
		if(getBalance() < value) { //gather balance and check funds.
			BlockChainMain.printError("Not enough funds to send. Transaction cancelled.");
			return null;
		}

		ArrayList<TxnInput> inputs = new ArrayList<TxnInput>();
    
		float total = 0;
		for (HashMap.Entry<String, TxnOutput> item: UTXOs.entrySet()) {
			TxnOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TxnInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for (TxnInput input: inputs) {
			UTXOs.remove(input.sourceTxnOutputId);
		}
		return newTransaction;
	}
}
