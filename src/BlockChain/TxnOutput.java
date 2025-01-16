package BlockChain;
import java.security.*;

// Owned by Transaction class
public class TxnOutput {
	public String id;
	public PublicKey recipient;
	public float value;
	public String parentTransactionId; // id of Transaction this output was created by
	
	//Constructor
	public TxnOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringHasher.applySha256(
				StringHasher.getStringFromKey(recipient) +
				Float.toString(value) +
				parentTransactionId
				);
	}
	
	//Check if coin belongs to you
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
}
