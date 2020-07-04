package one.harmony;

import java.math.BigInteger;

import org.web3j.tx.gas.StaticGasProvider;

class MyGasProvider extends StaticGasProvider {

	public MyGasProvider(BigInteger gasPrice, BigInteger gasLimit) {
		super(gasPrice, gasLimit);
		// TODO Auto-generated constructor stub
	}

}