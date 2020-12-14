import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException{
        System.out.println("Enter IP and port: ");

        Scanner in = new Scanner(System.in);
        String ip = in.next();

        int port = in.nextInt();
        System.out.println("Enter name of the file");
        String fileName = in.next();

        System.out.println(fileName);
        /** Задаю новый сокет по введёному ip и порту**/
        Socket clientSocket = new Socket(InetAddress.getByName(ip), port);
        Client client = new Client(clientSocket);
        /** Использую функции данного класса**/
        client.writeOutputStream(fileName);
        client.readInputStream();
    }

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void writeOutputStream(String fileName) throws IOException {     //getter
        /** Создал запрос**/
        String getter = "GET /" +fileName +" HTTP/1.1\n\n";
        /** Отправил запрос**/
        outputStream.write(getter.getBytes());
        outputStream.flush();

    }

    public void readInputStream() throws IOException {      //console output from server
        Scanner scan = new Scanner(inputStream);
        String str;
        while (scan.hasNextLine()){
            str = scan.nextLine();
            System.out.println(str);
        }

    }
}