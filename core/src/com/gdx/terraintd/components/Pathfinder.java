package com.gdx.terraintd.components;

import java.util.*;

public class Pathfinder {
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    public Pathfinder(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public List<int[]> findPath(String[][] gridArray) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);

        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.equals(endNode)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current, gridArray)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double moveCost = getMoveCost(current, neighbor, gridArray);
                double tentativeGScore = current.g + moveCost;

                if (!openSet.contains(neighbor) || tentativeGScore < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeGScore;
                    neighbor.h = heuristic(neighbor, endNode);
                    neighbor.f = neighbor.g + neighbor.h;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private double getMoveCost(Node from, Node to, String[][] gridArray) {
        String[] cell = getCell(to.x, to.y, gridArray);
        if (cell != null && cell[2].equals("Sand")) {
            return 2.0;
        }
        return 1.0;
    }

    private List<Node> getNeighbors(Node node, String[][] gridArray) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            if (isValidAndEmpty(newX, newY, gridArray)) {
                neighbors.add(new Node(newX, newY));
            }
        }

        return neighbors;
    }

    private boolean isValidAndEmpty(int x, int y, String[][] gridArray) {
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                return cell[2].equals("Grass") || cell[2].equals("Sand") || cell[2].equals("End");
            }
        }
        return false;
    }

    public List<int[]> findCriticalNodes(String[][] gridArray) {
        List<int[]> criticalNodes = new ArrayList<>();
        List<int[]> originalPath = findPath(gridArray);

        if (originalPath == null) {
            return criticalNodes;
        }

        for (int[] node : originalPath) {
            if ((node[0] == startX && node[1] == startY) || (node[0] == endX && node[1] == endY)) {
                continue;
            }

            String originalState = getCell(node[0], node[1], gridArray)[2];
            setCell(node[0], node[1], "Tower", gridArray);

            List<int[]> alternatePath = findPath(gridArray);

            if (alternatePath == null) {
                criticalNodes.add(node);
            }

            setCell(node[0], node[1], originalState, gridArray);
        }
        return criticalNodes;
    }

    private String[] getCell(int x, int y, String[][] gridArray) {
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                return cell;
            }
        }
        return null;
    }

    private void setCell(int x, int y, String value, String[][] gridArray) {
        for (String[] cell : gridArray) {
            if (Integer.parseInt(cell[0]) == x && Integer.parseInt(cell[1]) == y) {
                cell[2] = value;
                return;
            }
        }
    }

    private double heuristic(Node a, Node b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return dx + Math.max(0, (dy - dx) / 2);
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.x, node.y});
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private class Node implements Comparable<Node> {
        int x, y;
        Node parent;
        double g, h, f;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }
}