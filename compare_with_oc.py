# This is to generate random samples and test

import random
import os
import time
import subprocess
import shutil
import sys


SHOW_STAT = True


def cmp_files(a, b):
    t1, t2 = '', ''
    with open(a) as f1:
        t1 = f1.read()
        t1 = t1.replace('\n', '')
        t1 = t1.replace('\r', '')
        t1 = t1.replace(' ', '')
    with open(b) as f2:
        t2 = f2.read()
        t2 = t2.replace('\n', '')
        t2 = t2.replace('\r', '')
        t2 = t2.replace(' ', '')
    return t1 == t2


def remove_dir(m_dir):
    if os.path.exists(m_dir):
        # os.removedirs(inputs.INPUT_DIR_NAME)
        shutil.rmtree(m_dir)


def get_delta_time(delta_time):
    return "{0:.2f}".format((time.time() - delta_time) * 1000)


def generate_output(script='homework3.py',
                    input_dir='test_samples/TestCasesHW2'
                   ):
    # total time count
    total_delta_time = time.time()

    if not os.path.exists(input_dir):
        print('Input dir: ' + input_dir + ' not exists.')
        return

    dl = [d for d in os.listdir(input_dir)]
	
    if SHOW_STAT:
        print('Running Script \"' + script + '\" on ' + str(len(dl)) + ' samples.')

    # preparing the profiling buckets
    identical, diff, errored = [], [], []

    # iterate over input samples
    for idx, f in enumerate(dl):
        f_status = 'ing'

        # print stat
        if SHOW_STAT:
            print('Processing input file: ' + f + ' ... ' + str(idx + 1) + '/' + str(len(dl)))

        # check if input and output txt exists
        if not (os.path.exists(input_dir + '/' + f + '/input.txt') and os.path.exists(input_dir + '/' + f + '/output.txt')):
            print('Subdir ' + f + ' not valid, skipping...')
            continue

        # clear prev input.txt
        if os.path.exists('input.txt'):
            os.remove('input.txt')

        # copy input file
        shutil.copyfile(input_dir + '/' + f + '/input.txt', 'input.txt')
        delta_time = time.time()

        # run script
        cmd = [script]
        if script.endswith('.py'):
            cmd.insert(0, 'python')
        else:
            cmd[0] = "./" + cmd[0]
        subprocess.Popen(cmd).wait()

        # getting output and copy to output dir
        if not os.path.exists('output.txt'):
            if SHOW_STAT:
                print('Err in file: ' + f +
                      '. Missing output file. Please check the input file and your script')
            errored.append(f)
            f_status = 'Error'
            continue
        f_status = 'Completed'

        # see diff on this two files
        if cmp_files('output.txt', input_dir + '/' + f + '/output.txt'):
            identical.append(f)
        else:
            diff.append(f)
            shutil.copyfile('output.txt', input_dir + '/' + f + '/output_gen.txt')

        os.remove('output.txt')

        if SHOW_STAT:
            print('Processed ' + f + ' in ' + get_delta_time(delta_time) + 'ms. Status: ' + f_status)
    print('Identical: ' + str(len(identical)) + '. Diff: ' + str(len(diff)))
    print('All diff cases: ' + str(diff))

    if SHOW_STAT:
        print('Complete Batch. ' + str(len(identical) + len(diff)) + ' done. ' + str(len(errored)) + ' error.')
    

def main(script='homework3.py'):
    # see the above def of this function to understand all the params
    generate_output(script)


if __name__ == '__main__':
    if len(sys.argv) > 1:
        main(sys.argv[1])
    else:
        main()

