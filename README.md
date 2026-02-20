# java-ground-station
An abstract ground station GUI made in Java

Placeholder README file. 

How to run:
If you are a Windows or macOS system which features JRE, you can run the JAR file by double clicking. No additional Java software should be required.

Java 17+ reccomended. 

You can also go into the command line and run this command to use the JAR file. 

"java -jar java-ground-station.jar"

The window for the GUI will appear empty if there are no USB devices connected to the device you're on.

How to use:

The GUI consists of a system of tabs, where the top tabs are the serial devices that are detected. The more USB devices detected, the more top tabs will appear. The second tier of tabs below that are relating to specific details of the device itself and accessing certain special windows like the Debug Window and the Console Window. 

Once a "sender" and "reciever" device are connected via serial connection, you can see them appear in the top tabs. It is reccomended that you connect the USB devices first before running/opening the program. The sender device will send packets of bytes to the reader device. The data for this can be seen in the reciever's "Debug" tab window as a continuous stream of information. 

The reciever should recieve a continuous stream of information, while the sender is the one that is sending the information to the receiver and it won't be really getting too much data itself. 

You can also check the "QR Code" tab in each of the USB device's sections to see a QR code to a Google Maps link of the exact lat and long of the USB device.

If the program doesn't work, try re-running it by closing and starting it up again.