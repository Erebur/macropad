package Testing;

import Macropad.Macropad;
import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;

public class Test{
    public static void main(String[] args) throws AWTException {
        //testing the device
        try {
            int port = Macropad.autoPortSuchen();

            SerialPort comPort = null;
            comPort = SerialPort.getCommPorts()[ port ];
            comPort.openPort();




        }catch (Exception e ){
            e.printStackTrace();
        }

    }


}
