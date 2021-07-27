package Testing;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Test{
    public static void main(String[] args) throws AWTException {
        Integer  p = null;
        p = Integer.parseInt(JOptionPane.showInputDialog(null, Arrays.toString(SerialPort.getCommPorts())));

    }


}
