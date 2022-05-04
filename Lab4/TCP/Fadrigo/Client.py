#Server.py
#Coded in VSCode
#Researched by Fadrigo, Addison
#Modified by Escalona, Jose

from random_word import RandomWords #abit slow, install using 'pip install random_word' but make sure to have 'pip install pyyaml' first
import socket
import errno
import random
import string

#from NSCOM01.TCP.Fadrigo.Server import HEADER_LENGTH

HEADER_LENGTH = 10

print("====CLIENT====")

IP = input("Enter target IP (localhost if otherwise): ")
PORT = int(input("Enter port #: "))
USERNAME = input("Enter your username: ")

print("Creating socket...")
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("Attempting to connect to server...")
try:
    client_socket.connect((IP,PORT))
    print("Connected to server...")
except OSError as msg:
    client_socket.close()
    print(msg)
    exit()
client_socket.setblocking(False)
print(client_socket.getpeername())

def sendMessage(message):
    if message:
            message = message.encode('utf-8')
            message_header = f"{len(message):<{HEADER_LENGTH}}".encode('utf-8')
            client_socket.send(message_header + message)

INTRODUCED = False
RUNTIME = True

print("===============================")
print("Send /DISCONNECT to disconnect.")
print("Send /RANDOM to send random strings.")
print("Send /TERMINATESERVER to terminate server remotely and close client.")

while RUNTIME:
    if not INTRODUCED:
        sendMessage(USERNAME)
        INTRODUCED = True

    message = input(f'{USERNAME} > ')
    
    if(message == "/DISCONNECT"):
        RUNTIME = False
        client_socket.close()
    elif(message == "/TERMINATESERVER"):
        RUNTIME = False
        print("Remotely Terminating Server...")
        sendMessage(message)
        print("Terminating Client...")
    elif(message == "/RANDOM"):
        listlen = int(input("Enter random words to be generated and sent (limited to 10000): "))
        if(listlen > 10000):
            listlen = 10000
        strlen = int(input("Enter string length: "))
        for i in range(0,listlen):
            sendMessage(''.join(random.choice(string.ascii_lowercase) for i in range(strlen))) #https://www.educative.io/edpresso/how-to-generate-a-random-string-in-python
    else:
        sendMessage(message)
    
    try:
        while True:
            username_header = client_socket.recv(HEADER_LENGTH)
            if not len(username_header):
                print("Connection closed by the server")
                exit()

            username_length = int(username_header.decode('utf-8').strip())
            username = client_socket.recv(username_length).decode('utf-8')

            message_header = client_socket.recv(HEADER_LENGTH)
            message_length = int(message_header.decode('utf-8').strip())
            message = client_socket.recv(message_length).decode('utf-8')

            print(f'{username}>{message}')

    except IOError as e:
        if e.errno != errno.EAGAIN and e.errno != errno.EWOULDBLOCK:
            print('Reading error: {}'.format(str(e)))
            exit()
        continue
    except Exception as e:
        print('Exception: '.format(str(e)))
        exit()
