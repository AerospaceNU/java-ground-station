package src;
import com.fazecast.jSerialComm.SerialPort;

public class ConfigPage {
    //all of these values need to be set since they are currently just placeholders
    public static String BOARD_NAME = "UNKNOWN BOARD"; //string max len under 100 chars // Don't worry about datatype

    /*enum BoardName {
        BOARD_NAME("UNKNOWN BOARD");

        private int bn;

        private BoardName(int bn) {
            this.bn = bn;
        }

        public int getbn() {
            return bn;
        }
    }

    String test = BoardName.valueOf("BOARD_NAME").getbn();*/

    public static int FLIGHT_STATE = 0; //UNKNOWN_FLIGHT_STATE, value >= PRE_FLIGHT && value <= UNKNOWN_FLIGHT_STATE
public static float GROUND_ELEVATION = 0.0f; //Constants::STANDARD_TEMPERATURE_K, value >= 180.0 && value <= 350.0
public static float GROUND_TEMPERATURE = 0.0f;
public static int BOARD_ORIENTATION = 0;
//public static Quaternion_s LAUNCH_ANGLE = new Quaternion_s({0,0,0,1}, true);
//public static GyroscopeBias_s GYRO_BIAS = new GyroscopeBias_s({0.0f,0.0f,0.0f}, true);
public static float MAIN_ELEVATION = 0.0f;
public static int DROGUE_DELAY = 1000; //value < 60 * Units::S_TO_MS
public static float BATTERY_VOLTAGE_SENSOR_SCALE_FACTOR = 0.0f;
public static int PYRO_FIRE_DURATION = 0;
public static float RADIO_FREQUENCY = 915.0f;
}
