from graph_metadata import read_file
import os
import networkx as nx
import matplotlib.pyplot as plt


def plot(configuration):
    G = nx.Graph()
    color_map = configuration['color_list']
    print(color_map)
    for index, item in enumerate(color_map):
        G.add_node(index + 1)
    print(G)
    print(G.nodes)
    for e in configuration['edges']:
        print(e[0], e[1])
        G.add_edge(e[0], e[1])

    print(G)
    print(G.nodes)
    nx.draw(G, node_color=color_map, with_labels=True)
    plt.show()


if __name__ == "__main__":
    dir_name = os.getcwd()
    base_filename = 'test2.txt'
    actual_path = os.path.join(dir_name, base_filename)
    configuration = read_file(actual_path)
    plot(configuration)
