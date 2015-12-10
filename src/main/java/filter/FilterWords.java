package filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.BOMInputStream;


public class FilterWords {
	public static String FILE_NAME = "filter_words.txt";
	public static String createPatternFromString(String s){
		s = s.trim();
		s = s.replace("\\s+", "[\\W]*");
		return s + "\\W";
	}
	public static boolean isRemoved(StringBuilder content, Iterable<String> keywordList, int threshold) {
		HashSet<String> patternSet = new HashSet<String>();
		for(String keyword:keywordList) {
			patternSet.add(createPatternFromString(keyword));
		}
		int[] count = new int[patternSet.size()];
		int id = 0;
		for(String pattern: patternSet) {
			count[id] = countAppearance(content, pattern);
//			System.out.println(content);
			id++;
		}
		for (int c: count) {
			if (c > threshold) return true;
		}
		return false;
	}
	private static int countAppearance(StringBuilder content, String pattern) {
		Pattern regex =  Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = regex.matcher(content);
		int count = 0;
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()){
			matcher.appendReplacement(buffer, "<font color=\"red\">"+  Matcher.quoteReplacement(matcher.group()) + "</font>");
			count++;
		}
		matcher.appendTail(buffer);
		content.setLength(0);
		content.append(buffer);
		System.out.println(content);
		return count;
	}
	public static ArrayList<String> readWordList(String fileName) throws IOException {
		ArrayList<String> listKeyword = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(new FileInputStream(fileName)),Charset.forName("UTF-8")));
		String line = "";
		while((line = reader.readLine()) != null){
			String s = line.trim();
			if (s.length() > 0) {
				System.out.println(s);
				listKeyword.add(s);
			}
			
		}
		reader.close();
		return listKeyword;
	}
	public static void main(String[] args) throws IOException{
		ArrayList<String> listFilter = readWordList(FILE_NAME);
		
	}
}
