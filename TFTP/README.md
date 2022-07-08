# NSCOM01 - TFTP Client

## Coding Timelapse (TFTP Part): https://youtu.be/bEAS6tHReIk?t=1369

## Presentation and Demo: https://www.youtube.com/watch?v=gAvzzi8m4pg

## Authors
1. [Balcueva, J.](https://github.com/JushoB)
2. [Escalona, J.M.](https://github.com/jm55DLSU)
3. [Fadrigo, J.A.M.](https://github.com/ExME168)
4. [Fortiz, P.R.](https://github.com/prpfortiz)

## Directory
1. [Deliverable](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Deliverable) - Contains the zipped archive of the files submitted to the LMS.
2. [Documentation](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Documentation) - Contains all files and documents relating to development and usage of the program.
    1. [Program Details](https://github.com/jm55DLSU/NSCOM01/blob/main/TFTP/Documentation/NSCOM01%20-%20Program%20Design.pdf)
    2. [Runtime Instructions](https://github.com/jm55DLSU/NSCOM01/blob/main/TFTP/Documentation/Runtime%20Instructions.pdf)
3. [Java](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Java) - Source code of the project.
4. [References](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/References) - Contains third-party files used to explore/explain TFTP. None of it was used however.
5. [Server](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Server) - Contains a copy of the TFTPd64 which the interaction between the create client and TFTP server was predominantly tested.
6. [Wireshark](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Wireshark) - Contains the packet capture data that was used during the development of the TFTP Client.

## Program Specifications
1. Program Language: Java **(Java 17)**
2. Interface: GUI
3. Target Features:
    * Key Features
        * GUI or a command line-based user interface are acceptable.
        * The user is allowed to specify the server IP address.
        * Support for both upload and download of binary files.
        * When uploading, the program can send any file on the computer to the TFTP server as long as the file is accessible to the user using his / her OS privileges.
        * When downloading, the program must allow the user to provide the filename to use when saving the downloaded file to the client's computer.
        * Proper error handling at the minimum should include the following:
            * Timeout for unresponsive server
            * Handling of duplicate ACK
            * User prompt for file not found, access violation, and disk full errors
    * Optional Features
        * Support for option negotiation will merit additional points if correctly implemented.
        * Option to specify the transfer block size.
        * Communicate transfer size to a server when uploading.
        * To allow the user to manually ping the target host prior to transmission.

### [For Further Documentation](https://github.com/jm55DLSU/NSCOM01/blob/main/TFTP/Documentation/NSCOM01%20-%20Program%20Design.pdf)

### Pre-demo during development phase (also used as test file in succeding developments and testing): [Video](https://github.com/jm55DLSU/NSCOM01/blob/main/TFTP/Java/demo.mp4)
