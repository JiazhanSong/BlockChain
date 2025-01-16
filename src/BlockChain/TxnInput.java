package BlockChain;

//Owned by Transaction class
public class TxnInput {
	public String sourceTxnOutputId; // incoming value from existing transaction
	public TxnOutput UTXO; // unspent transaction output
	
	public TxnInput(String transactionOutputId) {
		this.sourceTxnOutputId = transactionOutputId;
	}
}
