package homework04;

import java.util.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseURL {
	public static ArrayList<String> uList = new ArrayList<>();;

	//递归调用，实现深度遍历,depth是用户指定的遍历深度，d表示depth-当前深度，d为0时，说明到头了
	public static void processPage(String URL, int depth, int d) throws Exception{
		if(d == depth) {
			uList.add(URL);
			System.out.println(URL);
		}
		if(d == 0)	   return;
		try {
			Document doc = Jsoup.connect(URL).timeout(0).get();
			Elements links = doc.select("a[href]");
			for(Element link: links) {
				//if(link.attr("href").contains("zju.edu")) {
				if(uList.size() == 200) break;
				String s = link.attr("abs:href");
				if(!uList.contains(s)) {
					for(int i = 0; i < (depth-d+1); i++)
						System.out.print(">");
					uList.add(s);
					System.out.println(s);	
				}
				processPage(s, depth, d-1);
				//}
			}
		}catch (Exception e) {
			return;
		}
	}
}
