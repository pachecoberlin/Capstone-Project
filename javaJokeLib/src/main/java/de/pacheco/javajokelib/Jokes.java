package de.pacheco.javajokelib;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Jokes {
    private static List<String> jokes = Arrays.asList("I asked God for a bike, but I know God " +
                    "doesn't work that way. So I stole a bike and asked for forgiveness.", "I want to die" +
                    " peacefully in my sleep, like my grandfather.. Not screaming and yelling like the " +
                    "passengers in his car.", "Do not argue with an idiot. He will drag you down to his " +
                    "level and beat you with experience.", "The last thing I want to do is hurt you. But " +
                    "it's still on the list.", "If sex is a pain in the ass, then you're doing it wrong.." +
                    ".", "The early bird might get the worm, but the second mouse gets the cheese.",
            "We live in a society where pizza gets to your house before the police.");

    private static String props = "Joke is from https://blogs.warwick.ac" +
            ".uk/xma/entry/100_one-sentence_jokes/\n\n";

    public static String giveMeAJoke() {
        Random rand = new Random();
        return props + jokes.get(rand.nextInt(jokes.size()));
    }
}