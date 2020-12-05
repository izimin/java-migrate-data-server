package service;

import repository.DbRepository;
import org.apache.commons.io.IOUtils;
import pojo.ChampionshipPojo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DbService {

    private DbRepository dbRepository;

    public DbService() {
        dbRepository = new DbRepository();
    }

    public void initDb() throws IOException {
        dbRepository.initDb(IOUtils.toString(DbService.class.getResourceAsStream("/init.sql"), "UTF-8"));
    }

    public void sendDataToNormalizeDb(List<ChampionshipPojo> championshipPojos) throws SQLException {
        dbRepository.sendDataToNormalizeDb(championshipPojos);
    }
}