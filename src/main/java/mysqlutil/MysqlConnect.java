package mysqlutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import constant.Constant;
import matching.CreatePattern;

public class MysqlConnect {
	private static final Logger logger = LoggerFactory.getLogger(MysqlConnect.class);
	protected Connection myConnection;
	public MysqlConnect(String hostName, String dbName, String user, String password) throws SQLException {
		String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName + "?characterEncoding=UTF-8";
		myConnection = DriverManager.getConnection(connectionURL, user, password);
	}
	public HashMap<Integer, String> getKeywordListInDomain(int domainID) {
		String querry = "SELECT keywordid, Name "
				+ " FROM domainid_keywordid, keyword "
				+ " where keywordid = keyword.Id and "
				+ " domainid = " + domainID + ";";
		HashMap<Integer, String> keywordList = new HashMap<Integer, String>();
		try {
			Statement statement = myConnection.createStatement();
			ResultSet rs = statement.executeQuery(querry);
			while(rs.next()) {
				keywordList.put(rs.getInt("keywordid"), rs.getString("Name").toLowerCase());
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywordList;
	}
	
	public String getDocContentInDomain(int keywordID) {
		String querry = "SELECT keywordid, Content "
				+ " FROM contents "
				+ " where "
				+ " keywordid = " + keywordID + " "
				+ " limit 1" + ";";
		String result = "";
		try {
			Statement statement = myConnection.createStatement();
			ResultSet rs = statement.executeQuery(querry);
			if(rs.next()) {
				result = rs.getString("Content");
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void processAllDocFromKeyword(int keywordID, Consumer<String> processor) throws SQLException {
		String querry = "SELECT Id,contents.Content "
				+ " FROM contents, sentiment_result "
				+ " where Id = AdmicroId and Positive != -1 and "
				+ " contents.keywordid = " + keywordID + " "
				+ ";";
		Statement statement = myConnection.createStatement();
		statement.setFetchSize(100);
		ResultSet rs = statement.executeQuery(querry);
		while(rs.next()){
			processor.accept(rs.getString("Content"));
		}
		rs.close();
	}
	public void closeConnection() throws SQLException {
		this.myConnection.close();
	}
	public static void main(String[] args) throws SQLException {
		MysqlConnect cn = new MysqlConnect(Constant.mainMysqlIP, Constant.mainMysqlDBName, 
				Constant.mainMysqlUser, Constant.mainMysqlPass);
		HashMap<Integer, String> keywords = cn.getKeywordListInDomain(2);
		for (Entry<Integer, String> entry:keywords.entrySet()){
			String s = entry.getValue();
			int id = entry.getKey();
			System.out.print(s + "\t" + id + "\t");
			//System.out.println(CreatePattern.createPatternFromString(s));
		}
	}
}
