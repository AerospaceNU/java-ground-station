package src;
//import java-ground-station.MainWindow;
import java.awt.EventQueue;

public class main {
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainWindow mainWindow = new MainWindow();
            }
    });
}
}