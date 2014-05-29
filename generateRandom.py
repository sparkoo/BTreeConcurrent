import random
import sys
import math

for power in range(7, 9):

    N = int(math.pow(10, power))

    #!/usr/bin/python
    fo = open("input_{0}.data".format(N), "wb")

    for i in range(N):
        s = str(random.randrange(N * 10)) + "\n"
        fo.write(bytes(s, 'UTF-8'))

    fo.close()