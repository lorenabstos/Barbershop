import java.util.ArrayList;

import App.Barbershop;
import App.Customer;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Barbershop barbershop = new Barbershop();
		ArrayList<Customer> customers = new ArrayList<>();
		char barbershopCase = 'A';
		Scanner scanner = new Scanner(System.in);
		int id = 0;

		System.out.println("Determine o tempo de sono do Tainha [1-5]: ");
		Barbershop.tainhaSleepingTime = scanner.nextInt();

		System.out.println("Determine o caso [A, B ou C]: ");
		barbershopCase = scanner.next().charAt(0);

		System.out.printf("\nO tempo de sono do Tainha é %d e o caso de teste %c.\n\n", Barbershop.tainhaSleepingTime, barbershopCase);

		while ( scanner.hasNext() ) {
			int category = scanner.nextInt();
			int cutHairTime = scanner.nextInt();

			Customer customer = new Customer(barbershop);
			customer.setCategory(category);
			customer.setCutHairTime(cutHairTime);
			customer.setId(id);

			customers.add(customer);
			id++;
		}

		barbershop.setBarbershopCase(barbershopCase);
		barbershop.setCustomersList(customers);
		barbershop.start();

		scanner.close();
	}
}