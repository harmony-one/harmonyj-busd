This sample project demonstrates deploying BUSD contract using [Harmony Java SDK](https://github.com/harmony-one/harmonyj) onto Harmony testnet, minting tokens and also transfering tokens between accounts.

This project is dependent on Harmonyj v1.0.19 as defined in `pom.xml`
```xml
<dependency>
	<groupId>one.harmony</groupId>
	<artifactId>harmonyj</artifactId>
	<version>1.0.19</version>
</dependency>
```
## Installation 

Install solc@0.4.24, as BUSD requires this particular solidity version.

```
npm install solc@0.4.24 -g
solcjs --version
```

My solcjs version shows: `0.4.24+commit.e67f0147.Emscripten.clang`

## Compiling BUSD contract

I have downloaded the Harmony's [BUSDImplementation.sol](https://github.com/harmony-one/busd-contract/blob/master/flattened/BUSDImplementation.sol).
Compile `BUSDImplementation.sol` to generate abi and bin files:

```
solcjs BUSDImplementation.sol --abi --bin --optimize
```

The above command generated following files:
```
-rw-r--r--  1 gupadhyaya  staff      2 Jul  3 18:10 BUSDImplementation_sol_SafeMath.abi
-rw-r--r--  1 gupadhyaya  staff    240 Jul  3 18:10 BUSDImplementation_sol_SafeMath.bin
-rw-r--r--  1 gupadhyaya  staff  10052 Jul  3 18:10 BUSDImplementation_sol_BUSDImplementation.abi
-rw-r--r--  1 gupadhyaya  staff  28346 Jul  3 18:10 BUSDImplementation_sol_BUSDImplementation.bin
```

## Generate java wrapper

```java
package one.harmony;

import one.harmony.codegen.SolidityFunctionWrapperGenerator;

public class CodeGenBUSD {

	public static void main(String[] args) {
		String[] options = new String[] { "-a", "BUSDImplementation_sol_BUSDImplementation.abi", "-b",
				"BUSDImplementation_sol_BUSDImplementation.bin", "-o", "./src/main/java/", "-p", "one.harmony" };
		SolidityFunctionWrapperGenerator.main(options);
	}
}
```

Running the above program generated `BUSDImplementation_sol_BUSDImplementation.java` under `src/main/java/one/harmony`.

## Deploying contract

Run Deploy.java
```java
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
```
The output contains the deployed contract address. Copy it to `Mint.java` and `Transfer.java`. e.g., the same output is shown below.
```
18:26:11.023 INFO [o.h.t.Handler]  signed transaction with chainId 2
18:26:11.036 INFO [o.h.t.Handler]  {"nonce":1,"gasPrice":1000000000,"gasLimit":6721900,"shardID":0,"toShardID":0,"recipient":null,"amount":0,...
18:26:26.072 INFO [o.h.t.Handler]  received transaction confirmation: 
TransactionReceipt {
	transactionHash='0xad45bf5e100b63d551a70e196ba86e12e76711753b9726603ff61fe1d7982b14', 
	transactionIndex='0x0', 
	blockHash='0xfbb4a05653dc0c96c52195400a2daa5fa1869911106539f908736d2c4ee64a52', 
	blockNumber='0x5ce2c', 
	cumulativeGasUsed='0x387c9e', 
	gasUsed='0x387c9e', 
	contractAddress='0x02a467f6903c3e6049f8eb1bc3c5cc373320af85', 
	root='null', 
	status='0x1', 
	from='0x0b585f8daefbc68a311fbd4cb20d9174ad174016', 
	to='', 
	logs=[Log{removed=false, logIndex='0x0', transactionIndex='0x0', transactionHash='0xad45bf5e100b63d551a70e196ba86e12e76711753b9726603ff61fe1d7982b14', blockHash='0xfbb4a05653dc0c96c52195400a2daa5fa1869911106539f908736d2c4ee64a52', blockNumber='0x5ce2c', address='0x02a467f6903c3e6049f8eb1bc3c5cc373320af85', data='0x', type='null', topics=[0x6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff625]}], 
	logsBloom='0x00000000000000000000000000000000000000000000000000000000000000400000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000008000000000000000000800000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
}
Contract deploy at 0x02a467f6903c3e6049f8eb1bc3c5cc373320af85
```

## Minting tokens

Run `Mint.java`:
```java
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
```

## Transfer tokens

Run `Transfer.java`

```java
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
```
