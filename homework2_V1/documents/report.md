# Homework02
<font face="consolas">
##文件
![](https://i.imgur.com/6bhRZdK.png)  
code文件夹包含java项目，源代码在scr文件夹中。

![](https://i.imgur.com/jDvNinR.png)  
documents文件夹包含report
##代码分析

###思路
* 用户输入若干数字
* 随机生成数独矩阵
* 根据用户输入的数字随机生成显示矩阵
* 通过显示矩阵遍历数独矩阵，输出数独题
* 直接通过数独矩阵输出答案

###实现
####1 随机生成数独矩阵(9*9填满)  

		public static int[][] SudoInit() {   
			int [][]seed = {  
				{8, 7, 4, 6, 3, 1, 5, 9, 2},  
				{5, 9, 6, 7, 2, 8, 4, 3, 1},  
				{2, 3, 1, 4, 5, 9, 6, 8, 7},  
				{4, 8, 2, 1, 9, 6, 7, 5, 3},  
				{7, 6, 5, 3, 8, 4, 2, 1, 9},  
				{9, 1, 3, 5, 7, 2, 8, 4, 6},  
				{3, 2, 9, 8, 6, 5, 1, 7, 4},  
				{1, 5, 7, 2, 4, 3, 9, 6, 8},  
				{6, 4, 8, 9, 1, 7, 3, 2, 5}  
		};
 该种子矩阵为一个满足数独规则的矩阵，接下来我们把这个种子矩阵随机打乱。同时保证其满足数独规则。  
#####数字交换(first swap)  
数独的难点就在于把1个数字变了之后，相应的会有一连串的数字需要改变。对于我们刚生成的矩阵，假设把第一行第一列的数字由8变成2，相应的这一行和这一列的2需要变成8，这个2相应的行和列上的8需要变成2，这个8相应的行和列上的2需要变成8…..一系列变换之后只是相当于把矩阵上所有的2变成了8，所有的8变成了2，得到的仍是一个合法的数独矩阵。如果我们先把8变成2，再把相应的2变成其它数字的话，3个或更多数字之间混着交换得到的会是一个非法的数独矩阵。 
为了矩阵合法，我们只对两个数字进行交换，把所有的x变成y，所有y变成x。x和y可以是1-9之间的任意数字。  

		int a, b;
		while(true) {
			a = r.nextInt(9)+1;
			b = r.nextInt(9)+1;
			if(a != b) break;
		}
		//the first swap
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int temp = seed[i][j];
				seed[i][j] = (temp == a ? b : (temp == b ? a : temp));
			}
		}
#####行、列交换(second swap)
第一步做完之后，看上去已经像是一个随机的矩阵了，但有一个很严重的问题。看初始的矩阵，可以看出来，九个1分布在九个位置，第一步不管怎么交换数值，交换前都是1，交换后可能都是6，也可能都是7，反正必定是相同的！所以需要再对列和行进行交换，打破这个规律。 
在交换行和列时需注意，交换不能打破矩阵的合法性。交换行不会打破列的合法行，但可能会打破3×3矩阵的合法性。交换列不会打破行的合法性，但可能打破3×3矩阵的合法性。为了避免这个问题，我们交换行，只在3×3矩阵内部交换。即第一行只能与第二、三行交换，第六列只能与第四、五列交换。其它行、列也一样。  

		for(int k = 0; k < 10; k++) {//k is swap times
			//row
			int base = 3 * r.nextInt(3);
			int i = base + r.nextInt(3);
			int j = base + r.nextInt(3);
			if(i != j) {
				int [] temp = seed[i];
				seed[i] = seed[j];
				seed[j] = temp;
			}
			//col
			base = 3 * r.nextInt(3);
			i = base + r.nextInt(3);
			j = base + r.nextInt(3);
			if(i != j) {
				for(int kk = 0; kk < 9; kk++) {
					int mid = seed[kk][i];
					seed[kk][i] = seed[kk][j];
					seed[kk][i] = mid;
				}
			}
		}
#####三行、三列交换(third swap)
		for(int k = 0; k < 10; k++) {//k is swap times
			//row
			int i = 3 * r.nextInt(3);
			int j = 3 * r.nextInt(3);
			if(i != j) {
				for(int ki = 0; ki < 3; ki++) {
					int [] temp = seed[i+ki];
					seed[i+ki] = seed[j+ki];
					seed[j+ki] = temp;
				}
			}
			//col
			i = 3 * r.nextInt(3);
			j = 3 * r.nextInt(3);
			if(i != j) {
				for(int kj = 0; kj < 3; kj++) {
					for(int kk = 0; kk < 9; kk++) {
						int mid = seed[kk][i+kj];
						seed[kk][i+kj] = seed[kk][j+kj];
						seed[kk][i+kj] = mid;
					}
				}
			}
		}
	####4.返回数独矩阵  
		return seed;

####2 读入用户输入  
将用户输入放入数组中。

		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to Sudoku ^_^");
		System.out.print("How many numbers would u like to input(25<=n<=50): ");
		int n = in.nextInt();
		int[] knowed = new int[n];
		for(int i = 0; i <knowed.length; i++) {
			knowed[i] = in.nextInt();
		}
		Arrays.sort(knowed);

####3 生成显示布尔矩阵  
遍历knowed数组（即遍历用户输入），对于每一个数knowed[k]，在数独矩阵的随机行中找到其所在的位置，并在dis矩阵中的相同位置标记为true（即显示）。  

		public static boolean[][] boolInit(int[][] sudo, int[] knowed) {
		Random r = new Random();
		boolean[][] dis = new boolean[9][9];
		for(int k = 0; k < knowed.length; ) {
			int i = r.nextInt(9);
			int j;
			for(j = 0; j < 9; j++) {
				if(sudo[i][j] == knowed[k]) break;
			}
			if(dis[i][j]) continue;
			dis[i][j] = true;
			k++;
		}
		return dis;
		}	

####4输出数独问题与答案
根据布尔显示矩阵，值为true的位置即为显示；而问题的答案，我们已经生成。  

	System.out.println("Problem:");
	System.out.println("+-----+-----+-----+");
	for(int i = 0; i < 9; i++) {
		if(i != 0 && i % 3 == 0) System.out.println("+-----------------+");
		System.out.print("|");
		for(int j = 0; j < 9; j++) {
			if(display[i][j]) System.out.print(sudo[i][j]);
			else              System.out.print(" ");
			if(j %3 == 2)     System.out.print("|");
			else              System.out.print(" ");
		}
		System.out.println();
	}
	System.out.println("+-----+-----+-----+");
	System.out.print("Press any key() to show the answer");
	in.next();
	System.out.println("Answer:");
	System.out.println("+-----+-----+-----+");
	for(int i = 0; i < 9; i++) {
		if(i != 0 && i % 3 == 0) System.out.println("+-----------------+");
		System.out.print("|");
		for(int j = 0; j < 9; j++) {
			System.out.print(sudo[i][j]);
			if(j %3 == 2)     System.out.print("|");
			else              System.out.print(" ");
		}
		System.out.println();
	}
	System.out.println("+-----+-----+-----+");

##注意事项
初始种子不能是  

	{1,2,3,4,5,6,7,8,9},
	{4,5,6,7,8,9,1,2,3},
	{7,8,9,1,2,3,4,5,6},
	{2,3,4,5,6,7,8,9,1},
	{5,6,7,8,9,1,2,3,4},
	{8,9,1,2,3,4,5,6,7},
	{3,4,5,6,7,8,9,1,2},
	{6,7,8,9,1,2,3,4,5},
	{9,1,2,3,4,5,6,7,8}

因为，如果设置这个，后面无论你怎么变换数字、交换行列，那么生成的数独只有一种规律：
比如第一行的前三个数字是1，8，5，那么，九宫格第二行第3列到第6列，或者第三行第3列到第6列，数字必然是1 8 5三个数字的组合（比如：1,5,8或者8,5,1或者8,1,5）。

##运行结果  

![](https://i.imgur.com/Kgaz0NS.png)
