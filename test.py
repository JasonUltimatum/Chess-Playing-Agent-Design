import os

input_dir = 'random_samples/'
ref_output_dir = 'random_samples_outputs/'
output_dir = 'my_outputs/'

n = 150

for f in range(n):
    print('----------------->test{0}:'.format(f))
    filename = 'sample_{0}.txt'.format(f)
    os.system('cp ' + input_dir + filename + ' ' +  './input.txt')
    os.system('java homework')
    
    ref_filename = filename
    os.system('diff -b output.txt ' + ref_output_dir + ref_filename)
    os.system('cp output.txt ' + output_dir + 'output{0}.txt'.format(f))

