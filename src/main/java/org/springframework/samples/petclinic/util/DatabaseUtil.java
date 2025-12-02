/*
 * Database utilities with SQL injection vulnerabilities
 */
package org.springframework.samples.petclinic.util;

import java.sql.*;

public class DatabaseUtil {

	// VULNERABILITY: SQL Injection via string concatenation
	public static ResultSet findUserByUsername(Connection conn, String username) throws SQLException {
		Statement stmt = conn.createStatement();
		// SQL injection vulnerability
		String query = "SELECT * FROM users WHERE username = '" + username + "'";
		return stmt.executeQuery(query);
	}

	// VULNERABILITY: SQL Injection in WHERE clause
	public static void updateUserEmail(Connection conn, String userId, String email) throws SQLException {
		Statement stmt = conn.createStatement();
		// SQL injection vulnerability
		String sql = "UPDATE users SET email = '" + email + "' WHERE id = " + userId;
		stmt.executeUpdate(sql);
	}

	// VULNERABILITY: SQL Injection in ORDER BY clause
	public static ResultSet getOwnersSorted(Connection conn, String sortColumn) throws SQLException {
		Statement stmt = conn.createStatement();
		// SQL injection in ORDER BY
		String query = "SELECT * FROM owners ORDER BY " + sortColumn;
		return stmt.executeQuery(query);
	}

	// FIXED: Use PreparedStatement to prevent SQL injection
	public static ResultSet executeCustomQuery(Connection conn, String tableName, String condition) 
			throws SQLException {
		// Use PreparedStatement with parameterized query
		String query = "SELECT * FROM " + tableName + " WHERE condition = ?";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, condition);
		return pstmt.executeQuery();
	}

	// VULNERABILITY: Connection not closed (resource leak)
	public static void executeDangerousQuery(String query) {
		try {
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/petclinic", "user", "pass");
			Statement stmt = conn.createStatement();
			stmt.executeQuery(query);
			// Connection and statement never closed - resource leak
		} catch (Exception e) {
			// Exception swallowed
		}
	}
}
