import os
import sys

def read_file(file_name):
    configuration = {}
    vertex_colors = []
    edge_set = []
    with open(file_name, 'r') as file:
        lines = file.readlines()
        configuration['NumberOfVertices'] = int(lines[0].strip())
        configuration['NumberOfEdges'] = int(lines[1].strip())
        lines = lines[2:]
        vertices = 0
        while vertices < configuration['NumberOfVertices']:
            color = lines[vertices].strip()
            vertex_colors.append(int(color.strip()))
            vertices = vertices + 1
        lines = lines[configuration['NumberOfVertices']:]
        edges = 0
        while edges < configuration['NumberOfEdges']:
            e = lines[edges].split(' ')
            t = (int(e[0].strip()), int(e[1].strip()))
            edge_set.append(t)
            edges = edges + 1
        configuration['edges'] = edge_set
        configuration['color_list'] = vertex_colors
    return configuration

if __name__ == "__main__":

    if len(sys.argv) != 2:
        print('Please provide a filename that contains data that conforms to the plotting schema:')
        print('$ python3 graph_metadata.py filename')
    else:
        filename = sys.argv[-1]
        dir_name = os.getcwd()
        base_filename = filename
        actual_path = os.path.join(dir_name, base_filename)
        print(read_file(actual_path))