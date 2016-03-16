import java.util.concurrent.*;
import java.util.Scanner;
import java.util.Random;

public class suavImagen implements Runnable
{
	final int DIM= 2000;
	static int dimension; 
	static int[][] matriz;
	Scanner teclado= new Scanner(System.in);
	static Semaphore s= new Semaphore(1);
	static int fil= 0, col= 0;
	static boolean suavizada= false;
	static Object cerrojo= new Object();
	static int opc;		
	
	public void run()
	{
		while(!suavizada)
		{
			synchronized(cerrojo)
			{
				//System.out.println("Suavizando ["+fil+"]["+col+"]");
							
				if(fil+1<dimension && col+1<dimension)
					matriz[fil][col]+= matriz[fil+1][col+1];
				if(fil<dimension && col+1<dimension && fil-1>=0)
					matriz[fil][col]-= matriz[fil][col+1]*matriz[fil-1][col+1];
				if(fil-1>=0 && col<dimension && fil<dimension)
					matriz[fil][col]-= matriz[fil-1][col];
				if(col-1>=0 && fil<dimension && col<dimension)
					matriz[fil][col]+= matriz[fil][col-1];

				if(col+1>= dimension)
				{
					col= 0;
					fil++;
				} 
				else
					col++;	
						
				if(col+1>= dimension && fil+1>= dimension)
					suavizada= true;
			}
		}
	}

	int getDimension()
	{
		return dimension;
	}
	
	void mostrarMenu()
	{
		
		System.out.println("1- Generar matriz 2000x2000 aleatoria");
		System.out.println("2- Introducir matriz");
		opc= teclado.nextInt();
				
		switch(opc)
		{
			case 1: generar(); break;
			case 2: introducir(); break;
			default: System.out.println("Opcion no valida"); break;
		}
	}
	
	void generar() 
	{	
		dimension= DIM;
		matriz= new int[DIM][DIM];
		for(int i= 0; i< DIM; i++)
			for(int j= 0; j< DIM; j++)
				matriz[i][j]= 1+(int)Math.floor(Math.random()*4);
		//mostrar(matriz, DIM);
	}
	
	static void mostrar(int[][] m, int n)
	{
		for(int i= 0; i< n; i++)
		{
			for(int j= 0; j< n; j++)
				System.out.print(m[i][j]+"      ");
			System.out.println();
		}
	}
	
	void introducir() 
	{
		System.out.println("Dimension?");
		dimension= teclado.nextInt();
		
		matriz= new int[dimension][dimension];
		for(int i= 0; i< dimension; i++)
			for(int j= 0; j< dimension; j++)
			{
				System.out.print("["+i+"]["+j+"]= ");
				matriz[i][j]= teclado.nextInt();
			}
		
		System.out.println("Matriz introducida:");
		mostrar(matriz, dimension);
	}
	
	int getOpc()
	{
		return opc;
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		int nt, cb, fil, col;
		suavImagen sIma= new suavImagen();
		long iniCrono, finCrono, t1, t2;
		
		sIma.mostrarMenu();
		
		fil= 0;
		col= 0;
		ExecutorService exec1= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		ExecutorService exec2= Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors());
		
		iniCrono= System.currentTimeMillis();
		
		suavImagen[] tareas1= new suavImagen[sIma.getDimension()];		
		for(int i= 0; i< sIma.getDimension(); i++)
		{
			tareas1[i]= new suavImagen();
			exec1.execute(tareas1[i]);			
		}
		
		exec1.shutdown();		
		do{}
		while(!exec1.isTerminated());

		finCrono= System.currentTimeMillis();
		
		t1= finCrono-iniCrono;
		
		
		iniCrono= System.currentTimeMillis();
		
		suavImagen[] tareas2= new suavImagen[sIma.getDimension()];		
		for(int i= 0; i< sIma.getDimension(); i++)
		{
			tareas2[i]= new suavImagen();
			exec2.execute(tareas2[i]);			
		}
		
		exec2.shutdown();		
		do{}
		while(!exec2.isTerminated());

		finCrono= System.currentTimeMillis();
		
		t2= finCrono-iniCrono;
		
		
		
		if(sIma.getOpc()==2)
		{
			System.out.println("Matriz suavizada:");		
			mostrar(matriz, sIma.getDimension());
		}
		
		System.out.println(">>Tiempo con "+Runtime.getRuntime().availableProcessors()+" hebras: "+t1+ "(ms)   ");
		System.out.println(">>Tiempo con "+2*Runtime.getRuntime().availableProcessors()+" hebras: "+t2+ "(ms)   ");
	}
}