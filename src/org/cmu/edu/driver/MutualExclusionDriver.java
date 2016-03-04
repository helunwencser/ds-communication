package org.cmu.edu.driver;
/* Team 7
 * Lunwen He - lunwenh
 * Alejandro Jove - ajovedel
 * Distributed Systems: lab0
 *
 * File: MutualExclusionDriver.java
 * Desc: Program driver for MutualExclusionService
 */

import java.util.Scanner;

import org.cmu.edu.mutualexclusion.MutualExclusionService;

public class MutualExclusionDriver {	
    private static String confFile = "../node_conf.yaml";
	private static String localName = null;
	
	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please select the local name.");
		String name = scanner.nextLine();
		while(name == null || name.length() == 0){
			System.out.println("Invalid local name, please select again");
			name = scanner.nextLine();
		}
		localName = name;
		
		MutualExclusionService mutualExclutionService = new MutualExclusionService(confFile, localName);
		while(true){
			System.out.println("Do you want to enter critical section? y/n: ");
			String option = scanner.nextLine();
			while(option == null || option.trim().length() == 0 || !option.matches("(y|n)")){
				System.out.println("Invalid operation, please input again y/n:");
				option = scanner.nextLine();
			}
			if(option.equals("y")){
				int time = 3 + (int) (Math.random()*5);
				mutualExclutionService.enterCS();
				System.out.println("I will stay in critical section for " + time + " seconds");
				try {
					Thread.sleep(time * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mutualExclutionService.exitCS();
			}else{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
