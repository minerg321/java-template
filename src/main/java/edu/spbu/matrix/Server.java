package edu.spbu.matrix;
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.*;
import java.net.Socket;
import java.util.regex.Pattern;

public class Server {
    public static void main(String[] args) throws IOException {
        /** Создаю сокет сервера **/
        ServerSocket serverSocket = new ServerSocket(8080);
        /** Пока не выпадет исключение жду подключения клиентов **/
        while (true) {
            System.out.println("Waiting for a client connection...");
            /** При появлении клиента принимаем его и сохраняем **/
            Socket clientSocket = serverSocket.accept();
            /** Создаём новый экземпляр сервера **/
            Server server = new Server(clientSocket);
            System.out.println("Client has connected successfully");
            /** При успешном подключении вызываем все функции нашего класса и заканчиваем работу с данным клиентом**/
            server.readInputStream();
            server.writeOutputStream();
            server.clientSocket.close();
        }
    }
    /** Обьявляем поля нашего класса **/
    public Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String fileName;
    /** Конструктор сервера для конткретного клиента **/
    public Server(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        //this.fileName = "site.html";
    }
    /** Конструктор сервера для конткретного клиента **/
    public void readInputStream() throws IOException {
        /** Создаем экземпляр для чтения буфера **/
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        /** Производим считывание**/
        while (true) {
            String s = in.readLine();
            /** если считывание произошло некорректно или не произашло вовсе, закончим цикл **/
            if(s == null || s.trim().length() == 0)
                break;
            /** Закидыванием в нашу строку то что считали и переводим на новую строку **/
            sb.append(s);
            sb.append('\n');
            /** Ищем файл с расширением .html **/
            if(s.contains(".html")) {
                /** Данная форма записи обусловлена устройством http запросов (GET /index.php HTTP/1.1) **/
                this.fileName = s.substring(s.indexOf("GET") + 5, s.length() - s.indexOf("GET") - 9);
                System.out.println(this.fileName);
            }
        }

    }
    public void writeOutputStream() throws IOException {
        File file = new File(fileName);
        /** Проверим на существование файл **/
        if (file.exists()) {
            /** Считаем все данные по данному нащванию файла **/
            String s = new String(Files.readAllBytes(Paths.get(fileName)));
            String response = "HTTP/1.1 200 OK\n" +
                    "Type: text/html\n" +
                    "Length: " + s.length() + "\n" +
                    "Closing connection\n\n" + s;
            /**Пишу response в буфер**/
            outputStream.write(response.getBytes());
            /**отправка содержимого буфера**/
            outputStream.flush();
        }
        else {
            outputStream.write("<html><h2>404:file not found</h2></html>\n".getBytes());
            outputStream.flush();
            outputStream.write(("File \"" + fileName + "\"  does not exist").getBytes());        //wrong name
            outputStream.flush();
        }

    }
}
