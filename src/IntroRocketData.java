package src;

public class IntroRocketData {
    //define a constructor
    //byte is 17 long

    public static int startFlag = 0xB5; //0xB5 //181
    int type = 1;
    int length = 20;
    int latitude = 0;
    int longitude = 0;
    int satellites = 0;
    int crc = 0;
    public static int endFlag = 0x62; //0x62 //98
    byte[] buffer = new byte[17];
    public IntroRocketData(int startFlag, int type, int length, int latitude, int longitude, int satellites, int crc, int endFlag)
    {
        this.startFlag = startFlag;
        this.type = type;
        this.length = length;
        this.latitude = latitude;
        this.longitude = longitude;
        this.satellites = satellites;
        this.crc = crc;
        this.endFlag = endFlag;
        //print out raw data from intro radio
    }
}
