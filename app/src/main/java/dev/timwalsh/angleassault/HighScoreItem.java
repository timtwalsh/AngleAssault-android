package dev.timwalsh.angleassault;


public class HighScoreItem {
    int _id;
    String _name;
    int _score;

    public HighScoreItem() {

    }

    public HighScoreItem(String name, int _score) {
        this._name = name;
        this._score = _score;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public int getScore() {
        return this._score;
    }

    public void setScore(int score) {
        this._score = score;
    }

    @Override
    public String toString() {
        return "HighScore{" +
                "_name='" + _name + '\'' +
                ", _score=" + _score +
                '}';
    }
}
