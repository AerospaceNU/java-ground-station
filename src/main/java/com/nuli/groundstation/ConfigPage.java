package com.nuli.groundstation;

import com.fazecast.jSerialComm.SerialPort;

public class ConfigPage {
    //all of these values need to be set since they are currently just placeholders
    //work in progress page for setting up configs
    public static String BOARD_NAME = "UNKNOWN BOARD"; //string max len under 100 chars // Don't worry about datatype

    enum BoardName {
        BOARD_NAME("UNKNOWN BOARD"); //work on config page and setting up some stuff ?

        private String bn; 

        private BoardName(String bn) {
            this.bn = bn;
        }

        public String getbn() {
            return bn;
        }
    }

    enum IntConfigs {
        FLIGHT_STATE(0),
        BOARD_ORIENTATION(0),
        DROGUE_DELAY(1000),
        PYRO_FIRE_DURATION(0);

        private final int value;
        
        private IntConfigs(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum FloatConfigs {
        GROUND_ELEVATION(0),
        GROUND_TEMPERATURE(0),
        MAIN_ELEVATION(0),
        BATTERY_VOLTAGE_SENSOR_SCALE_FACTOR(0),
        RADIO_FREQUENCY(915);

        private final float value;
        
        private FloatConfigs(float value){
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    String test = BoardName.valueOf("BOARD_NAME").getbn();

    /*public static int FLIGHT_STATE = 0; //UNKNOWN_FLIGHT_STATE, value >= PRE_FLIGHT && value <= UNKNOWN_FLIGHT_STATE
    public static float GROUND_ELEVATION = 0.0f; //Constants::STANDARD_TEMPERATURE_K, value >= 180.0 && value <= 350.0
    public static float GROUND_TEMPERATURE = 0.0f;
    public static int BOARD_ORIENTATION = 0;
    //public static Quaternion_s LAUNCH_ANGLE = new Quaternion_s({0,0,0,1}, true);
    //public static GyroscopeBias_s GYRO_BIAS = new GyroscopeBias_s({0.0f,0.0f,0.0f}, true);
    public static float MAIN_ELEVATION = 0.0f;
    public static int DROGUE_DELAY = 1000; //value < 60 * Units::S_TO_MS
    public static float BATTERY_VOLTAGE_SENSOR_SCALE_FACTOR = 0.0f;
    public static int PYRO_FIRE_DURATION = 0;
    public static float RADIO_FREQUENCY = 915.0f;*/
}
