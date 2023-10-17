package App;

import types.CustomerCategory;

public class Tainha implements Runnable {

	private static boolean finished = false;
	private Barbershop barbershop;

	public Tainha(Barbershop barbershop) {
		this.barbershop = barbershop;
	}

	public static boolean isFinished() {
		return finished;
	}

	@Override
	public void run() {

		System.out.printf("O Sargento Tainha chegou!\n");

		CustomerCategory customerCategory;
		int zerosAmount = 0;
		int i = 0;

		// Loop principal que controla o comportamento do Sargento Tainha enquanto ele está em execução.
		while (i < barbershop.getCustomersList().size() && zerosAmount < 3) {
			try { 
				// O Sargento Tainha "dorme" por um período de tempo determinado.
				Thread.sleep(Barbershop.tainhaSleepingTime * 1000); 
			} catch (InterruptedException e) {
				// Caso a thread seja interrompida enquanto estiver dormindo, a exceção é tratada aqui.
				Thread.currentThread().interrupt();
			}

			// Verifica a categoria do cliente atual na fila da barbearia.
			customerCategory = barbershop.getCustomersList().get(i).getCutomerCategory();

			// Conta o número de clientes que têm categoria igual a PAUSE.
			if (customerCategory == CustomerCategory.PAUSE) {
				zerosAmount++;
			} else {
				// Se o cliente não estiver em pausa, inicia uma nova thread para atendê-lo na barbearia.
				Thread t = new Thread(barbershop.getCustomersList().get(i));
				barbershop.getCustomerThreadList().add(t);
				t.start();
				zerosAmount = 0; // Reinicia o contador de clientes em pausa.
				System.out.printf("O Sargento Tainha adicionou um novo cliente.\n");
			}

			// Move para o próximo cliente na fila.
			i++;

			// Verifica se o número total de clientes na fila da barbearia atingiu o limite das cadeiras disponíveis.
			// Se sim, passa para o próximo cliente sem iniciar uma nova thread para ele.
			if (barbershop.officerQueue.size() + barbershop.sergeantQueue.size() + barbershop.corporalQueue.size() >= Barbershop.CHAIRS_AMOUNT) {
				i++;
			}
		}

		// Sinaliza que o Sargento Tainha terminou sua execução.
		finished = true;
		System.out.printf("O Sargento Tainha saiu!\n");
	}
}
