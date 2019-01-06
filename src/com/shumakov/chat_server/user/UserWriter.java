package com.shumakov.chat_server.user;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class UserWriter implements Runnable {

    private PrintWriter socketWriter;
    private Scanner inputReader;
    private Socket socket;

    public UserWriter (PrintWriter socketWriter, Scanner inputReader, Socket socket) {
        this.socketWriter = socketWriter;
        this.inputReader = inputReader;
        this.socket = socket;
    }

    public void run() {

        while (true) {
            String input = inputReader.nextLine();
            if (input.equals("@quit")) {
                socketWriter.println(input);
                socketWriter.close();
                break;
            }

            socketWriter.println(input);
        }
    }
}
