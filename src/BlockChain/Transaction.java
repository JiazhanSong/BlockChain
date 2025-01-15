package BlockChain;
import java.security.*;
import java.util.ArrayList;

public class Transaction {
	// Transaction data
	public PublicKey senderPK;
	public PublicKey recipientPK;
	public float value;
	
	// Transaction identifiers
	public String transactionId; // hash of transaction.
	public byte[] signature; // signed using private key
	
	private static int sequence = 0; // rough count of transactions, used for uniqueness
	
	public ArrayList<TransactionInput> inputs;
	public ArrayList<TransactionOutput> outputs;
	
	public Transaction(PublicKey sender, PublicKey recipient, float value,  ArrayList<TransactionInput> inputs) {
		this.senderPK = sender;
		this.recipientPK = recipient;
		this.value = value;
		this.inputs = inputs;
	}
	
	// Calculate transaction hash, used as transactionId
	private String calulateHash() {
		sequence++;
		return StringHasher.applySha256(
				StringHasher.getStringFromKey(senderPK) +
				StringHasher.getStringFromKey(recipientPK) +
				Float.toString(value) + sequence
				);
	}
	
	// Sign data with private Key
	public void generateSignature(PrivateKey privateKey) {
		String data = StringHasher.getStringFromKey(senderPK) + StringHasher.getStringFromKey(recipientPK) + Float.toString(value);
		signature = StringHasher.applyECDSASig(privateKey,data);		
	}
	
	// Verify data with public key
	public boolean verifiySignature() {
		String data = StringHasher.getStringFromKey(senderPK) + StringHasher.getStringFromKey(recipientPK) + Float.toString(value);
		return StringHasher.verifyECDSASig(senderPK, data, signature);
	}
}