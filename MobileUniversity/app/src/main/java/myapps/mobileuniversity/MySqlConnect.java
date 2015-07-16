package myapps.mobileuniversity;

/**
 * Created by RUAVAL on 11.07.2015.
 */
import java.sql.*;
import java.util.Properties;

public class MySqlConnect {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void readDataBase() throws Exception {
        try {
// This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
// Setup the connection with the DB
            Properties connInfo = new Properties();
            connInfo.put("user", "root");
            connInfo.put("password", "nighthunger00");
            connInfo.setProperty("characterEncoding", "utf8");
            connInfo.put("charSet", "utf8"); // (2)
            String url = "jdbc:mysql://mydb.cwdggskdmdmp.us-west-2.rds.amazonaws.com:3306";
            Class.forName ("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection (url,"root","nighthunger00");
// connect = DriverManager
//.getConnection("jdbc:mysql://mydb.cwdggskdmdmp.us-west-2.rds.amazonaws.com:3306/university", connInfo);

// Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
// Result set get the result of the SQL query
            resultSet = statement.executeQuery("SET NAMES utf8 COLLATE utf8_unicode_ci");
            resultSet = statement
                    .executeQuery("select * from University.Faculty");
            writeResultSet(resultSet);
// Prepare
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
// Now get some metadata from the database
// Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
        }
    }
     String getFaculty() throws SQLException {
// ResultSet is initially before the first data set

// It is possible to get the columns via name
// also possible to get the columns via the column number
// which starts at 1
// e.g. resultSet.getSTring(2);
            String idFaculty = resultSet.getString("idFaculty");
            String name = resultSet.getString("Name");
            return name;

    }
    private void writeResultSet(ResultSet resultSet) throws SQLException {
// ResultSet is initially before the first data set
        while (resultSet.next()) {
// It is possible to get the columns via name
// also possible to get the columns via the column number
// which starts at 1
// e.g. resultSet.getSTring(2);
            String idFaculty = resultSet.getString("idFaculty");
            String name = resultSet.getString("Name");
            System.out.println("id: " + idFaculty);
            System.out.println("Faculty: " + name);
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

}