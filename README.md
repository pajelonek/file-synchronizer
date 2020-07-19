#	Instrukcja uruchamiania aplikacji serwerowej

Kolejne kroki przeprowadzają użytkownika przez proces konfiguracyjny aplikacji serwerowe. Konfiguracja tej części jako 
pierwszej jest zalecana z powodu zależnych od siebie w kolejności kroków. W zależności od zabezpieczeń sieci oraz 
komputera, istnieje prawdopodobieństwo na dopasowanie instrukcji do posiadanego sprzętu.

## Prerekwizyty
Upewnij się, że zgodnie z częścią teoretyczną pracy posiadasz skonfigurowane narzędzia:
* Git - [Download & Install Git](https://git-scm.com/downloads)
* Maven - [Download & Install Maven](https://maven.apache.org/)
* OpenSSH Server - [Download & Install OpenSSH Server](https://www.bleepingcomputer.com/news/microsoft/how-to-install-the-built-in-windows-10-openssh-server/).
* Java - [Download & Install Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) 


###1.	Konfiguracja pliku  application-properties
Kiedy użytkownik wypakuje pliki do lokacji posiadającej wolne miejsce na dysku, pierwszym krokiem konfiguracyjnym jest 
ustawienie wartości właściwości w pliku application-properties. Plik ten znajduje się w ścieżce:
file-synchronizer\src\main\resources\application-properties

Jest to zwykły plik tekstowy, korzystający z wbudowanych właściwości pozwalający na definiowanie nowych przez użytkownika.
 ```
logging.level.root = INFO  
server.address = 0.0.0.0  
server.port = 8888  
server.absolute.path = C:\\Users\\Username\\randomDirectory\\serverFiles  
server.ssh.path = randomDirectory\\serverFiles  
cleaning.log.file.interval = 120  
 ```
Przedstawiony fragment kodu przedstawia plik należący do rozwiązania wchodzącego w skład kodu źródłowego.
Do właściwości zdefiniowanych przez Spring należą:
-	logging.level.root - określa stopień widoczności logów wyświetlanych przez aplikację. Wszystkimi możliwościami 
podanymi od najmniejszej widoczności są: OFF, FATAL, ERROR, WARN, INFO, DEBUG. TRACE oraz ALL. Zalecane jest 
pozostawienie domyślnej wartości INFO ponieważ większa widoczność logów powoduje ich mniejszą przejrzystość i zalecana 
jest w przypadku diagnozowania błędów programisty.
-	server.address - adres sieciowy, z którego powinien korzystać serwer. W zależności od sieci komputerowej zaleca się 
ustawienie wartości do swoich potrzeb. 
-	server.port - port sieciowy, którego powinien użyć serwer.
Do właściwości zdefiniowanych należą:
-	server.absolute.path - ścieżka lokalna do folderu, który serwer traktuje jako miejsce przechowywania wspólnych 
plików dla klientu. Do poprawnego działania rozwiązania, z powodu konfiguracji ssh, wymagane jest wybranie folderu 
należącego do ścieżki użytkownika lokalnego komputera. Aplikacja sama nie utworzy tego pliku a bez niego kolej kroki nie
będą możliwe. Zalecane jest utworzenie go podczas konfiguracji pliku application-properties.
-	server.ssh.path - ścieżka lokalna do folderu z perspektywy protokołu ssh. Jest to server.absolute.path skrócone o 
początek C:/Users/Username.
-	cleaning.log.file.interval - interwał w sekundach, który określa jak po ilu sekundach każda zmiana o modyfikacji
 pliku przez dowolnego klienta, zostaje skasowana. Zalecane jest pozostawienie domyślnej wartości.
###2.	Instalacja zależności
Aby zainstalować potrzebne zależności na stację roboczą, należy otworzyć dowolne okno konsoli, udać się do miejsca 
wypakowania plików oraz wpisać:
 ```
mvn clean install  
  ```
Spowoduje to pobranie wszystkich pakietów i bibliotek określonych w pliku pom.xml, z którego Apache Maven pobiera 
informacje co ma zainstalować.
Jeżeli na ekranie wyświetli się informacja “BUILD SUCCESS”, oznacza to poprawność instalacji zasobów z sieci i możliwość
 przejścia do kolejnych kroków konfiguracyjnych.
###3.	Konfiguracja OpenSSH Server
Po stronie aplikacji serwerowej wymagana jest obecność skonfigurowania serwera ssh. Autor sugeruje wykorzystać wbudowaną
 opcję systemu instalacji OpenSSH Server. Aby go zainstalować:
-	Otwórz menu Start
-	Wyszukaj "Programy i funkcje”
-	Wybierz opcję “Funkcje opcjonalne”
-	Kliknij przycisk “Dodaj funkcję”
-	Znajdź w liście dostępnych funkcji “OpenSSH Server”
-	Naciśnij “Zainstaluj”
Po poprawnej instalacji, wyszukaj program “Uruchom” z menu Start i wpisz:
 ```
services.msc  
 ```
Innym rozwiązaniem jest wpisanie w menu Start “Usługi” i w przypadku poprawnego wyszukania programu, uruchomić go. 
Następnie należy wyszukać usługę “OpenSSH Server” z dostępnej listy. W dalszej kolejności należy wybrać opcję “Uruchom” 
oraz z listy rozwijanej “Opcje uruchamiania” wybrać “Automatyczne”.
###4.	Stwórz plik authorized_keys
Kolejnym krokiem jest utworzenie folderu “.ssh” w folderze użytkownika stacji roboczej tj. 
C:/Użytkownicy/Nazwa_Użytkownika/.ssh.
Następnie otwórz dowolny edytor plików, utwórz plik bez rozszerzenia i nazwij go “authorized_keys”. Będzie on 
odpowiedzialny za przechowywanie kluczy publicznych. Wspomniany przez autora plik musi znajdować się w wcześniej 
utworzonym folderze “.ssh”.
W związku z tym, że komunikacja protokołem ssh wymaga odpowiednich zabezpieczeń, wymagane jest ustawienie poprawnych 
uprawnień do pliku “authorized_keys”.
W tym celu:
1.	Otwórz właściwości pliku
2.	Wejdź w “Zabezpieczenia”
3.	Przejdź do “Zaawansowane”
4.	Kliknij przycisk “Wyłącz dziedziczenie”
5.	Usuń dostęp wszystkich użytkowników z wyjątkiem użytkownika “System” oraz użytkownika obecnie zalogowanego
6.	Naciśnij przycisk “Zastosuj”
Po ustawieniu poprawnych zabezpieczeń, można bezpiecznie przesyłać klucze publiczne z klientów do pliku “authorized_keys”.
###5.	Konfiguracja rsync
W rozwiązaniu, jednym z założeń komunikacyjnych jest dostęp stacji roboczej z aplikacją serwera, do programu rsync. W 
tym celu autor umieścił pliki binarne programu rsync z dystrybucji Cygwina pod ścieżką src/main/java/resources/bin.zip.
Wybierz dowolną wolną lokalizację, wypakuj tam wspomniane pliki rsync a następnie dodaj tę ścieżkę do zmiennych 
środowiskowych systemu.
Aby zweryfikować poprawność dodawania zmiennej, otwórz wiersz linii komend i wpisz:
  ```
	rsync --version  
   ```
Jeżeli wiersz poleceń zwrócił obecną wersję program rsync, konfiguracja przebiegła poprawnie.
###6.	Uruchamianie testów
Aby uruchomić testy aplikacji, uruchom wiersz linii komend w lokalizacji projektu a następnie wpisz:
  ```
mvn test  
  ```
W przypadku wyświetlenia komunikatu “BUILD SUCCESS” testy przebiegły pomyślnie i  aplikacja jest gotowa do uruchomienia.
###7.	Uruchamianie aplikacji
W celu uruchomienia aplikacji, otwórz dowolny wiersz poleceń w lokalizacji projektu i wpisz:
  ```
	mvn clean spring-boot:run  
  ```
Wpisanie wymienionych komendy spowoduje zbudowanie projektu, następnie usunięciu niepotrzebnych plików, by na końcu 
uruchomić aplikację.
###8.	Tworzenie pliku wykonawczego
Aby utworzyć plik wykonawczy aplikacji serwera należy otworzyć wiersz poleceń w lokalizacji projektu, po czym wpisać:
  ```
mvn clean package spring-boot:repackage 
   ```
Spowoduje to utworzenie pliku wykonawczego file-synchronizer-server-1.0.0.jar w lokalizacji target/.
###9.	Uruchamianie pliku wykonawczego
Po poprawnym wykonaniu poleceń z poprzedniego podpunktu, należy uruchomić dowolny wiersz poleceń, odnaleźć utworzony 
plik oraz wpisać:
  ```
java -jar file-synchronizer-SNAPSHOT.jar 
  ```
W przypadku niepowodzenia podczas uruchamiania aplikacji, zalecane jest ponowne przeprowadzenie konfiguracji.
## Autor

* **Paweł Jelonek**

