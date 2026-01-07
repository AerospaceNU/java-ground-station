package src;

public class Commands {
    private String str;
    public String concatenatedCommand;
    //SerialPort port = SerialPort.getCommPorts();
    //SerialTab tab = new SerialTab(port);

    public Commands(String str){
        this.str = str;
        String[] splitStr = str.split(" ");
        String firstCommand = splitStr[0];
        String secondCommand = splitStr[1];

        concatenatedCommand = "--" + firstCommand + " -set" + secondCommand; 

        //check the "first command" and correspond that to a config value (i.e. "BOARD NAME"), then the second command is setting or changing what we already have
    }
}
