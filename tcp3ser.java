import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class tcp3ser {

    public tcp3ser (){
    }

    public static void main (String [] args)  throws IOException { //java tcp3ser port_number

        int accumulatorUdp = 0;
        DatagramChannel channelUdp =null;
        SelectionKey keyUDP =null;

        if(args.length!=1){
            System.out.println("You have to enter the parameters with this format: tcp3ser port_number");
        } else {
                String port = args[0]; //args[0] is the port_number
                int portServer = Integer.parseInt(port);
                Selector selector= Selector.open();
                Map<SocketChannel, Integer> map = new HashMap<>(); //es un mapa en el que guardamos cada cliente con su acumulador

                try{
                        
                        //UDP
                        channelUdp = DatagramChannel.open();
                        SocketAddress serverAddressUdp = new InetSocketAddress(portServer);
                        channelUdp.socket().bind(serverAddressUdp);
                        channelUdp.configureBlocking(false);
                        keyUDP = channelUdp.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE);

                        //TCP
                        ServerSocketChannel socketServerTcpChannel = ServerSocketChannel.open();
                        ServerSocket serverChann=socketServerTcpChannel.socket();
                        SocketAddress serverAddress = new InetSocketAddress(portServer);
                        serverChann.bind(serverAddress);
                        socketServerTcpChannel.configureBlocking(false);
                        socketServerTcpChannel.register(selector, SelectionKey.OP_ACCEPT);
                        
                } catch (SocketException exception1) {
                            System.out.println("[ERROR] Socket: " + exception1.getMessage());
                } catch (IOException exception2) {
                            System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                }  
                
                while(true){
                    try {
                            int channels = selector.select();
                            if (channels == 0) {
                                continue;
                            }
                                
                            Set<SelectionKey> selectedKeys = selector.selectedKeys();
                            Iterator<SelectionKey> iterator = selectedKeys.iterator();
                                
                            while(iterator.hasNext()){
                                SelectionKey key = iterator.next();
                                
                                if (!key.isValid()) {
                                    continue;
                                }
                                
                                if(key.isAcceptable()){ //aqui creo el canal y el socket de cada cliente
                    
                                    ServerSocketChannel server=(ServerSocketChannel) key.channel();
                                    SocketChannel socketChann=server.accept(); //socket del cliente que se conecta
                                    socketChann.configureBlocking(false);
                                    //System.out.println("There is a new TCP client");
                    
                                    socketChann.register(selector, SelectionKey.OP_READ); //lo registras en el selector con la opción de leer
                                    map.put(socketChann, 0); //lo metes en el mapa con el acumulador a 0
                                        
                                } else if(key.isReadable()){
                                        
                                    ByteBuffer bufferTCP = ByteBuffer.allocate(4);
                                    ByteBuffer bufferToSendTCP = ByteBuffer.allocate(4);
                                    ByteBuffer bufferUDP = ByteBuffer.allocate(4);
					                ByteBuffer buffersendUDP = ByteBuffer.allocate(4);
                                    SocketChannel channelConnection=null;
                                    
                                    
                                    try{
                                        accumulatorUdp=udpFunction(channelUdp,accumulatorUdp,bufferUDP,buffersendUDP,keyUDP);
                                        }catch(Exception exception4){
                                        tcpFunction(channelConnection,bufferTCP,bufferToSendTCP,map,key);
        
                                        }  
                                    } //end if is readable
                                  
                                    iterator.remove();
                            }
                                
                        } catch (SocketException exception1) {
                            System.out.println("[ERROR] Socket: " + exception1.getMessage());
                        } catch (IOException exception2) {
                            System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                        } catch(Exception exception3){
                            System.out.println("[ERROR] Exception: " + exception3.getMessage()); 
                        }
                    }//end while
        } //end else
    } //end main
    public static int udpFunction(DatagramChannel channelUdp,int accumulatorUdp,ByteBuffer bufferUDP,ByteBuffer buffersendUDP,SelectionKey keyUDP)  throws IOException{
        DatagramChannel channelForUdp = (DatagramChannel) keyUDP.channel();
        SocketAddress clientForUdp = channelForUdp.receive(bufferUDP);
                                        
        bufferUDP.flip();
        int numberUdp = bufferUDP.getInt();
        accumulatorUdp = accumulatorUdp+numberUdp;
        System.out.println("Value of the accumulator UDP: "+accumulatorUdp);

                                                
        buffersendUDP.putInt(accumulatorUdp);
        buffersendUDP.flip();
        channelUdp.send(buffersendUDP, clientForUdp);
        return accumulatorUdp;
                                        
    }
    public static void tcpFunction(SocketChannel channelConnection, ByteBuffer bufferTCP,ByteBuffer bufferToSendTCP,Map<SocketChannel, Integer> map,SelectionKey key)  throws IOException{
        try{

            channelConnection = (SocketChannel) key.channel();
            int numRead = channelConnection.read(bufferTCP);
            
            bufferTCP.flip(); 
            int numberReceived = bufferTCP.getInt();

            int accumulatorTcp=map.get(channelConnection) + numberReceived;
            map.put(channelConnection,accumulatorTcp); //actualizar en el mapa el valor del acumulador
            System.out.println("Value of the accumulator TCP: "+ accumulatorTcp);
            
            //to send the accumulator
            bufferToSendTCP.clear();
            bufferToSendTCP.putInt(accumulatorTcp);
            bufferToSendTCP.flip();
            channelConnection.write(bufferToSendTCP);
        }catch(Exception exception3){
                 
            map.remove(channelConnection);
            channelConnection.close();
        }
    }
}//end class