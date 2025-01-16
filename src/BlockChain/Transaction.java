package BlockChain;
import java.security.*;
import java.util.ArrayList;

public class Transaction {
	// Transaction data
	public PublicKey senderPK;
	public PublicKey recipientPK;
	public float value;
	
	// Transaction identifiers
	public String txnId; // hash of transaction.
	public byte[] signature; // signed using private key
	
	private static int sequence = 0; // rough count of transactions, used for uniqueness
	private static Transaction genesisTxn;
	
	public ArrayList<TxnInput> inputs;
	public ArrayList<TxnOutput> outputs;
	
	public Transaction(PublicKey sender, PublicKey recipient, float value,  ArrayList<TxnInput> inputs) {
		this.senderPK = sender;
		this.recipientPK = recipient;
		this.value = value;
		this.inputs = inputs;
		this.outputs = new ArrayList<TxnOutput>();
	}
	
	public static Transaction initGenesisTransaction(Wallet genesisWallet, PublicKey recipient, float value) {
        if (genesisTxn == null ) {
        	// manually set all fields
        	genesisTxn = new Transaction(genesisWallet.publicKey, recipient, 100f, null);
        	genesisTxn.generateSignature(genesisWallet.privateKey);
    		genesisTxn.txnId = "0";
    		genesisTxn.outputs.add(new TxnOutput(genesisTxn.recipientPK, genesisTxn.value, genesisTxn.txnId));
        }
        return genesisTxn;
    }
	
	public static Transaction getGenesisTransaction() {
        return genesisTxn;
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
		signature = StringHasher.createECDSASig(privateKey,data);		
	}
	
	// Verify data with public key
	public boolean verifiySignature() {
		String data = StringHasher.getStringFromKey(senderPK) + StringHasher.getStringFromKey(recipientPK) + Float.toString(value);
		return StringHasher.verifyECDSASig(senderPK, data, signature);
	}
	
	// Returns true if new transaction could be created.	
	public boolean processTxn() {
		if (verifiySignature() == false) {
			BlockChainMain.printError("Signature failed to verify");
			return false;
		}
				
		// check transaction inputs:
		for(var input : inputs) {
			input.UTXO = BlockChainMain.UTXOs.get(input.sourceTxnOutputId);
		}

		// make sure minimum is met:
		float totalInputValue = getInputsValue();
		if(totalInputValue < BlockChainMain.minimumTransaction) {
			BlockChainMain.printError("Transaction inputs is too small: " + getInputsValue());
			return false;
		}
		
		// generate outputs:
		float leftOver = totalInputValue - value;
		txnId = calulateHash();
		// send value to recipient
		outputs.add(new TxnOutput( this.recipientPK, value,txnId));
		// send leftovers to sender	as a new transaction
		outputs.add(new TxnOutput( this.senderPK, leftOver,txnId));
				
		for(TxnOutput output : outputs) {
			BlockChainMain.UTXOs.put(output.id , output);
		}
		
		// remove spent transaction inputs from UTXO
		for(TxnInput input : inputs) {
			if(input.UTXO == null) continue;
			BlockChainMain.UTXOs.remove(input.UTXO.id);
		}
		
		return true;
	}
		
	// returns sum of input values
	public float getInputsValue() {
		float total = 0;
		for(TxnInput input : inputs) {
			if(input.UTXO == null) continue;
			total += input.UTXO.value;
		}
		return total;
	}

	//returns sum of output values:
	public float getOutputsValue() {
		float total = 0;
		for(TxnOutput output : outputs) {
			total += output.value;
		}
		return total;
	}
}