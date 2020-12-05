package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChampionshipPojo implements Serializable {
    private Integer idChampionship;
    private Integer yearChampionship;
    private Integer idCountry;
    private String nameCountry;
    private Integer idCompetition;
    private Integer idKindOfSport;
    private String nameKindOfSport;
    private Integer idAthlete;
    private String nameAthlete;
    private Integer ageAthlete;
}
