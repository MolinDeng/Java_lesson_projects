package hw5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class Server {
	//Swing UI 组件
	private JFrame frame;//扩展java.awt.Frame的外部窗体
	private JTextArea contentArea;//用于输入多行文本的文本域
	private JTextField txt_message;//单行文本域message
	private JTextField txt_max;//单行文本域max(人数)
	private JTextField txt_port;//单行文本域port(端口)
	private JButton btn_start;//开始按钮
	private JButton btn_stop;//停止按钮
	private JButton btn_send;//发送按钮
	private JPanel northPanel;//通用容器，它是AWT面板和画布组件的替代组件
	private JPanel southPanel;
	private JScrollPane rightPanel;//滚动窗格
	private JScrollPane leftPanel;
	private JSplitPane centerSplit;//有两个分隔区的容器，这两个分隔区可以水平排列或者垂直排列且分隔区的大小能自动调整
	private JList userList;//显示选项列表的组件
	private DefaultListModel listModel;

	private ServerSocket serverSocket;
	private ServerThread serverThread;//多线程
	private ArrayList<ClientThread> clients;

	private boolean isStart = false;

	public static void main(String[] args) {
		new Server();
	}
	// 绘制窗口
	public void CreateWindows() {
		//窗口界面设计
		frame = new JFrame("Server");//创建顶层容器
		// 更改JFrame的图标：
		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Server.class.getResource("messages.png")));
		contentArea = new JTextArea();
		contentArea.setEditable(false);//设置为不可写
		contentArea.setForeground(Color.red);//
		txt_message = new JTextField();
		txt_max = new JTextField("10");
		txt_port = new JTextField("6666");
		btn_start = new JButton("启动");
		btn_stop = new JButton("停止");
		btn_send = new JButton("发送");
		btn_stop.setEnabled(false);
		listModel = new DefaultListModel();//创建一个listModle实体
		userList = new JList(listModel);//利用listModel建立JList
		/*
		 *在JList中所显示的所有项目，都是“存储”在和它捆绑在一起的一个ListModel中的。
		 *在程序中要对JList中的项目进行 诸如增加、删除等操作时，都不是直接在JList中进行的，
		 *而是在这个ListModel中完成的。
		 */
		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("写消息 ^_^"));//面板标题
		southPanel.add(txt_message, "Center");//在容器中添加文本框
		southPanel.add(btn_send, "East");//添加一个按钮
		
		rightPanel = new JScrollPane(userList);
		rightPanel.setBorder(new TitledBorder("在线用户"));

		leftPanel = new JScrollPane(contentArea);
		leftPanel.setBorder(new TitledBorder("消息显示区"));

		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
				rightPanel);//垂直分割
		centerSplit.setDividerLocation(500);//距离左边框100像素
		
		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 6));//布局为Grid网格布局,一行6个组件
		/*把容器分成若干行和若干列的网格区域：
		 *可以添加m*n个组件,用add(Component c)将组件添加进入,从第一行第一个开始到最后一行最后一个(每个网格强制相等)
		 */
		northPanel.add(new JLabel("人数上限"));
		northPanel.add(txt_max);
		northPanel.add(new JLabel("端口"));
		northPanel.add(txt_port);
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		northPanel.setBorder(new TitledBorder("配置信息"));

		frame.setLayout(new BorderLayout());/*默认布局,容器空间简单的划分为东南西北中5个区域,
		中间的区域最大 ,由BorderLayout 中的静态变量WEST,EAST,SOUTH,NORTH,CENTER表示区域*/
		frame.add(northPanel, "North");
		frame.add(centerSplit, "Center");
		frame.add(southPanel, "South");
		frame.setSize(600, 400);
		//frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());//设置全屏
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2,
				(screen_height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}
	
	// 执行消息发送
	public void send() {
		if (!isStart) {
			JOptionPane.showMessageDialog(frame, "服务器还未启动,不能发送消息！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (clients.size() == 0) {
			JOptionPane.showMessageDialog(frame, "没有用户在线,不能发送消息！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String message = txt_message.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendServerMessage(message);// 群发服务器消息
		contentArea.append("服务器：" + txt_message.getText() + "\r\n");//显示内容
		txt_message.setText(null);//将输入框中内容清除
	}
	
	// 事件响应
	public Server() {
		//绘制窗口
		CreateWindows();

		/*事件处理
		 * 利用匿名内部类实现多个内部类响应不同组件产生的各种事件
		 */
		// 关闭窗口时事件
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isStart) {
					closeServer();// 关闭服务器
				}
				System.exit(0);// 退出程序
			}
		});

		// 文本框按回车键时事件
		txt_message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		// 单击发送按钮时事件
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {//agr0
				send();
			}
		});

		// 单击启动服务器按钮时事件
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isStart) {
					JOptionPane.showMessageDialog(frame, "服务器已处于启动状态，不要重复启动！",
							"错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int max;
				int port;
				try {
					try {
						max = Integer.parseInt(txt_max.getText());//文本转化为十进制
					} catch (Exception e1) {
						throw new Exception("人数上限为正整数！");
					}
					if (max <= 0) {
						throw new Exception("人数上限为正整数！");
					}
					try {
						port = Integer.parseInt(txt_port.getText());
					} catch (Exception e1) {
						throw new Exception("端口号为正整数！");
					}
					if (port <= 0) {
						throw new Exception("端口号 为正整数！");
					}
					serverStart(max, port);
					contentArea.append("服务器已成功启动!人数上限：" + max + ",端口：" + port
							+ "\r\n");
					JOptionPane.showMessageDialog(frame, "服务器成功启动!");
					btn_start.setEnabled(false);
					txt_max.setEnabled(false);
					txt_port.setEnabled(false);
					btn_stop.setEnabled(true);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(),
							"错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 单击停止服务器按钮时事件
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isStart) {
					JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					closeServer();
					btn_start.setEnabled(true);
					txt_max.setEnabled(true);
					txt_port.setEnabled(true);
					btn_stop.setEnabled(false);
					contentArea.append("服务器成功停止!\r\n");
					JOptionPane.showMessageDialog(frame, "服务器成功停止！");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	// 启动服务器
	public void serverStart(int max, int port) throws java.net.BindException {
		try {
			clients = new ArrayList<ClientThread>();
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket, max);
			serverThread.start();
			isStart = true;
		} catch (BindException e) {
			isStart = false;
			throw new BindException("端口号已被占用，请换一个！");
		} catch (Exception e1) {
			e1.printStackTrace();
			isStart = false;
			throw new BindException("启动服务器异常！");
		}
	}

	// 关闭服务器
	@SuppressWarnings("deprecation")
	public void closeServer() {
		try {
			if (serverThread != null)
				serverThread.stop();// 停止服务器线程

			for (int i = clients.size() - 1; i >= 0; i--) {
				// 给所有在线用户发送关闭命令
				clients.get(i).getWriter().println("CLOSE");
				clients.get(i).getWriter().flush();
				// 释放资源
				clients.get(i).stop();// 停止此条为客户端服务的线程
				clients.get(i).reader.close();
				clients.get(i).writer.close();
				clients.get(i).socket.close();
				clients.remove(i);
			}
			if (serverSocket != null) {
				serverSocket.close();// 关闭服务器端连接
			}
			listModel.removeAllElements();// 清空用户列表
			isStart = false;
		} catch (IOException e) {
			e.printStackTrace();
			isStart = true;
		}
	}

	// 群发服务器消息，即为服务器端输入的消息
	public void sendServerMessage(String message) {
		for (int i = clients.size() - 1; i >= 0; i--) {
			clients.get(i).getWriter().println("服务器：" + message + "(多人发送)");
			clients.get(i).getWriter().flush();
		}
	}

	// 服务器线程
	class ServerThread extends Thread {
		private ServerSocket serverSocket;
		private int max;// 人数上限

		// 服务器线程的构造方法
		public ServerThread(ServerSocket serverSocket, int max) {
			this.serverSocket = serverSocket;
			this.max = max;
		}

		public void run() {
			while (true) {// 不停的等待客户端的链接
				try {
					Socket socket = serverSocket.accept();
					if (clients.size() == max) {// 如果已达人数上限
						BufferedReader r = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						PrintWriter w = new PrintWriter(socket
								.getOutputStream());
						// 接收客户端的基本用户信息
						String inf = r.readLine();
						StringTokenizer st = new StringTokenizer(inf, "@");
						User user = new User(st.nextToken(), st.nextToken());
						// 反馈连接成功信息
						w.println("MAX@服务器：对不起，" + user.getName() +"@"
								+ user.getIp() + "，服务器在线人数已达上限，请稍后尝试连接！");
						w.flush();
						// 释放资源
						r.close();
						w.close();
						socket.close();
						continue;
					}
					ClientThread client = new ClientThread(socket);
					client.start();// 开启对此客户端服务的线程
					clients.add(client);//添加进入list中
					listModel.addElement(client.getUser().getName());// 更新在线列表
					contentArea.append(client.getUser().getName()
							+ client.getUser().getIp() + "上线!\r\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 为一个客户端服务的线程
	class ClientThread extends Thread {
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter writer;
		private User user;

		public BufferedReader getReader() {
			return reader;
		}

		public PrintWriter getWriter() {
			return writer;
		}

		public User getUser() {
			return user;
		}

		// 客户端线程的构造方法
		public ClientThread(Socket socket) {
			try {
				this.socket = socket;
				reader = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				// 接收客户端的基本用户信息
				String inf = reader.readLine();
				StringTokenizer st = new StringTokenizer(inf, "@");
				user = new User(st.nextToken(), st.nextToken());
				// 反馈连接成功信息
				writer.println(user.getName() + user.getIp() + "与服务器连接成功!");
				writer.flush();
				// 反馈当前在线用户信息
				if (clients.size() > 0) {
					String temp = "";
					for (int i = clients.size() - 1; i >= 0; i--) {
						temp += (clients.get(i).getUser().getName() + "/" + clients
								.get(i).getUser().getIp())
								+ "@";
					}
					writer.println("USERLIST@" + clients.size() + "@" + temp);
					writer.flush();
				}
				// 向所有在线用户发送该用户上线命令
				for (int i = clients.size() - 1; i >= 0; i--) {
					clients.get(i).getWriter().println(
							"ADD@" + user.getName() + user.getIp());
					clients.get(i).getWriter().flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("deprecation")
		public void run() {// 不断接收客户端的消息，进行处理。
			String message = null;
			while (true) {
				try {
					message = reader.readLine();// 接收客户端消息
					if (message.equals("CLOSE"))// 下线命令
					{
						contentArea.append(this.getUser().getName()
								+ this.getUser().getIp() + "下线!\r\n");
						// 断开连接释放资源
						reader.close();
						writer.close();
						socket.close();

						// 向所有在线用户发送该用户的下线命令
						for (int i = clients.size() - 1; i >= 0; i--) {
							clients.get(i).getWriter().println(
									"DELETE@" + user.getName());
							clients.get(i).getWriter().flush();
						}

						listModel.removeElement(user.getName());// 更新在线列表

						// 删除此条客户端服务线程
						for (int i = clients.size() - 1; i >= 0; i--) {
							if (clients.get(i).getUser() == user) {
								ClientThread temp = clients.get(i);
								clients.remove(i);// 删除此用户的服务线程
								temp.stop();// 停止这条服务线程
								return;
							}
						}
					} else {
						dispatcherMessage(message);// 转发消息
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// 群发消息
		public void dispatcherMessage(String message) {
			StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
			String source = stringTokenizer.nextToken();
			String owner = stringTokenizer.nextToken();
			String content = stringTokenizer.nextToken();
			message = source + "说：" + content;
			contentArea.append(message + "\r\n");
			if (owner.equals("ALL")) {// 群发
				for (int i = clients.size() - 1; i >= 0; i--) {
					clients.get(i).getWriter().println(message + "(多人发送)");
					clients.get(i).getWriter().flush();
				}
			}
		}
	}
}
