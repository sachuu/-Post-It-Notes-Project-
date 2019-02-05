package cp372;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import cp372.Server.Board;
import cp372.Server;

public class Client extends JFrame{
	static String serverAddress = null;
	static String port = null;
	static int portNum = 0;
	static Socket socket;
	
	static BufferedReader in;
	static PrintWriter out;
	
	public static void main(String[] args) throws Exception, ClassNotFoundException {
		Scanner scanner = new Scanner(System.in);
		
		JFrame frame = new JFrame("Client");								//Setup JFrame and buttons for the client 
		
		JButton connect = new JButton("Connect");
		connect.setBounds(100,100,140,40);
		
		JButton disconnect = new JButton("Disconnect");
		disconnect.setBounds(325, 100, 140, 40);
		
		JButton send = new JButton("Send");
		send.setBounds(100,475,140,40);
		
		JButton post = new JButton("Post");
		post.setBounds(325,475,140,40);
		
		JButton get = new JButton("Get");
		get.setBounds(100,625,140,40);
		
		JButton pin = new JButton("Pin");
		pin.setBounds(325,708,140,40);
		
		JButton unpin = new JButton("unpin");
		unpin.setBounds(475,708,140,40);
		
		JLabel label = new JLabel();
		label.setText("Enter IP");
		label.setBounds(10,10,100,100);
		
		JLabel label2 = new JLabel(); 
		label2.setText("Enter Port");
		label2.setBounds(300, 10, 100, 100);
		
		JLabel label3 = new JLabel(); 
		label3.setText("Enter Message");
		label3.setBounds(10, 210, 100, 100);
		
		JLabel label4 = new JLabel(); 
		label4.setText("Get");
		label4.setBounds(10, 515, 100, 100);
		
		JLabel label5 = new JLabel(); 
		label5.setText("Pin/Unpin");
		label5.setBounds(10, 665, 100, 100);
		
		JLabel label6 = new JLabel(); 
		label6.setText("Results");
		label6.setBounds(10, 745, 100, 100);
		
		JTextField text = new JTextField(); //IP Textfield
		text.setBounds(110,50,130,30);
		
		JTextField text2 = new JTextField(); //Port Textfield
		text2.setBounds(400,50,130,30);
		
		JTextField text3 = new JTextField(); //Message Textfield
		text3.setBounds(110,250,500,200);
		
		JTextField text4 = new JTextField(); 
		text4.setBounds(110,550,500,50);
		
		JTextField text5 = new JTextField(); 
		text5.setBounds(110,700,200,50);
		
		JTextField text6 = new JTextField(); 
		text6.setBounds(110,775,500,100);
		
		frame.add(connect);					//Add buttons and labels to the frame
		frame.add(disconnect);
		frame.add(label);
		frame.add(label2);
		frame.add(label3);
		frame.add(label4);
		frame.add(label5);
		frame.add(label6);
		frame.add(text);
		frame.add(text2);
		frame.add(text3);
		frame.add(text4);
		frame.add(text5);
		frame.add(text6);
		frame.add(send);
		frame.add(post);
		frame.add(get);
		frame.add(pin);
		frame.add(unpin);
		
		frame.setSize(650,1000);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connect.addActionListener(new ActionListener() {															//Action listener for the various buttons such as connect
			@Override
			public void actionPerformed(ActionEvent e) {
				serverAddress = text.getText();																		//Get IP Address from textbox
				port = text2.getText();																				//Parse it into an integer and pass it into the port
				portNum = Integer.parseInt(port);
				label.setText("Ip Submitted");										
				//while(runTest) {
					try{
						Socket socket = new Socket(serverAddress, portNum);											//Pass the values taken from the textbox into a new socket
						// Streams for conversing with server
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));		//Output and Input streams for the sockets
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						
						// Consume and display welcome message from the server
						System.out.println(in.readLine());
						
						disconnect.addActionListener(new ActionListener() {											//Action Listener for Disconnect Button
							int dcCheck = 0;																		//Counter prevents user from spamming disconnect button				
							
							@Override
							public void actionPerformed(ActionEvent e) {
								out.print("Disconnected from server");
								try {
									socket.close(); 																//closes socket / disconnects
									frame.setVisible(false);
									frame.dispose();
								} catch (IOException e1) {
									System.out.println(e1);
								} finally {
									if(dcCheck == 0) {																//Checks counter before printing to prevent spam 
									System.out.println("Connection with client closed");
									dcCheck += 1;
								}
							  }
							}
						});
						send.addActionListener(new ActionListener() {
							String userInput = null;
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									userInput = text3.getText();
									out.println(userInput);
									System.out.println(in.readLine());
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} finally {												
								}
							  }
							});
						
						get.addActionListener(new ActionListener() {
							String userInput = null;
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									userInput = text4.getText();
									out.println(userInput);
								} finally {												
								}
							  }
							});
						
						pin.addActionListener(new ActionListener() {
							String userInput = null;
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									userInput = text5.getText();
									out.println(userInput);
								} finally {												
								}
							  }
							});
						
						unpin.addActionListener(new ActionListener() {
							String userInput = null;
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									userInput = text5.getText();
									out.println(userInput);
								} finally {												
								}
							  }
							});
						
					}
					catch(UnknownHostException e1) {
						System.out.println("Invalid IP");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
	}
}
