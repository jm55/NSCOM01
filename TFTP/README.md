# NSCOM01 - TFTP Client

## Authors
1. Balcueva, J.
2. Escalona, J.M.
3. Fadrigo, J.A.M.
4. Fortiz, P.R.

## Program Specifications
1. Program Language: Java
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

### [For Further Documentation](https://github.com/jm55DLSU/NSCOM01/tree/main/TFTP/Documentation)
