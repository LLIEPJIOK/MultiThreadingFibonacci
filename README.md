# A simple multi-threaded application for calculating the nth digit in the Fibonacci series
The input is a directory in which tests with the extension ".IN" and answers to them with the extension ".OUT" are stored.
Three threads are used for processing: the first reads the data, checks it for correctness and passes it on, 
the second carries out calculations and writes the answer to a file with the extension ".RES", 
the third checks the received answer with the given answer in the directory and, if necessary, corrects it. 
It is also possible to stop the 2nd thread during calculations.

![Image Link](https://github.com/LLIEPJIOK/MultiThreadingFibonicci/blob/master/images/View.png)
