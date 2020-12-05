import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import pojo.ChampionshipPojo;
import service.DbService;
import service.DesService;
import service.RsaService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class App {

    private final static String QUEUE_NAME = "championships";

    public static void main(String[] args) throws IOException {
        DbService dbService = new DbService();
        dbService.initDb();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);
                Socket socket = serverSocket.accept();
                new Thread(new HandleAClient(socket, dbService)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                factory.setUsername("");
                factory.setPassword("");
                Channel channelRecv = factory.newConnection().createChannel();

                factory.setHost("");
                Channel channelSend = factory.newConnection().createChannel();

                channelSend.queueDeclare(QUEUE_NAME, false, false, false, null);
                channelRecv.queueDeclare(QUEUE_NAME, false, false, false, null);


                DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                    try (ByteArrayInputStream b = new ByteArrayInputStream(delivery.getBody())) {
                        try (ObjectInputStream o = new ObjectInputStream(b)) {
                            dbService.sendDataToNormalizeDb((List<ChampionshipPojo>) o.readObject());
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                };

                channelRecv.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });
            } catch (TimeoutException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    static class HandleAClient implements Runnable {
        private Socket socket;
        private DbService dbService;

        HandleAClient(Socket socket, DbService dbService) throws IOException {
            this.socket = socket;
            this.dbService = dbService;
        }

        public void run() {
            try {
                System.out.println("Клиент подключился");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                RsaService rsaService = new RsaService();

                oos.writeObject(rsaService.getPublicKey());
                String key = rsaService.decrypt((String) ois.readObject());
                DesService desService = new DesService(key);
                List<ChampionshipPojo> championshipPojos = desService.decrypt((String) ois.readObject());

                dbService.sendDataToNormalizeDb(championshipPojos);

                System.out.println("Нормализованная БД заполнена успешно!");
            } catch (IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}