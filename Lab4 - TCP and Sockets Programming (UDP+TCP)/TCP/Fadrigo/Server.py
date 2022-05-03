#Server.py
#Coded in VSCode
#Researched by Fadrigo, Addison
#Modified by Escalona, Jose

from email import message_from_file
from http import server
import socket
import select

HEADER_LENGTH = 10

print("====SERVER====")

IP = "127.0.0.1" #localhost since it is in a server role
PORT = int(input("Enter port (Recommended: 49152 to 65535): "))

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((IP, PORT))
server_socket.listen()

sockets_list = [server_socket]
clients = {}

print("Server listening @ port: {}".format(PORT))
print("Server IP: {}".format(socket.gethostbyname(socket.gethostname()))) #https://www.geeksforgeeks.org/display-hostname-ip-address-python/

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
        else:
            message = receive_message(notified_socket)

            if message is False:
                print('Closed connection from: {}'.format(clients[notified_socket]['data'].decode('utf-8')))
                sockets_list.remove(notified_socket)
                del clients[notified_socket]
                continue

            user = clients[notified_socket]

            if(message  ["data"].decode("utf-8")=="/TERMINATESERVER"):   
                print("Terminating Server...")
                RUNTIME = False
                server_socket.close()
            else:
                print(f'Received message from {user["data"].decode("utf-8")}: {message["data"].decode("utf-8")}')
                for client_socket in clients:
                    if client_socket != notified_socket:
                        client_socket.send(user['header'] + user['data'] + message['header'] + message['data'])
    
    for notified_socket in exception_sockets:
        sockets_list.remove(notified_socket)
        del clients[notified_socket]

exit()