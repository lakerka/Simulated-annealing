from __future__ import print_function
from subprocess import call
import subprocess
import os
import sys
import timeit

thread_count = int(sys.argv[1])
out_filename = str(sys.argv[2])

call(['javac root/*.java'], shell=True)
with open(out_filename, 'w') as out_file:
    for i in xrange(1, thread_count + 1):
        print('\nThread count: %d' % i, file=out_file)

        start = timeit.default_timer()
        process = subprocess.Popen(['java root.Main "%d"' % i], shell=True, stdout=subprocess.PIPE )
        stdout = process.communicate()[0]
        stop = timeit.default_timer()
        print(stdout[:len(stdout) - 1], file=out_file)
        print('Took seconds:', stop - start, file=out_file)
