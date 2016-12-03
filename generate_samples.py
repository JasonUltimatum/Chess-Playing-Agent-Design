# This is to generate random samples and test

import random
import os
import time
import subprocess
import shutil
import sys


SHOW_STAT = True


def remove_dir(m_dir):
    if os.path.exists(m_dir):
        # os.removedirs(inputs.INPUT_DIR_NAME)
        shutil.rmtree(m_dir)


def get_delta_time(delta_time):
    return "{0:.2f}".format((time.time() - delta_time) * 1000)


def generate_one(n, Xs, Os, mode, youplay, depth, cell_values, output_file):

    # open file and gen meta data
    f = open(output_file, 'w')
    f.write(str(n) + '\n')
    f.write(mode + '\n')
    f.write(youplay + '\n')
    f.write(str(depth) + '\n')

    # gen cell values
    for i in range(n):
        tmp = random.randint(1, cell_values)
        f.write(str(tmp))
        for j in range(n - 1):
            f.write(' ')
            tmp = random.randint(1, cell_values)
            f.write(str(tmp))
        f.write('\n')

    # gen board state
    # sampling
    all_sel_pts = random.sample(list(range(n * n)), Xs + Os)
    X_cord = all_sel_pts[:Xs]
    O_cord = all_sel_pts[Xs:]
    all_pts = ['.'] * (n * n)
    for i in X_cord:
        all_pts[i] = 'X'
    for i in O_cord:
        all_pts[i] = 'O'

    # write to file
    for i in range(n):
        for j in range(n):
            f.write(all_pts[i * n + j])
        if i != n-1:
            f.write('\n')
    f.close()


def generate_batch(n, Xs, Os, mode, depth, youplay, cell_values, num=10, output_folder='random_samples'):
    """
    :param n: dimension, can be a single value or a [a, b] a <= b for the random interval, be more than 1
    :param Xs: number of Xs, can be a single value or a [a, b] a <= b for the random interval
    :param Os: number of Os, can be a single value or a [a, b] a <= b for the random interval
    :param mode: mode, can be a single one, 'MINIMAX',  or ['MINIMAX', 'ALPHABETA']
    :param depth: depth, can be a single value or a [a, b] a <= b for the random interval
    :param youplay: youplay, can be a single value or ['O', 'X']
    :param cell_values: the upper limit of random cell values,
            can be a single value or a [a, b] a <= b for the random interval
    :param output_folder: name of the output_folder
    :param num: number of sample input files
    :return: None
    """
    delta_time = time.time()
    remove_dir(output_folder)
    os.makedirs(output_folder)
    for i in range(num):
        t_n = random.randint(n[0], n[1]) if isinstance(n, list) else n
        t_Xs = random.randint(Xs[0], Xs[1]) if isinstance(Xs, list) else Xs
        t_Os = random.randint(Os[0], Os[1]) if isinstance(Os, list) else Os
        t_youplay = youplay[random.randint(0, 1)] if isinstance(youplay, list) else youplay
        t_depth = random.randint(depth[0], depth[1]) if isinstance(depth, list) else depth
        t_cell_values = random.randint(cell_values[0], cell_values[1]) \
            if isinstance(cell_values, list) else cell_values
        t_mode = mode[random.randint(0, 1)] if isinstance(mode, list) else mode
        t_input_file = output_folder + '/' + 'sample_' + str(i) + '.txt'
        generate_one(t_n, t_Xs, t_Os, t_mode, t_youplay, t_depth, t_cell_values, t_input_file)
    if SHOW_STAT:
        print('Generated ' + str(num) + ' samples in ' + get_delta_time(delta_time) + 'ms')


def generate_output(script='homework3.py',
                    input_dir='random_samples',
                    output_dir='random_samples_outputs'
                   ):
    # total time count
    total_delta_time = time.time()

    fl = [f for f in os.listdir(input_dir) if f.endswith('.txt')]
    if SHOW_STAT:
        print('Running Script \"' + script + '\" on ' + str(len(fl)) + ' samples.')

    # clear all previous output dir
    if os.path.exists(output_dir):
        shutil.rmtree(output_dir)
    os.makedirs(output_dir)

    # preparing the profiling buckets
    finished, errored = [], []

    # iterate over input samples
    for idx, f in enumerate(fl):
        f_status = 'ing'

        # print stat
        if SHOW_STAT:
            print('Processing input file: ' + f + ' ... ' + str(idx + 1) + '/' + str(len(fl)))

        # clear prev input.txt
        if os.path.exists('input.txt'):
            os.remove('input.txt')

        # copy input file
        shutil.copyfile(input_dir + '/' + f, 'input.txt')
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
        else:
            shutil.copyfile('output.txt', output_dir + '/' + f)
            os.remove('output.txt')
            finished.append(f)
            f_status = 'Done'
        if SHOW_STAT:
            print('Processed ' + f + ' in ' + get_delta_time(delta_time) + 'ms. Status: ' + f_status)

    if SHOW_STAT:
        print('Complete Batch. ' + str(len(finished)) + ' done. ' + str(len(errored)) + ' error.')


def main(script='homework3.py'):
    # see the above def of this function to understand all the params
    generate_batch([4, 8], 2, 4, ['MINIMAX', 'ALPHABETA'], [1, 5], ['O', 'X'], 50, 20)
    generate_output(script)


if __name__ == '__main__':
    if len(sys.argv) > 1:
        main(sys.argv[1])
    else:
        main()

