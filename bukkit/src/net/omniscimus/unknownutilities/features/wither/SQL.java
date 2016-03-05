package net.omniscimus.unknownutilities.features.wither;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.omniscimus.unknownutilities.UnknownUtilities;
import code.husky.mysql.MySQL;

public class SQL {

	private final UnknownUtilities plugin;

	private MySQL mySQL;
	private Connection con;
	private final String database;

	protected SQL(UnknownUtilities plugin, String hostName, String port, String database, String username, String password) throws ClassNotFoundException, SQLException {
		this.plugin = plugin;
		this.database = database;

		mySQL = new MySQL(plugin, hostName, port, database, username, password);
		executeUpdate("CREATE TABLE IF NOT EXISTS " + database + ".wither (playeruuid CHAR(36) NOT NULL UNIQUE, timesfought SMALLINT, timeswon SMALLINT, shortesttime BIGINT, numberofwithers SMALLINT, lastdate DATE)");
	}

	// ====== SQL setup methods ======
	
	private Connection getCon() throws ClassNotFoundException, SQLException {
		if(con == null) {
			con = mySQL.openConnection();
			return con;
		}
		else if(con.isValid(60000)) return con;
		else {
			con = mySQL.openConnection();
			return con;
		}
	}
	public boolean closeSQLConnection() {
		try {
			con.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ======= Statement executors ======
	
	/**
	 * Executes some SQL.
	 * @param sql a String of SQL that should be executed.
	 * @throws SQLException if there's something wrong with the query
	 * @throws ClassNotFoundException if the SQL connection couldn't be opened
	 */
	private void executeUpdate(String sql) throws SQLException, ClassNotFoundException {
		Statement statement = getCon().createStatement();
		try {
			statement.executeUpdate(sql);
		} finally {
			statement.close();
		}
	}
	
	/**
	 * Executes some SQL and returns the results.
	 * @param sql a String of SQL that should be executed.
	 * @throws SQLException if there's something wrong with the query
	 * @throws ClassNotFoundException if the SQL connection couldn't be opened
	 * @return the stuff the query returned
	 */
	private ResultSet executeQuery(String sql) throws ClassNotFoundException, SQLException {
		Statement statement = getCon().createStatement();
		try {
			return statement.executeQuery(sql);
		} finally {
			statement.close();
		}
	}
	
	// ======= Getters ======
	
	/**
	 * Make a top X of players and their number of wither kills.
	 * @param topX a limit for the query. This method will make a top topX.
	 * @return a List with Pairs of UUIDs (players) and Integers (number of wither kills).
	 */
	protected List<Pair<UUID, Integer>> getTopMostWitherKills(int topX) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT playeruuid,timeswon FROM " + database + ".wither ORDER BY timeswon DESC LIMIT " + topX + ";");
		
		int playeruuidColumnPosition = rs.findColumn("playeruuid");
		int timeswonColumnPosition = rs.findColumn("timeswon");
		
		List<Pair<UUID, Integer>> resultList = new ArrayList<Pair<UUID, Integer>>();
		while(rs.next()) {
			resultList.add(new Pair<UUID, Integer>(UUID.fromString(rs.getString(playeruuidColumnPosition)), rs.getInt(timeswonColumnPosition)));
		}
		return resultList;
	}
	protected List<Pair<UUID, Long>> getTopQuickestWitherKills(int topX) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT playeruuid,shortesttime FROM " + database + ".wither ORDER BY shortesttime ASC LIMIT " + topX + ";");
		
		int playeruuidColumnPosition = rs.findColumn("playeruuid");
		int shortesttimeColumnPosition = rs.findColumn("shortesttime");
		
		List<Pair<UUID, Long>> resultList = new ArrayList<Pair<UUID, Long>>();
		while(rs.next()) {
			resultList.add(new Pair<UUID, Long>(UUID.fromString(rs.getString(playeruuidColumnPosition)), rs.getLong(shortesttimeColumnPosition)));
		}
		return resultList;
	}
	protected List<Pair<UUID, Integer>> getTopMostWithersAtOnce(int topX) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT playeruuid,numberofwithers FROM " + database + ".wither ORDER BY numberofwithers DESC LIMIT " + topX + ";");
		
		int playeruuidColumnPosition = rs.findColumn("playeruuid");
		int numberofwithersColumnPosition = rs.findColumn("numberofwithers");
		
		List<Pair<UUID, Integer>> resultList = new ArrayList<Pair<UUID, Integer>>();
		while(rs.next()) {
			resultList.add(new Pair<UUID, Integer>(UUID.fromString(rs.getString(playeruuidColumnPosition)), rs.getInt(numberofwithersColumnPosition)));
		}
		return resultList;
	}
	
	@SuppressWarnings("deprecation")
	protected int getWitherKills(String playerName) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT timeswon FROM " + database + ".wither WHERE playeruuid = '" + plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString() + "';");
		if(rs.next()) {
			return rs.getInt("timeswon");
		}
		else {
			return 0;
		}
	}
	@SuppressWarnings("deprecation")
	protected int getWitherFights(String playerName) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT timesfought FROM " + database + ".wither WHERE playeruuid = '" + plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString() + "';");
		if(rs.next()) {
			return rs.getInt("timesfought");
		}
		else {
			return 0;
		}
	}
	protected int getWitherDefeats(String playerName) throws ClassNotFoundException, SQLException {
		return getWitherFights(playerName) - getWitherKills(playerName);
	}
	
	/**
	 * Gets the elapsed time of the quickest wither kill for a player.
	 * @param playerName Player to check
	 * @return player's shortest wither kill in milliseconds
	 * @throws SQLException if there's something wrong with the query
	 * @throws ClassNotFoundException if the SQL connection couldn't be opened
	 */
	@SuppressWarnings("deprecation")
	protected long getShortestWitherKillTime(String playerName) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT shortesttime FROM " + database + ".wither WHERE playeruuid = '" + plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString() + "';");
		if(rs.next()) {
			if(rs.getLong("shortesttime") == 0) return Long.MAX_VALUE;
			else return rs.getLong("shortesttime");
		}
		else return rs.getLong("shortesttime");
	}
	
	@SuppressWarnings("deprecation")
	protected int getNumberOfWithersAtOnce(String playerName) throws SQLException, ClassNotFoundException {
		ResultSet rs = executeQuery("SELECT numberofwithers FROM " + database + ".wither WHERE playeruuid = '" + plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString() + "';");
		if(rs.next()) {
			return rs.getInt("numberofwithers");
		}
		else {
			return 0;
		}
	}
	
	// ====== Setters ======

	@SuppressWarnings("deprecation")
	protected void setWitherKills(String playerName, int witherKills) throws SQLException, ClassNotFoundException {
		PreparedStatement setVotesStatement = getCon().prepareStatement("INSERT INTO " + database + ".wither (playeruuid, timeswon, lastdate) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playeruuid = VALUES(playeruuid), timeswon = VALUES(timeswon), lastdate = VALUES(lastdate);");
		try {
			setVotesStatement.setString(1, String.valueOf(plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString()));
			setVotesStatement.setInt(2, witherKills);
			setVotesStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			setVotesStatement.execute();
		} finally {
			setVotesStatement.close();
		}
	}
	@SuppressWarnings("deprecation")
	protected void setWitherFights(String playerName, int witherFights) throws SQLException, ClassNotFoundException {
		PreparedStatement setVotesStatement = getCon().prepareStatement("INSERT INTO " + database + ".wither (playeruuid, timesfought, lastdate) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playeruuid = VALUES(playeruuid), timesfought = VALUES(timesfought), lastdate = VALUES(lastdate);");
		try {
			setVotesStatement.setString(1, String.valueOf(plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString()));
			setVotesStatement.setInt(2, witherFights);
			setVotesStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			setVotesStatement.execute();
		} finally {
			setVotesStatement.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void updateShortestWitherKillTime(String playerName, long milliseconds) throws SQLException, ClassNotFoundException {
		if(getShortestWitherKillTime(playerName) > milliseconds) {
			PreparedStatement setTimeStatement = getCon().prepareStatement("INSERT INTO " + database + ".wither (playeruuid, shortesttime) VALUES (?, ?) ON DUPLICATE KEY UPDATE playeruuid = VALUES(playeruuid), shortesttime = VALUES(shortesttime);");
			try {
				setTimeStatement.setString(1, String.valueOf(plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString()));
				setTimeStatement.setLong(2, milliseconds);
				setTimeStatement.execute();
			} finally {
				setTimeStatement.close();
			}
		}
		
	}
	@SuppressWarnings("deprecation")
	protected void updateNumberOfWithersAtOnce(String playerName, int number) throws ClassNotFoundException, SQLException {
		if(getNumberOfWithersAtOnce(playerName) < number) {
			PreparedStatement setNumberStatement = getCon().prepareStatement("INSERT INTO " + database + ".wither (playeruuid, numberofwithers) VALUES (?, ?) ON DUPLICATE KEY UPDATE playeruuid = VALUES(playeruuid), numberofwithers = VALUES(numberofwithers);");
			try {
				setNumberStatement.setString(1, String.valueOf(plugin.getServer().getOfflinePlayer(playerName).getUniqueId().toString()));
				setNumberStatement.setInt(2, number);
				setNumberStatement.execute();
			} finally {
				setNumberStatement.close();
			}
		}
		
	}

}
