package com.fileparser;

public class WordOccurrenceTuple {
    String word;
    Long amountOfOccurrences;

    public WordOccurrenceTuple(String word, Long amountOfOccurrences) {
        this.word = word;
        this.amountOfOccurrences = amountOfOccurrences;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getAmountOfOccurrences() {
        return amountOfOccurrences;
    }

    public void setAmountOfOccurrences(Long amountOfOccurrences) {
        this.amountOfOccurrences = amountOfOccurrences;
    }

    @Override
    public boolean equals(Object obj) {
        WordOccurrenceTuple wordOccurrenceTuple = (WordOccurrenceTuple) obj;
        return wordOccurrenceTuple.word.equals(this.word) && wordOccurrenceTuple.amountOfOccurrences == this.amountOfOccurrences;
    }
}

