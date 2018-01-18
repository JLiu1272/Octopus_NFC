package com.enterprise.jliu.uitesting.constants;

/**
 * Created by JenniferLiu on 4/12/2017.
 */

/* Exception Class */
import com.enterprise.jliu.uitesting.exceptions.UnknownStation;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;


public class ConstantValues {

    private final int NUM_STATONS = 4;

    private Map<String, Station> STATION_INFO = new HashMap<>();

    public ConstantValues()
    {
        Station[] stations = new Station[NUM_STATONS];
        stations[0] = new Station("Wan Chai", 4.00);
        stations[1] = new Station("Causeway Bay", 4.00);
        stations[2] = new Station("Central", 4.00);
        stations[3] = new Station("Kennedy Town", 4.00);

        STATION_INFO.put("01", stations[0]);
        STATION_INFO.put("02", stations[1]);
        STATION_INFO.put("03", stations[2]);
        STATION_INFO.put("04", stations[3]);
    }

    public Station hexToStation(String hex) throws UnknownStation
    {
        if (!STATION_INFO.containsKey(hex)){
            throw new UnknownStation("Station does not exist");
        }
        return STATION_INFO.get(hex);
    }

}
