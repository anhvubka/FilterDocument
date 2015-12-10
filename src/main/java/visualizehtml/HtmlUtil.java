package visualizehtml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class HtmlUtil {
	public static File writeHTML(String fileName, Iterable<String> lines) throws IOException {
		File f = new File(fileName);
		boolean check = false;
		if(!f.exists()){
			f.createNewFile();
			check = true;
		}
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true), StandardCharsets.UTF_8));
		///BufferedWriter output = new BufferedWriter((new FileWriter(f,true)));
		if(check) {
			output.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
			output.newLine();
		}
		output.append("<p> ");
		for (String s: lines) {
			output.append(s);
			output.newLine();
			output.append("<br/>");
			output.newLine();
		}
		output.append("-----------------------------------------------------------------------------------------------------------------------------------------------");
		output.newLine();
		output.append("</p>");
		output.newLine();
		output.close();
		return f;
	}
}
