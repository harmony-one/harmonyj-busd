package one.harmony;

import java.math.BigInteger;

import one.harmony.cmd.Keys;
import one.harmony.transaction.ChainID;
import one.harmony.transaction.Handler;

public class Deploy {

	public static void importDeployAccount() throws Exception {
		Keys.cleanKeyStore();
		// private key corresponding to one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy
		String key = "fd416cb87dcf8ed187e85545d7734a192fc8e976f5b540e9e21e896ec2bc25c3";
		String accountName = "a1";
		Keys.importPrivateKey(key, accountName);
	}

	public static void main(String[] args) throws Exception {
		importDeployAccount();

		String from = "one1pdv9lrdwl0rg5vglh4xtyrv3wjk3wsqket7zxy";
		String passphrase = "harmony-one";
		String node = "https://api.s0.b.hmny.io"; // could also use the local node e.g., http://127.0.0.1:9500/"
		// let's deploy to testnet
		Handler handler = new Handler(from, passphrase, node, ChainID.TESTNET);
		MyGasProvider contractGasProvider = new MyGasProvider(new BigInteger("1"), new BigInteger("6721900"));

		BUSDImplementation_sol_BUSDImplementation contract = BUSDImplementation_sol_BUSDImplementation
				.deploy(handler, contractGasProvider).send();
		System.out.println("Contract deploy at " + contract.getContractAddress());
	}
}
