package com.example.ah.push;

import java.util.List;

/**
 * Created by ah on 12/07/2018.
 */

public class IncomingMessage {


    public String time;
    public Board body;

    public class Board
    {
        public String boardid;
        public List<Sensor> sensors;

        public class Sensor
        {
            String sensorid;
            String value;
        }
    }



    public IncomingMessage(){

    }
}

