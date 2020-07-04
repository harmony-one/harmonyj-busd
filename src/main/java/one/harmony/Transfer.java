package one.harmony;

import java.math.BigInteger;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import one.harmony.account.Address;
import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class Transfer {
	static String contractAddress = "0x02a467f6903c3e6049f8eb1bc3c5cc373320af85";

	public static void main(String[] args) throws Exception {
		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		String toOneAddr = "one1pf75h0t4am90z8uv3y0dgunfqp4lj8wr3t5rsp";
		String to = Address.parseBech32(toOneAddr); // needs hex address
		String passphrase = "harmony-one";
		String node = "https://api.s0.b.hmny.io"; // could also use the local node e.g., http://127.0.0.1:9500/"
		// let's deploy to testnet
		Handler handler = new Handler(from, passphrase, node, ChainID.TESTNET);
		MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900"));

		BUSDImplementation_sol_BUSDImplementation contract = BUSDImplementation_sol_BUSDImplementation
				.load(contractAddress, contractGasProvider);
		contract.setHandler(handler);

		System.out.println("Total supply is " + contract.totalSupply().send());

		System.out.println("Balance of " + toOneAddr + " before transfer is " + contract.balanceOf(to).send());

		// let's transfer 100 tokens
		TransactionReceipt receipt = contract.transfer(to, BigInteger.valueOf(100)).send();
		for (BUSDImplementation_sol_BUSDImplementation.TransferEventResponse response : contract
				.getTransferEvents(receipt)) {
			System.out.println("Transfer from " + Address.toBech32(response.from) + " to "
					+ Address.toBech32(response.to) + " value " + response.value);
		}

		System.out.println("Balance of " + toOneAddr + " after transfer is " + contract.balanceOf(to).send());
	}

}
