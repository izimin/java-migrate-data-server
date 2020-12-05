package repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pojo.ChampionshipPojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DbRepository {
    private HikariDataSource ds;

    public DbRepository() {
        ds = new HikariDataSource(
                new HikariConfig("/hikaricp.properties")
        );
    }

    public void initDb(String init) {
        try (Connection conn = ds.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(init);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToNormalizeDb(List<ChampionshipPojo> championshipPojos) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            for (ChampionshipPojo pojo : championshipPojos) {

                // Чемпионат
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.championship(id, year) " +
                                "VALUES (?, ?) \n" +
                                "ON CONFLICT (id)\n" +
                                "DO NOTHING;"
                );
                preparedStatement.setInt(1, pojo.getIdChampionship());
                preparedStatement.setInt(2, pojo.getYearChampionship());
                preparedStatement.execute();

                // Страна
                preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.country(id, name) " +
                                "VALUES (?, ?) \n" +
                                "ON CONFLICT (id)\n" +
                                "DO NOTHING;"
                );
                preparedStatement.setInt(1, pojo.getIdCountry());
                preparedStatement.setString(2, pojo.getNameCountry());
                preparedStatement.execute();

                // Вид спорта
                preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.kind_of_sport(id, name) " +
                                "VALUES (?, ?) \n" +
                                "ON CONFLICT (id)\n" +
                                "DO NOTHING;"
                );
                preparedStatement.setInt(1, pojo.getIdKindOfSport());
                preparedStatement.setString(2, pojo.getNameKindOfSport());
                preparedStatement.execute();

                // Соревнования
                preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.competition(id, kind_of_sport_id, championship_id) " +
                                "VALUES (?, ?, ?) \n" +
                                "ON CONFLICT (id, kind_of_sport_id, championship_id)\n" +
                                "DO NOTHING;"
                );
                preparedStatement.setInt(1, pojo.getIdCompetition());
                preparedStatement.setInt(2, pojo.getIdKindOfSport());
                preparedStatement.setInt(3, pojo.getIdChampionship());
                preparedStatement.execute();

                // Спортсмен
                preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.athlete(id, name, age) " +
                                "VALUES (?, ?, ?) \n" +
                                "ON CONFLICT (id)\n" +
                                "DO NOTHING;"
                );

                preparedStatement.setInt(1, pojo.getIdAthlete());
                preparedStatement.setString(2, pojo.getNameAthlete());
                preparedStatement.setInt(3, pojo.getYearChampionship());
                preparedStatement.execute();


                // Рефка
                preparedStatement = conn.prepareStatement(
                        "INSERT INTO public.ref_competition_athlete(competition_id, athlete_id) " +
                                "VALUES (?, ?) \n" +
                                "ON CONFLICT (competition_id, athlete_id)\n" +
                                "DO NOTHING;"
                );
                preparedStatement.setInt(1, pojo.getIdCompetition());
                preparedStatement.setInt(2, pojo.getIdAthlete());
                preparedStatement.execute();
            }
        }
    }
}