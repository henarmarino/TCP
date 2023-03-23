import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class tcp2ser {

    public tcp2ser (){
    }

    public static void main (String [] args){ //java tcp2ser port_number
        Socket socketClient = null;
        ServerSocket socketServer = null;
        
        if(args.length!=1){
            System.out.println("You have to enter the parameters with this format: tcp2ser port_number");
        } else {
                String port = args[0]; //args[0] is the port_number
                int portServer = Integer.parseInt(port);
                
                try{
                    //to connect to the server
                      
                        socketServer = new ServerSocket(portServer);
                        } catch (SocketException exception1) {
                            System.out.println("[ERROR] Socket: " + exception1.getMessage());
                        } catch (IOException exception2) {
                            System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                        }  
                        while(true){
                            try {
                                
                                //to listen
                                socketClient = socketServer.accept();
                                ClientConnection newClient = new ClientConnection(socketClient);
                                Thread t1;
                                t1= new Thread(newClient);
                                t1.start();
                                System.out.println("There is a new client connected");
                
                
                            } catch (SocketException exception1) {
                                System.out.println("[ERROR] Socket: " + exception1.getMessage());
                            } catch (IOException exception2) {
                                System.out.println("[ERROR] IO: " + exception2.getMessage()); 
                            }   //end second try  
                        }//end while
        } //end else
    } //end main

    static class ClientConnection implements Runnable { 
        int accumulator=0;
        Socket socketClientConnection;

        ClientConnection(Socket socketRreceived) { 
                socketClientConnection = socketRreceived; 

            }
        

        public void run(){
            try{
                //we receive the information from the client
                while(socketClientConnection.isConnected()){
    
                    DataInputStream infoClientToReceive = new DataInputStream(socketClientConnection.getInputStream());
    
                    String ask = new String(infoClientToReceive.readUTF());
    
                    String askBySpace[] = ask.trim().split(" ");
                    for (int i=0; i<askBySpace.length; i++) {
                        int number = Integer.parseInt(askBySpace[i]);
                        accumulator += number;
                        System.out.println("The accumulator is: " + accumulator);
                    }

                    //send the accumulator to the client
                    DataOutputStream infoServerToSend = new DataOutputStream(socketClientConnection.getOutputStream()); 
    
                    infoServerToSend.writeInt(accumulator);
    
    
                } // end second while
            } catch(Exception e){
                System.out.println("The client has been disconnected");
            }
        //we close the socket
            try{
                socketClientConnection.close();
            } catch(IOException exception2) {
                System.out.println("[ERROR] IO: " + exception2.getMessage()); 
            }  
        
    
    
        }//end run
    }//end new class
}//end class