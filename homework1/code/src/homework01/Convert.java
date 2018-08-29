package homework01;

import java.util.Scanner;

public class Convert {
	public static int hex2dec(String hex){
		int dec = 0;
		int flag = 0, i, j;
		if(hex.charAt(0) == '-') flag = 1;
		
		for(i = hex.length() - 1, j = 0; i >= flag + 2; i--, j++){
			int x = map(hex.charAt(i));
			if(x == -1){
				System.out.println("Invalid input!");//throw
				break;
			}
			dec += x * Math.pow(16, j);
		}
		if(flag == 1) dec = -dec;
		return dec;
	}

	public static int map(char c){
		if(c >= '0' && c <= '9') 
			return (int)(c - 48);
		else if(c >= 'A' && c <= 'F')
			return (int)(c - 55);
		else if(c >= 'a' && c <= 'f')
			return (int)(c - 87);
		else 
			return -1;
	}

	public static String dec2hex(int dec){
		char[] buffer = new char[10];
		int i = 0, flag = 0, q, r;
		if(dec < 0) {
			dec = -dec;
			flag = 1;
		}
		
		while(true){
			q = dec / 16;
			r = dec % 16;
			if(r >= 10 && r <= 15){
				buffer[i] = (char)(r + 55);
			}
			else {
				buffer[i] = (char)(r + 48);
			}
			i++;
			if(q == 0) break;
			dec = q;
		}
		buffer[i++] = 'x';
		buffer[i++] = '0';
		if(flag == 1) buffer[i++] = '-';
		
		StringBuffer tmp = new StringBuffer(); 
		tmp.append(buffer, 0, i);
		tmp.reverse();
		String hex = tmp.toString();
		return hex;
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int dec;
		String hex;
		int mode;
		while(true) {
			dec = 0;
			hex = null;
			mode = -1;
			System.out.println("Choose mode:HEX2DEC(input 0)|DEC2HEX(input 1)|Quit(input 2)");
			System.out.println("Example:Input...0 0x10; Output...0x10->16");
			mode = in.nextInt();
			if(mode == 2) {
				System.out.println("Quit the program!");
				break;
			}
			switch( mode ){
				case 0:
					hex = in.next();
					dec = hex2dec(hex);
					System.out.println(hex+"->"+dec);
					break;
				case 1:
					dec = in.nextInt();
					hex = dec2hex(dec);
					System.out.println(dec+"->"+hex);
					break;
				default:
					System.out.println("Error");
					break;
			}
		}
	}

}
