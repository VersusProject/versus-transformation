/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgment if the
 * software is used.
 *
 *
 *    Date: 08-09-2013
 */


import java.util.Scanner;

public class FragmentTests {

	/**
	 * Test awkward parsing
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String name = null;
		System.out.print("> ");
		name = scanner.nextLine();
		int firstIndex = (name.indexOf("Simulated_Data") + "Simulated_Data".length() + 3);
		String nameFT = name.substring(0, firstIndex) + "_ft"
				+ name.substring(firstIndex, name.indexOf(".")) + "-ft.tif";
		System.out.println(nameFT);
		scanner.close();

	}
}