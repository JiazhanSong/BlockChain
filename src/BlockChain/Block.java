package BlockChain;
import java.util.concurrent.ThreadLocalRandom;

public class Block {
	public String hash;
	public String previousHash;
	private String data;
	private long random;
	private int nonce;

	public Block(String data, String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.random = ThreadLocalRandom.current().nextLong();
		this.hash = calculateHash(); // call after setting previous hash
	}
	
	public String calculateHash() {
		String blockString = previousHash + Long.toString(random) + Integer.toString(nonce) + data;
		return StringHasher.applySha256(blockString);
	}
	
	public void mineBlock(int difficulty) {
		String target = "0".repeat(difficulty);
		
		while(!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		
		System.out.println("Block Mined : " + hash);
	}
}
