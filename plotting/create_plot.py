from graph_metadata import read_file
import os
import sys
import networkx as nx
import matplotlib.pyplot as plt


def plot(configuration):
    G = nx.Graph()
    color_map = configuration['color_list']

    for index, item in enumerate(color_map):
        G.add_node(index + 1)

    print('--- edges ---')
    for e in configuration['edges']:
        print(f'{e[0]}-{e[1]}')
        G.add_edge(e[0], e[1])

    print('--- metadata ---')
    print(G)
    print(f'color map: {color_map}')

    plt.figure(1, figsize=(40, 40))
    nx.draw_random(G, node_size=600, node_color=color_map, with_labels=True)
    plt.show()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('Please provide a filename that contains data that conforms to the plotting schema:')
        print('$ python3 create_plot.py filename')
    else:
        dir_name = os.getcwd()
        base_filename = sys.argv[-1]
        actual_path = os.path.join(dir_name, base_filename)
        configuration = read_file(actual_path)
        plot(configuration)
