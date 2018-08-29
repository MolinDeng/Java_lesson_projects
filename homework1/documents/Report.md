# Homework01

![](https://i.imgur.com/YTgVkpU.png)

## Requirement

![](https://i.imgur.com/HD9gGGx.png)

## Install Java&Eclipse

### Install JDK
![](https://i.imgur.com/y3bnyab.png)
![](https://i.imgur.com/WGsJ09z.png)
![](https://i.imgur.com/2u4EphU.png)
![](https://i.imgur.com/nu3VVCF.jpg)
![](https://i.imgur.com/WgY9SOq.jpg)
### Configure environment variables
![](https://i.imgur.com/viyEJdO.png)   
(1)新建->变量名"JAVA_HOME"，变量值"C:\Java\jdk1.8.0_05"(即JDK的安装路径).    

(2)编辑->变量名"Path"，在原变量值的最后面加上“;%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin”.   

(3)新建->变量名“CLASSPATH”,变量值“.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar”.  

![](https://i.imgur.com/1IhRrdo.png)
![](https://i.imgur.com/cYC7qI0.jpg)  

确认Java是否正确安装：
![](https://i.imgur.com/QErlAyX.png)  

### Install Eclipse
![](https://i.imgur.com/yAGKObc.jpg)
![](https://i.imgur.com/Hl15byt.jpg)

## Code description

###Design

* 1.User choose convertion mode: hex2dec or dec2hex.
* 2.Do the convertion.
* 3.Output the result and repeat the first step.

### Part of inplemention
* hex2dec function

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

* map function: convert a character(hexadecimal representation) to an integer(decimal representation)

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

* dec2hex function

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
## Result
![](https://i.imgur.com/spYbxPk.png)