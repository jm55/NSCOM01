#Server.py
#Coded in VSCode
#Researched by Fadrigo, Addison
#Modified by Escalona, Jose

import socket
import select

#import click #for cls
import os

HEADER_LENGTH = 10

def printDisplayHeader():
    print("====SERVER====")
    print("Server IP listening @ port: {}:{}".format(IP,PORT)) #https://www.geeksforgeeks.org/display-hostname-ip-address-python/
    print("==============")

def cls():
    _ = os.system('cls')

cls()
print("====SERVER====")
IP = ""
mode = input("'Network' Mode or 'Localhost' Mode?\n(Defaults to localhost if input is invalid): ")
if(mode.lower() == 'network'):
    IP = socket.gethostbyname(socket.gethostname()) #replaced with live system hostname/ip; only works correctly if the computer has one network device
else:
    IP = "localhost"
PORT = int(input("Enter port (Recommended: 49152 to 65535): "))

cls()
printDisplayHeader()

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((IP, PORT))
server_socket.listen(1)

sockets_list = [server_socket]
clients = {}

def receive_message(client_socket):
    try:
        message_header = client_socket.recv(HEADER_LENGTH)
        
        if not len(message_header):
            return False
        
        message_length = int(message_header.decode('utf-8').strip())
        return {'header':message_header, 'data':client_socket.recv(message_length)}
    except:
        return False

RUNTIME = True

client_list = []

while RUNTIME:
    read_sockets, _, exception_sockets = select.select(sockets_list, [], sockets_list)

    for notified_socket in read_sockets:
        if notified_socket == server_socket:
            client_socket, client_address = server_socket.accept()
            user = receive_message(client_socket)

            if user is False:
                continue

            sockets_list.append(client_socket)
            clients[client_socket] = user

            print("Accepted new connection from {}:{} with username: {}".format(*client_address, user['data'].decode('utf-8')))
            client_list.append([client_socket.getpeername()[0],client_socket.getpeername()[1],clients[client_socket]['data'].decode('utf-8')])
        else:
            message = receive_message(notified_socket)

            if message is False:
                print('Closed connection from: {}'.format(clients[notified_socket]['data'].decode('utf-8')))
                sockets_list.remove(notified_socket)
                del clients[notified_socket]
                continue

            user = clients[notified_socket]

            if(message["data"].decode("utf-8")=="/TERMINATESERVER"): #TERMINATE SERVER VIA 'SSH'
                print("Terminating Server...")
                RUNTIME = False
                server_socket.close()
            elif(message["data"].decode("utf-8")=="/HARDCLS"): #CLS SERVER VIA 'SSH'
                cls()
                printDisplayHeader()
            else: #STANDARD MESSAGE
                print(f'Received message from {user["data"].decode("utf-8")}: {message["data"].decode("utf-8")}')
                for client_socket in clients:
                    if client_socket != notified_socket:
                        client_socket.send(user['header'] + user['data'] + message['header'] + message['data'])
    
    for notified_socket in exception_sockets:
        sockets_list.remove(notified_socket)
        del clients[notified_socket]

exit()