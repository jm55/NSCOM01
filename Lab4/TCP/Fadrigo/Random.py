#Server.py
#Coded in VSCode


import random
import string

listlen = int(input("Enter random words to be generated and sent: "))
for i in range(1,listlen):
    print(''.join(random.choice(string.ascii_lowercase) for i in range(10)))