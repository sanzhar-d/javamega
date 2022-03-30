package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.MentorDao;
import kg.itschool.crm.model.Mentor;

import java.sql.*;

public class MentorDaoImpl implements MentorDao {

    public MentorDaoImpl() {
        Connection connection = null;
        Statement statement = null;

        try {  // api:driver://host:port/database_name
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_mentors(" +
                    "id           BIGSERIAL, " +
                    "first_name   VARCHAR(50)  NOT NULL, " +
                    "last_name     VARCHAR(50)  NOT NULL, " +
                    "email        VARCHAR(100) NOT NULL UNIQUE, " +
                    "phone_number CHAR(13)     NOT NULL, " +
                    "salary       MONEY        NOT NULL, " +
                    "dob          DATE         NOT NULL CHECK(dob < NOW()), " +
                    "date_created TIMESTAMP    NOT NULL DEFAULT NOW(), " +
                    "" +
                    "CONSTRAINT pk_manager_id PRIMARY KEY(id), " +
                    "CONSTRAINT chk_manager_salary CHECK (salary > MONEY(0))," +
                    "CONSTRAINT chk_manager_first_name CHECK(LENGTH(first_name) > 2));";

            System.out.println("Creating statement...");
            statement = connection.createStatement();
            System.out.println("Executing create table statement...");
            statement.execute(ddlQuery);

        } catch (SQLException e) {
            System.out.println("Some error occurred");
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public Mentor save(Mentor mentor) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Mentor savedMentor = null;

        try {
            System.out.println("Connecting to database...");
            connection = getConnection();
            System.out.println("Connection succeeded.");

            String createQuery = "INSERT INTO tb_mentors(" +
                    "last_name, first_name, phone_number, salary, date_created, dob, email) " +

                    "VALUES(?, ?, ?, MONEY(?), ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, mentor.getLastName());
            preparedStatement.setString(2, mentor.getFirstName());
            preparedStatement.setString(3, mentor.getPhoneNumber());
            preparedStatement.setString(4, mentor.getSalary() + "");
            preparedStatement.setTimestamp(5, Timestamp.valueOf(mentor.getDateCreated()));
            preparedStatement.setDate(6, Date.valueOf(mentor.getDob()));
            preparedStatement.setString(7, mentor.getEmail());

            preparedStatement.execute();
            close(preparedStatement);

            String readQuery = "SELECT * FROM tb_mentors ORDER BY id DESC LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            savedMentor = new Mentor();
            savedMentor.setId(resultSet.getLong("id"));
            savedMentor.setFirstName(resultSet.getString("first_name"));
            savedMentor.setLastName(resultSet.getString("last_name"));
            savedMentor.setEmail(resultSet.getString("email"));
            savedMentor.setPhoneNumber(resultSet.getString("phone_number"));
            savedMentor.setSalary(Double.valueOf(resultSet.getString("salary").replaceAll("[^\\d\\.]", "")));
            savedMentor.setDob(resultSet.getDate("dob").toLocalDate());
            savedMentor.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return savedMentor;
    }


    @Override
    public Mentor findById(Long id) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Mentor mentor = null;

        try {
            connection = getConnection();

            String readQuery = "SELECT * FROM tb_mentors WHERE id = ?";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            mentor = new Mentor();
            mentor.setId(resultSet.getLong("id"));
            mentor.setFirstName(resultSet.getString("first_name"));
            mentor.setLastName(resultSet.getString("last_name"));
            mentor.setEmail(resultSet.getString("email"));
            mentor.setPhoneNumber(resultSet.getString("phone_number"));
            mentor.setSalary(Double.valueOf(resultSet.getString("salary").replaceAll("[^\\d\\.]", "")));
            mentor.setDob(resultSet.getDate("dob").toLocalDate());
            mentor.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());



        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return mentor;
    }

    private void close(AutoCloseable closeable) {
        try {
            System.out.println(closeable.getClass().getSimpleName() + " closing...");
            closeable.close();
            System.out.println(closeable.getClass().getSimpleName() + " closed.");
        } catch (Exception e) {
            System.out.println("Could not close " + closeable.getClass().getSimpleName());
            e.printStackTrace();
        }
    }
}
