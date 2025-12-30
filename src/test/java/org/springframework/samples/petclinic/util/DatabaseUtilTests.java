package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseUtilTests {

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    }

    @Test
    void testFindUserByUsername() throws SQLException {
        // Arrange
        String username = "testUser";
        String expectedQuery = "SELECT * FROM users WHERE username = 'testUser'";

        // Act
        ResultSet result = DatabaseUtil.findUserByUsername(mockConnection, username);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testFindUserByUsername_WithSQLInjection() throws SQLException {
        // Arrange
        String maliciousUsername = "admin' OR '1'='1";
        String expectedQuery = "SELECT * FROM users WHERE username = 'admin' OR '1'='1'";

        // Act
        ResultSet result = DatabaseUtil.findUserByUsername(mockConnection, maliciousUsername);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testUpdateUserEmail() throws SQLException {
        // Arrange
        String userId = "123";
        String email = "test@example.com";
        String expectedSql = "UPDATE users SET email = 'test@example.com' WHERE id = 123";

        // Act
        DatabaseUtil.updateUserEmail(mockConnection, userId, email);

        // Assert
        verify(mockStatement).executeUpdate(expectedSql);
    }

    @Test
    void testUpdateUserEmail_WithSQLInjection() throws SQLException {
        // Arrange
        String userId = "123; DROP TABLE users;--";
        String email = "malicious@example.com";
        String expectedSql = "UPDATE users SET email = 'malicious@example.com' WHERE id = 123; DROP TABLE users;--";

        // Act
        DatabaseUtil.updateUserEmail(mockConnection, userId, email);

        // Assert
        verify(mockStatement).executeUpdate(expectedSql);
    }

    @Test
    void testGetOwnersSorted() throws SQLException {
        // Arrange
        String sortColumn = "last_name";
        String expectedQuery = "SELECT * FROM owners ORDER BY last_name";

        // Act
        ResultSet result = DatabaseUtil.getOwnersSorted(mockConnection, sortColumn);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testGetOwnersSorted_WithSQLInjection() throws SQLException {
        // Arrange
        String maliciousSortColumn = "last_name; DROP TABLE owners;--";
        String expectedQuery = "SELECT * FROM owners ORDER BY last_name; DROP TABLE owners;--";

        // Act
        ResultSet result = DatabaseUtil.getOwnersSorted(mockConnection, maliciousSortColumn);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testExecuteCustomQuery() throws SQLException {
        // Arrange
        String tableName = "owners";
        String condition = "city = 'Madison'";
        String expectedQuery = "SELECT * FROM owners WHERE city = 'Madison'";

        // Act
        ResultSet result = DatabaseUtil.executeCustomQuery(mockConnection, tableName, condition);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testExecuteCustomQuery_WithSQLInjection() throws SQLException {
        // Arrange
        String maliciousTableName = "owners; DROP TABLE pets;--";
        String condition = "1=1";
        String expectedQuery = "SELECT * FROM owners; DROP TABLE pets;-- WHERE 1=1";

        // Act
        ResultSet result = DatabaseUtil.executeCustomQuery(mockConnection, maliciousTableName, condition);

        // Assert
        verify(mockStatement).executeQuery(expectedQuery);
        assertThat(result).isEqualTo(mockResultSet);
    }

    @Test
    void testExecuteDangerousQuery() {
        // This test verifies that the method doesn't throw exceptions
        // We can't easily test for resource leaks in a unit test
        String query = "SELECT * FROM owners";
        
        // Act & Assert - should not throw exception
        DatabaseUtil.executeDangerousQuery(query);
    }

    @Test
    void testExecuteDangerousQuery_WithInvalidQuery() {
        // Even with an invalid query, the method should not throw exceptions due to exception swallowing
        String invalidQuery = "INVALID SQL QUERY";
        
        // Act & Assert - should not throw exception due to exception swallowing
        DatabaseUtil.executeDangerousQuery(invalidQuery);
    }
}
