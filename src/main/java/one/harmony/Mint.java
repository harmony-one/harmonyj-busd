package one.harmony;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class Mint {
	static String contractAddress = "0x02a467f6903c3e6049f8eb1bc3c5cc373320af85";

	public static void main(String[] args) throws Exception {
		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		String passphrase = "harmony-one";
		String node = "https://api.s0.b.hmny.io"; // could also use the local node e.g., http://127.0.0.1:9500/"
		// let's deploy to testnet
		Handler handler = new Handler(from, passphrase, node, ChainID.TESTNET);
		MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900"));

		BUSDImplementation_sol_BUSDImplementation contract = BUSDImplementation_sol_BUSDImplementation
				.load(contractAddress, contractGasProvider);
		contract.setHandler(handler);

		System.out.println("Total supply before minting " + contract.totalSupply().send());
		// let's mind 10000 tokens
		TransactionReceipt receipt = contract.increaseSupply(BigInteger.valueOf(10000)).send();
		for (BUSDImplementation_sol_BUSDImplementation.SupplyIncreasedEventResponse response : contract
				.getSupplyIncreasedEvents(receipt)) {
			System.out.println("Supply increased by " + response.to + " to " + response.value);
		}

		System.out.println("Total supply after minting " + contract.totalSupply().send());

		// need to unpause so that transfers can begin
		contract.unpause().send();
	}
}
