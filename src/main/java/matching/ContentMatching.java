package matching;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import constant.Constant;
import filter.FilterWords;
import mysqlutil.MysqlConnect;
import visualizehtml.HtmlUtil;

public class ContentMatching {
	public static String matchContent(String content, Pattern mainPttr, Pattern otherKeywordPttr) {
		Matcher mainMatch = mainPttr.matcher(content);
		int otherMatchCount = 0;
		int matchCount = 0;
		if (!mainMatch.find()) return "";
		else mainMatch.reset();
		StringBuffer mainBuffer = new StringBuffer();
		while (mainMatch.find()){
			String mainStr = Matcher.quoteReplacement(mainMatch.group());
			mainMatch.appendReplacement(mainBuffer, "<font color=\"blue\">"+  mainStr+ "</font>");
			matchCount++;
		}
		mainMatch.appendTail(mainBuffer);
		content = mainBuffer.toString();
		Matcher otherMatch = otherKeywordPttr.matcher(content);
		
		if(!otherMatch.find()) return "";
		else otherMatch.reset();
		StringBuffer otherBuffer = new StringBuffer();
		String otherMatchStr = "";
		while (otherMatch.find()) {
			otherMatchStr = Matcher.quoteReplacement(otherMatch.group());
			if (!mainPttr.matcher(otherMatchStr).matches()) {
				otherMatchCount++;
				otherMatch.appendReplacement(otherBuffer, "<font color=\"red\">"+  otherMatchStr + "</font>");
			}
		}
		otherMatch.appendTail(otherBuffer);
		if (otherMatchCount <= matchCount || otherMatchCount < 4) return "";
		System.out.println(otherMatchCount);
		return otherBuffer.toString();
	}
	public static Pattern createPatternFromList(Iterable<Pattern> listStr) {
		HashSet<String> pttrStrs = new HashSet<String>();
		for(Pattern pttr: listStr) {
			pttrStrs.add(pttr.pattern());
		}
		String newPttr = StringUtils.join(pttrStrs, "|");
		Pattern otherKeywordPttr = Pattern.compile(newPttr, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		return otherKeywordPttr;
	}
	public static void main(String[] args) throws SQLException, IOException {
		MysqlConnect cn = new MysqlConnect(Constant.mainMysqlIP, Constant.mainMysqlDBName, 
				Constant.mainMysqlUser, Constant.mainMysqlPass);
		
		HashMap<Integer, String> keywords = cn.getKeywordListInDomain(2);
		keywords.put(10000, "[\\w]+[\\W]*bank");
		HashMap<Integer,Pattern> pttrList = new HashMap<Integer,Pattern>();
		ArrayList<String> filterWords = FilterWords.readWordList(FilterWords.FILE_NAME);
		
		keywords.forEach((id, keyword) -> {
			pttrList.put(id, CreatePattern.createPatternFromString(keyword));
		});
		Pattern otherPttr = createPatternFromList(pttrList.values());
		System.out.println(otherPttr.pattern());
		for (Entry<Integer, Pattern> pttrEntry: pttrList.entrySet()) {
			int keywordID = pttrEntry.getKey();
			System.out.println(keywordID);
			Pattern mainPttr = pttrEntry.getValue();
			cn.processAllDocFromKeyword(keywordID, resultString -> {
				StringBuilder resultBuilder = new StringBuilder(resultString);
				boolean isRemoved = FilterWords.isRemoved(resultBuilder, filterWords, 3);
				if (isRemoved){
					try {
						//System.out.println(resultString);
						HtmlUtil.writeHTML(keywords.get(keywordID) + ".html",
								Arrays.asList(resultBuilder.toString().split("[!?]|(\\. )")));
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

//		for (Entry<Integer, Pattern> pttrEntry: pttrList.entrySet()) {
//			int keywordID = pttrEntry.getKey();
//			System.out.println(keywordID);
//			Pattern mainPttr = pttrEntry.getValue();
//			cn.processAllDocFromKeyword(keywordID, resultString -> {
//				String visualizeOutput = matchContent(resultString, mainPttr, otherPttr);
//				if (visualizeOutput.trim().length() > 0)
//					try {
//						String[] lines = visualizeOutput.split("[!?]|(\\. )");
//						HtmlUtil.writeHTML(keywords.get(keywordID) + ".html",lines);
//						System.out.println(visualizeOutput);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			});
//		}
		
	}
}
