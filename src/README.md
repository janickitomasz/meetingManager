# PoC 
Projekt to Proof of Concept aplikacji znajdującej terminy spotkań, z założeniami:
+ na wejściu dostajemy listę użytkowników z ich kalenndarzami(godziny pravy i zabookowane spotkania)
+ na wejściu podawany jest również zakres, w którym mają być szukane spotkania
+ oraz ilosć i czas ich trwania

Stworzono 2 endpointy:
+ 1 znajduje spotkania przy założeniu, że wszyscy uczestnicy muszą być obecni
+ 2 znajduje spotkania z maksymalną liczbą uczestników

# Algorytm

## Częścią wspólną obu endpointów jest konwersja danych wejściowych.
Tworzone są dla wszystkich uczestników listy dostepnych slotów. Czas trwania slotu podawany jest globalnie dla plikacji w pliku .properties
Przy tworzeniu listy slotów brane są pod uwagę podane godziny pracy i strefa czasowa podana na wejściu
z listy slotów usuwane są te zajęte już przez zabookowane spotkania

Wszystkie czasy sa przeliczane do strefy czasowej podanej w pliku .properties

## Znajdowanie "pełnych" spotkań

1. Pobierane są listy dostępnych slotów dla każdego uczestnika (uprzednio przeliczone na wspólną strefę czasową)
2. Przy pomocy funkcji .retainAll z JDK zachowywane są tylko te sloty, które występują we wszystkich listach (dostosowane metody equals() i hashcode())
3. Iterując po wspólnych slotach, dodawane są te, które "graniczą" ze sobą czasowo i gdy zebranie zostanie odpowiednia ilość slotów (w zależności od długości spotkania w danych wejściowych), wybierane są na termin spotkania
4. Znalezione spotkania zwracane są po ograniczeniu ich ilości do żądanej w danych wejściowych

## Znajdowanie najlepszych możliwych spotkań

1. Iterowanie następuje począwszy od początku okienka podanego w danych wejściowych i trwa do znalezienia żadanej ilości "pełnych" spotkań, lub do końca okienka
2. Krok iteracji to wielkość slota z .properties. W ten sposób wyznaczane są godziny rozpoczęcia i zakończenia każdego slota
3. Dla każdego slota sprawdzana jest lista uczestników mająca wolny termin w X nasepnych slotów, gdzie X to długość spotkania (podana w "slotach")
   1. Jeśli wszyscy uczestnicy mają wolne termniny dodawane jest ono do kolejki, a iteracja trwa dalej, począwszy od daty zakończenia tego spotkania
   2. Jeśli lista "wolnych" uczestników jest mniejsza od listy wszystkich uczestników, to spotkanie jest dodawane do kolejki, jeśli lista jest dłuższa niż 1. Iteracja jest kontynuowana dla nastepnego slota
4. Po uzyskaniu kolejki spotkań z różnymi datami rozpoczęcia, następuje wybranie żądanej ilości spotkań. 
   1. Iterowanie nasepuje powszystkich spotkaniach z kolejki. 
   2. Sprawdzane jest czy dane spotkanie z kolejki nie trwa w czasie jednego z juz wybranych spotkań. Jeśli nie, to jest dodawane do wybranych spotkań
5. Zwracana jest lista spotkań ograniczona do żądanej ilosci

# Rozwój czyli ToDo
Aplkikacja jest tylko Proof of Concept. Nie dodano unit testów, któe powinny się znaleźć dla wsyztskich metod publicznych z serwisów. Można uznać również sens dodania unit testów dla metod equals i hashcode obiektów i recordów, dl aktórych zostały one zmodyfikowane.
Zwracany jest zawsze kod 200 z wiadmością. Sensowne mogłoby być używanie kodów błędów przy braku możliwośći ustalenia spotkań, jak również obsługę i logowanie innych spotkań.
W celach serwisowych możńa rozważyć generowanie UUID dla każdego zapytania i przekazywania go "głębiej" w kolejnych metodach.
Dodać należy takze logowanie, a kod zrefaktoryzować (zbyt długie moetody, nieujednolicone nazwewnictwo, duży konstruktor AttendeCalendar - logikę z konstruktora można by wynieść do osbnej klasy).

## Ograniczenia
Obecnie długość slota z properties jest bardzo istotne dla działania programu. Każde zabookowane spotkanie w danych wejściowych, musi zajmować 1 slot. Dane początku i końca okienka oraz godziny pracy uczestników, muszą wypadać na początku lub końcu jakiegoś slota
(czyli np, nie możńa przy slocie 60 minut, podawać godziny pracy 8:15-16-15, ani mieć zabookowanego spotkania 8:05-8:40). 
Dodanie funkcjonalności wymaga dopisania odpowiednich klas przeliczających czasy lub zmniejszeni czasu slota. 


# Testy 
Przykłądowe zapytania wraz JSONami umieszczono i zapytaniami  w katalogu