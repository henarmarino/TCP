import java.io.*;
import java.util.*;
import java.net.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class tcp1ser {

    public static void main (String [] args){ //java tcp1ser port_number

        if(args.length!=1){
            System.out.println("You have to enter the parameters with this format: tcp1ser port_number");
        } else {
            try{

                
                String port = args[0]; //args[0] is the port_number
                int portServer = Integer.parseInt(port);

                //to connect to the server
                ServerSocket socketServer = new ServerSocket(portServer);

                while(true){
                try {
                    
                    //to listen
                    Socket socketClient = socketServer.accept();
                    int accumulator=0;
                    System.out.println("There is a new client connected");
                    
                    try{
                    //we receive the information from the client
                    while(socketClient.isConnected()){

                        DataInputStream infoClientToReceive = new DataInputStream(socketClient.getInputStream());

                        String ask = new String(infoClientToReceive.readUTF());

                        String askBySpace[] = ask.trim().split(" ");
                        for (int i=0; i<askBySpace.length; i++) {
                            int number = Integer.parseInt(askBySpace[i]);
                            accumulator += number;
                            System.out.println("The accumulator is: " + accumulator);
                        }

                        //send the accumulator to the client
                        DataOutputStream infoServerToSend = new DataOutputStream(socketClient.getOutputStream()); 

                        infoServerToSend.writeInt(accumulator);


                    } // end second while
            } catch(Exception e){
                System.out.println("The client has been disconnected");
            }
            //we close the socket
            socketClient.close();
            //socketServer.close();


                } catch (SocketException exception1) {
                    System.out.println("[ERROR] Socket: " + exception1.getMessage());
                } catch (IOException exception2) {
                    System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                }   //end second try  
                }//end while
                } catch (SocketException exception1) {
                    System.out.println("[ERROR] Socket: " + exception1.getMessage());
                } catch (IOException exception2) {
                    System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                }   
        } //end else
    } //end main
} //end class