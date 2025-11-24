package utils;

import elements.Elements;

import java.io.*;
import java.util.*;

public class LoadMap 
{
    public static Elements.Element[][] fromFile(String filename) 
    {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            return null;
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        Elements.Element[][] grid = new Elements.Element[rows][cols];

        for (int y = 0; y < rows; y++) {
            String line = lines.get(y);
            for (int x = 0; x < cols; x++) {
                char c = line.charAt(x);
                switch (c) {
                    case 'S': grid[y][x] = new Elements.Sand(); break;
                    case 'W': grid[y][x] = new Elements.Water(); break;
                    case 'D': grid[y][x] = new Elements.Dirt(); break;
                    case 'O': grid[y][x] = new Elements.Stone(); break;
                    default:  grid[y][x] = new Elements.Empty(); break;
                }
            }
        }
        return grid;
    }
}
