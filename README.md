# File-synchronizer server

It's server side of the project regarding file synchronizing through ssh with rsync on Windows 10.

Server performs scanning a directory where we store our files to share and gives client information on
how to manage their files as well as what actions performed other clients. The communication is based 
on REST architecture and all actions to server are ssh based.

Except installing necessary feature it is required to configure application.properties file as well as ssh configuration
which is described below.

## Prerequisites
Make sure you have installed all of the following prerequisites on your development machine:
* Git - [Download & Install Git](https://git-scm.com/downloads). OSX and Linux machines typically have this already installed.
* Maven - [Download & Install Maven](https://maven.apache.org/) - Dependency Management
* OpenSSH Server - [Download & Install OpenSSH Server](https://www.bleepingcomputer.com/news/microsoft/how-to-install-the-built-in-windows-10-openssh-server/). Windows 10 should already have this feature
enabled. Only install this system feature, configuration will be described below.
* Java - [Download & Install Java](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) - Runtime Environment essential to run application. At least 1.8 version. 


## Clone repository
To clone repository copy link from github, run git, go to the directory where you want
to copy repository and then type:

```
git clone [copied link from github]
```

### Configuration of application-properties
```
logging.level.root=INFO /* For futher development change to DEBUG */
server.address=0.0.0.0 /* It will automount server ip to users */
server.port=8888  /* Choose any free port to use */
server.absolute.path=C:\\Users\\pawst\\serverFiles /* An example of how to provide path
where our application will stores files, your directory needs to be in your user directory */
server.ssh.path=serverFiles /* Path to your directory for ssh, cut everything to your USERNAME */
cleaning.log.file.interval=120  /* Interval of how often server cleans his log list needed to 
inform clients about changes from other clients. Should be at least two times greater that pooler
interval from client */
```

### Installing

A step by step series of examples that tell you how to get a development env running

Firstly we need to install project along with all required dependencies:

Go to the folder where you cloned repository and run:
```
mvn clean install
```
If build completed successfully, we need to configure ssh along with rsync.

## SSH Server configuration
If you haven't installed OpenSSH Server yet, it is essential for application to work.
I recommend enabling system feature: [Download & Install OpenSSH Server](https://www.bleepingcomputer.com/news/microsoft/how-to-install-the-built-in-windows-10-openssh-server/)
After it got installed, go to run tab and type: 
 ```
services.msc 
```
and turn-on OpenSSH Server(it is also recommended enabling auto-start). It is by default turned-off.


Go to your User Directory under C:/Users/User and create a directory with name ".ssh".
Now open Git and go to C:/Users/User/.ssh/ and run:
```
touch authorized_keys
```
you can as well open any text editor e.g. Notepad++ and create a file
"authorized_keys" without extension inside mentioned directory(.ssh).

#####It is important to change permissions for "authorized_keys" file. 
To correctly change permissions for a file:
- Open properties
- Go to security
- Open Advanced
- Disable inheritance
- Delete all users permissions EXCEPT SYSTEM AND YOURS USER
- Click confirm


##### You will need to copy public keys from your ssh users to this file.

## Rsync configuration
In this project clients use rsync to send files. On server side we need to add rsync binaries
to the systems variables path.

The safest option is to copy binaries from src/main/java/resources/bin.zip.
Choose where you want to store binaries, unpack them and add to your environment variable "Path" your_new_path/bin.
Validate if you added binaries successfully by running in cmd:
```
rsync --version
```
If you receive a current version of rsync, configuration went properly.
### Running tests

To run tests go to your repository directory and run:
```
mvn test
```
### Deployment

If you want to create an executable jar with all dependencies to deploy application on server type:

```
mvn clean package spring-boot:repackage
```

The jar file will be created in /target directory as file-synchronizer-SNAPSHOT.jar.

To run application:
 ```
java -jar file-synchronizer-SNAPSHOT.jar
 ```
## Authors

* **Paweł Jelonek** - *Initial work*
