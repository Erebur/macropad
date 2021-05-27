import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws InterruptedException, AWTException {
        System.out.println(Arrays.toString(SerialPort.getCommPorts()));
    }
}
