import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GraficoDupla extends JFrame {
	
	
	double[]X;
	double []Y;
	double []ID;
	
	int recuoX=75;
	int recuoY=50;
	int fps=0;
	Image dbi;
	Graphics dbg;
	
	
	double px=0;
	double py=0;
	
	
	double ratioX;
	double ratioY;
	
	double minX;
	double minY;
	double maxX;
	double maxY;
	
	public GraficoDupla(double[]X,double []Y,double[]ID,int fps){
		this.fps=fps;
		this.ID=ID;
		this.X=X;
		this.Y=Y;
		setSize(1000,1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		revalidate();
		
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		

		 minX= min(X);
		 minY= min(Y);
		
		 maxX=max(X);
		 maxY=max(Y);
		 
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
				if(e.getKeyChar()=='k'){
					minX+=400;
					maxX-=400;
				}
				if(e.getKeyChar()=='l'){
					minX-=400;
					maxX+=400;
				}
				
				if(e.getKeyCode()== KeyEvent.VK_S){
					minY-=ratioY*5;
					maxY-=ratioY*5;
				}
				if(e.getKeyCode()== KeyEvent.VK_D){
					minX+=ratioX*5;
					maxX+=ratioX*5;
				}
				if(e.getKeyCode()== KeyEvent.VK_A){
					minX-=ratioX*5;
					maxX-=ratioX*5;
				}
				if(e.getKeyCode()== KeyEvent.VK_W){
					minY+=ratioY*5;
					maxY+=ratioY*5;
				}
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				
				 int notches = arg0.getWheelRotation();
				 //parte do zoom no X
				 double zeta=(minX+(getMousePosition().x-recuoX)*ratioX);
				 double Zx=(zeta-minX)/(maxX-minX);
				 // parte de zoom no Y
				 double beta =(minY+((getHeight()-getMousePosition().y)-recuoY)*ratioY);
				 double Zy=(beta-minY)/(maxY-minY);
				
				 double R= (maxY-minY)/(maxX-minX);
				 
			       if (notches < 0) {
			    	   maxX-=(maxX-minX)*0.09*(1-((zeta-minX)/(maxX-minX)));
			    	   minX=(zeta-(Zx*maxX))/(1-Zx);
			    	   double deltaX= (maxX-minX);
				    	  maxY=((R*deltaX)+((beta)/(1-Zy)))/(1+((Zy)/(1-Zy)));
				    	  minY=(beta-(Zy*maxY))/(1-Zy);	
			    	   
			       } else {
			    	   maxX+=(maxX-minX)*0.09*(1-((zeta-minX)/(maxX-minX)));
			    	   minX=(zeta-(Zx*maxX))/(1-Zx);
			    	   double deltaX= (maxX-minX);
				    	  maxY=((R*deltaX)+((beta)/(1-Zy)))/(1+((Zy)/(1-Zy)));
				    	  minY=(beta-(Zy*maxY))/(1-Zy);	
			       }
				
			}
		});
		
	}
	
	//bloco para controle de atualização de tela
	{
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					repaint();
					try {
						Thread.sleep((1000/fps));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}).start();
		
	}
	
	
	
	public void salvarImagem(String nome, File file) {
		BufferedImage image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		try{
			ImageIO.write(image, "png",new File(file.getAbsolutePath()+"//"+nome+".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	double min(double []d){
		double auxMin=Double.MAX_VALUE;
		for(double d1: d)if(d1<auxMin)auxMin=d1;
		return auxMin;
	}
	double max(double []d){
		double auxMax=Double.MIN_VALUE;
		for(double d1: d)if(d1>auxMax)auxMax=d1;
		return auxMax;
	}
	
	void barrinhaX(Graphics g,int x,int y){
		g.drawLine(x, y-3, x, y+3);
	}
	void barrinhaY(Graphics g,int x,int y){
		g.drawLine(x-3, y, x+3, y);
	}
	
	void EscalaX(Graphics g,double ratio, int n,double min){
		
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		
		g.drawLine(0, getHeight()-recuoY, getWidth(),getHeight()-recuoY);
		double auxratio=getWidth()/n;
		
		for(int i=0;i<n;i++){
			barrinhaX(g,(int)(recuoX+i*auxratio),getHeight()-recuoY);
			
			DecimalFormat df = new DecimalFormat("0.0##");
			String result = df.format(((min+((i*auxratio)*ratio))/3600));
			
			g.drawString(result+"h",(int)(recuoX+i*auxratio)-10,(int) getHeight()-recuoY+20);
		}
	}
	
void EscalaY(Graphics g,double ratio, int n,double min){
		
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
		
		g.drawLine(recuoX, 0, recuoX,getHeight());
		
		double auxratio=getHeight()/n;
		
		for(int i=0;i<n;i++){
			barrinhaY(g,recuoX,getHeight()-(int)(recuoY+i*auxratio));
			DecimalFormat df = new DecimalFormat("0.0##");
			String result = df.format(min+((i*auxratio)*ratio));
			g.drawString(result,recuoX-30,getHeight()-(int)(recuoY+i*auxratio));
			//g.fillRect(x, y, width, height);
		}
	}
	
	
	public static void main(String []args){
		
		File folder = new File("C:\\Users\\gusta\\Downloads\\outRainerLimite3500\\outRainer");
		File[] listOfFiles = folder.listFiles();

		for (int i =10; i ==10; i++) {
		  if (listOfFiles[i].isFile()) {
			  try{
					File f= listOfFiles[i];
					Scanner sc= new Scanner(f);
					String saida="";
					int cont=0;
					while(sc.hasNextLine()){
						saida+=sc.nextLine()+"\n";
						cont++;
					}
					double[]x=new double[cont];
					double[]y=new double[cont];
					double[]id=new double[cont];
					
					cont=0;
					for(String s:saida.split("\n")){
						x[cont]=Double.parseDouble(s.split(";")[0]);
						y[cont]=Double.parseDouble(s.split(";")[1]);
						id[cont]=Integer.parseInt(s.split(";")[2]);
						//System.out.println("X:"+x[cont]+", Y:"+y[cont]);
						cont++;
						
						
					}
					
					GraficoDupla g= new GraficoDupla(x,y,id,60);
					g.salvarImagem(f.getName(), new File("C:\\Users\\gusta\\Desktop\\temposOnibus"));
					}catch(Exception e){
						
					}
			  
			  
			  
			  
			  
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
		
		
		
		
		
	}
	
	public void paint(Graphics g){
		
		dbi= createImage(getWidth(),getHeight());
		dbg= dbi.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbi, 0, 0, this);
		
		
	}
	
	public void paintComponent(Graphics g){
		
		
		
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		/*
		double minX= min(X)*zoom+movX;
		double minY= min(Y)*zoom+movY;
		
		double maxX=max(X)*zoom+movX;
		double maxY=max(Y)*zoom+movY;
		*/
		
		
		
		//System.out.println("Xmouse"+(minX+(getMousePosition().x-recuoX)*ratioX)/3600);
		//System.out.println("Ymouse"+(minY+((getHeight()-getMousePosition().y)-recuoY)*ratioY));
	
		
		 ratioX= (maxX-minX)/getWidth();
		 ratioY= (maxY-minY)/getHeight();
		
		//System.out.println("MINX"+minX);
		//System.out.println("MINY"+minY);
		
		
		
		g.setColor(Color.red);
		
		for(int i=0;i<X.length;i++){
			if(ID!=null)g.setColor(corHash(ID[i]));
			
			g.fillOval(recuoX+(int)((X[i]-minX)/ratioX)-7,getHeight()-(int)((Y[i]-minY)/ratioY)-7-recuoY, 14, 14);
			
		}
		
		EscalaX(g,ratioX,10,minX);
		EscalaY(g,ratioY,20,minY);
		
		try{
		g.setFont(new Font(Font.SERIF, Font.ITALIC, 10));
		g.setColor(Color.RED);
		DecimalFormat df = new DecimalFormat("0.0##");
		String aux=df.format((minX+(getMousePosition().x-recuoX)*ratioX)/3600);
		g.drawString("("+aux+")", getMousePosition().x-20, getHeight()-10);
		String aux2=df.format((minY+((getHeight()-getMousePosition().y)-recuoY)*ratioY));
		g.drawString("("+aux2+")", 10, getMousePosition().y);
		}catch(Exception e){}
		
		//colocando a mira
		
		
		
	}
	
	Color corHash(double seed){
		
		
		
		int red =(int)((int)(Math.pow(2.1, (int)(seed)%40))%255);

		int green =(int)((int)(Math.pow(2.23,(int)(seed)%40))%255);
		int blue= (int)((int)(Math.pow(2.4,(int)(seed)%40))%255);
		
		//JOptionPane.showMessageDialog(null,"red"+red+" blue"+blue+" green"+green);
		
		return new Color(red,green,blue);
	}
	
	
	
	

}
