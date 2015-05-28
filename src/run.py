from subprocess import call
import subprocess
import os
import sys
import timeit

thread_count = int(sys.argv[1])
if len(sys.argv) == 3:
    out_filename = str(sys.argv[2])
    sys.stdout = open(out_filename, 'w')

call(['javac root/*.java'], shell=True)

for i in xrange(1, thread_count + 1):
    print '\nThread count: %d' % i
    start = timeit.default_timer()

    process = subprocess.Popen(['java root.Main "%d"' % i], shell=True, stdout=subprocess.PIPE )
    process_stdout = process.communicate()[0]

    stop = timeit.default_timer()
    print process_stdout.replace('\n', '')
    print 'Took seconds:', stop - start
