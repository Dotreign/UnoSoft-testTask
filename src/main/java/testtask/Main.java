package testtask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Main {

  static ArrayList<HashSet<String>> groups = new ArrayList<>();
  static ArrayList<HashMap<String, Integer>> wordsPositions = new ArrayList<>();
  static HashMap<Integer, Integer> joinedGroups = new HashMap<>();

  public static void main(String[] args) throws Exception {
    long startTime = Instant.now().getEpochSecond();

    try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
      br.lines().forEach(Main::processLine);
      long algorithmTime = Instant.now().getEpochSecond();
      System.out.println(algorithmTime - startTime);
    }
    int counter = 0;
    int moreThanOneCounter = 0;
    groups.removeAll(Collections.singleton(null));
    groups.sort((a, b) -> a.size() < b.size() ? 1 : a.size() > b.size() ? -1 : 0);
    try (Writer writer = new FileWriter("output.txt", StandardCharsets.UTF_8)) {
      for (HashSet<String> group : groups) {
        if (group == null) {
          continue;
        }
        if (group.size() > 1) {
          moreThanOneCounter += 1;
        }
        counter += 1;
        writer.write("Group " + counter + "\n");
        for (String line : group) {
          writer.write(line + "\n");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    long endTime = Instant.now().getEpochSecond();
    System.out.println(moreThanOneCounter);
    System.out.println(endTime - startTime);

  }

  private static void processLine(String line) {

    Integer groupNumber = null;
    ArrayList<Integer> otherGroups = new ArrayList<>();
    ArrayList<String> parts = new ArrayList<>();

    for (String part : line.split(";")) {
      if (part.isEmpty()) {
        parts.add("");
        continue;
      }
      part = part.substring(1, part.length() - 1);
      if (!part.contains("\"")) {
        parts.add(part);
      }
    }

    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);

      if (wordsPositions.size() <= i) {
        break;
      } else {
        HashMap<String, Integer> position = wordsPositions.get(i);
        if (position.containsKey(part)) {
          Integer num = position.get(part);
          while (joinedGroups.containsKey(num)) {
            num = joinedGroups.get(num);
          }
          if (groupNumber == null) {
            groupNumber = num;
          } else {
            otherGroups.add(num);
          }
        }
      }
    }

    if (groupNumber != null) {
      if (otherGroups.isEmpty()) {
        HashSet<String> group = groups.get(groupNumber);
        group.add(line);
      } else {
        HashSet<String> group = groups.get(groupNumber);
        for (Integer num : otherGroups) {
          if (num != groupNumber) {
            HashSet<String> groupToMerge = groups.get(num);
            if (groupToMerge == null) {
              continue;
            }
            group.addAll(groups.get(num));
            groups.set(num, null);
            joinedGroups.put(num, groupNumber);
          }
        }
        group.add(line);
      }

    } else {
      groupNumber = groups.size();
      HashSet<String> group = new HashSet<>();
      group.add(line);
      groups.add(group);
    }

    for (int i = 0; i < parts.size(); i++) {
      if (wordsPositions.size() <= i) {
        HashMap<String, Integer> position = new HashMap<>();
        if (parts.get(i).isEmpty()) {
          wordsPositions.add(position);
          continue;
        }
        position.put(parts.get(i), groupNumber);
        wordsPositions.add(position);
      } else {
        if (parts.get(i).isEmpty()) {
          continue;
        }
        HashMap<String, Integer> position = wordsPositions.get(i);
        position.put(parts.get(i), groupNumber);
      }
    }

  }
}
