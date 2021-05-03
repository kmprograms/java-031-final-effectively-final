import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class App1 {

    private volatile int factor2 = 11;

    // Wymuszanie na zmiennej ze ma byc final lub effectively final ma
    // dodatkowo podkreslic / zabezpieczyc ze nie moze na niej odbyc sie zadna modyfikacja

    // Kiedy wyrazenie lambda przechwytuje zmienna factor to tak naprawde tworzy jej kopie.
    // Kiedy metoda zwraca wyrazenie lambda to dzieki tej kopii nawet jezeli dane metody
    // w tym jej parametr factor zostana zwolnione to kopia utworzona
    // dla wyrazenia lambda bedzie istniala tak dlugo, jak instancja wyrazenia lambda ktora
    // moze przeciez byc wywolana pozniej w dogodnym dla nas momencie.

    UnaryOperator<Integer> mapper(int factor) {
        // return value -> value * factor++;
        return value -> value * factor;
    }

    UnaryOperator<Integer> mapper2(int factor) {
        // Zastosowanie zmiennej instancyjnej pozwala ja uzywac wewnatrz wyrazenia lambda i nie musi
        // byc final lub effectively final poniewaz takie zmienne sa przechowywane na stercie i
        // kompilator gwarantuje nam ze lambda zawsze bedzie miec dostep do najbardziej aktualnej
        // wartosci

        // Jezeli zalezy nam dodatkowo na pracy z wieloma watkami to taka zmienna mozemy jeszcze
        // opatrzyc slowem kluczowym volatile i wtedy najbardziej aktualna wartosc zmiennej
        // factor2 bedzie dostepna rowniez dla wszystkich watkow

        // Kiedy uzywasz zmienna instancyjna wtedy jako final jest traktowany this a jego
        // pola skladowe mozna juz bezpiecznie stosowac w wyrazeniach lambda. Nie mniej
        // jednak zawsze nalezy zachowac ostroznosc w aplikacjach wielowatkowych
        // Wyscigi -> w opisie link do filmu na YouTube

        factor2++;
        return value -> value * factor2;
    }

    public static void main(String[] args) {

        // Wyrazenie lambda moze "z zewnatrz" przechwycic:
        // -> zmienna statyczna,
        // -> zmienna instancyjna
        // -> zmienna lokalna (musza byc final lub effectively final)

        // -> Zmienna final to taka, ktorej nadajesz modyfikator final.

        // -> Zmienna ktorej wartosci juz nigdy nie zmieniasz po jej inicjalizacji traktujemy jako
        // effectively final

        // Stosowanie przechwytywania zmiennych zewnetrznych jako final lub effectively final
        // powoduje ze nie ma problemow z wielowatkowoscia.
        // Zalozmy przez chwile ze mamy mozliwosc modyfikowania w wyrazeniu lambda przechwyconej
        // zmiennej
        // Kazdy watek rusza z wlasnym stackiem, w zwiazku z czym nie mamy pewnosci czy
        // watek ktory wlasnie uruchomilismy bedzie monitorowal stan zmiennej isActive
        // z innego stacka innego watka. Zastosowanie final lub effectively final powoduje ze
        // ten problem znika - nie musimy dbac samodzielnie o synchronizacje pracy watkow.

        /*
            void doAction() {
                boolean isActive = true;
                executor.execute(() -> {
                    while(isActive) {
                        System.out.println("I'm running ...")
                    }
                });
                isActive = false;
            }
        */

        int f = 11;
        var m = new App1().mapper(f);

    }
}
