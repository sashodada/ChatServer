package com.shumakov.chat_server.user;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class User {

    private ExecutorService pool;

    private Socket socket;
    private PrintWriter socketOutput;
    private BufferedReader socketInput;

    public void run() throws IOException {

        pool = Executors.newFixedThreadPool(2);

        Scanner inputReader = new Scanner(System.in);

        System.out.println("Welcome to THE_CHAT!");
        System.out.println("Please, input port to connect to:");

        socket = new Socket("localhost", Integer.parseInt(inputReader.nextLine()));
        socketOutput = new PrintWriter(socket.getOutputStream(), true);
        socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        pool.execute(new UserPrinter(socketInput, socket));
        pool.execute(new UserWriter(socketOutput, inputReader, socket));

        pool.shutdown();
        try {
            while (pool.awaitTermination(500, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
    }

    public void close() {

        if (socketOutput != null) {
            socketOutput.close();
        }

        try {
            if (socketInput != null) {
                socketInput.close();
            }
        } catch (IOException e) {
            System.out.println("IOException occurred while closing socket input stream!");
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException occurred while closing socket!");
        }
    }

    public static void main(String[] args) {
        User user = new User();

        try {
            user.run();
        } catch (IOException e) {
            user.close();
            System.out.println("Connection terminated!");
        }
    }
}