# Tubes1_agario-supremacy
Repository Tugas Besar Pertama Mata Kuliah Strategi Algoritma

## Table of Contents
* [General Information](#general-information)
* [Languange Used](#languange-used)
* [Strategy](#strategy)
* [Screenshots](#screenshots)
* [Repository Structure](#repository-structure)
* [Setup](#setup)
* [How to Compile and Run](#how-to-compile-and-run)
* [Project Status](#project-status)
* [Acknowledgements](#acknowledgements)
* [Contacts](#contacts)

## General Information
Sebuah program implementasi algoritma Greedy pada bot dalam permainan Game Galaxio oleh Entellect Challenge 2021. 
Repository ini dibuat dan mengandung file yang dibutuhkan untuk menyelesaikan Tugas Besar Pertama IF2211 Strategi Algoritma.
Contributors: 
- 13521062 Go Dillon Audris
- 13521063 Salomo Reinhart Gregory Manalu
- 13521172 Nathan Tenka

## Language Used
- Java Language (99.2%)
- Dockerfile (0.8%)

## Strategy
Strategi Algoritma Greedy yang diterapkan pada bot kami adalah strategi greedy by size. Strategi ini berusaha untuk memilih aksi yang akan menyebabkan ukuran bot bertambah dan mencegah ukuran bot mengecil akibat ancaman dari luar. Strategi ini terdiri atas:
1. Menembak teleporter ke lawan yang lebih kecil jika ukuran bot terbesar dan melakukan teleport kepada lawan jika terdapat lawan kecil di dekat teleporter.
2. Menghindari objek berbahaya atau ancaman berupa: torpedo lawan, musuh yang lebih besar, teleporter lawan, dan gas clouds.
3. Menyerang lawan dengan torpedo jika ukuran bot cukup.
4. Memakan food terdekat dengan bot
5. Bergerak menuju ke tengah atau pusat world

## Screenshots

## Repository Structure
```bash
.
│   README.md
│   
├───doc
│      Tubes1_K2_13521062, 13521063, 13521172_agario supremacy.pdf
│
├───src/main/java
│   │
│   ├───Enums
│   │      ObjectTypes.java
│   │      PlayerActions.java
│   │
│   ├───Greedy
│   │      Avoid.java
│   │      Food.java
│   │      Greedy.java
│   │      Helper.java
│   │      Teleport.java
│   │      Torpedo.java
│   │
│   ├───Models
│   │      GameObject.java
│   │      GameState.java
│   │      GameStateDto.java
│   │      LocalState.java
│   │      PlayerAction.java
│   │      Position.java
│   │      World.java
│   │
│   └───Services
│          BotService.java
│       
│       Main.java
│
├───target
│       classes
│       generated-sources
│       libs
│       maven-archiver
│       maven-status
│       agario supremacy.jar
│
├───Dockerfile
│
└───pom.xml
```

## Setup
Pastikan anda telah menginstall beberapa persyaratan yang dibutuhkan untuk menjalankan permainan dan bot:
1. Java (minimal Java 11)     : https://www.oracle.com/java/technologies/downloads/#java8
2. .NET FrameWork 3.1 dan 5.0 : https://dotnet.microsoft.com/en-us/download/dotnet
3. NodeJs                     : https://nodejs.org/en/download/
4. Maven                      : https://maven.apache.org/download.cgi

## How to Compile and Run
Setelah setup berhasil dilakukan, ikuti langkah dibawah untuk menjalankan program:
1. Download starter pack permainan dari situs : https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2
2. Pindahkan repository ini ke file starter-bots
3. Jika source code belum dibuild menjadi file jar, maka buka terminal pada directory repository dan jalankan command
   "mvn clean package"
4. Setelah source code dibuild menjadi file jar, maka edit file run.bat atau run.sh menjadi seperti script berikut

```bash
@echo off
:: Game Runner
cd ./runner-publish/
start "" dotnet GameRunner.dll

:: Game Engine
cd ../engine-publish/
timeout /t 1
start "" dotnet Engine.dll

:: Game Logger
cd ../logger-publish/
timeout /t 1
start "" dotnet Logger.dll

:: Bots
cd ../reference-bot-publish/
timeout /t 3
start "" "../starter-bots/Tubes1_agario-supremacy/target/agario supremacy.jar"
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
cd ../

pause
```
5. Bagian : : Bots dapat dimodifikasi dengan path menuju bot anda sendiri. Anda juga dapat mengganti jumlah bot yang mengikuti permainan dengan memodifikasi file appsettings.json pada folder engine-publish dan runner-publish

## Project Status
Proyek ini telah selesai secara utuh (Completed)

## Acknowledgements
- Terima kasih kepada Tuhan yang Maha Esa
- Terima kasih kepada para dosen pengampu: Bu Ulfa, Pak Rinaldi, dan Pak Rila
- Terima kasih kepada Tim Asisten Kuliah IF2211

## Contacts
Diciptakan dan diatur oleh agario supremacy