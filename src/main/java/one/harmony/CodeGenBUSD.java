package one.harmony;

import one.harmony.codegen.SolidityFunctionWrapperGenerator;

public class CodeGenBUSD {

	public static void main(String[] args) {
		String[] options = new String[] { "-a", "BUSDImplementation_sol_BUSDImplementation.abi", "-b",
				"BUSDImplementation_sol_BUSDImplementation.bin", "-o", "./src/main/java/", "-p", "one.harmony" };
		SolidityFunctionWrapperGenerator.main(options);
	}
}
