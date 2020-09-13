package de.pacheco.capstone.jokes;

public class Joke {

    private String joke;
    private String name;

    public Joke() {
    }

    public Joke(String text, String name) {
        this.joke = text;
        this.name = name;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}