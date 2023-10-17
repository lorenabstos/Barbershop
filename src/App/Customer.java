package App;

import java.time.Duration;
import java.time.Instant;

import types.CustomerCategory;

public class Customer implements Runnable {

	private CustomerCategory cutomerCategory; // Categoria do cliente
	private int cutHairTime; // Tempo necessário para cortar o cabelo
	private int id; // Identificador único do cliente
	private Instant startTime; // Hora de chegada do cliente
	private Instant endTime; // Hora de saída do cliente
	private Duration elapsedTime; // Tempo total gasto na barbearia
	private Barbershop barbershop; // Instância da barbearia

	public Customer(Barbershop barbershop) {
		this.barbershop = barbershop;
	}

	public CustomerCategory getCutomerCategory() {
		return cutomerCategory;
	}

	public void setCutomerCategory(CustomerCategory category) {
		this.cutomerCategory = category;
	}

	public void setCategory(int category) {
		this.cutomerCategory = CustomerCategory.values()[category];
	}

	public int getCutHairTime() {
		return cutHairTime;
	}

	public void setCutHairTime(int serviceTime) {
		this.cutHairTime = serviceTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Duration elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	@Override
    public void run() {
		System.out.printf("Um %s chegou na barbearia. Seu identificador é: %d\n", cutomerCategory.name(), id);
		this.setStartTime(Instant.now());

		CustomerCategory costumerID = this.getCutomerCategory();

		try {
				Barbershop.mutex.acquire();

				costumerID = this.getCutomerCategory();

				// Adiciona o cliente à fila apropriada com base em sua categoria.
				if (costumerID == CustomerCategory.OFFICER) {
						barbershop.officerQueue.add(this);
				} else if (costumerID == CustomerCategory.SERGEANT) {
						barbershop.sergeantQueue.add(this);
				} else if (costumerID == CustomerCategory.CORPORAL) {
						barbershop.corporalQueue.add(this);
				}

		} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
		} finally {
				Barbershop.mutex.release(); // Libera o mutex (semaforo) para outros clientes
		}

		Barbershop.barbersSemaphore.release(); // Libera um barbeiro para atender o cliente

		try {
				Barbershop.customersSemaphore.acquire(); // Aguarda a conclusão do atendimento
		} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
		}

		System.out.printf("Um %s saiu. Seu identificador é: %d\n", cutomerCategory.name(), id);
	}
}
