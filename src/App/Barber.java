package App;

import utils.Metrics;
import java.time.Instant;
import types.BarbersName;
import java.time.Duration;
import types.CustomerCategory;

public class Barber implements Runnable {

    private BarbersName barberID; // Identificador do barbeiro
    Barbershop barbershop; // Referência à barbearia onde o barbeiro trabalha

    public static Metrics totalServiceTime; // Métricas de tempo total de serviço por categoria
    public static Metrics totalWaitingTime; // Métricas de tempo total de espera por categoria
    public static Metrics servicesAmount; // Métricas de quantidade de serviços por categoria

    public Barber(BarbersName barberID, Barbershop barbershop) {
        this.barberID = barberID;
        this.barbershop = barbershop;

        totalServiceTime = new Metrics();
        totalWaitingTime = new Metrics();
        servicesAmount = new Metrics();
    }

    @Override
    public void run() {
        Customer cp = null; // Cliente atualmente atendido pelo barbeiro

        System.out.printf("O barbeiro %s chegou na barbearia.\n", barberID.name());

        while (true) {
            // Verifica se não há clientes nas filas e se o "Sargento Tainha" terminou
            if (barbershop.officerQueue.isEmpty() && barbershop.sergeantQueue.isEmpty()
                    && barbershop.corporalQueue.isEmpty()) {
                if (Tainha.isFinished()) {
                    System.out.printf("O barbeiro %s saiu.\n", barberID.name());
                    return; // Encerra a execução do barbeiro
                }

                System.out.printf("O barbeiro %s está na espera por clientes.\n", barberID.name());

                try {
                    Barbershop.barbersSemaphore.acquire(); // Aguarda a chegada de clientes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            try {
                Barbershop.mutex.acquire();
                if (barbershop.getBarbershopCase() == 'A' || barbershop.getBarbershopCase() == 'B') {
                    // Seleciona o próximo cliente a atender com base nas filas
                    if (!barbershop.officerQueue.isEmpty()) {
                        cp = barbershop.officerQueue.poll();
                    } else if (!barbershop.sergeantQueue.isEmpty()) {
                        cp = barbershop.sergeantQueue.poll();
                    } else if (!barbershop.corporalQueue.isEmpty()) {
                        cp = barbershop.corporalQueue.poll();
                    }
                } else if (barbershop.getBarbershopCase() == 'C') {
                    // Seleciona o próximo cliente a atender com base na categoria do barbeiro
                    if (barberID == BarbersName.RECRUTA_ZERO) {
                        if (!barbershop.officerQueue.isEmpty()) {
                            cp = barbershop.officerQueue.poll();
                        } else {
                            if (!barbershop.sergeantQueue.isEmpty()) {
                                cp = barbershop.sergeantQueue.poll();
                            } else if (!barbershop.corporalQueue.isEmpty()) {
                                cp = barbershop.corporalQueue.poll();
                            }
                        }
                    } else if (barberID == BarbersName.DENTINHO) {
                        if (!barbershop.sergeantQueue.isEmpty()) {
                            cp = barbershop.sergeantQueue.poll();
                        } else {
                            if (!barbershop.officerQueue.isEmpty()) {
                                cp = barbershop.officerQueue.poll();
                            } else if (!barbershop.corporalQueue.isEmpty()) {
                                cp = barbershop.corporalQueue.poll();
                            }
                        }
                    } else if (barberID == BarbersName.OTTO) {
                        if (!barbershop.corporalQueue.isEmpty()) {
                            cp = barbershop.corporalQueue.poll();
                        } else {
                            if (!barbershop.officerQueue.isEmpty()) {
                                cp = barbershop.officerQueue.poll();
                            } else if (!barbershop.sergeantQueue.isEmpty()) {
                                cp = barbershop.sergeantQueue.poll();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continue;
            } finally {
                Barbershop.mutex.release();
            }

            if (cp == null)
                continue;

            cp.setEndTime(Instant.now());
            cp.setElapsedTime(Duration.between(cp.getStartTime(), cp.getEndTime()));

            if (cp.getCutomerCategory() == CustomerCategory.OFFICER) {
                // Atualiza as métricas de tempo de espera, tempo de serviço e quantidade de serviços
                totalWaitingTime.setOfficer(totalWaitingTime.getOfficer() + cp.getElapsedTime().getSeconds());
                totalServiceTime.setOfficer(totalServiceTime.getOfficer() + (double) cp.getCutHairTime());
                servicesAmount.setOfficer(servicesAmount.getOfficer() + 1.0);
            } else if (cp.getCutomerCategory() == CustomerCategory.SERGEANT) {
                totalWaitingTime.setSergeant(totalWaitingTime.getSergeant() + cp.getElapsedTime().getSeconds());
                totalServiceTime.setSergeant(totalServiceTime.getSergeant() + (double) cp.getCutHairTime());
                servicesAmount.setSergeant(servicesAmount.getSergeant() + 1.0);
            } else if (cp.getCutomerCategory() == CustomerCategory.CORPORAL) {
                totalWaitingTime.setCorporal(totalWaitingTime.getCorporal() + cp.getElapsedTime().getSeconds());
                totalServiceTime.setCorporal(totalWaitingTime.getCorporal() + (double) cp.getCutHairTime());
                servicesAmount.setCorporal(servicesAmount.getCorporal() + 1.0);
            }

            System.out.printf("O barbeiro %s está cortando o cabelo de um %s.\n", barberID.name(), cp.getCutomerCategory().name());

            try {
                Thread.sleep(cp.getCutHairTime() * 1000); // Simula o corte de cabelo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

						System.out.printf("O barbeiro %s finalizou o corte de cabelo do %s.\n", barberID.name(), cp.getCutomerCategory().name());

						if ( cp.getCutomerCategory() == CustomerCategory.CORPORAL )
							barbershop.nCorporal++;
						else if ( cp.getCutomerCategory() == CustomerCategory.OFFICER)
							barbershop.nOfficer++;
						else if ( cp.getCutomerCategory() == CustomerCategory.SERGEANT)
							barbershop.nSergeant++;

            Barbershop.customersSemaphore.release(); // Libera o cliente após o serviço
        }
    }
}
