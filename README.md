# Macropad
This is a small Java application that connects to a Arduino

---
## How it works 
- The Arduino sends the number of the Pressed Button
- The Java Programm executes the corresponding command
- Profit 
## KeyPress
- pressed synchronous by Java.awt.Robot and released after all of them are pressed 
- not possible to print the same Key multiple times by one command
## Config
- Saved in ~/.config/macropad/macropad.conf