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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Main {

  static ArrayList<HashSet<String>> groups = new ArrayList<>();
  static ArrayList<HashMap<String, Integer>> wordsPositions = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    long startTime = Instant.now().getEpochSecond();

    try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
      br.lines().forEach(Main::processLine);
    }

    int counter = 0;
    int moreThanOneCounter = 0;
    groups.sort((a, b) -> a.size() < b.size() ? 1 : a.size() > b.size() ? -1 : 0);
    try (
        Writer writer = new FileWriter("output.txt", StandardCharsets.UTF_8)
    ) {
      for (HashSet<String> group :
          groups) {
        if (group.size() > 1) {
          moreThanOneCounter += 1;
        }
        counter += 1;
        writer.write("Group " + counter + "\n");
        for (String line :
            group) {
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

    List<String> parts = Arrays.stream(line.split(";"))
        .map(part -> part.substring(1, part.length() - 1)).filter(part -> !part.contains("\""))
        .toList();

    for (int i = 0; i < parts.size(); i++) {
      String part = parts.get(i);

      if (wordsPositions.size() <= i) {
        break;
      } else {
        if (wordsPositions.get(i).containsKey(part)) {
          groupNumber = wordsPositions.get(i).get(part);
          break;
        }
      }
    }

    if (groupNumber != null) {
      HashSet<String> group = groups.get(groupNumber - 1);
      group.add(line);
    } else {
      groupNumber = groups.size() + 1;
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
