package edu.spbu.matrix;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException{
        System.out.println("Enter IP and port: ");

        Scanner in = new Scanner(System.in);
        String ip = in.next();

        int port = in.nextInt();
        /** Задаю новый сокет по введёному ip и порту**/
        Client client = new Client(ip,port);
        /** Использую функции данного класса**/
         client.Start();
    }

    private Socket socket;
    public Client(String ip,int port) throws IOException {
        Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
        this.socket = clientSocket;
    }

    public void writeOutputStream(String fileName) throws IOException {     //getter
        /** Создал запрос**/
        String getter = "GET /" +fileName +" HTTP/1.1\n\n";
        /** Отправил запрос**/
        OutputStream outputStream = this.socket.getOutputStream();;
        outputStream.write(getter.getBytes());
        outputStream.flush();

    }
    public void Start() throws IOException {
        System.out.println("Enter name of the file");
        Scanner in = new Scanner(System.in);
        String fileName = in.next();
        this.writeOutputStream(fileName);
        this.readInputStream();
    }

    public void readInputStream() throws IOException {      //console output from server
        InputStream inputStream =this.socket.getInputStream(); ;
        Scanner scan = new Scanner(inputStream);
        String str;
        while (scan.hasNextLine()){
            str = scan.nextLine();
            System.out.println(str);
        }

    }
}