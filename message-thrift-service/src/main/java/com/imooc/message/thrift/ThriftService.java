package com.imooc.message.thrift;

import com.imooc.thrift.message.MessageService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class ThriftService {
    @Autowired
    private MessageService.Iface messageService;

    @Value("${server.port}")
    private int port;

    @PostConstruct
    public void startThriftService() {
        TProcessor processor = new MessageService.Processor<>(messageService);
        TNonblockingServerSocket socket = null;
        try {
            socket = new TNonblockingServerSocket(port);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        TNonblockingServer.Args args = new TNonblockingServer.Args(socket);
        args.processor(processor);
        args.transportFactory(new TFastFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());
        TServer server = new TNonblockingServer(args);
        server.serve();
    }

}
