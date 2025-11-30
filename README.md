LungLink_Server IS DESIGNED FOR:
- Managging all client and doctor connections through TCP
- Handling login and registration requests
- Managing doctor-patient connection
- Receiving and storing signals (EMG/ECG) in CSV format
- Managing patient medicalHitsory and observations from the doctor
- Interacting with the SQLite database

## PROJECT STRUCTURE

```

  src/main/java/
 ├── main/
 │   └── ServerMain.java

 ├── network/
 │   ├── ServerConnection.java
 │   ├── ClientHandler.java
 │   ├── DataReceiver.java
 │   ├── DataSender.java
 │   └── FileUtils.java

 ├── services/
 │   ├── ServerService.java
 │   ├── AuthenticationService.java
 │   ├── MedicalHistoryService.java
 │   ├── AssignmentService.java
 │   └── SignalService.java

 ├── jdbc/
 │   ├── JDBCConnectionManager.java
 │   ├── JDBCUser.java
 │   ├── JDBCClient.java
 │   ├── JDBCDoctor.java
 │   ├── JDBCSignal.java
 │   └── JDBCMedicalHistory.java

 ├── pojos/
 │   ├── User.java
 │   ├── Client.java
 │   ├── Doctor.java
 │   ├── DoctorSpecialty.java
 │   ├── Signal.java
 │   ├── TypeSignal.java
 │   └── MedicalHistory.java

 └── utils/
     ├── SecurityUtils.java
     ├── UIUtils.java
     └── DateUtils.java

database/
 └── lunglink.db

signals/
 └── (CSV signals stored here)

```

## GUIDE

```
1- Open the project
2- Run: main.ServerMain
3- Once the server start listening on port 9000 clients and doctors can connect through TCP
4- The server will automatically: 
a) Handle login and register requests
b) Manage doctor-patient connectionç
c) Recieve signals
d) Store data in the database
6- Stop the server (terminate execution)

```

## AUTHORS
- Martina Zandio
- Ana Losada
- Jimena Aineto
- Paula Reyero
- Sara Menor 


  
