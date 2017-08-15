package JavaBean;

public class Command {
    enum Type{
        POWER,BRIGHTNESS,MOUSE,SCREENSHOOT,SEARCH,VOLUME,EXPLORER
    }
    Type type;
    String command;
}
