package matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreatePattern {
	public static Pattern pattr = Pattern.compile("([\\w]+)[\\W]*bank", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	public static Pattern createPatternFromString(String s){
		s = s.trim();
		Matcher matcher = pattr.matcher(s);
		if (matcher.matches()){
			String prefix = matcher.group(1);
			String regex = "(" + prefix + "[\\W]*bank\\W)";
			return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		} else {
			s = s.replace(" ", "[\\W]*");
			s = "(" + s + "\\W)";
			return Pattern.compile(s, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		
	}
}
