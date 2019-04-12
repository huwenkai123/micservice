package com.school.user.thrift;

import com.school.thrift.message.MessageService;
import com.school.thrift.user.UserService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceProvider {
    @Value("${thrift.user.ip}")
    private String serverIp;
    @Value("${thrift.user.port}")
    private int serverPort;


    @Value("${thrift.message.ip}")
    private String messageServerIp;
    @Value("${thrift.message.port}")
    private int messageServerPort;

    @Value("${thrift.message.java.ip}")
    private String messageJavaServerIp;
    @Value("${thrift.message.java.port}")
    private int messageJavaServerPort;


    private enum ServiceType{
        USER,MESSAGE,JAVAMESSAGE
    }

    public UserService.Client getUserService() {
        return getService(serverIp, serverPort, ServiceType.USER);
    }

    public MessageService.Client getMessageService() {
        return getService(messageServerIp, messageServerPort, ServiceType.MESSAGE);
    }

    public MessageService.Client getJavaMessage() {
        return getService(messageJavaServerIp, messageJavaServerPort, ServiceType.JAVAMESSAGE);
    }

    public <T> T getService(String ip, Integer port, ServiceType serviceType) {
        TSocket socket = new TSocket(ip, port, 3000);
        TTransport transport = new TFramedTransport(socket);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        TServiceClient result = null;
        switch (serviceType) {
            case USER:
                result = new UserService.Client(protocol);
                break;
            case MESSAGE:
                result = new MessageService.Client(protocol);
                break;
            case JAVAMESSAGE:
                result = new MessageService.Client(protocol);
                break;
        }
        UserService.Client client = new UserService.Client(protocol);
        return (T)result;
    }
}
