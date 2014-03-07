package Main;

import java.util.Scanner;

public class UI {
		Scanner scan;
	public UI(){
		scan = new Scanner(System.in);
	}
	public void display(String A){
		System.out.println(A);
	}
	public String vraag(){
		return scan.nextLine();
	}
}
