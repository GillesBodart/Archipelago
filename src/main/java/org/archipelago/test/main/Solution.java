package org.archipelago.test.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

public class Solution {

    static Map<String, Integer> testsWords = new HashMap<>();
    static Map<String, Integer> occurence = new HashMap<>();
    static boolean alternateWord = false;

    public static void main(String[] args) {
        // Scanner in = new Scanner(System.in);
        // int len = in.nextInt();
        // int maxLength = Integer.MIN_VALUE;
        // String s = in.next();
        String s = "azertyuiopmlkjhgfdsqwxcvbn";
        Set<String> letters = extractLetters(s);
        feedOccurence(s);
        int max = 0;
        if (!alternateWord && s.length() > 2) {
            max = 2;
        }else{
            max = removeLetter(letters, new HashSet<String>(), s, -1);
        }
        System.out.println(max);
    }


    public static int removeLetter(Set<String> allLetters, Set<String> consumed, String word, int max) {
        String test = getTestCase(consumed, word);
        System.out.println(String.format("%s %s %d", test, consumed, max));
        if (isValid(test)) {
            max = max > test.length() ? max : test.length();
        } else if (extractLetters(test).size() > 2) {
            Set<String> tmp = new HashSet(allLetters);
            tmp.removeAll(consumed);
            for (String remain : tmp) {
                Set<String> feed = new HashSet(consumed);
                feed.add(remain);
                if (!testsWords.containsKey(getTestCase(feed, word))) {
                    int tmpMax = removeLetter(allLetters, feed, word, max);
                    max = max > tmpMax ? max : tmpMax;
                    testsWords.put(test, tmpMax);
                }
            }
        }
        return max;
    }

    private static String getTestCase(Set<String> consumed, String word) {
        StringJoiner sj = new StringJoiner("|", "(", ")");
        for (String letter : consumed) {
            sj.add(letter);
        }
        String regex = sj.toString();
        String test = word.replaceAll(regex, "");
        return test;
    }

    static boolean isValid(String word) {
        if (word.length() == 0)
            return true;
        boolean valid = extractLetters(word).size() <= 2;
        char letter = word.charAt(0);
        for (int i = 1; i < word.length() && valid; i++) {
            valid &= letter != word.charAt(i);
            letter = word.charAt(i);
        }
        return valid;
    }

    static Set<String> extractLetters(String s) {
        Set<String> letters = new TreeSet<>();
        for (String letter : s.split("")) {
            letters.add(letter);
        }
        return letters;
    }

    static void feedOccurence(String word) {
        for (String letter : word.split("")) {
            if (occurence.containsKey(letter)) {
                occurence.put(letter, occurence.get(letter) + 1);
            } else {
                occurence.put(letter, 1);
            }
        }
        for (Integer val : occurence.values()) {
            alternateWord |= val > 2;
        }
    }

}
