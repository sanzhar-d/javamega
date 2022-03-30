package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.CourseDao;
import kg.itschool.crm.dao.daoutil.Log;
import kg.itschool.crm.model.Course;
import java.sql.*;

public class CourseDaoImpl implements CourseDao {

    public CourseDaoImpl() {
        Connection connection = null;
        Statement statement = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " establishing connection");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), connection.getClass().getSimpleName(), " connection established");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_courses(" +
                    "id             BIGSERIAL,              " +
                    "name           VARCHAR(50)  NOT NULL,  " +
                    "price          MONEY        NOT NULL,  " +
                    "course_format  VARCHAR      NOT NULL,  " +
                    "date_created   TIMESTAMP    NOT NULL DEFAULT NOW(), " +
                    "" +
                    "CONSTRAINT pk_course_id PRIMARY KEY(id))";

            Log.info(this.getClass().getSimpleName(), Statement.class.getSimpleName(), " creating statement...");
            statement = connection.createStatement();
            Log.info(this.getClass().getSimpleName(), Statement.class.getSimpleName(), " executing create table statement...");
            statement.execute(ddlQuery);

        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public Course save(Course course) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Course savedCourse = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connecting to database...");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connection succeeded.");

            String createQuery = "INSERT INTO tb_courses(" +
                    "name, price, course_format, date_created ) " +

                    "VALUES(?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, course.getName());
            preparedStatement.setString(2, String.valueOf(course.getPrice()));
            preparedStatement.setString(3, String.valueOf(course.getCourseFormat()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(course.getDateCreated()));

            preparedStatement.execute();
            close(preparedStatement);

            String readQuery = "SELECT * FROM tb_courses ORDER BY id DESC LIMIT 1";

            preparedStatement = connection.prepareStatement(readQuery);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            savedCourse = new Course();
            savedCourse.setId(resultSet.getLong("id"));
            savedCourse.setName(resultSet.getString("name"));
            savedCourse.setPrice(Double.parseDouble(resultSet.getString("price")));
            savedCourse.setCourseFormat(resultSet.getString("course_format"));
            savedCourse.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return savedCourse;
    }

    @Override
    public Course findById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Course course = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connecting to database...");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connection succeeded.");

            String readQuery = "SELECT * FROM tb_courses WHERE id = ?";

            preparedStatement = connection.prepareStatement(readQuery);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            course = new Course();
            course.setId(resultSet.getLong("id"));
            course.setName(resultSet.getString("name"));
            course.setPrice(Double.parseDouble(resultSet.getString("price")));
            course.setCourseFormat(resultSet.getString("course_format"));
            course.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());

        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }
        return course;
    }

    private void close(AutoCloseable closeable) {
        try {
            Log.info(this.getClass().getSimpleName(), closeable.getClass().getSimpleName()," closing...");
            closeable.close();
            Log.info(this.getClass().getSimpleName(), closeable.getClass().getSimpleName()," closed...");
        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
