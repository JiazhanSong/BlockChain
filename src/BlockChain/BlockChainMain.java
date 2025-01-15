package BlockChain;
import java.util.ArrayList;
import com.google.gson.*;
import java.security.Security;

public class BlockChainMain {
	public static int difficulty = 5;
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static Wallet walletA;
	public static Wallet walletB;
	
	public static Boolean isChainValid() {
		for(int i=1; i < blockchain.size(); i++) {
			Block currentBlock = blockchain.get(i);
			Block previousBlock = blockchain.get(i-1);
			
			// Validate current block
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println(String.format("Hash invalid for Block at index %d)", i));			
				return false;
			}
			// Validate with previous block
			if(!currentBlock.previousHash.equals(previousBlock.hash)) {
				System.out.println(String.format("Previous hash mismatch for Block at index %d)", i));
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		//Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		//Create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringHasher.getStringFromKey(walletA.privateKey));
		System.out.println(StringHasher.getStringFromKey(walletA.publicKey));
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifiySignature());
				
		String previousHash = "0";
		for (int i=0; i<4; i++) {
			blockchain.add(new Block(String.format("Block %d... ", i), previousHash));
			System.out.println(String.format("Mining block %d... ", i));
			blockchain.get(i).mineBlock(difficulty);
			
			// update for next iteration
			previousHash = blockchain.get(blockchain.size()-1).hash;
		}
		
		System.out.println("\nBlockchain validation: " + isChainValid());
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe blockchain: ");
		System.out.println(blockchainJson);
	}
}
