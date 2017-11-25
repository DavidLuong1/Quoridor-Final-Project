import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;


public class Server extends JFrame{

	private JTextArea jtaP1;
	private JTextArea jtaP2;
	private JTextArea jtaP3;
	private JTextArea jtaP4;
	

	//The JTextArea that holds the output of the chat program
	private JTextArea jtaChatLog;
	

   //Create vector of InnerClass for the players (clients)
   private Vector<PlayerThread> players = new Vector<PlayerThread>();
   
   public static void main(String [] args){
      new Server();
   } //End of main
   
   public Server(){
 	
		JPanel jpPlayers = new JPanel(new GridLayout(0,1));
		
		jtaP1 = new JTextArea(5, 20);
		jtaP1.setBackground(Color.BLACK);
		jtaP1.setForeground(Color.GREEN);
		
		jtaP2 = new JTextArea(5, 20);
		jtaP2.setBackground(Color.BLACK); 
		jtaP2.setForeground(Color.GREEN);
		
		jtaP3 = new JTextArea(5, 20);
		jtaP3.setBackground(Color.BLACK);
		jtaP3.setForeground(Color.GREEN);
		
		jtaP4 = new JTextArea(5, 20);
		jtaP4.setBackground(Color.BLACK);
		
		jtaP4.setForeground(Color.GREEN);
		
		//jtaP3.setText("TESTING");
		
		
		
		jpPlayers.add(new JLabel("Player 1"));
		jpPlayers.add(jtaP1);
		
		jpPlayers.add(new JLabel("Player 2"));
		jpPlayers.add(jtaP2);
		
		jpPlayers.add(new JLabel("Player 3"));
		jpPlayers.add(jtaP3);
		
		jpPlayers.add(new JLabel("Player 4"));
		jpPlayers.add(jtaP4);
		
		jtaChatLog = new JTextArea(5,50);
		
		JScrollPane scroll = new JScrollPane(jtaChatLog);
		
		add(scroll, BorderLayout.WEST);
		add(jpPlayers, BorderLayout.CENTER);
		
		jtaChatLog.setEditable(false);
		jtaP1.setEditable(false);
		jtaP2.setEditable(false); 
		jtaP3.setEditable(false);
		jtaP4.setEditable(false);
		
		//jtaChatLog.setText("TEsting");
		
		jtaChatLog.setForeground(Color.GREEN);
		
		jtaChatLog.setBackground(Color.BLACK);
		
		jpPlayers.setBackground(Color.GRAY);
		
		
		setTitle("Server-side Gui");
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	  

      try{
      
         //set up serversocket, specifying the same port# as the client 
         ServerSocket ss = new ServerSocket(16789);
         
         // get a client connection, and a socket
         Socket cs = null; //cs means client socket
      
         // prints out the localhost
         System.out.println(InetAddress.getLocalHost());

   
         while(true){
            
            // Wait for a client to connect 
            System.out.println("Waiting for a client to connect.... ");
            cs = ss.accept(); // accept a client 
            System.out.println("Have a client connection: " + cs);	
				jtaP1.setText(""+cs);
            //after accepting the client, start the thread
            PlayerThread pt = new PlayerThread(cs);
            pt.start();
            
            //add player to player vector
            players.add(pt);
               
         } // end of while loop
         
      } //end try 

      catch (SocketException se){
         System.out.println("A client has disconnected");
         //se.printStackTrace();
      } //end of catch
         
      catch (IOException ioe){
         //ioe.printStackTrace();
      } //end of catch       
   
   } //End of server constructor 

	//jtaToString creates a string that will create the output for the server player JTAs
	public String jtaToString(String name, String address){
		String result = String.format("%s%n%s%n", name, address);

		return result;

	}

   
   class PlayerThread extends Thread{
   
      private Socket cs = null;

      OutputStream out = null;
      ObjectOutputStream oos = null;
      
      // open input from the client 
      InputStream in = null;
      ObjectInputStream ois = null;
      
      public PlayerThread(Socket cs_){
         cs = cs_;
      }
   
      //start the work of the thread 
      public void run(){
      	String name = "no name";

         try{
 
            // output to the client
            out = cs.getOutputStream(); 
            oos = new ObjectOutputStream(out);
            
            // open input from the client 
            in = cs.getInputStream(); 
            ois = new ObjectInputStream(in); 
           
            PlayersInfo playersInfo = new PlayersInfo();
				//ChatMessage cm = null;

            Object genObject = null; 
            while((genObject = (Object)ois.readObject()) != null){
               if (genObject instanceof PlayerName){
                  PlayerName p = (PlayerName)genObject;
						/*if(p.getPlayerName().equals(null)){
							p.setPlayerName("NO NAME");
						}*/
						name = p.getPlayerName();
						if(players.size() <= 1){
							jtaP1.setText(name + "\n" + cs);
						}
						else if(players.size() == 2){
							jtaP2.setText(jtaToString(name, (cs+"")));

						}
						else if(players.size() == 3){
							jtaP3.setText(jtaToString(name, (cs+"")));

						}
						else if(players.size() == 4){
							jtaP4.setText(jtaToString(name, (cs+"")));

						}
                  playersInfo.setName(p.getPlayerName()); //adds the player to the playerInfo arraylist 
                  for(PlayerThread pt: players){
                     pt.sendInfo(playersInfo);
                  }
               }//End of if
               else if (genObject instanceof ChatMessage){
                  ChatMessage cm = (ChatMessage)genObject;
						cm.setName(name);
						jtaChatLog.append(cm.toString());
                  for(PlayerThread pt: players){
                     pt.sendInfo(cm);
                  }  
               }
               
               
            }//End of while 
             
               
            // close everything
            oos.close();
            ois.close();
            cs.close(); 

         } // end of try 
         
         catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
         }
         
         catch (SocketException se){
            System.out.println("A player has disconnected");
         }
         
         catch(NullPointerException npe){
            //npe.printStackTrace();
         }
         catch(IOException ioe){
            ioe.printStackTrace();
         } 
         
      } //End of run method 
      
      
      public void sendInfo(Object p){
      
         try{
            //send object back to client
            oos.writeObject(p);
            oos.flush();
         }
         
         catch (IOException io){
            io.printStackTrace();
         }

      } //End of sendMessage method 
   
   
   } // End of InnerClass PlayerThread
   

} //End of Server Class
