package kg.itschool.crm.dao.impl;

import kg.itschool.crm.dao.CourseFormatDao;
import kg.itschool.crm.dao.daoutil.Log;
import kg.itschool.crm.model.CourseFormat;

import java.sql.*;

public class CourseFormatDaoImpl implements CourseFormatDao {

    public CourseFormatDaoImpl() {
        Connection connection = null;
        Statement statement = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " establishing connection");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), connection.getClass().getSimpleName(), " connection established");

            String ddlQuery = "CREATE TABLE IF NOT EXISTS tb_course_format(" +
                    "id                 BIGSERIAL, " +
                    "format             VARCHAR(50)  NOT NULL, " +
                    "course_duration_weeks INTEGER NOT NULL, " +
                    "lesson_duration    TIMESTAMP        NOT NULL, " +
                    "lesson_per_week    INTEGER         NOT NULL, " +
                    "is_online          BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "date_created TIMESTAMP    NOT NULL DEFAULT NOW(), " +
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
    public CourseFormat save(CourseFormat courseFormat) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        CourseFormat savedCourseFormat = null;

        try {
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connecting to database...");
            connection = getConnection();
            Log.info(this.getClass().getSimpleName(), Connection.class.getSimpleName(), " connection succeeded.");

            String createQuery = "INSERT INTO tb_course_format(" +
                    "format, courseDurationWeeks, lessonDuration, lessonPerWeek, isOnline, date_created ) " +

                    "VALUES(?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(createQuery);
            preparedStatement.setString(1, courseFormat.getFormat());
            preparedStatement.setInt(2, courseFormat.getCourseDurationWeeks());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(String.valueOf(courseFormat.getLessonDuration())));
            preparedStatement.setInt(4, courseFormat.getLessonPerWeek());
            preparedStatement.setBoolean(5, courseFormat.isOnline());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(courseFormat.getDateCreated()));




        } catch (SQLException e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        } finally {
            close(resultSet);
            close(preparedStatement);
            close(connection);
        }


        return null;
    }

    @Override
    public CourseFormat findById(Long id) {
        return null;
    }

    private void close(AutoCloseable closeable) {
        try {
            Log.info(this.getClass().getSimpleName(), closeable.getClass().getSimpleName(), " closing...");
            closeable.close();
            Log.info(this.getClass().getSimpleName(), closeable.getClass().getSimpleName(), " closed...");
        } catch (Exception e) {
            Log.error(this.getClass().getSimpleName(), e.getStackTrace()[0].getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }
}
