# Instrukcja uruchamiania aplikacji serwerowej

Kolejne kroki przeprowadzają użytkownika przez proces konfiguracyjny aplikacji serwerowe. Konfiguracja tej części jako 
pierwszej jest zalecana z powodu zależnych od siebie w kolejności kroków. W zależności od zabezpieczeń sieci oraz 
komputera istnieje prawdopodobieństwo na dopasowanie instrukcji do posiadanego sprzętu.

## Prerekwizyty
Proszę upewnić się, że posiadasz skonfigurowane narzędzia:
* Maven - [Download & Install Maven](https://maven.apache.org/)
* OpenSSH Server - [Download & Install OpenSSH Server](https://www.openssh.com/)
* Java - [Download & Install Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) 

### Konfiguracja pliku  application.properties
Kiedy użytkownik wypakuje pliki do lokacji posiadającej wolne miejsce na dysku, pierwszym krokiem konfiguracyjnym jest 
ustawienie wartości zmiennych w pliku application.properties. Plik ten znajduje się w ścieżce:
```
file-synchronizer/src/main/resources/application.properties
```
Jest to zwykły plik tekstowy, korzystający ze wbudowanych właściwości Spring oraz pozwalający na definiowanie nowych 
przez użytkownika.
Przykład pliku z domyślnymi wartościami:
```
logging.level.root = INFO
server.address = 0.0.0.0
server.port = 8888
server.absolute.path = /home/osboxes/directory/serverFiles
server.ssh.path = directory/serverFiles
cleaning.log.file.interval = 120  
```
Przedstawiony fragment przedstawia plik należący do rozwiązania wchodzącego w skład kodu źródłowego.
Do właściwości zdefiniowanych przez Spring należą:
- logging.level.root — określa stopień widoczności logów wyświetlanych przez aplikację. Wszystkimi możliwościami 
podanymi od najmniejszej widoczności są: OFF, FATAL, ERROR, WARN, INFO, DEBUG. TRACE oraz ALL. Zalecane jest 
pozostawienie domyślnej wartości INFO, ponieważ większa widoczność logów powoduje ich mniejszą przejrzystość i zalecana 
jest w przypadku diagnozowania błędów programisty.
- server.address — adres sieciowy, z którego powinien korzystać serwer. W zależności od sieci komputerowej zaleca się 
ustawienie wartości do swoich potrzeb, domyślnie 0.0.0.0;
- server.port — port sieciowy, z którego powinien korzystać serwer.
Do właściwości zdefiniowanych przez użytkownika należą:
- server.absolute.path — ścieżka lokalna do folderu, który serwer traktuje jako miejsce przechowywania wspólnych 
plików dla klienta.
!Important! Do poprawnego działania rozwiązania, z powodu konfiguracji ssh, wymagane jest wybranie folderu 
należącego do folderu użytkownika lokalnego komputera. Aplikacja nie utworzy tego pliku a bez niego kolej kroki nie
będą możliwe. Zalecane jest utworzenie go podczas konfiguracji pliku application-properties. !Important!
- server.ssh.path — ścieżka lokalna do folderu z perspektywy protokołu ssh. Na przykładzie jest to server.absolute.path
 skrócone o początek /home/osboxes/.
!Important! Ważne jest, żeby server.ssh.path zawsze było skróceniem server.absolute.path o '/home/username'.
- cleaning.log.file.interval — interwał w sekundach, który określa jak po ilu sekundach każda zmiana o modyfikacji
 pliku przez dowolnego klienta, zostaje skasowana. Zalecane jest pozostawienie domyślnej wartości.
 
### Instalacja zależności
Aby zainstalować potrzebne zależności na stację roboczą, należy otworzyć dowolne okno konsoli, udać się do miejsca 
wypakowania plików oraz wpisać:
```
mvn clean install  
```
Spowoduje to pobranie wszystkich pakietów i bibliotek określonych w pliku pom.xml.
Jeżeli na ekranie wyświetli się informacja “BUILD SUCCESS”, oznacza to poprawność instalacji zasobów z sieci i możliwość
przejścia do kolejnych kroków konfiguracyjnych.

### Konfiguracja OpenSSH Server
#### Linux
W przypadku systemów Linux nie jest wymagana żadna akcja, ponieważ OpenSSH jest domyślnie zainstalowane na większości systemów.
Jeżeli jednak system operacyjny nie posiada OpenSSH, należy go zainstalować na maszynę.
#### Windows
Po stronie aplikacji serwerowej wymagana jest obecność skonfigurowanego serwera ssh. Zalecane jest wykorzystanie 
wbudowanej opcji systemu instalacji OpenSSH Server. Aby go zainstalować:
- Otwórz menu Start
- Wyszukaj "Programy i funkcje”
- Wybierz opcję “Funkcje opcjonalne”
- Kliknij przycisk “Dodaj funkcję”
- Znajdź w liście dostępnych funkcji “OpenSSH Server”
- Naciśnij “Zainstaluj”
Po poprawnej instalacji, wyszukaj program “Uruchom” z menu Start i wpisz:
```
services.msc  
```
Innym rozwiązaniem jest wpisanie w menu Start “Usługi” i w przypadku poprawnego wyszukania programu, uruchomić go. 
Następnie należy wyszukać usługę “OpenSSH Server” z dostępnej listy. W dalszej kolejności należy wybrać opcję “Uruchom”,
następnie z “Opcje uruchamiania” wybieramy “Automatyczne”.

### Plik authorized_keys
Kolejnym krokiem jest utworzenie pliku "authorized_keys w folderze użytkownika “.ssh”.
#### Windows
Należy otworzyć dowolny edytor plików, utworzyć plik bez rozszerzenia i nazwać go “authorized_keys”. Będzie on 
odpowiedzialny za przechowywanie kluczy publicznych. Wspomniany przez autora plik musi znajdować się we wcześniej 
utworzonym folderze “.ssh”.
W związku z tym, że komunikacja protokołem ssh wymaga odpowiednich zabezpieczeń, wymagane jest ustawienie poprawnych 
uprawnień do pliku “authorized_keys”.
W tym celu:
- Otwórz właściwości pliku
- Wejdź w “Zabezpieczenia”
- Przejdź do “Zaawansowane”
- Kliknij przycisk “Wyłącz dziedziczenie”
- Usuń dostęp wszystkich użytkowników z wyjątkiem użytkownika “System” oraz obecnie zalogowanego użytkownika
- Naciśnij przycisk “Zastosuj”
Po ustawieniu poprawnych zabezpieczeń, można bezpiecznie przesyłać klucze publiczne z klientów do pliku “authorized_keys”.
#### Linux
W przypadku systemów Linux, udaj się do twojego lokalnego folderu .ssh i następnie utwórz pusty plik "authorized_keys".
Przykladem utworzenia pliku jest:
```
touch authorized_keys
```
Proszę pamiętać o domyślnych ustawieniach plików ssh:
- chmod 700 ~/.ssh
- chmod 644 ~/.ssh/authorized_keys
- chmod 644 ~/.ssh/known_hosts
- chmod 644 ~/.ssh/config
- chmod 600 ~/.ssh/id_rsa
- chmod 644 ~/.ssh/id_rsa.pub

### Konfiguracja rsync
#### Windows
W rozwiązaniu, jednym z założeń komunikacyjnych jest dostęp stacji roboczej z aplikacją serwera, do programu rsync. W 
tym celu autor umieścił pliki binarne programu rsync z dystrybucji Cygwina pod ścieżką src/main/java/resources/bin.zip.
Wybierz dowolną wolną lokalizację, wypakuj tam wspomniane pliki rsync, a następnie dodaj tę ścieżkę do zmiennych 
środowiskowych systemu.
#### Linux
Rsync jest domyślnie zainstalowaną aplikacją na większość systemów Linux.
Aby zweryfikować istnienie narzędzia na maszynie proszę wpisać:
```
rsync --version  
```
Jeżeli wiersz poleceń zwrócił obecną wersję program rsync, nie jest wymagana żadna akcja użytkownika w tym kroku. 
W przeciwnym wypadku zainstaluj narzędzie zgodnie z posiadanym systemem operacyjnym.

### Uruchamianie testów
Aby uruchomić testy aplikacji, uruchom wiersz linii komend w lokalizacji projektu, a następnie wpisz:
```
mvn test  
```
W przypadku wyświetlenia komunikatu “BUILD SUCCESS” testy przebiegły pomyślnie i aplikacja jest gotowa do uruchomienia.

### Uruchamianie aplikacji
W celu uruchomienia aplikacji otwórz dowolny wiersz poleceń w lokalizacji projektu i wpisz:
```
mvn clean spring-boot:run  
```
Wpisanie wymienionych komendy spowoduje zbudowanie projektu, następnie usunięciu niepotrzebnych plików, by na końcu 
uruchomić aplikację.

### Tworzenie pliku wykonawczego
Aby utworzyć plik wykonawczy aplikacji serwera, należy otworzyć wiersz poleceń w lokalizacji projektu, po czym wpisać:
```
mvn clean package spring-boot:repackage 
```
Spowoduje to utworzenie pliku wykonawczego file-synchronizer-server-1.0.0.jar w lokalizacji target/.

### Uruchamianie pliku wykonawczego
Po poprawnym wykonaniu poleceń z poprzedniego podpunktu należy uruchomić dowolny wiersz poleceń, odnaleźć utworzony 
plik oraz wpisać:
```
java -jar file-synchronizer-SNAPSHOT.jar 
```
W przypadku niepowodzenia podczas uruchamiania aplikacji zalecane jest ponowne przeprowadzenie konfiguracji.

## Autor
* **Paweł Jelonek**

