/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    int wordLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        ArrayList<String> anagrams;
        ArrayList<String> size_anagrams;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            String sortedWord = sortLetters(word);
            if (!lettersToWord.containsKey(sortedWord)) {
                anagrams = new ArrayList<>();
                anagrams.add(word);
                lettersToWord.put(sortedWord, anagrams);
            }
            else {
                anagrams = lettersToWord.get(sortedWord);
                anagrams.add(word);
            }
            if (!sizeToWords.containsKey(word.length())) {
                size_anagrams = new ArrayList<>();
                size_anagrams.add(word);
                sizeToWords.put(new Integer(word.length()), size_anagrams);
            }
            else {
                size_anagrams = sizeToWords.get(word.length());
                size_anagrams.add(word);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        if (wordSet.contains(word))
            if (!word.contains(base))
                return true;
        return false;
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String target = new String();
        for (int i=0; i<wordList.size(); i++) {
            target = wordList.get(i);
            if (target.length() != targetWord.length())
                    continue;
            if (sortLetters(target).equalsIgnoreCase(sortLetters(targetWord)))
                result.add(target);
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char c = 'a'; c <= 'z'; c++) {
            String sortedWord = sortLetters(word+c);
            for (int i=0; lettersToWord.containsKey(sortedWord) && i<lettersToWord.get(sortedWord).size(); i++) {
                if (isGoodWord(lettersToWord.get(sortedWord).get(i), word))
                    result.add(lettersToWord.get(sortedWord).get(i));
            }
        }
        return result;
    }

    public List<String> getAnagramsWithOneOrTwoMoreLetters(String word) {
        ArrayList<String> result = (ArrayList<String>) getAnagramsWithOneMoreLetter(word);
        for (char c = 'a'; c <= 'z'; c++) {
            for (char x = c; x <= 'z'; x++) {
                String sortedWord = sortLetters(word+c+x);
                for (int i=0; lettersToWord.containsKey(sortedWord) && i<lettersToWord.get(sortedWord).size(); i++) {
                    if (isGoodWord(lettersToWord.get(sortedWord).get(i), word))
                        result.add(lettersToWord.get(sortedWord).get(i));
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> words = sizeToWords.get(wordLength);
        int i = random.nextInt(words.size());
        while (true) {
            String word = words.get((i++)%words.size());
            int count = getAnagramsWithOneMoreLetter(word).size();
            if (count >= MIN_NUM_ANAGRAMS) {
                wordLength = (wordLength == MAX_WORD_LENGTH)?DEFAULT_WORD_LENGTH:++wordLength;
                return word;
            }
        }
//        return "sabre";
    }

    public String sortLetters(String word) {
        char[] arr = word.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }
}
