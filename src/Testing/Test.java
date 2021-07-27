package Testing;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
 
public class Test{
    public static void main(String[] args) throws AWTException {
        Object[] tmp =  SerialPort.getCommPorts();
        String[] ports = new String[ tmp.length];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = tmp[i].toString();
        }

        for (int i = 0, portsLength = ports.length; i < portsLength; i++) {
            String port = ports[ i ];
            System.out.println(port);
        }
    }


}
