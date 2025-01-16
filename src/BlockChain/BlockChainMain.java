package BlockChain;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.Security;

public class BlockChainMain {
	public static int difficulty = 5;
	public static float minimumTransaction = 0.1f;
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	// Global list of unspent transactions
	public static HashMap<String,TxnOutput> UTXOs = new HashMap<String,TxnOutput>();
	
	public static Wallet walletA;
	public static Wallet walletB;
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
	public static void printError(String message)
	{
		System.out.println(String.format("#ERROR - %s", message));
	}
	
	public static Boolean isChainValid() {
		System.out.println("\nValidating blockchain...");
		
		String hashTarget = "0".repeat(difficulty);
		HashMap<String,TxnOutput> liveUnspentTransactions = new HashMap<String,TxnOutput>();
		Transaction genesisTransaction = Transaction.getGenesisTransaction();
		liveUnspentTransactions.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		// skip genesis block
		for (int i=1; i < blockchain.size(); i++) {
			Block currentBlock = blockchain.get(i);
			Block previousBlock = blockchain.get(i-1);
			
			// validate block hashes
			if (!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				printError("Current hashes not equal");
				return false;
			}
			if (!previousBlock.hash.equals(currentBlock.previousHash) ) {
				printError("Previous hashes not equal");
				return false;
			}
			if (!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				printError("Block has not been mined");
				return false;
			}
			
			// validate transactions
			for (Transaction currentTransaction : currentBlock.transactions) {
				if (!currentTransaction.verifiySignature()) {
					printError("Signature on transaction is invalid");
					return false; 
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					printError("Inputs are not equal to outputs on transaction");
					return false; 
				}
				
				for (TxnInput input: currentTransaction.inputs) {	
					TxnOutput tempOutput = liveUnspentTransactions.get(input.sourceTxnOutputId);
					
					if(tempOutput == null) {
						printError("Referenced input on Transaction is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						printError("Referenced input Transaction value is Invalid");
						return false;
					}
					
					liveUnspentTransactions.remove(input.sourceTxnOutputId);
				}
				for (TxnOutput output: currentTransaction.outputs) {
					liveUnspentTransactions.put(output.id, output);
				}
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		// Create wallets:
		walletA = new Wallet("WalletA");
		walletB = new Wallet("WalletB");		
		Wallet coinbase = new Wallet("coinbase");
		
		// Initialize genesis transaction. Send 100 coins to WalletA: 
		Transaction genesisTransaction = Transaction.initGenesisTransaction(coinbase, walletA.publicKey, 100f);
		// Store in UTXO
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		System.out.println("Initializing genesis block... ");
		Block genesis = Block.initGenesisBlock(genesisTransaction);
		addBlock(genesis);
		
		walletA.printBalance();
		walletB.printBalance();
		
		// Start the blockchain!
		Block block1 = new Block(genesis.hash);
		System.out.println();
		System.out.println("WalletA tries to send funds (50) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 50f));
		addBlock(block1);
		walletA.printBalance();
		walletB.printBalance();
		
		Block block2 = new Block(block1.hash);
		System.out.println();
		System.out.println("WalletA tries to send more funds (500) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 500f));
		addBlock(block2);
		walletA.printBalance();
		walletB.printBalance();
		
		Block block3 = new Block(block2.hash);
		System.out.println();
		System.out.println("WalletB tries to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20f));
		walletA.printBalance();
		walletB.printBalance();
		
		isChainValid();
	}
}
