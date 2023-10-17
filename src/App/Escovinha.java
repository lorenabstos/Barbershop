package App;

import utils.Metrics;

public class Escovinha implements Runnable {

    private Metrics totalOcupationState = new Metrics();
    private Metrics totalQueueSizes = new Metrics();
    private int readsAmount = 0;
    Barbershop barbershop;

    public Escovinha(Barbershop barbershop) {
        this.barbershop = barbershop;
    }

    @Override
    public void run() {
        while (!Barbershop.barberShopIsClosed) {
            readsAmount++;

            // Calcula as métricas de ocupação atual da barbearia.
            Metrics currOcupationState = new Metrics(
                (double) barbershop.officerQueue.size(),
                (double) barbershop.sergeantQueue.size(),
                (double) barbershop.corporalQueue.size()
            );
            Metrics queueSizes = new Metrics(
                (double) barbershop.officerQueue.size(),
                (double) barbershop.sergeantQueue.size(),
                (double) barbershop.corporalQueue.size()
            );

            // Converte as métricas em porcentagens com base no número de cadeiras disponíveis.
            currOcupationState.divide((double) Barbershop.CHAIRS_AMOUNT);

            // Exibe o estado de ocupação atual da barbearia.
            System.out.printf(
                "\nEstado de Ocupação:\nOficial: %.2f%%\nSargento: %.2f%%\nCabo: %.2f%%\nVazio: %.2f%%\n\n",
                currOcupationState.getOfficer() * 100.0,
                currOcupationState.getSergeant() * 100.0,
                currOcupationState.getCorporal() * 100.0,
                currOcupationState.getEmpty() * 100.0
            );

            // Atualiza as métricas totais com as métricas atuais.
            totalOcupationState.add(currOcupationState);
            totalQueueSizes.add(queueSizes);

            try {
                Thread.sleep(3000); // Sleep de 3 segundos (3000 milissegundos)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Calcula as médias das métricas totais.
        totalOcupationState.divide((double) readsAmount);
        totalQueueSizes.divide((double) readsAmount);

        // Exibe as métricas finais.
        System.out.printf("\n\nQuantidade de Leituras Feitas pelo Escovinha: %d\n\n", readsAmount);

        System.out.printf(
            "\nEstado de Ocupação das Cadeiras em Média:\nOficial: %.2f%%\nSargento: %.2f%%\nCabo: %.2f%%\nVazio: %.2f%%\n\n",
            totalOcupationState.getOfficer() * 100.0,
            totalOcupationState.getSergeant() * 100.0,
            totalOcupationState.getCorporal() * 100.0,
            totalOcupationState.getEmpty() * 100.0
        );

        System.out.printf(
            "Comprimento Médio das Filas:\nOficial: %.2f\nSargento: %.2f\nCabo: %.2f\n\n",
            totalQueueSizes.getOfficer(),
            totalQueueSizes.getSergeant(),
            totalQueueSizes.getCorporal()
        );

        // Exibe as métricas de tempo médio de atendimento por categoria.
        System.out.printf(
            "Tempo Médio de Atendimento por Categoria:\nOficial: %.2f\nSargento: %.2f\nCabo: %.2f\n\n",
            (Barber.servicesAmount.getOfficer() != 0.0) ? (Barber.totalServiceTime.getOfficer() / Barber.servicesAmount.getOfficer()) : 0.0,
            (Barber.servicesAmount.getSergeant() != 0.0) ? (Barber.totalServiceTime.getSergeant() / Barber.servicesAmount.getSergeant()) : 0.0,
            (Barber.servicesAmount.getCorporal() != 0.0) ? (Barber.totalServiceTime.getCorporal() / Barber.servicesAmount.getCorporal()) : 0.0
        );

        // Exibe as métricas de tempo médio de espera por categoria.
        System.out.printf(
            "Tempo Médio de Espera por Categoria:\nOficial: %.2f s\nSargento: %.2f s\nCabo: %.2f s\n\n",
            (Barber.servicesAmount.getOfficer() != 0.0) ? (Barber.totalWaitingTime.getOfficer() / Barber.servicesAmount.getOfficer()) : 0.0,
            (Barber.servicesAmount.getSergeant() != 0.0) ? (Barber.totalWaitingTime.getSergeant() / Barber.servicesAmount.getSergeant()) : 0.0,
            (Barber.servicesAmount.getCorporal() != 0.0) ? (Barber.totalWaitingTime.getCorporal() / Barber.servicesAmount.getCorporal()) : 0.0
        );

        // Exibe o número total de clientes por categoria.
        System.out.printf(
            "Número total de cliente(s) por categoria:\nOficial(is): %d\nSargento(s): %d\nCabo(s): %d\nPausa(s): %d\n\n",
            barbershop.nOfficer, barbershop.nSergeant, barbershop.nCorporal, barbershop.nBreak
        );
    }
}
