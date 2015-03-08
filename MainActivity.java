package com.m1sclick.graphsearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;


public class MainActivity extends Activity {

    private static ArrayList<Vertex> graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void findButtonClick(View view) {
        Spinner fromSpinner = (Spinner) findViewById(R.id.fromSearchSpinner);
        int from = (int) fromSpinner.getSelectedItemId();
        Spinner toSpinner = (Spinner) findViewById(R.id.toSearchSpinner);
        int to = (int) toSpinner.getSelectedItemId();
        if (fromSpinner.getSelectedItem() != null || toSpinner.getSelectedItem() != null) {
            String result = "";
            result = djAlgorithm(from, to);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(result);
            dialog.setPositiveButton("OK", null);
            dialog.create().show();
        } else {
            Toast toast = Toast.makeText(this, R.string.wrongVerticesError, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void createVerticesButtonClick(View view) {
        EditText text = (EditText) findViewById(R.id.vertexText);
        String input = text.getText().toString();
        if (input.length() <= 0) {
            Toast toast = Toast.makeText(this, R.string.verticesError, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            graph = createVertices(input);
            ArrayAdapter<Vertex> adapter = new ArrayAdapter<Vertex>(this, android.R.layout.simple_spinner_item, graph);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner fromList = (Spinner) findViewById(R.id.fromSpinner);
            fromList.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner toList = (Spinner) findViewById(R.id.toSpinner);
            toList.setAdapter(adapter);
            Spinner fromSpinner = (Spinner) findViewById(R.id.fromSearchSpinner);
            fromSpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner toSpinner = (Spinner) findViewById(R.id.toSearchSpinner);
            toSpinner.setAdapter(adapter);
            Toast toast = Toast.makeText(this, R.string.verticesSuccess, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void createEdgeButtonClick(View view) {
        EditText text = (EditText) findViewById(R.id.lengthText);
        if (text.getText().length() <= 0) {
            Toast toast = Toast.makeText(this, R.string.lengthError, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            double length = Double.parseDouble(text.getText().toString());
            Spinner fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
            int from = (int) fromSpinner.getSelectedItemId();
            Spinner toSpinner = (Spinner) findViewById(R.id.toSpinner);
            int to = (int) toSpinner.getSelectedItemId();
            if (fromSpinner.getSelectedItem() != null || toSpinner.getSelectedItem() != null) {
                graph.get(from).edges.add(new Edge(graph.get(to), length));
                Toast toast = Toast.makeText(this, R.string.edgesSuccess, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, R.string.edgeError, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    class Vertex implements Comparable<Vertex> {
        public final String name;
        public ArrayList<Edge> edges = new ArrayList<Edge>();
        public double minDistance = Double.POSITIVE_INFINITY;
        public Vertex previous;

        public Vertex(String argName) {
            name = argName;
        }

        public String toString() {
            return name;
        }

        public int compareTo(Vertex other) {
            return Double.compare(minDistance, other.minDistance);
        }
    }

    class Edge {
        public final Vertex target;
        public final double weight;

        public Edge(Vertex argTarget, double argWeight) {
            target = argTarget;
            weight = argWeight;
        }

        public String toString() {
            return target + " " + weight;
        }

    }

    public ArrayList<Vertex> createVertices(String input) {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        String[] ves = input.split(" ");
        LinkedHashSet<String> hs = new LinkedHashSet<String>(Arrays.asList(ves));
        ves = hs.toArray(new String[hs.size()]);
        for (String name : ves) {
            vertices.add(new Vertex(name));
        }
        return vertices;
    }

    public String djAlgorithm(int fid, int toid) {
        if (graph.get(fid).minDistance != Double.POSITIVE_INFINITY || graph.get(toid).minDistance != Double.POSITIVE_INFINITY) {
            for (Vertex v : graph) {
                v.minDistance = Double.POSITIVE_INFINITY;
            }
        }
        Vertex from = graph.get(fid);
        Vertex to = graph.get(toid);
        long startTime = System.currentTimeMillis();
        computePaths(from);
        String result = "Алгоритм Дейкстры:" + "\r\n";
        result += "Сложность алгоритма:  O(n^2)" + "\r\n";
        result += "Дистанция до: " + to + " - " + to.minDistance + "\r\n";
        List<Vertex> path = getShortestPathTo(to);
        result += "Путь: " + path + "\r\n";
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        result += "Затраченое время: " + totalTime + " мс.";
        return result;
    }

    public void computePaths(Vertex source) {
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();
            for (Edge e : u.edges) {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            if (vertex.previous != null && vertex.previous.previous != null && vertex.previous.previous.name.equals(vertex.name)) {
                path.add(vertex);
                path.add(vertex.previous);
                break;
            } else {
                path.add(vertex);
            }
        }

        Collections.reverse(path);
        return path;
    }

    public String bfAlgorithm() {
        return "BellmanFord";
    }

    public String fyAlgorthm() {
        return "FloydYorshel";
    }

    public String asAlgorithm() {
        return "A*";
    }

}
