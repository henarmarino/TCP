import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;
import java.net.SocketTimeoutException;


public class tcp1cli {

	public static void main (String [] args){  //java tcp1cli ip_address port_number

		if (args.length!=2) {
			System.out.println("You have to enter the parameters with this format: tcp1cli ip_address port_number");
		} else {
			try {
				//to read
				Scanner keyboard = new Scanner (System.in);

				InetAddress ipAddress = InetAddress.getByName(args[0]); //args[0] is the ip_address
				String port = args[1]; //args[1] is the port_number
				int portServer = Integer.parseInt(port);
				//int ClientSkips= 0;
                
				//to connect to the server
				Socket socketClient = new Socket();
            	SocketAddress socketServer = new InetSocketAddress(ipAddress,portServer);

				socketClient.connect(socketServer); //we make the connection
				

				while(true){ // to make the accumulator 0 in each client

					System.out.println("Enter a row of numbers separated by spaces. The 0 or the enter means the end of the message:");
					String clientNumbers= keyboard.nextLine(); 
					String separatedNumbers[] = clientNumbers.split(" "); //numbers separated by spaces

					if(Integer.parseInt(separatedNumbers[0])==0){
						//System.out.println("The 0 means end of the message, so it cannot be the first number");
						//we close the socket
						break;
					}

					String numbersToSend = "";
					//put the numbers into the string to send them
					for (int i=0; i<separatedNumbers.length; i++) {
						Integer number = Integer.parseInt(separatedNumbers[i]);
						if (number != 0) {
							numbersToSend+= separatedNumbers[i]+" ";
						} else
							break;
					}

					//byte[] bytes = new byte[numbersToSend.length()];
					//ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					//DataOutputStream dataOutputStream = new DataOutputStream(socketClient.getOutputStream()); 
					//dataOutputStream.writeChars(numbersToSend);
					//bytes = numbersToSend.getBytes();
					//dataOutputStream.close(); 

					//we send the information to the server
					DataOutputStream infoClientToSend = new DataOutputStream(socketClient.getOutputStream()); 

					infoClientToSend.writeUTF(numbersToSend);

					//we receive the accumulator from the server
					DataInputStream inforServerToReceive = new DataInputStream(socketClient.getInputStream());

					int answer = inforServerToReceive.readInt();
					System.out.println("The accumulator received by the server is: " + answer);


				} //end while
				//we close the socket
				socketClient.close();
				} catch(SocketTimeoutException exception1) { //Error when the server has taken more than 10 seconds to answer
					System.out.println("[ERROR] Time server: The server has taken more than 10 seconds to answer");
					System.exit(-1);
				} catch (SocketException exception2) {
					System.out.println("[ERROR] Socket: " + exception2.getMessage());
					System.exit(-1);
				} catch (IOException exception3) {
					System.out.println("[ERROR] IO: " + exception3.getMessage()); 
					System.exit(-1);
				}	
			
		}//end else
	}

}//end class