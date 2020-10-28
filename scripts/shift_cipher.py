#!/bin/env python2

# This script is used to brute force an encrypted message with cesar code by shifting all caracters one by one

file = 'example.txt'

text = open(file,'rb').read()

i = -100
while i < 100:
    print(''.join([chr(ord(c) - i) for c in text]))
    i += 1
