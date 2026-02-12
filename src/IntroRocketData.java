package src;

public class IntroRocketData {
    //define a constructor
    //byte is 17 long

    int startFlag = 181; //0xB5
    int type = 1;
    int length = 20;
    int latitude = 0;
    int longitude = 0;
    int satellites = 0;
    int crc = 0;
    int endFlag = 98; //0x62
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
