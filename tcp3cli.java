import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;


public class tcp3cli {

	public static void main (String [] args){  //java tcp3cli ip_address port_number
     
		if (args.length!=2 && args.length!=3 ) {
			System.out.println("You have to enter the parameters with this format: tcp3cli ip_address port_number [-u]");
		} else{

			try {
				//to read
				Scanner keyboard = new Scanner (System.in);

				InetAddress ipAddress = InetAddress.getByName(args[0]); //args[0] is the ip_address
				String port = args[1]; //args[1] is the port_number
				int portServer = Integer.parseInt(port);
				
                if(args.length==3){ //UDP
                   
                    ByteBuffer bufferToSendUdp = ByteBuffer.allocate(4);
                    ByteBuffer bufferToRecUdp = ByteBuffer.allocate(4);

                    while(true){

                        DatagramChannel channelUdp = DatagramChannel.open();

                        SocketAddress serverAddress = new InetSocketAddress(ipAddress, portServer);

                        System.out.println("Enter a number. The 0 means exit:");
                        int clientNumberUdp= keyboard.nextInt(); 

                        if(clientNumberUdp==0){

                            //we close the socket
                            channelUdp.close();
                            System.exit(-1);
                        }
                        //to send the udp number
                        bufferToSendUdp.clear();
                        bufferToSendUdp.putInt(clientNumberUdp);
                        bufferToSendUdp.flip();
                        channelUdp.send(bufferToSendUdp, serverAddress);
                        
                        //to receive the udp number
                        bufferToRecUdp.clear();
                        channelUdp.receive(bufferToRecUdp);
                        bufferToRecUdp.flip();
                        int acc = bufferToRecUdp.getInt();
                        System.out.println("The accumulator UDP received by the server is: "+acc);
                        //channelUdp.close();
                    }
                } //end if UDP
                    
                //TCP
                //to connect to the server
                SocketChannel socketClient=SocketChannel.open();

                SocketAddress socketServer = new InetSocketAddress(ipAddress,portServer);
                socketClient.connect(socketServer); //we make the connection.

                ByteBuffer bufferSend = ByteBuffer.allocate(4);
                ByteBuffer bufferReceived = ByteBuffer.allocate(4);
				while(true){ // to make the accumulator 0 in each client

					System.out.println("Enter a number. The 0 means exit:");
					int clientNumber= keyboard.nextInt(); 
					

					if(clientNumber==0){
						//we close the socket
                        socketClient.close();
						break;
					}

                    //to send the number tcp
					bufferSend.clear();
                    bufferSend.putInt(clientNumber);
                    bufferSend.flip();
                    socketClient.write(bufferSend);
                
					//to receive the number tcp	
					bufferReceived.clear();
					socketClient.read(bufferReceived);
					bufferReceived.flip();
							
					int answer = bufferReceived.getInt();
					System.out.println("The accumulator TCP received by the server is: " + answer);


				
				}//end while
			
			} //end try
			catch(SocketTimeoutException exception1) { //Error when the server has taken more than 15 seconds to answer
			    System.out.println("[ERROR] Timeout: The server has taken more than 15 seconds to connect");
				System.exit(-1);
			} catch (SocketException exception2) {
				System.out.println("[ERROR] Socket: " + exception2.getMessage());
				System.exit(-1);
			} catch (IOException exception3) {
				System.out.println("[ERROR] IO: " + exception3.getMessage()); 
				System.exit(-1);
			} catch(Exception e){
                
            }
			
		} //end else
    }//end main

}//end class