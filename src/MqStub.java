import com.ibm.disthub2.client.Message;
import com.ibm.mq.jms.*;

import javax.jms.*;

public class MqStub {

    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqQSession;
            MQQueue mqIn;
            MQQueue mqOut;

            MQQueueReceiver mqReceiver;
			MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");
            mqCF.setPort(1410);
            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createQueueConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqQSession.createQueue("MQ.IN");
            mqOut = (MQQueue) mqQSession.createQueue("MQ.OUT");

            mqReceiver = (MQQueueReceiver) mqQSession.createReceiver(mqIn);
			mqSender = (MQQueueSender) mqQSession.createSender(mqOut);

            MessageListener ListenerIn = msg -> {
                System.out.println("Получили сообщение в MQ.IN");
                if (msg instanceof TextMessage) {
                    try {
                        TextMessage tMsg = (TextMessage) msg;
                        System.out.println("MQ.IN: " + tMsg.getText());
                        mqSender.send(msg);
                        mqQSession.commit();
						System.out.println("Cообщение было отправлено в MQ.OUT");
					} catch (JMSException e) {
                        e.printStackTrace();
                    }
                }

            };

            mqReceiver.setMessageListener(ListenerIn);
			mqConn.start();
            System.out.println("Stub Started");

		} catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}