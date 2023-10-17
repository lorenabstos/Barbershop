package App;

import java.util.concurrent.Semaphore;

import types.BarbersName;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;


public class Barbershop {
	public static final double CHAIRS_AMOUNT = 20.0; // Número total de cadeiras na barbearia

    private List<Thread> customerThreadList; // Lista de threads de clientes
    private List<Thread> barberThreadList; // Lista de threads de barbeiros

    public static int tainhaSleepingTime; // Tempo de sono do "Sargento Tainha"

    public static Semaphore customersSemaphore; // Semáforo para clientes
    public static Semaphore barbersSemaphore; // Semáforo para barbeiros
    public static Semaphore mutex; // Semáforo para controle de acesso às filas

    public static int barbAmount; // Quantidade de barbeiros

    private List<Customer> customersList; // Lista de clientes

    public Queue<Customer> officerQueue = new LinkedList<>(); // Fila para clientes da categoria "Oficial"
    public Queue<Customer> sergeantQueue = new LinkedList<>(); // Fila para clientes da categoria "Sargento"
    public Queue<Customer> corporalQueue = new LinkedList<>(); // Fila para clientes da categoria "Cabo"

    private Map<BarbersName, Queue<Customer>> barberQueues = new HashMap<>(); // Mapeamento das filas de barbeiros

    public static boolean barberShopIsClosed = false; // Indica se a barbearia está fechada
    public int nOfficer = 0; // Contador de clientes da categoria "Oficial"
    public int nSergeant = 0; // Contador de clientes da categoria "Sargento"
    public int nCorporal = 0; // Contador de clientes da categoria "Cabo"
    public int nBreak = 0; // Contador de clientes em pausa

    public char barbershopCase; // Caso de teste para a barbearia

    public Barbershop() {
        customerThreadList = new ArrayList<>();
        barberThreadList = new ArrayList<>();
        customersSemaphore = new Semaphore(0, true);
        barbersSemaphore = new Semaphore(0, true);
        mutex = new Semaphore(1, true);

        barberQueues.put(BarbersName.RECRUTA_ZERO, officerQueue);
        barberQueues.put(BarbersName.DENTINHO, sergeantQueue);
        barberQueues.put(BarbersName.OTTO, corporalQueue);
    }

	public void setBarbershopCase(char barbershopCase) {
		this.barbershopCase = barbershopCase;
	}

	public void setCustomersList(List<Customer> customersList) {
		this.customersList = customersList;
	}

	public char getBarbershopCase() {
		return barbershopCase;
	}

	public List<Customer> getCustomersList() {
		return customersList;
	}

	public List<Thread> getBarberThreadList() {
		return barberThreadList;
	}

	public List<Thread> getCustomerThreadList() {
		return customerThreadList;
	}

	public Customer getNextCustomer(BarbersName barberID) {
		Customer customer = null;

		try {
				mutex.acquire();

				if (barberQueues.containsKey(barberID)) {
						Queue<Customer> queue = barberQueues.get(barberID);
						customer = queue.poll();
				}
		} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
		} finally {
				mutex.release();
		}

		return customer;
	}

	public void start() {

		if ( this.barbershopCase == '\0' || this.customersList == null || this.customersList.size() == 0 ) {
			System.err.println("É necessário informar uma lista de clientes e/ou o caso de teste.");
			return;
		}

		if ( this.barbershopCase == 'A') barbAmount = 1;
		else if ( this.barbershopCase == 'B') barbAmount = 2;
		else if ( this.barbershopCase == 'C') barbAmount = 3;

		for ( int i = 0; i < barbAmount; i++ ) {
			Barber barber = new Barber(BarbersName.values()[i], this);
			Thread t = new Thread(barber);
			barberThreadList.add(t);

			t.start();
			
			try {
					Thread.sleep(1000); 
			} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}

		Thread escovinhaThread = new Thread(new Escovinha(this));
		Thread tainhaThread = new Thread(new Tainha(this));
		escovinhaThread.start();
		tainhaThread.start();

		try {
			tainhaThread.join();
		} catch (InterruptedException e) {
				e.printStackTrace();
		}

		for ( Thread customerThread : customerThreadList ) {
			try {
					customerThread.join();
			} catch (InterruptedException e) {
					e.printStackTrace();
			}
		}

		for (Thread barberThread : barberThreadList) {
				barberThread.interrupt();
		}

		barberShopIsClosed = true;
		
		try {
			escovinhaThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
