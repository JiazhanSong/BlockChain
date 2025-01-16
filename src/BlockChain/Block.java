package BlockChain;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Block {
	public String hash;
	public String previousHash;
	public String merkleRoot;
	private long random;
	private int nonce;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	
	static final String genesisHash = "0";
	private static Block genesisBlock;

	public Block(String previousHash ) {
		this.previousHash = previousHash;
		this.random = ThreadLocalRandom.current().nextLong();
		this.hash = calculateHash(); // call after setting previous hash
	}
	
	public static Block initGenesisBlock(Transaction genesisTransaction) {
        if (genesisBlock == null ) {
        	// manually set all fields
        	genesisBlock = new Block(genesisHash);
        	genesisBlock.addTransaction(genesisTransaction);
        }
        return genesisBlock;
    }
	
	public String calculateHash() {
		String blockString = previousHash + Long.toString(random) + Integer.toString(nonce) + merkleRoot;
		return StringHasher.applySha256(blockString);
	}
	
	public void mineBlock(int difficulty) {
		merkleRoot = StringHasher.getMerkleRoot(transactions);
		String target = "0".repeat(difficulty);
		
		while(!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		
		System.out.println("Block mined : " + hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if (transaction == null) return false;	
		
		if (previousHash != genesisHash) {
			if((transaction.processTxn() != true)) {
				System.out.println("Transaction processing failed.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction added to block.");
		return true;
	}
}
