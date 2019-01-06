package com.shumakov.chat_server.user;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class UserPrinter implements Runnable {

    private BufferedReader socketReader;
    private Socket socket;

    public UserPrinter(BufferedReader socketReader, Socket socket) {
        this.socketReader = socketReader;
        this.socket = socket;
    }

    public void run() {

        try {
            String response;
            while (true) {
                response = socketReader.readLine();
                System.out.println(response);
            }
        } catch (Exception e) {
            try {
                socketReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            System.out.println("Session ended, goodbye!");
        }
    }
}
