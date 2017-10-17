#!/usr/bin/env python
#
# Borrowed from: http://dpk.io/pagerank
# The output of this script will be put into a unit-test as an expected output

from math import sqrt, pow


def pagerank(graph, damping=0.85, epsilon=1.0e-8):
    inlink_map = {}
    outlink_counts = {}

    def new_node(node):
        if node not in inlink_map:
            inlink_map[node] = set()
        if node not in outlink_counts:
            outlink_counts[node] = 0

    for tail_node, head_node in graph:
        new_node(tail_node)
        new_node(head_node)
        if tail_node == head_node:
            continue

        if tail_node not in inlink_map[head_node]:
            inlink_map[head_node].add(tail_node)
            outlink_counts[tail_node] += 1

    all_nodes = set(inlink_map.keys())
    for node, outlink_count in outlink_counts.items():
        if outlink_count == 0:
            outlink_counts[node] = len(all_nodes)
            for l_node in all_nodes: inlink_map[l_node].add(node)

    initial_value = 1 / len(all_nodes)
    ranks = {}
    for node in inlink_map.keys():
        ranks[node] = initial_value

    delta = 1.0
    n_iterations = 0
    while delta > epsilon:
        new_ranks = {}
        for node, inlinks in inlink_map.items():
            new_ranks[node] = ((1 - damping) / len(all_nodes)) + (
                damping * sum(ranks[inlink] / outlink_counts[inlink] for inlink in inlinks))
        delta = sqrt(sum(pow(new_ranks[node] - ranks[node], 2) for node in new_ranks.keys()))
        ranks, new_ranks = new_ranks, ranks
        n_iterations += 1

    return ranks, n_iterations


def main():
    # Adjacency matrix from src/test/scala/com/github/tartakynov/osm/algorithms/PageRankSpec.scala
    # [0, 1, 0, 1, 0, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 1, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    # [1, 0, 1, 0, 0, 0, 0, 0, 0, 0]
    # [0, 1, 0, 1, 0, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 1, 0, 0, 0, 0, 0]
    # [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
    # [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    graph = [
        ('A1', 'A2'),
        ('A1', 'C'),
        ('A2', 'D'),
        ('E', 'A1'),
        ('E', 'B'),
        ('F', 'A2'),
        ('F', 'C'),
        ('G', 'D'),
        ('H', 'J')
    ]

    print pagerank(graph)


if __name__ == '__main__':
    main()
